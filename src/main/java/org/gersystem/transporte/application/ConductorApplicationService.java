package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.service.ConductorDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.*;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.ConductorMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConductorApplicationService {
    private final ConductorRepository conductorRepository;
    private final ConductorDomainService conductorDomainService;
    private final ConductorMapper conductorMapper;

    @Transactional(readOnly = true)
    public ConductorDTO obtenerConductorPorId(Long id) {
        return conductorRepository.findById(id)
                .map(conductorMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado"));
    }

    @Transactional(readOnly = true)
    public PageDTO<ConductorDTO> obtenerTodosLosConductores(String nombre, Boolean activo, Pageable pageable) {
        Page<Conductor> conductores;
        if (nombre != null || activo != null) {
            conductores = conductorRepository.findByActivoAndNombreContaining(activo, nombre, pageable);
        } else {
            conductores = conductorRepository.findAll(pageable);
        }
        return new PageDTO<>(conductores.map(conductorMapper::toDto));
    }

    @Transactional(readOnly = true)
    public List<ConductorDTO> obtenerConductoresSinVehiculos() {
        return conductorRepository.findByVehiculosIsEmpty()
                .stream()
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
                .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado"));
        conductor.setActivo(false);
        conductorRepository.save(conductor);
    }

    @Transactional(readOnly = true)
    public Page<ConductorDTO> listarConductores(Boolean activo, Pageable pageable) {
        Page<Conductor> conductores = activo != null ? 
            conductorRepository.findByActivo(activo, pageable) : 
            conductorRepository.findAll(pageable);
        return conductores.map(conductorMapper::toDto);
    }

    @Transactional
    public ConductorDTO activarConductor(Long id) {
        Conductor conductor = conductorDomainService.activarConductor(id);
        return conductorMapper.toDto(conductor);
    }

    @Transactional
    public ConductorDTO desactivarConductor(Long id) {
        Conductor conductor = conductorDomainService.desactivarConductor(id);
        return conductorMapper.toDto(conductor);
    }

    @Transactional
    public ConductorDTO actualizarConductor(Long id, UpdateConductorDTO updateConductorDTO) {
        Conductor conductorActualizado = new Conductor();
        conductorActualizado.setNombre(updateConductorDTO.getNombre());
        conductorActualizado.setLicencia(updateConductorDTO.getLicencia());
        
        Conductor conductor = conductorDomainService.actualizarConductor(id, conductorActualizado);
        return conductorMapper.toDto(conductor);
    }

    @Transactional
    public ConductorDTO crearConductor(CreateConductorDTO createConductorDTO) {
        if (createConductorDTO.getLicencia() != null && !createConductorDTO.getLicencia().matches("[A-Z]\\d{5}")) {
            throw new ValidationException("Formato de licencia inválido. Debe ser una letra mayúscula seguida de 5 dígitos");
        }
        
        Conductor conductor = conductorMapper.toEntity(createConductorDTO);
        Conductor conductorCreado = conductorDomainService.crearConductor(conductor);
        return conductorMapper.toDto(conductorCreado);
    }
} 
