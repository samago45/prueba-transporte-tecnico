package org.gersystem.transporte.application;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.service.PedidoDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.PedidoMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoApplicationService {

    private final PedidoRepository pedidoRepository;
    private final PedidoDomainService pedidoDomainService;
    private final PedidoMapper pedidoMapper;

    public PedidoApplicationService(PedidoRepository pedidoRepository,
                                  PedidoDomainService pedidoDomainService,
                                  PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoDomainService = pedidoDomainService;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional
    @CacheEvict(value = {"pedidos", "pedidosPorEstado", "pedidosPorConductor"}, allEntries = true)
    public PedidoDTO crearPedido(CreatePedidoDTO createPedidoDTO, Long vehiculoId) {
        Pedido pedido = pedidoMapper.toEntity(createPedidoDTO);
        Pedido pedidoCreado = pedidoDomainService.crearPedido(pedido, vehiculoId);
        return pedidoMapper.toDto(pedidoCreado);
    }

    @Transactional
    @CacheEvict(value = {"pedidos", "pedidosPorEstado", "pedidosPorConductor"}, allEntries = true)
    public PedidoDTO actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedidoActualizado = pedidoDomainService.actualizarEstadoPedido(id, nuevoEstado);
        return pedidoMapper.toDto(pedidoActualizado);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pedidos", key = "#id")
    public PedidoDTO obtenerPedido(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pedidosPorEstado", key = "{#estado?.name(), #conductorId, #pageable.pageNumber, #pageable.pageSize}")
    public Page<PedidoDTO> listarPedidos(EstadoPedido estado, Long conductorId, Pageable pageable) {
        Page<Pedido> pedidos;
        
        if (estado != null) {
            pedidos = pedidoRepository.findByEstado(estado, pageable);
        } else if (conductorId != null) {
            pedidos = pedidoRepository.findByConductorId(conductorId, pageable);
        } else {
            pedidos = pedidoRepository.findAll(pageable);
        }
        
        return pedidos.map(pedidoMapper::toDto);
    }
} 