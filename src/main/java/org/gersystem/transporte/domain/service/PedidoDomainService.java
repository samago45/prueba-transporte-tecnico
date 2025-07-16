package org.gersystem.transporte.domain.service;

import javax.persistence.EntityNotFoundException;
import org.gersystem.transporte.application.exception.BusinessException;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.infrastructure.adapters.repository.PedidoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
            throw new BusinessException("El vehículo no está activo");
        }
    }

    private void validarCapacidadVehiculo(Vehiculo vehiculo, BigDecimal pesoPedido) {
        BigDecimal capacidadDisponible = vehiculo.getCapacidad();
        List<Pedido> pedidosActivos = pedidoRepository.findByVehiculoAndEstadoIn(
                vehiculo, 
                List.of(EstadoPedido.PENDIENTE, EstadoPedido.EN_PROCESO)
        );

        BigDecimal pesoTotal = pedidosActivos.stream()
                .map(Pedido::getPeso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (pesoTotal.add(pesoPedido).compareTo(capacidadDisponible) > 0) {
            throw new BusinessException("El vehículo no tiene capacidad suficiente");
        }
    }

    private void validarConductorActivo(Conductor conductor) {
        if (conductor == null || !conductor.isActivo()) {
            throw new BusinessException("El conductor no está activo o no existe");
        }
    }

    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        if (estadoActual == EstadoPedido.ENTREGADO || estadoActual == EstadoPedido.CANCELADO) {
            throw new BusinessException("No se puede cambiar el estado de un pedido completado o cancelado");
        }

        if (estadoActual == EstadoPedido.PENDIENTE && nuevoEstado == EstadoPedido.ENTREGADO) {
            throw new BusinessException("Un pedido pendiente no puede pasar directamente a completado");
        }
    }

    @Transactional(readOnly = true)
    public Page<Pedido> buscarPedidos(
            EstadoPedido estado,
            Long conductorId,
            Long vehiculoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        
        Specification<Pedido> spec = Specification.where(null);

        if (estado != null) {
            spec = spec.and(PedidoSpecification.conEstado(estado));
        }

        if (conductorId != null) {
            spec = spec.and(PedidoSpecification.conConductorId(conductorId));
        }

        if (vehiculoId != null) {
            spec = spec.and(PedidoSpecification.conVehiculoId(vehiculoId));
        }

        if (fechaInicio != null || fechaFin != null) {
            spec = spec.and(PedidoSpecification.creadoEntreFechas(fechaInicio, fechaFin));
        }

        return pedidoRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));
    }
} 
