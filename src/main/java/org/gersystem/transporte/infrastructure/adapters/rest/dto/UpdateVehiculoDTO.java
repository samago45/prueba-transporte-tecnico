package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateVehiculoDTO {
    
    @NotBlank(message = "La placa es requerida")
    @Pattern(regexp = "[A-Z]{3}\\d{3}", message = "La placa debe tener el formato AAA999")
    private String placa;
    
    @NotNull(message = "La capacidad es requerida")
    @Positive(message = "La capacidad debe ser mayor a 0")
    private BigDecimal capacidad;
    
    private boolean activo;
} 
