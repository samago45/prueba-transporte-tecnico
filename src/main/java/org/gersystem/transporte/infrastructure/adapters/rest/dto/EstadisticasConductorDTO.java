package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadisticasConductorDTO {
    private Long pedidosEntregadosMes;
    private Long totalPedidosEntregados;
    private Double promedioCapacidadUtilizada;
} 
