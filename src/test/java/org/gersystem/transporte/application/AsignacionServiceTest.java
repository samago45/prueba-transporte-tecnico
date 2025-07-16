package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.domain.service.ConductorDomainService;
import org.gersystem.transporte.domain.service.VehiculoDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private ConductorDomainService conductorDomainService;

    @Mock
    private VehiculoDomainService vehiculoDomainService;

    @InjectMocks
    private AsignacionService asignacionService;

    private Conductor conductor;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        conductor = new Conductor();
        conductor.setId(1L);
        conductor.setNombre("Juan Pérez");
        conductor.setLicencia("A12345");
        conductor.setActivo(true);
        conductor.setVehiculos(new ArrayList<>());

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
    }

    @Test
    @DisplayName("Debe asignar vehículo a conductor exitosamente")
    void asignarVehiculoAConductor_DebeAsignarExitosamente() {
        // Arrange
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        doNothing().when(conductorDomainService).validarLimiteDeVehiculos(conductor);
        when(conductorDomainService.asignarVehiculo(1L, 1L)).thenReturn(conductor);

        // Act & Assert
        assertDoesNotThrow(() -> asignacionService.asignarVehiculoAConductor(1L, 1L));

        verify(conductorRepository).findById(1L);
        verify(vehiculoRepository).findById(1L);
        verify(conductorDomainService).validarLimiteDeVehiculos(conductor);
        verify(conductorDomainService).asignarVehiculo(1L, 1L);
    }

    @Test
    @DisplayName("Debe fallar al asignar vehículo a conductor inactivo")
    void asignarVehiculoAConductor_DebeFallarConductorInactivo() {
        // Arrange
        conductor.setActivo(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El conductor no está activo");
    }

    @Test
    @DisplayName("Debe fallar al asignar vehículo inactivo")
    void asignarVehiculoAConductor_DebeFallarVehiculoInactivo() {
        // Arrange
        vehiculo.setActivo(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El vehículo no está activo");
    }

    @Test
    @DisplayName("Debe desasignar vehículo exitosamente")
    void desasignarVehiculo_DebeDesasignarExitosamente() {
        // Arrange
        vehiculo.setConductor(conductor);
        conductor.getVehiculos().add(vehiculo);
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act & Assert
        assertDoesNotThrow(() -> asignacionService.desasignarVehiculo(1L));

        verify(conductorRepository).save(conductor);
        verify(vehiculoRepository).save(vehiculo);
        assertThat(conductor.getVehiculos()).isEmpty();
        assertThat(vehiculo.getConductor()).isNull();
    }

    @Test
    @DisplayName("Debe fallar al desasignar vehículo no asignado")
    void desasignarVehiculo_DebeFallarVehiculoNoAsignado() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.desasignarVehiculo(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El vehículo no está asignado a ningún conductor");
    }
} 
