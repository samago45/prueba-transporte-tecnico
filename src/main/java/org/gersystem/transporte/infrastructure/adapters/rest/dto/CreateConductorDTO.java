package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO para crear un nuevo conductor")
public class CreateConductorDTO {
    
    @NotBlank(message = "El nombre es requerido")
    @Schema(description = "Nombre completo del conductor", example = "Juan Pérez")
    private String nombre;

    @NotBlank(message = "La licencia es requerida")
    @Pattern(
        regexp = "^[A-Z]\\d{5}$",
        message = "La licencia debe tener el formato de una letra mayúscula seguida de 5 dígitos (ejemplo: A12345)"
    )
    @Schema(
        description = "Número de licencia del conductor",
        example = "A12345",
        pattern = "^[A-Z]\\d{5}$"
    )
    private String licencia;
} 