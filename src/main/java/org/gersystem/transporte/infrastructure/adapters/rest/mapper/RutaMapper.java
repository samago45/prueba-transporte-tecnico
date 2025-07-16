package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Ruta;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateRutaDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.RutaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RutaMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activa", constant = "true")
    Ruta toEntity(CreateRutaDTO createRutaDTO);
    
    RutaDTO toDto(Ruta ruta);
} 
