package org.gersystem.transporte.integration;

import org.gersystem.transporte.domain.model.*;
import org.gersystem.transporte.domain.repository.MantenimientoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.domain.service.MantenimientoDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class MantenimientoIntegrationTest {

    @Autowired
    private MantenimientoDomainService mantenimientoDomainService;

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    private Vehiculo vehiculo;
    private Mantenimiento mantenimiento;

    @BeforeEach
    void setUp() {
        // Crear vehículo
        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Crear mantenimiento
        mantenimiento = new Mantenimiento();
        mantenimiento.setDescripcion("Mantenimiento preventivo programado");
        mantenimiento.setTipo(TipoMantenimiento.PREVENTIVO);
        mantenimiento.setEstado(EstadoMantenimiento.PROGRAMADO);
        mantenimiento.setFechaProgramada(LocalDateTime.now().plusDays(1));
        mantenimiento.setVehiculo(vehiculo);
    }

    @Test
    @DisplayName("Debe completar flujo de mantenimiento exitosamente")
    @Transactional
    void flujoCompletoMantenimiento_DebeCompletarseCorrectamente() {
        // Act - Programar mantenimiento
        Mantenimiento mantenimientoCreado = mantenimientoDomainService.programarMantenimiento(mantenimiento);

        // Assert - Verificar programación
        assertThat(mantenimientoCreado).isNotNull();
        assertThat(mantenimientoCreado.getId()).isNotNull();
        assertThat(mantenimientoCreado.getEstado()).isEqualTo(EstadoMantenimiento.PROGRAMADO);

        // Act - Iniciar mantenimiento
        Mantenimiento mantenimientoIniciado = mantenimientoDomainService
                .actualizarEstadoMantenimiento(mantenimientoCreado.getId(), EstadoMantenimiento.EN_PROCESO);

        // Assert - Verificar inicio
        assertThat(mantenimientoIniciado.getEstado()).isEqualTo(EstadoMantenimiento.EN_PROCESO);

        // Act - Completar mantenimiento
        Mantenimiento mantenimientoCompletado = mantenimientoDomainService
                .actualizarEstadoMantenimiento(mantenimientoCreado.getId(), EstadoMantenimiento.COMPLETADO);

        // Assert - Verificar completado
        assertThat(mantenimientoCompletado.getEstado()).isEqualTo(EstadoMantenimiento.COMPLETADO);
        assertThat(mantenimientoCompletado.getVehiculo().isActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe manejar mantenimiento de vehículo inactivo")
    @Transactional
    void flujoMantenimiento_DebeManejarVehiculoInactivo() {
        // Arrange - Desactivar vehículo
        vehiculo.setActivo(false);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Act & Assert - Intentar programar mantenimiento
        assertThatThrownBy(() -> mantenimientoDomainService.programarMantenimiento(mantenimiento))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El vehículo no está activo");
    }

    @Test
    @DisplayName("Debe validar transiciones de estado correctamente")
    @Transactional
    void flujoMantenimiento_DebeValidarTransicionesEstado() {
        // Arrange - Crear mantenimiento inicial
        Mantenimiento mantenimientoCreado = mantenimientoDomainService.programarMantenimiento(mantenimiento);

        // Act & Assert - Intentar completar sin iniciar
        assertThatThrownBy(() -> mantenimientoDomainService
                .actualizarEstadoMantenimiento(mantenimientoCreado.getId(), EstadoMantenimiento.COMPLETADO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Transición de estado inválida");
    }

    @Test
    @DisplayName("Debe manejar múltiples mantenimientos para un vehículo")
    @Transactional
    void flujoMantenimiento_DebeManejarMultiplesMantenimientos() {
        // Act - Programar primer mantenimiento
        Mantenimiento primerMantenimiento = mantenimientoDomainService.programarMantenimiento(mantenimiento);

        // Arrange - Crear segundo mantenimiento
        Mantenimiento segundoMantenimiento = new Mantenimiento();
        segundoMantenimiento.setDescripcion("Mantenimiento correctivo urgente");
        segundoMantenimiento.setTipo(TipoMantenimiento.CORRECTIVO);
        segundoMantenimiento.setEstado(EstadoMantenimiento.PROGRAMADO);
        segundoMantenimiento.setFechaProgramada(LocalDateTime.now().plusDays(2));
        segundoMantenimiento.setVehiculo(vehiculo);

        // Act - Programar segundo mantenimiento
        Mantenimiento segundoMantenimientoCreado = mantenimientoDomainService
                .programarMantenimiento(segundoMantenimiento);

        // Assert - Verificar ambos mantenimientos
        assertThat(mantenimientoRepository.findByVehiculo(vehiculo, null).getContent())
                .hasSize(2)
                .extracting(Mantenimiento::getTipo)
                .containsExactlyInAnyOrder(TipoMantenimiento.PREVENTIVO, TipoMantenimiento.CORRECTIVO);
    }

    @Test
    @DisplayName("Debe cancelar mantenimiento correctamente")
    @Transactional
    void flujoMantenimiento_DebeCancelarCorrectamente() {
        // Arrange - Crear mantenimiento inicial
        Mantenimiento mantenimientoCreado = mantenimientoDomainService.programarMantenimiento(mantenimiento);

        // Act - Cancelar mantenimiento
        Mantenimiento mantenimientoCancelado = mantenimientoDomainService
                .actualizarEstadoMantenimiento(mantenimientoCreado.getId(), EstadoMantenimiento.CANCELADO);

        // Assert - Verificar cancelación
        assertThat(mantenimientoCancelado.getEstado()).isEqualTo(EstadoMantenimiento.CANCELADO);

        // Act & Assert - Intentar modificar mantenimiento cancelado
        assertThatThrownBy(() -> mantenimientoDomainService
                .actualizarEstadoMantenimiento(mantenimientoCreado.getId(), EstadoMantenimiento.EN_PROCESO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se puede modificar un mantenimiento cancelado");
    }
} 