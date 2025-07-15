package org.gersystem.transporte.application;

import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.EstadisticasDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    @Cacheable("estadisticas_generales")
    public EstadisticasDTO obtenerEstadisticasGenerales() {
        long totalConductores = conductorRepository.count();
        long conductoresActivos = conductorRepository.countByActivoTrue();
        long totalVehiculos = vehiculoRepository.count();
        long vehiculosActivos = vehiculoRepository.countByActivoTrue();
        long totalPedidos = pedidoRepository.count();
        long pedidosEnProceso = pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO);
        long pedidosEntregados = pedidoRepository.countByEstado(EstadoPedido.ENTREGADO);
        
        BigDecimal pesoTotalTransportado = pedidoRepository.calcularPesoTotalTransportado();
        
        double promedioVehiculosPorConductor = conductoresActivos > 0 
            ? (double) vehiculosActivos / conductoresActivos 
            : 0.0;

        double porcentajeConductoresActivos = totalConductores > 0 
            ? (double) conductoresActivos * 100 / totalConductores 
            : 0.0;

        double porcentajeVehiculosActivos = totalVehiculos > 0 
            ? (double) vehiculosActivos * 100 / totalVehiculos 
            : 0.0;

        double porcentajePedidosEntregados = totalPedidos > 0 
            ? (double) pedidosEntregados * 100 / totalPedidos 
            : 0.0;

        return EstadisticasDTO.builder()
                .totalConductores(totalConductores)
                .conductoresActivos(conductoresActivos)
                .totalVehiculos(totalVehiculos)
                .vehiculosActivos(vehiculosActivos)
                .totalPedidos(totalPedidos)
                .pedidosEnProceso(pedidosEnProceso)
                .pedidosEntregados(pedidosEntregados)
                .pesoTotalTransportado(pesoTotalTransportado)
                .promedioVehiculosPorConductor(BigDecimal.valueOf(promedioVehiculosPorConductor)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .porcentajeConductoresActivos(BigDecimal.valueOf(porcentajeConductoresActivos)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .porcentajeVehiculosActivos(BigDecimal.valueOf(porcentajeVehiculosActivos)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .porcentajePedidosEntregados(BigDecimal.valueOf(porcentajePedidosEntregados)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .build();
    }
} 