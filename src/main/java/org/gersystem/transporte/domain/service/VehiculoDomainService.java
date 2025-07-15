package org.gersystem.transporte.domain.service;

import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VehiculoDomainService {

    private static final Pattern PATRON_PLACA = Pattern.compile("^[A-Z]{3}\\d{3}$");
    
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
} 