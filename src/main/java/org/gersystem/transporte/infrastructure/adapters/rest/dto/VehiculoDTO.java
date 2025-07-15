package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehiculoDTO {
    private Long id;
    private String placa;
    private BigDecimal capacidad;
    private boolean activo;
    private Long conductorId;
} 