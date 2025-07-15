package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;
import org.gersystem.transporte.domain.model.EstadoPedido;

import java.math.BigDecimal;

@Data
public class PedidoDTO {
    private Long id;
    private String descripcion;
    private BigDecimal peso;
    private EstadoPedido estado;
    private VehiculoDTO vehiculo;
    private ConductorDTO conductor;

    @Data
    public static class VehiculoDTO {
        private Long id;
        private String placa;
        private BigDecimal capacidad;
    }

    @Data
    public static class ConductorDTO {
        private Long id;
        private String nombre;
        private String licencia;
    }
} 