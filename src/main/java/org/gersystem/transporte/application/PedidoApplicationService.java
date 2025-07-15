package org.gersystem.transporte.application;

import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.service.PedidoDomainService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoApplicationService {

    private final PedidoDomainService pedidoDomainService;

    @Transactional
    public Pedido crearPedido(Pedido pedido, Long vehiculoId) {
        return pedidoDomainService.crearPedido(pedido, vehiculoId);
    }

    @Transactional
    public Pedido actualizarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        return pedidoDomainService.actualizarEstadoPedido(pedidoId, nuevoEstado);
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedido(Long id) {
        return pedidoDomainService.obtenerPedido(id);
    }

    @Transactional(readOnly = true)
    public Page<Pedido> buscarPedidos(
            EstadoPedido estado,
            Long conductorId,
            Long vehiculoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        return pedidoDomainService.buscarPedidos(estado, conductorId, vehiculoId, fechaInicio, fechaFin, pageable);
    }
} 