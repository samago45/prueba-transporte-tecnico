package org.gersystem.transporte.domain.service;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    public void validarCapacidadVehiculo(Vehiculo vehiculo, Pedido pedido) {
        if (vehiculo.getCapacidad().compareTo(pedido.getPeso()) < 0) {
            throw new IllegalArgumentException("El vehículo no tiene capacidad suficiente para el peso del pedido.");
        }
    }

    public void validarConductorActivo(Conductor conductor) {
        if (!conductor.isActivo()) {
            throw new IllegalStateException("El conductor asignado no está activo.");
        }
    }
} 