package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;
import org.gersystem.transporte.domain.model.EstadoMantenimiento;
import org.gersystem.transporte.domain.model.TipoMantenimiento;

import java.time.LocalDateTime;

@Data
public class MantenimientoDTO {
    private Long id;
    private Long vehiculoId;
    private String vehiculoPlaca;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaRealizada;
    private TipoMantenimiento tipo;
    private String descripcion;
    private EstadoMantenimiento estado;
    private String observaciones;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
} 
