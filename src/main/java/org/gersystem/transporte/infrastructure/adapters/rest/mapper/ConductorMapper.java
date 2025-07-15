package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.UpdateConductorDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {VehiculoMapper.class})
public interface ConductorMapper {

    @Mapping(target = "vehiculos", source = "vehiculos")
    ConductorDTO toDto(Conductor conductor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehiculos", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Conductor toEntity(CreateConductorDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehiculos", ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntityFromDto(UpdateConductorDTO dto, @MappingTarget Conductor conductor);
} 