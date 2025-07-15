package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.service.EstadisticasDomainService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/estadisticas")
@RequiredArgsConstructor
@Tag(name = "Estadísticas", description = "API para obtener estadísticas del sistema")
public class EstadisticasController {

    private final EstadisticasDomainService estadisticasDomainService;

    @GetMapping("/generales")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Estadísticas generales", description = "Obtiene estadísticas generales del sistema")
    public ResponseEntity<EstadisticasDomainService.EstadisticasGenerales> obtenerEstadisticasGenerales() {
        return ResponseEntity.ok(estadisticasDomainService.obtenerEstadisticasGenerales());
    }

    @GetMapping("/conductores/{conductorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Estadísticas por conductor", description = "Obtiene estadísticas específicas de un conductor")
    public ResponseEntity<EstadisticasDomainService.EstadisticasConductor> obtenerEstadisticasConductor(
            @PathVariable Long conductorId) {
        return ResponseEntity.ok(estadisticasDomainService.obtenerEstadisticasConductor(conductorId));
    }
} 