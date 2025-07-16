package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePedidoDTO {
    
    @NotBlank(message = "La descripción es requerida")
    private String descripcion;
    
    @NotNull(message = "El peso es requerido")
    @Positive(message = "El peso debe ser mayor a 0")
    private BigDecimal peso;
    
    @NotNull(message = "El ID del vehículo es requerido")
    @Positive(message = "El ID del vehículo debe ser mayor a 0")
    private Long vehiculoId;
} 
