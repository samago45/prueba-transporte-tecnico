package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VehiculoDomainService {

    private static final Pattern PATRON_PLACA = Pattern.compile("^[A-Z]{3}\\d{3}$");
    
    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;

    @Value("${vehiculo.capacidad.maxima:5000.00}")
    private BigDecimal capacidadMaxima;

    @Value("${vehiculo.capacidad.minima:100.00}")
    private BigDecimal capacidadMinima;

    public void validarPlaca(Vehiculo vehiculo) {
        if (!PATRON_PLACA.matcher(vehiculo.getPlaca()).matches()) {
            throw new IllegalArgumentException("La placa debe tener el formato AAA999");
        }
    }

    public void validarCapacidad(Vehiculo vehiculo) {
        if (vehiculo.getCapacidad().compareTo(capacidadMaxima) > 0) {
            throw new IllegalArgumentException(
                    String.format("La capacidad del vehículo no puede ser mayor a %.2f kg", capacidadMaxima)
            );
        }
        
        if (vehiculo.getCapacidad().compareTo(capacidadMinima) < 0) {
            throw new IllegalArgumentException(
                    String.format("La capacidad del vehículo no puede ser menor a %.2f kg", capacidadMinima)
            );
        }
    }

    public void validarDisponibilidadVehiculo(Vehiculo vehiculo) {
        if (!vehiculo.isActivo()) {
            throw new IllegalStateException("El vehículo no está activo");
        }

        if (vehiculo.getConductor() != null) {
            throw new IllegalStateException("El vehículo ya está asignado a un conductor");
        }
    }

    @Transactional
    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        validarPlaca(vehiculo);
        validarCapacidad(vehiculo);
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public Vehiculo actualizarVehiculo(Long id, Vehiculo vehiculoActualizado) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        
        validarPlaca(vehiculoActualizado);
        validarCapacidad(vehiculoActualizado);

        vehiculo.setPlaca(vehiculoActualizado.getPlaca());
        vehiculo.setCapacidad(vehiculoActualizado.getCapacidad());
        vehiculo.setActivo(vehiculoActualizado.isActivo());

        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public Vehiculo activarVehiculo(Long id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        vehiculo.setActivo(true);
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public Vehiculo desactivarVehiculo(Long id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        vehiculo.setActivo(false);
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public Vehiculo asignarConductor(Long vehiculoId, Long conductorId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        
        Conductor conductor = conductorRepository.findById(conductorId)
                .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado"));

        if (!conductor.isActivo()) {
            throw new IllegalStateException("El conductor no está activo");
        }

        vehiculo.setConductor(conductor);
        return vehiculoRepository.save(vehiculo);
    }
} 