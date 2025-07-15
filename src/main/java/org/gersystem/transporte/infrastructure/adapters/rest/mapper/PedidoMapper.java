package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {VehiculoMapper.class, ConductorMapper.class})
public interface PedidoMapper {

    @Mapping(target = "vehiculo", source = "vehiculo")
    @Mapping(target = "conductor", source = "conductor")
    @Named("excludeAuditing")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "peso", source = "peso")
    @Mapping(target = "estado", source = "estado")
    PedidoDTO toDto(Pedido pedido);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "vehiculo", ignore = true)
    @Mapping(target = "conductor", ignore = true)
    Pedido toEntity(CreatePedidoDTO dto);
} 