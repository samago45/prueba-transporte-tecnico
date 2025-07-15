package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EstadisticasDTO {
    private long totalConductores;
    private long conductoresActivos;
    private long totalVehiculos;
    private long vehiculosActivos;
    private long totalPedidos;
    private long pedidosEnProceso;
    private long pedidosEntregados;
    private BigDecimal pesoTotalTransportado;
    private double promedioVehiculosPorConductor;
    private double porcentajeConductoresActivos;
    private double porcentajeVehiculosActivos;
    private double porcentajePedidosEntregados;
} 