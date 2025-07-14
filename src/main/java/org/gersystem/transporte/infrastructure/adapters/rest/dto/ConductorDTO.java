package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConductorDTO {
    private Long id;
    private String nombre;
    private boolean activo;
    private List<VehiculoDTO> vehiculos;
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
} 