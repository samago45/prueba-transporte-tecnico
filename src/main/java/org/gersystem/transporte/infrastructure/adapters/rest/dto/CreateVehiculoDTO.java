package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateVehiculoDTO {
    private String placa;
    private BigDecimal capacidad;
} 