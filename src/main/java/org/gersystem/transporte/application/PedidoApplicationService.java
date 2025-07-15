package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.domain.service.PedidoDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ResourceNotFoundException;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.PedidoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoApplicationService {

    private final PedidoRepository pedidoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;
    private final PedidoDomainService pedidoDomainService;
    private final PedidoMapper pedidoMapper;

    public PedidoApplicationService(PedidoRepository pedidoRepository,
                                    VehiculoRepository vehiculoRepository,
                                    ConductorRepository conductorRepository,
                                    PedidoDomainService pedidoDomainService,
                                    PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.conductorRepository = conductorRepository;
        this.pedidoDomainService = pedidoDomainService;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional
    public PedidoDTO registrarPedido(CreatePedidoDTO createPedidoDTO) {
        Vehiculo vehiculo = vehiculoRepository.findById(createPedidoDTO.getVehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + createPedidoDTO.getVehiculoId()));
        
        Conductor conductor = conductorRepository.findById(createPedidoDTO.getConductorId())
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + createPedidoDTO.getConductorId()));

        Pedido pedido = pedidoMapper.toEntity(createPedidoDTO);
        
        pedidoDomainService.validarConductorActivo(conductor);
        pedidoDomainService.validarCapacidadVehiculo(vehiculo, pedido);

        pedido.setVehiculo(vehiculo);
        pedido.setConductor(conductor);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        
        Pedido nuevoPedido = pedidoRepository.save(pedido);
        return pedidoMapper.toDto(nuevoPedido);
    }

    @Transactional
    public PedidoDTO actualizarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + pedidoId));
        
        // Aquí se podría añadir lógica de negocio sobre la transición de estados
        // (ej. no se puede pasar de COMPLETADO a EN_PROGRESO)
        
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDto(pedidoActualizado);
    }
} 