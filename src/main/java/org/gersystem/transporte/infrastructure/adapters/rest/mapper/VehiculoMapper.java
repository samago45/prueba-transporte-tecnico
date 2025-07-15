package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.UpdateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.VehiculoDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ConductorMapper.class})
public interface VehiculoMapper {

    @Mapping(target = "conductor", source = "conductor")
    VehiculoDTO toDto(Vehiculo vehiculo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conductor", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Vehiculo toEntity(CreateVehiculoDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conductor", ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntityFromDto(UpdateVehiculoDTO dto, @MappingTarget Vehiculo vehiculo);
} 