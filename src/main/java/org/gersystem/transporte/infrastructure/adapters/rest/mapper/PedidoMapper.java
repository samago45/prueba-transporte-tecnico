package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VehiculoMapper.class, ConductorMapper.class})
public interface PedidoMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "vehiculo", ignore = true)
    @Mapping(target = "conductor", ignore = true)
    Pedido toEntity(CreatePedidoDTO createPedidoDTO);
    
    PedidoDTO toDto(Pedido pedido);
} 