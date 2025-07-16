package org.gersystem.transporte.domain.service;

import lombok.Data;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EstadisticasDomainService {

    private final PedidoRepository pedidoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;

    public EstadisticasDomainService(PedidoRepository pedidoRepository,
                                    VehiculoRepository vehiculoRepository,
                                    ConductorRepository conductorRepository) {
        this.pedidoRepository = pedidoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.conductorRepository = conductorRepository;
    }

    @Cacheable(value = "estadisticasGenerales", key = "'general'")
    public EstadisticasGenerales obtenerEstadisticasGenerales() {
        EstadisticasGenerales estadisticas = new EstadisticasGenerales();
        
        // Métricas de vehículos
        estadisticas.setTotalVehiculos(vehiculoRepository.count());
        estadisticas.setVehiculosActivos(vehiculoRepository.countByActivoTrue());
        
        // Métricas de conductores
        estadisticas.setTotalConductores(conductorRepository.count());
        estadisticas.setConductoresActivos(conductorRepository.countByActivoTrue());
        
        // Métricas de pedidos
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        estadisticas.setPedidosMesActual(pedidoRepository.countByCreatedDateAfter(inicioMes));
        estadisticas.setPedidosCompletados(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO));
        estadisticas.setPedidosCancelados(pedidoRepository.countByEstado(EstadoPedido.CANCELADO));
        
        return estadisticas;
    }

    @Cacheable(value = "estadisticasConductor", key = "#conductorId")
    public EstadisticasConductor obtenerEstadisticasConductor(Long conductorId) {
        EstadisticasConductor estadisticas = new EstadisticasConductor();
        
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        estadisticas.setPedidosEntregadosMes(
            pedidoRepository.countByConductorIdAndEstadoAndCreatedDateAfter(
                conductorId, EstadoPedido.ENTREGADO, inicioMes
            )
        );
        
        estadisticas.setTotalPedidosEntregados(
            pedidoRepository.countByConductorIdAndEstado(conductorId, EstadoPedido.ENTREGADO)
        );
        
        return estadisticas;
    }

    @Data
    public static class EstadisticasGenerales {
        private Long totalVehiculos;
        private Long vehiculosActivos;
        private Long totalConductores;
        private Long conductoresActivos;
        private Long pedidosMesActual;
        private Long pedidosCompletados;
        private Long pedidosCancelados;
    }

    @Data
    public static class EstadisticasConductor {
        private Long pedidosEntregadosMes;
        private Long totalPedidosEntregados;
    }
} 
