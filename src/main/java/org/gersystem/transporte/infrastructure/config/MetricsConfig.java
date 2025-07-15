package org.gersystem.transporte.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de métricas personalizadas para el negocio de transporte.
 * 
 * Métricas implementadas:
 * - Número de pedidos por hora
 * - Disponibilidad de vehículos
 * - Tiempo de respuesta de APIs
 * - Tasa de éxito de asignaciones
 */
@Configuration
public class MetricsConfig {

    @Bean
    public Counter pedidosCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transporte.pedidos.total")
                .description("Número total de pedidos creados")
                .register(meterRegistry);
    }

    @Bean
    public Counter pedidosPorHoraCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transporte.pedidos.por_hora")
                .description("Número de pedidos por hora")
                .register(meterRegistry);
    }

    @Bean
    public Counter vehiculosDisponiblesGauge(MeterRegistry meterRegistry) {
        return Counter.builder("transporte.vehiculos.disponibles")
                .description("Número de vehículos disponibles")
                .register(meterRegistry);
    }

    @Bean
    public Counter vehiculosEnMantenimientoCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transporte.vehiculos.en_mantenimiento")
                .description("Número de vehículos en mantenimiento")
                .register(meterRegistry);
    }

    @Bean
    public Timer asignacionTimer(MeterRegistry meterRegistry) {
        return Timer.builder("transporte.asignacion.tiempo")
                .description("Tiempo de respuesta para asignaciones")
                .register(meterRegistry);
    }

    @Bean
    public Counter asignacionesExitosasCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transporte.asignaciones.exitosas")
                .description("Número de asignaciones exitosas")
                .register(meterRegistry);
    }

    @Bean
    public Counter asignacionesFallidasCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transporte.asignaciones.fallidas")
                .description("Número de asignaciones fallidas")
                .register(meterRegistry);
    }

    @Bean
    public Timer pedidoCreationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("transporte.pedidos.creacion.tiempo")
                .description("Tiempo de creación de pedidos")
                .register(meterRegistry);
    }

    @Bean
    public Counter conductoresActivosCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transporte.conductores.activos")
                .description("Número de conductores activos")
                .register(meterRegistry);
    }
} 