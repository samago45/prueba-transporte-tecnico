package org.gersystem.transporte.domain.service;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoDomainService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoDomainService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public void validarPlaca(Vehiculo vehiculo) {
        if (vehiculoRepository.existsByPlaca(vehiculo.getPlaca())) {
            throw new IllegalStateException("La placa " + vehiculo.getPlaca() + " ya está registrada.");
        }
    }
} 