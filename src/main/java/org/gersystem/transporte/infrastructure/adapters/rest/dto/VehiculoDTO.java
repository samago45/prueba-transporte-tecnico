package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Información del vehículo")
public class VehiculoDTO {
    
    @Schema(description = "ID del vehículo")
    private Long id;
    
    @Schema(description = "Placa del vehículo", example = "ABC123")
    private String placa;
    
    @Schema(description = "Capacidad en kilogramos", example = "5000")
    private Integer capacidad;
    
    @Schema(description = "Estado del vehículo")
    private boolean activo;
} 