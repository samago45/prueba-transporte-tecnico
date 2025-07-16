package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.gersystem.transporte.domain.model.TipoMantenimiento;

@Data
public class CreateMantenimientoDTO {
    
    @NotNull(message = "El ID del vehículo es requerido")
    private Long vehiculoId;
    
    @NotNull(message = "La fecha programada es requerida")
    private String fechaProgramada;
    
    @NotNull(message = "El tipo de mantenimiento es requerido")
    private TipoMantenimiento tipo;
    
    private String descripcion;
} 
