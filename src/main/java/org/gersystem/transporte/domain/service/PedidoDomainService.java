package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoDomainService {

    private final PedidoRepository pedidoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;
    private final ConductorDomainService conductorDomainService;

    public PedidoDomainService(PedidoRepository pedidoRepository,
                              VehiculoRepository vehiculoRepository,
                              ConductorRepository conductorRepository,
                              ConductorDomainService conductorDomainService) {
        this.pedidoRepository = pedidoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.conductorRepository = conductorRepository;
        this.conductorDomainService = conductorDomainService;
    }

    @Transactional
    public Pedido crearPedido(Pedido pedido, Long vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));

        validarVehiculoActivo(vehiculo);
        validarCapacidadVehiculo(vehiculo, pedido.getPeso());
        validarConductorActivo(vehiculo.getConductor());

        pedido.setVehiculo(vehiculo);
        pedido.setConductor(vehiculo.getConductor());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));

        validarTransicionEstado(pedido.getEstado(), nuevoEstado);
        pedido.setEstado(nuevoEstado);

        return pedidoRepository.save(pedido);
    }

    private void validarVehiculoActivo(Vehiculo vehiculo) {
        if (!vehiculo.isActivo()) {
            throw new IllegalStateException("El vehículo no está activo");
        }
    }

    private void validarCapacidadVehiculo(Vehiculo vehiculo, BigDecimal pesoPedido) {
        BigDecimal capacidadDisponible = vehiculo.getCapacidad();
        List<Pedido> pedidosActivos = pedidoRepository.findByVehiculoAndEstadoIn(
                vehiculo, 
                List.of(EstadoPedido.PENDIENTE, EstadoPedido.EN_PROGRESO)
        );

        BigDecimal pesoTotal = pedidosActivos.stream()
                .map(Pedido::getPeso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (pesoTotal.add(pesoPedido).compareTo(capacidadDisponible) > 0) {
            throw new IllegalStateException("El vehículo no tiene capacidad suficiente");
        }
    }

    private void validarConductorActivo(Conductor conductor) {
        if (conductor == null || !conductor.isActivo()) {
            throw new IllegalStateException("El conductor no está activo o no existe");
        }
    }

    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        if (estadoActual == EstadoPedido.COMPLETADO || estadoActual == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede cambiar el estado de un pedido completado o cancelado");
        }

        if (estadoActual == EstadoPedido.PENDIENTE && nuevoEstado == EstadoPedido.COMPLETADO) {
            throw new IllegalStateException("Un pedido pendiente no puede pasar directamente a completado");
        }
    }
} 