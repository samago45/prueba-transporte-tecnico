package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateConductorDTO {
    private String nombre;
    private String licencia;
} 
