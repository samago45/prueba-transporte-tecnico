package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.VehiculoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehiculoMapper {
    VehiculoDTO toDto(Vehiculo vehiculo);
    Vehiculo toEntity(CreateVehiculoDTO createVehiculoDTO);
} 