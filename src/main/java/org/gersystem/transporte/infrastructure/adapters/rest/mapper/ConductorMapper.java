package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateConductorDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = VehiculoMapper.class)
public interface ConductorMapper {
    ConductorDTO toDto(Conductor conductor);
    Conductor toEntity(CreateConductorDTO createConductorDTO);
} 