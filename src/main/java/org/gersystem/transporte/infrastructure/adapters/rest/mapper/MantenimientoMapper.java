package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Mantenimiento;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateMantenimientoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.MantenimientoDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", 
        uses = {VehiculoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MantenimientoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehiculo", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "fechaRealizada", ignore = true)
    @Mapping(target = "observaciones", ignore = true)
    @Mapping(target = "fechaProgramada", source = "fechaProgramada")
    Mantenimiento toEntity(CreateMantenimientoDTO dto);

    @Named("toDto")
    @Mapping(target = "vehiculoId", source = "vehiculo.id")
    @Mapping(target = "vehiculoPlaca", source = "vehiculo.placa")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    MantenimientoDTO toDto(Mantenimiento mantenimiento);

    @IterableMapping(qualifiedByName = "toDto")
    List<MantenimientoDTO> toDtoList(List<Mantenimiento> mantenimientos);
} 
