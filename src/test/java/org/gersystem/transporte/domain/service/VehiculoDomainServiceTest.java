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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoDomainServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private ConductorRepository conductorRepository;

    @InjectMocks
    private VehiculoDomainService vehiculoDomainService;

    private Vehiculo vehiculo;
    private Conductor conductor;

    @BeforeEach
    void setUp() {
        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);

        conductor = new Conductor();
        conductor.setId(1L);
        conductor.setNombre("Juan Pérez");
        conductor.setLicencia("A12345");
        conductor.setActivo(true);
    }

    @Test
    @DisplayName("Debe validar placa correctamente")
    void validarPlaca_DebeValidarFormatoCorrectamente() {
        // Act & Assert
        assertThatThrownBy(() -> {
            vehiculo.setPlaca("123"); // Formato inválido
            vehiculoDomainService.validarPlaca(vehiculo);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de placa inválido");

        // Placa válida
        vehiculo.setPlaca("ABC123");
        vehiculoDomainService.validarPlaca(vehiculo);
    }

    @Test
    @DisplayName("Debe validar capacidad correctamente")
    void validarCapacidad_DebeValidarRangoPermitido() {
        // Act & Assert
        assertThatThrownBy(() -> {
            vehiculo.setCapacidad(BigDecimal.ZERO);
            vehiculoDomainService.validarCapacidad(vehiculo);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La capacidad debe ser mayor a 0");

        // Capacidad válida
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculoDomainService.validarCapacidad(vehiculo);
    }

    @Test
    @DisplayName("Debe crear vehículo exitosamente")
    void crearVehiculo_DebeCrearExitosamente() {
        // Arrange
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        // Act
        Vehiculo resultado = vehiculoDomainService.crearVehiculo(vehiculo);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPlaca()).isEqualTo("ABC123");
        assertThat(resultado.getCapacidad()).isEqualByComparingTo(new BigDecimal("1000.00"));
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    @DisplayName("Debe actualizar vehículo exitosamente")
    void actualizarVehiculo_DebeActualizarExitosamente() {
        // Arrange
        Vehiculo vehiculoActualizado = new Vehiculo();
        vehiculoActualizado.setId(1L);
        vehiculoActualizado.setPlaca("XYZ789");
        vehiculoActualizado.setCapacidad(new BigDecimal("2000.00"));
        vehiculoActualizado.setActivo(true);

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculoActualizado);

        // Act
        Vehiculo resultado = vehiculoDomainService.actualizarVehiculo(1L, vehiculoActualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPlaca()).isEqualTo("XYZ789");
        assertThat(resultado.getCapacidad()).isEqualByComparingTo(new BigDecimal("2000.00"));
        verify(vehiculoRepository).save(any(Vehiculo.class));
    }

    @Test
    @DisplayName("Debe activar vehículo exitosamente")
    void activarVehiculo_DebeActivarExitosamente() {
        // Arrange
        vehiculo.setActivo(false);
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        // Act
        Vehiculo resultado = vehiculoDomainService.activarVehiculo(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isActivo()).isTrue();
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    @DisplayName("Debe desactivar vehículo exitosamente")
    void desactivarVehiculo_DebeDesactivarExitosamente() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        // Act
        Vehiculo resultado = vehiculoDomainService.desactivarVehiculo(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isActivo()).isFalse();
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    @DisplayName("Debe asignar conductor exitosamente")
    void asignarConductor_DebeAsignarExitosamente() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        // Act
        Vehiculo resultado = vehiculoDomainService.asignarConductor(1L, 1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getConductor()).isNotNull();
        assertThat(resultado.getConductor().getId()).isEqualTo(1L);
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    @DisplayName("Debe validar vehículo existente al asignar conductor")
    void asignarConductor_DebeValidarVehiculoExistente() {
        // Arrange
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehiculoDomainService.asignarConductor(999L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Vehículo no encontrado");
    }

    @Test
    @DisplayName("Debe validar conductor existente al asignar")
    void asignarConductor_DebeValidarConductorExistente() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(conductorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehiculoDomainService.asignarConductor(1L, 999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Conductor no encontrado");
    }

    @Test
    @DisplayName("Debe validar conductor activo al asignar")
    void asignarConductor_DebeValidarConductorActivo() {
        // Arrange
        conductor.setActivo(false);
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));

        // Act & Assert
        assertThatThrownBy(() -> vehiculoDomainService.asignarConductor(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El conductor no está activo");
    }
} 