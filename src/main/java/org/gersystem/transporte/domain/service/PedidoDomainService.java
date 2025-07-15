package org.gersystem.transporte.domain.service;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.stereotype.Service;

@Service
public class PedidoDomainService {

    public void validarConductorActivo(Conductor conductor) {
        if (!conductor.isActivo()) {
            throw new IllegalStateException("El conductor no está activo.");
        }
    }

    public void validarCapacidadVehiculo(Vehiculo vehiculo, Pedido pedido) {
        // Lógica para validar si el pedido excede la capacidad del vehículo.
        // Por ahora, asumiremos que siempre es válido.
        // Ejemplo: if (pedido.getPeso() > vehiculo.getCapacidadCarga()) { ... }
    }
} 