package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConductorDTO {
    private Long id;
    private String nombre;
    private String licencia;
    private boolean activo;
    private List<VehiculoDTO> vehiculos;

    @Data
    public static class VehiculoDTO {
        private Long id;
        private String placa;
        private boolean activo;
    }
} 
