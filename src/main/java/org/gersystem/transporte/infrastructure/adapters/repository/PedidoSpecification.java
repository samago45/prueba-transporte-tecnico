package org.gersystem.transporte.infrastructure.adapters.repository;

import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PedidoSpecification {

    public static Specification<Pedido> conEstado(EstadoPedido estado) {
        return (root, query, cb) -> {
            if (estado == null) {
                return null;
            }
            return cb.equal(root.get("estado"), estado);
        };
    }

    public static Specification<Pedido> conConductorId(Long conductorId) {
        return (root, query, cb) -> {
            if (conductorId == null) {
                return null;
            }
            return cb.equal(root.get("conductor").get("id"), conductorId);
        };
    }

    public static Specification<Pedido> conVehiculoId(Long vehiculoId) {
        return (root, query, cb) -> {
            if (vehiculoId == null) {
                return null;
            }
            return cb.equal(root.get("vehiculo").get("id"), vehiculoId);
        };
    }

    public static Specification<Pedido> creadoEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return (root, query, cb) -> {
            if (fechaInicio == null && fechaFin == null) {
                return null;
            }
            if (fechaInicio == null) {
                return cb.lessThanOrEqualTo(root.get("createdDate"), fechaFin);
            }
            if (fechaFin == null) {
                return cb.greaterThanOrEqualTo(root.get("createdDate"), fechaInicio);
            }
            return cb.between(root.get("createdDate"), fechaInicio, fechaFin);
        };
    }
} 
