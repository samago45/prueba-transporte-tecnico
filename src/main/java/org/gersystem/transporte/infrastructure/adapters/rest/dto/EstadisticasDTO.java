package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class EstadisticasDTO {
    private Long totalConductores;
    private Long conductoresActivos;
    private Long totalVehiculos;
    private Long vehiculosActivos;
    private Long totalPedidos;
    private Long pedidosEnProceso;
    private Long pedidosEntregados;
    private BigDecimal pesoTotalTransportado;
    private Double promedioVehiculosPorConductor;
    private Double porcentajeConductoresActivos;
    private Double porcentajeVehiculosActivos;
    private Double porcentajePedidosEntregados;
    private List<ConductorSimpleDTO> conductoresSinVehiculos;
    private List<ConteoVehiculosDTO> vehiculosPorConductor;

    @Data
    public static class ConductorSimpleDTO {
        private Long id;
        private String nombre;
        private String licencia;
    }
} 
