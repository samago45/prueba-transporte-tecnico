package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConductorDomainServiceTest {

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private ConductorDomainService conductorDomainService;

    private Conductor conductor;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        conductor = new Conductor();
        conductor.setId(1L);
        conductor.setNombre("Juan Pérez");
        conductor.setLicencia("A12345");
        conductor.setActivo(true);
        conductor.setVehiculos(Collections.emptyList());

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
    }

    @Test
    @DisplayName("Debe crear conductor exitosamente")
    void crearConductor_DebeCrearExitosamente() {
        // Arrange
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        // Act
        Conductor resultado = conductorDomainService.crearConductor(conductor);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan Pérez");
        assertThat(resultado.getLicencia()).isEqualTo("A12345");
        assertThat(resultado.isActivo()).isTrue();
        verify(conductorRepository).save(conductor);
    }

    @Test
    @DisplayName("Debe validar formato de licencia al crear conductor")
    void crearConductor_DebeValidarFormatoLicencia() {
        // Arrange
        conductor.setLicencia("123"); // Formato inválido

        // Act & Assert
        assertThatThrownBy(() -> conductorDomainService.crearConductor(conductor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de licencia inválido");
    }

    @Test
    @DisplayName("Debe actualizar conductor exitosamente")
    void actualizarConductor_DebeActualizarExitosamente() {
        // Arrange
        Conductor conductorActualizado = new Conductor();
        conductorActualizado.setId(1L);
        conductorActualizado.setNombre("Juan Pérez Actualizado");
        conductorActualizado.setLicencia("B67890");
        conductorActualizado.setActivo(true);

        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductorActualizado);

        // Act
        Conductor resultado = conductorDomainService.actualizarConductor(1L, conductorActualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan Pérez Actualizado");
        assertThat(resultado.getLicencia()).isEqualTo("B67890");
        verify(conductorRepository).save(any(Conductor.class));
    }

    @Test
    @DisplayName("Debe activar conductor exitosamente")
    void activarConductor_DebeActivarExitosamente() {
        // Arrange
        conductor.setActivo(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        // Act
        Conductor resultado = conductorDomainService.activarConductor(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isActivo()).isTrue();
        verify(conductorRepository).save(conductor);
    }

    @Test
    @DisplayName("Debe desactivar conductor exitosamente")
    void desactivarConductor_DebeDesactivarExitosamente() {
        // Arrange
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        // Act
        Conductor resultado = conductorDomainService.desactivarConductor(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isActivo()).isFalse();
        verify(conductorRepository).save(conductor);
    }

    @Test
    @DisplayName("Debe asignar vehículo a conductor exitosamente")
    void asignarVehiculo_DebeAsignarExitosamente() {
        // Arrange
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(conductorRepository.save(any(Conductor.class))).thenReturn(conductor);

        // Act
        Conductor resultado = conductorDomainService.asignarVehiculo(1L, 1L);

        // Assert
        assertThat(resultado).isNotNull();
        verify(conductorRepository).save(conductor);
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    @DisplayName("Debe validar conductor existente al asignar vehículo")
    void asignarVehiculo_DebeValidarConductorExistente() {
        // Arrange
        when(conductorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> conductorDomainService.asignarVehiculo(999L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Conductor no encontrado");
    }

    @Test
    @DisplayName("Debe validar vehículo existente al asignar")
    void asignarVehiculo_DebeValidarVehiculoExistente() {
        // Arrange
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> conductorDomainService.asignarVehiculo(1L, 999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Vehículo no encontrado");
    }

    @Test
    @DisplayName("Debe validar conductor activo al asignar vehículo")
    void asignarVehiculo_DebeValidarConductorActivo() {
        // Arrange
        conductor.setActivo(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));

        // Act & Assert
        assertThatThrownBy(() -> conductorDomainService.asignarVehiculo(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El conductor no está activo");
    }

    @Test
    @DisplayName("Debe validar vehículo activo al asignar")
    void asignarVehiculo_DebeValidarVehiculoActivo() {
        // Arrange
        vehiculo.setActivo(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act & Assert
        assertThatThrownBy(() -> conductorDomainService.asignarVehiculo(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El vehículo no está activo");
    }
} 