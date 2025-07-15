package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConteoVehiculosDTO {
    private Long conductorId;
    private String nombreConductor;
    private Integer cantidadVehiculos;
} 