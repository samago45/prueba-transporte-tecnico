package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.infrastructure.adapters.repository.ConductorSpecification;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConteoVehiculosDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PageDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ResourceNotFoundException;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.ConductorMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConductorApplicationService {

    private final ConductorRepository conductorRepository;
    private final ConductorMapper conductorMapper;
    private final ConductorSpecification conductorSpecification;

    public ConductorApplicationService(ConductorRepository conductorRepository, ConductorMapper conductorMapper, ConductorSpecification conductorSpecification) {
        this.conductorRepository = conductorRepository;
        this.conductorMapper = conductorMapper;
        this.conductorSpecification = conductorSpecification;
    }

    public ConductorDTO crearConductor(CreateConductorDTO createConductorDTO) {
        Conductor conductor = conductorMapper.toEntity(createConductorDTO);
        // Aquí se podrían añadir más validaciones de negocio del dominio
        Conductor nuevoConductor = conductorRepository.save(conductor);
        return conductorMapper.toDto(nuevoConductor);
    }

    @Transactional(readOnly = true)
    public ConductorDTO obtenerConductorPorId(Long id) {
        return conductorRepository.findById(id)
                .map(conductorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public PageDTO<ConductorDTO> obtenerTodosLosConductores(String nombre, Boolean activo, Pageable pageable) {
        Specification<Conductor> spec = Specification.where(conductorSpecification.nombreContains(nombre))
                                                     .and(conductorSpecification.esActivo(activo));

        Page<ConductorDTO> dtoPage = conductorRepository.findAll(spec, pageable)
                                  .map(conductorMapper::toDto);
        return new PageDTO<>(dtoPage);
    }

    @Transactional(readOnly = true)
    public List<ConductorDTO> obtenerConductoresSinVehiculos() {
        return conductorRepository.findConductoresSinVehiculos().stream()
                .map(conductorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConteoVehiculosDTO> contarVehiculosPorConductor() {
        return conductorRepository.countVehiculosByConductor();
    }

    @Transactional
    public void eliminarConductor(Long id) {
        Conductor conductor = conductorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + id));
        conductor.setActivo(false);
        conductorRepository.save(conductor);
    }
} 