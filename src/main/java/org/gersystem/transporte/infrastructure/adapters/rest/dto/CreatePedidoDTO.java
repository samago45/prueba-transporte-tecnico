package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePedidoDTO {
    private String descripcion;
    private BigDecimal peso;
    private Long vehiculoId;
    private Long conductorId;

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public Long getConductorId() {
        return conductorId;
    }
} 