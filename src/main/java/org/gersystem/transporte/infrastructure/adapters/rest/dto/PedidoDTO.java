package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;
import org.gersystem.transporte.domain.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoDTO {
    private Long id;
    private String descripcion;
    private BigDecimal peso;
    private EstadoPedido estado;
    private VehiculoDTO vehiculo;
    private ConductorDTO conductor;
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
} 