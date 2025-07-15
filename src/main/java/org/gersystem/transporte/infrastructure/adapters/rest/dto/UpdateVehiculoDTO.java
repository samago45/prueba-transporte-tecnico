package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateVehiculoDTO {
    @NotBlank(message = "La placa es requerida")
    private String placa;
    
    @NotNull(message = "La capacidad es requerida")
    @Positive(message = "La capacidad debe ser mayor a 0")
    private BigDecimal capacidad;
    
    private boolean activo;
} 