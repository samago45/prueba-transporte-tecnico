package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConteoVehiculosDTO {
    private Long conductorId;
    private String nombreConductor;
    private Long cantidadVehiculos;
} 