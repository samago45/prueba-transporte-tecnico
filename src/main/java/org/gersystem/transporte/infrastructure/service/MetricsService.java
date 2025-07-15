package org.gersystem.transporte.infrastructure.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servicio para gestionar métricas de negocio en tiempo real.
 * 
 * Actualiza métricas como:
 * - Disponibilidad de vehículos
 * - Conductores activos
 * - Estadísticas de pedidos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final Counter pedidosCounter;
    private final Counter pedidosPorHoraCounter;
    private final Counter vehiculosDisponiblesGauge;
    private final Counter vehiculosEnMantenimientoCounter;
    private final Timer asignacionTimer;
    private final Counter asignacionesExitosasCounter;
    private final Counter asignacionesFallidasCounter;
    private final Timer pedidoCreationTimer;
    private final Counter conductoresActivosCounter;

    // Métricas de estado actual
    private final AtomicInteger vehiculosDisponibles = new AtomicInteger(0);
    private final AtomicInteger conductoresActivos = new AtomicInteger(0);
    private final AtomicInteger vehiculosEnMantenimiento = new AtomicInteger(0);

    /**
     * Incrementa el contador de pedidos creados.
     */
    public void incrementarPedidosCreados() {
        pedidosCounter.increment();
        pedidosPorHoraCounter.increment();
        log.debug("Pedido creado - Contador incrementado");
    }

    /**
     * Registra el tiempo de creación de un pedido.
     */
    public Timer.Sample iniciarTiempoCreacionPedido() {
        return Timer.start(meterRegistry);
    }

    /**
     * Finaliza y registra el tiempo de creación de pedido.
     */
    public void finalizarTiempoCreacionPedido(Timer.Sample sample) {
        sample.stop(pedidoCreationTimer);
        log.debug("Tiempo de creación de pedido registrado");
    }

    /**
     * Registra una asignación exitosa.
     */
    public void registrarAsignacionExitosa() {
        asignacionesExitosasCounter.increment();
        log.debug("Asignación exitosa registrada");
    }

    /**
     * Registra una asignación fallida.
     */
    public void registrarAsignacionFallida() {
        asignacionesFallidasCounter.increment();
        log.debug("Asignación fallida registrada");
    }

    /**
     * Registra el tiempo de una asignación.
     */
    public Timer.Sample iniciarTiempoAsignacion() {
        return Timer.start(meterRegistry);
    }

    /**
     * Finaliza y registra el tiempo de asignación.
     */
    public void finalizarTiempoAsignacion(Timer.Sample sample) {
        sample.stop(asignacionTimer);
        log.debug("Tiempo de asignación registrado");
    }

    /**
     * Actualiza el número de vehículos disponibles.
     */
    public void actualizarVehiculosDisponibles(int cantidad) {
        vehiculosDisponibles.set(cantidad);
        log.debug("Vehículos disponibles actualizados: {}", cantidad);
    }

    /**
     * Actualiza el número de conductores activos.
     */
    public void actualizarConductoresActivos(int cantidad) {
        conductoresActivos.set(cantidad);
        log.debug("Conductores activos actualizados: {}", cantidad);
    }

    /**
     * Actualiza el número de vehículos en mantenimiento.
     */
    public void actualizarVehiculosEnMantenimiento(int cantidad) {
        vehiculosEnMantenimiento.set(cantidad);
        log.debug("Vehículos en mantenimiento actualizados: {}", cantidad);
    }

    /**
     * Actualiza métricas cada 5 minutos.
     */
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void actualizarMetricasPeriodicas() {
        log.info("Actualizando métricas periódicas...");
        
        // Aquí podrías agregar lógica para actualizar métricas
        // basadas en datos de la base de datos
        
        log.info("Métricas actualizadas - Vehículos disponibles: {}, Conductores activos: {}, En mantenimiento: {}", 
                vehiculosDisponibles.get(), conductoresActivos.get(), vehiculosEnMantenimiento.get());
    }

    /**
     * Obtiene estadísticas actuales del sistema.
     */
    public String obtenerEstadisticas() {
        return String.format(
            "Estadísticas del Sistema:\n" +
            "- Vehículos disponibles: %d\n" +
            "- Conductores activos: %d\n" +
            "- Vehículos en mantenimiento: %d\n" +
            "- Pedidos totales: %.0f\n" +
            "- Asignaciones exitosas: %.0f\n" +
            "- Asignaciones fallidas: %.0f",
            vehiculosDisponibles.get(),
            conductoresActivos.get(),
            vehiculosEnMantenimiento.get(),
            pedidosCounter.count(),
            asignacionesExitosasCounter.count(),
            asignacionesFallidasCounter.count()
        );
    }
} 