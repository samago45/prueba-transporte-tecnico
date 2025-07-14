package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VehiculoDTO {
    private Long id;
    private String placa;
    private BigDecimal capacidad;
    private boolean activo;
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
} 