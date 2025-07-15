package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.domain.service.ConductorDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsignacionService {

    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;
    private final ConductorDomainService conductorDomainService;

    public AsignacionService(VehiculoRepository vehiculoRepository,
                             ConductorRepository conductorRepository,
                             ConductorDomainService conductorDomainService) {
        this.vehiculoRepository = vehiculoRepository;
        this.conductorRepository = conductorRepository;
        this.conductorDomainService = conductorDomainService;
    }

    @Transactional
    @CacheEvict(value = "vehiculosLibres", allEntries = true)
    public void asignarVehiculo(Long conductorId, Long vehiculoId) {
        Conductor conductor = conductorRepository.findById(conductorId)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + conductorId));

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + vehiculoId));

        if (!conductor.isActivo()) {
            throw new IllegalStateException("No se puede asignar un vehículo a un conductor inactivo.");
        }

        if (!vehiculo.isActivo()) {
            throw new IllegalStateException("No se puede asignar un vehículo inactivo.");
        }
        
        if (vehiculo.getConductor() != null) {
            throw new IllegalStateException("El vehículo ya está asignado a otro conductor.");
        }

        conductorDomainService.validarLimiteDeVehiculos(conductor);

        vehiculo.setConductor(conductor);
        vehiculoRepository.save(vehiculo);
    }

    @Transactional
    @CacheEvict(value = "vehiculosLibres", allEntries = true)
    public void desasignarVehiculo(Long vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + vehiculoId));

        if (vehiculo.getConductor() == null) {
            // El vehículo ya está libre, no hay nada que hacer.
            return;
        }

        vehiculo.setConductor(null);
        vehiculoRepository.save(vehiculo);
    }
} 