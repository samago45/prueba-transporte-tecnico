package org.gersystem.transporte.infrastructure.adapters.repository;

import org.gersystem.transporte.domain.model.Conductor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ConductorSpecification {

    public Specification<Conductor> nombreContains(String nombre) {
        return (root, query, cb) -> {
            if (nombre == null || nombre.isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
        };
    }

    public Specification<Conductor> esActivo(Boolean activo) {
        return (root, query, cb) -> {
            if (activo == null) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.equal(root.get("activo"), activo);
        };
    }
} 
