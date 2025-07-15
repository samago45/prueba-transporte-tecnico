package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateRutaDTO {
    
    @NotBlank(message = "El nombre de la ruta es requerido")
    private String nombre;
    
    @NotBlank(message = "El punto de origen es requerido")
    private String puntoOrigen;
    
    @NotBlank(message = "El punto de destino es requerido")
    private String puntoDestino;
    
    @NotNull(message = "La distancia es requerida")
    @Positive(message = "La distancia debe ser mayor a 0")
    private Double distanciaKm;
    
    @NotNull(message = "El tiempo estimado es requerido")
    @Positive(message = "El tiempo estimado debe ser mayor a 0")
    private Integer tiempoEstimadoMinutos;
} 