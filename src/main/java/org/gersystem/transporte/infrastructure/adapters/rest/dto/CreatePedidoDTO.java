package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePedidoDTO {
    
    @NotBlank(message = "La descripción es requerida")
    private String descripcion;
    
    @NotNull(message = "El peso es requerido")
    @Positive(message = "El peso debe ser mayor a 0")
    private BigDecimal peso;
} 