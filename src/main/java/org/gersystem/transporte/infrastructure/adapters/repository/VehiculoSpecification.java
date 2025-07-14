package org.gersystem.transporte.infrastructure.adapters.repository;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class VehiculoSpecification {

    public Specification<Vehiculo> placaContains(String placa) {
        return (root, query, cb) -> {
            if (placa == null || placa.isEmpty()) {
                return cb.isTrue(cb.literal(true)); // Siempre verdadero si no hay filtro
            }
            return cb.like(cb.lower(root.get("placa")), "%" + placa.toLowerCase() + "%");
        };
    }

    public Specification<Vehiculo> esActivo(Boolean activo) {
        return (root, query, cb) -> {
            if (activo == null) {
                return cb.isTrue(cb.literal(true)); // Siempre verdadero si no hay filtro
            }
            return cb.equal(root.get("activo"), activo);
        };
    }
} 