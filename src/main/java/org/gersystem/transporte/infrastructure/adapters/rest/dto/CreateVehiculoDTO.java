package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateVehiculoDTO {
    
    @NotBlank(message = "La placa es requerida")
    @Pattern(regexp = "^[A-Z]{3}\\d{3}$", message = "La placa debe tener el formato AAA999")
    @Schema(description = "Placa del vehículo", example = "ABC123", required = true)
    private String placa;

    @Positive(message = "La capacidad debe ser mayor a 0")
    @Schema(description = "Capacidad en kilogramos", example = "5000", required = true)
    private Integer capacidad;
} 