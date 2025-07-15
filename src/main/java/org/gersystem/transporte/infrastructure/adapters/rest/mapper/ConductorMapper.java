package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateConductorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConductorMapper {
    ConductorDTO toDto(Conductor conductor);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehiculos", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Conductor toEntity(CreateConductorDTO createConductorDTO);
} 