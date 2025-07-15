package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.EstadoMantenimiento;
import org.gersystem.transporte.domain.model.Mantenimiento;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateMantenimientoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.MantenimientoDTO;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {VehiculoMapper.class})
public abstract class MantenimientoMapper {
    
    @Autowired
    protected VehiculoRepository vehiculoRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehiculo", expression = "java(obtenerVehiculo(dto.getVehiculoId()))")
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "fechaRealizada", ignore = true)
    @Mapping(target = "observaciones", ignore = true)
    public abstract Mantenimiento toEntity(CreateMantenimientoDTO dto);

    @Mapping(target = "vehiculoId", source = "vehiculo.id")
    @Mapping(target = "vehiculoPlaca", source = "vehiculo.placa")
    public abstract MantenimientoDTO toDto(Mantenimiento mantenimiento);

    @AfterMapping
    protected void setDefaultValues(@MappingTarget Mantenimiento mantenimiento) {
        if (mantenimiento.getEstado() == null) {
            mantenimiento.setEstado(EstadoMantenimiento.PENDIENTE);
        }
    }

    protected Vehiculo obtenerVehiculo(Long id) {
        if (id == null) {
            return null;
        }
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
    }
} 