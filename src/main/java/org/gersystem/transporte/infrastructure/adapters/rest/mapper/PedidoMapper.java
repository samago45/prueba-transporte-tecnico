package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VehiculoMapper.class, ConductorMapper.class})
public interface PedidoMapper {

    PedidoDTO toDto(Pedido pedido);

    @Mapping(source = "vehiculoId", target = "vehiculo.id")
    @Mapping(source = "conductorId", target = "conductor.id")
    Pedido toEntity(CreatePedidoDTO createPedidoDTO);
} 