package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.EstadoMantenimiento;
import org.gersystem.transporte.domain.model.Mantenimiento;
import org.gersystem.transporte.domain.service.MantenimientoDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateMantenimientoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.MantenimientoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.MantenimientoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mantenimientos")
@RequiredArgsConstructor
@Tag(name = "Mantenimientos", description = "API para gestión de mantenimientos de vehículos")
public class MantenimientoController {

    private final MantenimientoDomainService mantenimientoDomainService;
    private final MantenimientoMapper mantenimientoMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Programar mantenimiento", description = "Programa un nuevo mantenimiento para un vehículo")
    public ResponseEntity<MantenimientoDTO> programarMantenimiento(
            @Valid @RequestBody CreateMantenimientoDTO createMantenimientoDTO) {
        Mantenimiento mantenimiento = mantenimientoMapper.toEntity(createMantenimientoDTO);
        Mantenimiento mantenimientoCreado = mantenimientoDomainService.programarMantenimiento(mantenimiento);
        return ResponseEntity.ok(mantenimientoMapper.toDto(mantenimientoCreado));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar estado", description = "Actualiza el estado de un mantenimiento")
    public ResponseEntity<MantenimientoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoMantenimiento nuevoEstado) {
        Mantenimiento mantenimientoActualizado = mantenimientoDomainService.actualizarEstadoMantenimiento(id, nuevoEstado);
        return ResponseEntity.ok(mantenimientoMapper.toDto(mantenimientoActualizado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Obtener mantenimiento", description = "Obtiene los detalles de un mantenimiento por ID")
    public ResponseEntity<MantenimientoDTO> obtenerMantenimiento(@PathVariable Long id) {
        Mantenimiento mantenimiento = mantenimientoDomainService.obtenerMantenimiento(id);
        return ResponseEntity.ok(mantenimientoMapper.toDto(mantenimiento));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar mantenimientos", description = "Lista los mantenimientos con filtros opcionales")
    public ResponseEntity<Page<MantenimientoDTO>> listarMantenimientos(
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) EstadoMantenimiento estado,
            Pageable pageable) {
        Page<Mantenimiento> mantenimientos = mantenimientoDomainService.listarMantenimientos(vehiculoId, estado, pageable);
        return ResponseEntity.ok(mantenimientos.map(mantenimientoMapper::toDto));
    }
} 