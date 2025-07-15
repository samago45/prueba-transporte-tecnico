package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.application.exception.BusinessException;
import org.gersystem.transporte.domain.model.*;
import org.gersystem.transporte.domain.repository.MantenimientoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    public Mantenimiento programarMantenimiento(Mantenimiento mantenimiento, Long vehiculoId, String fechaProgramadaStr) {
        // Validar y obtener vehículo
        Vehiculo vehiculo = obtenerYValidarVehiculo(vehiculoId);
        mantenimiento.setVehiculo(vehiculo);
        
        // Convertir y validar fecha
        LocalDateTime fechaProgramada = convertirYValidarFecha(fechaProgramadaStr);
        mantenimiento.setFechaProgramada(fechaProgramada);
        
        validarFechaProgramada(fechaProgramada);
        validarMantenimientosPendientes(vehiculo.getId(), fechaProgramada);
        
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

    private Vehiculo obtenerYValidarVehiculo(Long vehiculoId) {
        if (vehiculoId == null) {
            throw new IllegalArgumentException("El ID del vehículo es obligatorio");
        }
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId));
        
        if (!vehiculo.isActivo()) {
            throw new IllegalStateException("El vehículo no está activo");
        }
        
        return vehiculo;
    }

    private LocalDateTime convertirYValidarFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new BusinessException("La fecha programada es obligatoria");
        }

        try {
            // Intenta primero con formato completo ISO
            if (fecha.contains("T")) {
                return LocalDateTime.parse(fecha);
            }
            
            // Si solo es fecha (YYYY-MM-DD), agrega la hora 00:00
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(fecha, formatter);
            return localDate.atStartOfDay();
            
        } catch (DateTimeParseException e) {
            throw new BusinessException("Formato de fecha inválido. Use YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss");
        }
    }

    private void validarFechaProgramada(LocalDateTime fechaProgramada) {
        if (fechaProgramada.isBefore(LocalDateTime.now())) {
            throw new BusinessException("La fecha programada no puede ser anterior a la fecha actual");
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
            throw new BusinessException("Ya existe un mantenimiento programado para este vehículo en un rango de 24 horas");
        }
    }

    private void validarTransicionEstado(EstadoMantenimiento estadoActual, EstadoMantenimiento nuevoEstado) {
        if (estadoActual == EstadoMantenimiento.COMPLETADO || estadoActual == EstadoMantenimiento.CANCELADO) {
            throw new IllegalStateException("No se puede modificar un mantenimiento cancelado");
        }

        if (estadoActual == EstadoMantenimiento.PENDIENTE && nuevoEstado == EstadoMantenimiento.COMPLETADO) {
            throw new IllegalStateException("Un mantenimiento debe pasar por el estado EN_PROCESO antes de completarse");
        }
    }
} 