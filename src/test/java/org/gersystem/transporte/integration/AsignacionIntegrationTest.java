package org.gersystem.transporte.integration;

import org.gersystem.transporte.TransporteApplication;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = TransporteApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        conductor.setVehiculos(new ArrayList<>());
        conductor = conductorRepository.save(conductor);

        // Crear vehículo
        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo = vehiculoRepository.save(vehiculo);
    }

    @Test
    @DisplayName("Debe completar flujo de asignación correctamente")
    @Transactional
    void flujoCompletoAsignacion_DebeCompletarseCorrectamente() {
        // Act - Asignar vehículo
        asignacionService.asignarVehiculoAConductor(conductor.getId(), vehiculo.getId());

        // Assert - Verificar asignación
        Conductor conductorActualizado = conductorRepository.findById(conductor.getId()).orElseThrow();
        assertThat(conductorActualizado.getVehiculos()).hasSize(1);
        assertThat(conductorActualizado.getVehiculos().get(0).getPlaca()).isEqualTo("ABC123");
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
    @DisplayName("Debe manejar asignación múltiple correctamente")
    @Transactional
    void flujoAsignacion_DebeManejarAsignacionMultiple() {
        // Arrange - Crear segundo vehículo
        final Vehiculo vehiculo2 = new Vehiculo();
        vehiculo2.setPlaca("XYZ789");
        vehiculo2.setCapacidad(new BigDecimal("1500.00"));
        vehiculo2.setActivo(true);
        vehiculoRepository.save(vehiculo2);

        // Act - Asignar primer vehículo
        asignacionService.asignarVehiculoAConductor(conductor.getId(), vehiculo.getId());

        // Act & Assert - Intentar asignar segundo vehículo
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(conductor.getId(), vehiculo2.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El conductor ya tiene el máximo de vehículos permitidos");

        // Verify
        Conductor conductorActualizado = conductorRepository.findById(conductor.getId()).orElseThrow();
        assertThat(conductorActualizado.getVehiculos()).hasSize(1);
    }

    @Test
    @DisplayName("Debe manejar vehículo inactivo correctamente")
    @Transactional
    void flujoAsignacion_DebeManejarVehiculoInactivo() {
        // Arrange
        vehiculo.setActivo(false);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Act & Assert
        assertThatThrownBy(() -> asignacionService.asignarVehiculoAConductor(conductor.getId(), vehiculo.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El vehículo no está activo");
    }

    @Test
    @DisplayName("Debe manejar vehículo no asignado correctamente")
    @Transactional
    void flujoDesasignacion_DebeManejarVehiculoNoAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> asignacionService.desasignarVehiculo(vehiculo.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El vehículo no está asignado a ningún conductor");
    }
} 
