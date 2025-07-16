package org.gersystem.transporte.infrastructure.adapters.repository;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class VehiculoSpecification {

    public Specification<Vehiculo> placaContains(String placa) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(placa)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("placa")), "%" + placa.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }

    public Specification<Vehiculo> esActivo(Boolean activo) {
        return (root, query, criteriaBuilder) -> {
            if (activo != null) {
                return criteriaBuilder.equal(root.get("activo"), activo);
            }
            return criteriaBuilder.conjunction();
        };
    }
} 
