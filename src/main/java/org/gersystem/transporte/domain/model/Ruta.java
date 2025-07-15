package org.gersystem.transporte.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Ruta extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la ruta es requerido")
    private String nombre;

    @NotBlank(message = "El punto de origen es requerido")
    private String puntoOrigen;

    @NotBlank(message = "El punto de destino es requerido")
    private String puntoDestino;

    @NotNull(message = "La distancia es requerida")
    private Double distanciaKm;

    @NotNull(message = "El tiempo estimado es requerido")
    private Integer tiempoEstimadoMinutos;

    private boolean activa = true;
} 