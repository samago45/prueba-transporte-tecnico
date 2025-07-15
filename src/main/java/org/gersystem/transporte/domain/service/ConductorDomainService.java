package org.gersystem.transporte.domain.service;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.springframework.stereotype.Service;

@Service
public class ConductorDomainService {

    private static final int MAX_VEHICULOS_POR_CONDUCTOR = 3;

    private final ConductorRepository conductorRepository;

    public ConductorDomainService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    public void validarLimiteDeVehiculos(Conductor conductor) {
        Integer conteo = conductorRepository.contarVehiculosAsignados(conductor.getId());
        if (conteo >= MAX_VEHICULOS_POR_CONDUCTOR) {
            throw new IllegalStateException("El conductor ya ha alcanzado el límite máximo de " + MAX_VEHICULOS_POR_CONDUCTOR + " vehículos asignados.");
        }
    }
} 