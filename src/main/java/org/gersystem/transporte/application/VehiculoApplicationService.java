package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.domain.service.VehiculoDomainService;
import org.gersystem.transporte.infrastructure.adapters.repository.VehiculoSpecification;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PageDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.VehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.VehiculoMapper;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculoApplicationService {

    private final VehiculoRepository vehiculoRepository;
    private final VehiculoDomainService vehiculoDomainService;
    private final VehiculoMapper vehiculoMapper;
    private final VehiculoSpecification vehiculoSpecification;

    public VehiculoApplicationService(VehiculoRepository vehiculoRepository,
                                      VehiculoDomainService vehiculoDomainService,
                                      VehiculoMapper vehiculoMapper,
                                      VehiculoSpecification vehiculoSpecification) {
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoDomainService = vehiculoDomainService;
        this.vehiculoMapper = vehiculoMapper;
        this.vehiculoSpecification = vehiculoSpecification;
    }

    @Transactional
    public VehiculoDTO crearVehiculo(CreateVehiculoDTO createVehiculoDTO) {
        Vehiculo vehiculo = vehiculoMapper.toEntity(createVehiculoDTO);
        vehiculoDomainService.validarPlaca(vehiculo);
        // Aquí podríamos añadir más validaciones de negocio
        Vehiculo nuevoVehiculo = vehiculoRepository.save(vehiculo);
        return vehiculoMapper.toDto(nuevoVehiculo);
    }

    @Transactional(readOnly = true)
    public VehiculoDTO obtenerVehiculoPorId(Long id) {
        return vehiculoRepository.findById(id)
                .map(vehiculoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + id));
    }

    public PageDTO<VehiculoDTO> obtenerTodosLosVehiculos(String placa, Boolean activo, Pageable pageable) {
        Specification<Vehiculo> spec = Specification.where(vehiculoSpecification.placaContains(placa))
                                                    .and(vehiculoSpecification.esActivo(activo));
        Page<VehiculoDTO> dtoPage = vehiculoRepository.findAll(spec, pageable).map(vehiculoMapper::toDto);
        return new PageDTO<>(dtoPage);
    }
    
    @Cacheable("vehiculosLibres")
    @Transactional(readOnly = true)
    public List<VehiculoDTO> obtenerVehiculosLibres() {
        // Simulación de una operación costosa
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return vehiculoRepository.findVehiculosLibres().stream()
                .map(vehiculoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarVehiculo(Long id) {
        vehiculoRepository.findById(id).ifPresent(vehiculo -> {
            vehiculo.setActivo(false);
            vehiculoRepository.save(vehiculo);
        });
    }
} 