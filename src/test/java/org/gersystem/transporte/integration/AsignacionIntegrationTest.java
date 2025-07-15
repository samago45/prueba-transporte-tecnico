package org.gersystem.transporte.integration;

import org.gersystem.transporte.application.AsignacionService;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AsignacionIntegrationTest {

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    private Conductor conductor;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        // Crear conductor
        conductor = new Conductor();
        conductor.setNombre("Juan Pérez");
        conductor.setLicencia("A12345");
        conductor.setActivo(true);
        conductor = conductorRepository.save(conductor);

        // Crear vehículo
        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo = vehiculoRepository.save(vehiculo);
    }

    @Test
    @DisplayName("Debe completar flujo de asignación exitosamente")
    @Transactional
    void flujoCompletoAsignacion_DebeCompletarseCorrectamente() {
        // Act - Asignar vehículo a conductor
        boolean resultadoAsignacion = asignacionService.asignarVehiculoAConductor(
                conductor.getId(), vehiculo.getId());

        // Assert - Verificar asignación
        assertThat(resultadoAsignacion).isTrue();

        // Recargar entidades desde la base de datos
        Conductor conductorActualizado = conductorRepository.findById(conductor.getId()).orElseThrow();
        Vehiculo vehiculoActualizado = vehiculoRepository.findById(vehiculo.getId()).orElseThrow();

        // Verificar relaciones
        assertThat(conductorActualizado.getVehiculos()).contains(vehiculoActualizado);
        assertThat(vehiculoActualizado.getConductor()).isEqualTo(conductorActualizado);

        // Act - Desasignar vehículo
        boolean resultadoDesasignacion = asignacionService.desasignarVehiculo(vehiculo.getId());

        // Assert - Verificar desasignación
        assertThat(resultadoDesasignacion).isTrue();

        // Recargar entidades nuevamente
        conductorActualizado = conductorRepository.findById(conductor.getId()).orElseThrow();
        vehiculoActualizado = vehiculoRepository.findById(vehiculo.getId()).orElseThrow();

        // Verificar que las relaciones se han eliminado
        assertThat(conductorActualizado.getVehiculos()).doesNotContain(vehiculoActualizado);
        assertThat(vehiculoActualizado.getConductor()).isNull();
    }

    @Test
    @DisplayName("Debe manejar asignación con conductor inactivo")
    @Transactional
    void flujoAsignacion_DebeManejarConductorInactivo() {
        // Arrange - Desactivar conductor
        conductor.setActivo(false);
        conductor = conductorRepository.save(conductor);

        // Act & Assert - Intentar asignar vehículo
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(
                conductor.getId(), vehiculo.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El conductor no está activo");

        // Verificar que no se realizó la asignación
        Vehiculo vehiculoActualizado = vehiculoRepository.findById(vehiculo.getId()).orElseThrow();
        assertThat(vehiculoActualizado.getConductor()).isNull();
    }

    @Test
    @DisplayName("Debe manejar asignación con vehículo inactivo")
    @Transactional
    void flujoAsignacion_DebeManejarVehiculoInactivo() {
        // Arrange - Desactivar vehículo
        vehiculo.setActivo(false);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Act & Assert - Intentar asignar vehículo
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(
                conductor.getId(), vehiculo.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El vehículo no está activo");

        // Verificar que no se realizó la asignación
        Conductor conductorActualizado = conductorRepository.findById(conductor.getId()).orElseThrow();
        assertThat(conductorActualizado.getVehiculos()).isEmpty();
    }

    @Test
    @DisplayName("Debe manejar desasignación de vehículo no asignado")
    @Transactional
    void flujoDesasignacion_DebeManejarVehiculoNoAsignado() {
        // Act & Assert - Intentar desasignar vehículo sin asignar
        boolean resultado = asignacionService.desasignarVehiculo(vehiculo.getId());

        // Verificar que la operación se completó sin cambios
        assertThat(resultado).isTrue();
        Vehiculo vehiculoActualizado = vehiculoRepository.findById(vehiculo.getId()).orElseThrow();
        assertThat(vehiculoActualizado.getConductor()).isNull();
    }

    @Test
    @DisplayName("Debe manejar asignación múltiple de vehículos")
    @Transactional
    void flujoAsignacion_DebeManejarAsignacionMultiple() {
        // Arrange - Crear segundo vehículo
        Vehiculo vehiculo2 = new Vehiculo();
        vehiculo2.setPlaca("XYZ789");
        vehiculo2.setCapacidad(new BigDecimal("2000.00"));
        vehiculo2.setActivo(true);
        vehiculo2 = vehiculoRepository.save(vehiculo2);

        // Act - Asignar ambos vehículos
        asignacionService.asignarVehiculoAConductor(conductor.getId(), vehiculo.getId());
        asignacionService.asignarVehiculoAConductor(conductor.getId(), vehiculo2.getId());

        // Assert - Verificar asignaciones
        Conductor conductorActualizado = conductorRepository.findById(conductor.getId()).orElseThrow();
        assertThat(conductorActualizado.getVehiculos()).hasSize(2);
        assertThat(conductorActualizado.getVehiculos())
                .extracting(Vehiculo::getPlaca)
                .containsExactlyInAnyOrder("ABC123", "XYZ789");
    }
} 