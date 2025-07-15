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
        when(conductorDomainService.asignarVehiculo(1L, 1L)).thenReturn(conductor);
        when(vehiculoDomainService.asignarConductor(1L, 1L)).thenReturn(vehiculo);

        // Act
        boolean resultado = asignacionService.asignarVehiculoAConductor(1L, 1L);

        // Assert
        assertThat(resultado).isTrue();
        verify(conductorDomainService).asignarVehiculo(1L, 1L);
        verify(vehiculoDomainService).asignarConductor(1L, 1L);
    }

    @Test
    @DisplayName("Debe manejar error cuando conductor no existe")
    void asignarVehiculoAConductor_DebeManejarConductorInexistente() {
        // Arrange
        when(conductorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(999L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Conductor no encontrado");
    }

    @Test
    @DisplayName("Debe manejar error cuando vehículo no existe")
    void asignarVehiculoAConductor_DebeManejarVehiculoInexistente() {
        // Arrange
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(1L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vehículo no encontrado");
    }

    @Test
    @DisplayName("Debe desasignar vehículo exitosamente")
    void desasignarVehiculo_DebeDesasignarExitosamente() {
        // Arrange
        vehiculo.setConductor(conductor);
        conductor.getVehiculos().add(vehiculo);
        
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        // Act
        boolean resultado = asignacionService.desasignarVehiculo(1L);

        // Assert
        assertThat(resultado).isTrue();
        assertThat(vehiculo.getConductor()).isNull();
        verify(vehiculoRepository).save(vehiculo);
        verify(conductorRepository).save(conductor);
    }

    @Test
    @DisplayName("Debe manejar error al desasignar vehículo inexistente")
    void desasignarVehiculo_DebeManejarVehiculoInexistente() {
        // Arrange
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.desasignarVehiculo(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vehículo no encontrado");
    }

    @Test
    @DisplayName("Debe validar conductor activo al asignar")
    void asignarVehiculoAConductor_DebeValidarConductorActivo() {
        // Arrange
        conductor.setActivo(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El conductor no está activo");
    }

    @Test
    @DisplayName("Debe validar vehículo activo al asignar")
    void asignarVehiculoAConductor_DebeValidarVehiculoActivo() {
        // Arrange
        vehiculo.setActivo(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El vehículo no está activo");
    }
} 