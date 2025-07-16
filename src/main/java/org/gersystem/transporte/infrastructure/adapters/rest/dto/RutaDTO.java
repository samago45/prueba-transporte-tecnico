package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RutaDTO {
    private Long id;
    private String nombre;
    private String puntoOrigen;
    private String puntoDestino;
    private Double distanciaKm;
    private Integer tiempoEstimadoMinutos;
    private boolean activa;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
} 
