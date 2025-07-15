package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.*;
import org.gersystem.transporte.domain.repository.MantenimientoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class MantenimientoDomainService {

    private final MantenimientoRepository mantenimientoRepository;
    private final VehiculoRepository vehiculoRepository;

    public MantenimientoDomainService(MantenimientoRepository mantenimientoRepository,
                                    VehiculoRepository vehiculoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional
    public Mantenimiento programarMantenimiento(Mantenimiento mantenimiento) {
        validarVehiculoActivo(mantenimiento.getVehiculo().getId());
        validarFechaProgramada(mantenimiento.getFechaProgramada());
        validarMantenimientosPendientes(mantenimiento.getVehiculo().getId(), mantenimiento.getFechaProgramada());
        
        return mantenimientoRepository.save(mantenimiento);
    }

    @Transactional
    public Mantenimiento actualizarEstadoMantenimiento(Long mantenimientoId, EstadoMantenimiento nuevoEstado) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(mantenimientoId)
                .orElseThrow(() -> new EntityNotFoundException("Mantenimiento no encontrado"));

        validarTransicionEstado(mantenimiento.getEstado(), nuevoEstado);
        
        if (nuevoEstado == EstadoMantenimiento.COMPLETADO) {
            mantenimiento.setFechaRealizada(LocalDateTime.now());
        }
        
        mantenimiento.setEstado(nuevoEstado);
        return mantenimientoRepository.save(mantenimiento);
    }

    @Transactional(readOnly = true)
    public Mantenimiento obtenerMantenimiento(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mantenimiento no encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<Mantenimiento> listarMantenimientos(Long vehiculoId, EstadoMantenimiento estado, Pageable pageable) {
        if (vehiculoId != null && estado != null) {
            return mantenimientoRepository.findByVehiculoIdAndEstado(vehiculoId, estado, pageable);
        } else if (vehiculoId != null) {
            return mantenimientoRepository.findByVehiculoId(vehiculoId, pageable);
        } else if (estado != null) {
            return mantenimientoRepository.findByEstado(estado, pageable);
        }
        return mantenimientoRepository.findAll(pageable);
    }

    private void validarVehiculoActivo(Long vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));

        if (!vehiculo.isActivo()) {
            throw new IllegalStateException("No se puede programar mantenimiento para un vehículo inactivo");
        }
    }

    private void validarFechaProgramada(LocalDateTime fechaProgramada) {
        if (fechaProgramada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha programada no puede ser anterior a la fecha actual");
        }
    }

    private void validarMantenimientosPendientes(Long vehiculoId, LocalDateTime fechaProgramada) {
        List<Mantenimiento> mantenimientosPendientes = mantenimientoRepository
                .findByVehiculoIdAndEstadoAndFechaProgramadaBetween(
                    vehiculoId,
                    EstadoMantenimiento.PENDIENTE,
                    fechaProgramada.minusHours(24),
                    fechaProgramada.plusHours(24)
                );

        if (!mantenimientosPendientes.isEmpty()) {
            throw new IllegalStateException("Ya existe un mantenimiento programado para este vehículo en un rango de 24 horas");
        }
    }

    private void validarTransicionEstado(EstadoMantenimiento estadoActual, EstadoMantenimiento nuevoEstado) {
        if (estadoActual == EstadoMantenimiento.COMPLETADO || estadoActual == EstadoMantenimiento.CANCELADO) {
            throw new IllegalStateException("No se puede modificar un mantenimiento completado o cancelado");
        }

        if (estadoActual == EstadoMantenimiento.PENDIENTE && nuevoEstado == EstadoMantenimiento.COMPLETADO) {
            throw new IllegalStateException("Un mantenimiento debe pasar por el estado EN_PROCESO antes de completarse");
        }
    }
} 