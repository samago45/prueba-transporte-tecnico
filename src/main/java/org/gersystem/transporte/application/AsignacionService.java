package org.gersystem.transporte.application;

import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.domain.service.ConductorDomainService;
import org.gersystem.transporte.domain.service.VehiculoDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConductorDomainService conductorDomainService;
    private final VehiculoDomainService vehiculoDomainService;

    @Transactional
    public boolean asignarVehiculoAConductor(Long conductorId, Long vehiculoId) {
        Conductor conductor = conductorRepository.findById(conductorId)
                .orElseThrow(() -> new IllegalArgumentException("Conductor no encontrado"));
        
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        if (!conductor.isActivo()) {
            throw new IllegalStateException("El conductor no está activo");
        }

        if (!vehiculo.isActivo()) {
            throw new IllegalStateException("El vehículo no está activo");
        }

        conductorDomainService.asignarVehiculo(conductorId, vehiculoId);
        vehiculoDomainService.asignarConductor(vehiculoId, conductorId);

        return true;
    }

    @Transactional
    public boolean desasignarVehiculo(Long vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        if (vehiculo.getConductor() != null) {
            Conductor conductor = vehiculo.getConductor();
            conductor.getVehiculos().remove(vehiculo);
            vehiculo.setConductor(null);
            
            conductorRepository.save(conductor);
            vehiculoRepository.save(vehiculo);
            return true;
        }
        return false;
    }
} 