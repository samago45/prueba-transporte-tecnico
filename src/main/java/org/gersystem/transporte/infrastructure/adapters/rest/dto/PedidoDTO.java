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
} 