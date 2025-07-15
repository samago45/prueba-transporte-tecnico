package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.application.exception.ValidationException;
import org.gersystem.transporte.domain.service.EstadisticasDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/estadisticas")
@RequiredArgsConstructor
@Tag(name = "Estadísticas", description = "API para obtener estadísticas y métricas del sistema de transporte")
public class EstadisticasController {

    private final EstadisticasDomainService estadisticasDomainService;

    @GetMapping("/generales")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Obtener estadísticas generales del sistema",
        description = "Retorna un resumen completo de las métricas del sistema incluyendo totales y porcentajes de conductores, vehículos y pedidos. " +
                     "Requiere rol de ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estadísticas obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EstadisticasDomainService.EstadisticasGenerales.class),
                examples = @ExampleObject(value = """
                    {
                        "totalConductores": 50,
                        "conductoresActivos": 45,
                        "totalVehiculos": 40,
                        "vehiculosActivos": 35,
                        "totalPedidos": 1000,
                        "pedidosEnProceso": 150,
                        "pedidosEntregados": 850,
                        "pesoTotalTransportado": 25000.5,
                        "promedioVehiculosPorConductor": 0.8,
                        "porcentajeConductoresActivos": 90.0,
                        "porcentajeVehiculosActivos": 87.5,
                        "porcentajePedidosEntregados": 85.0
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<EstadisticasDomainService.EstadisticasGenerales> obtenerEstadisticasGenerales() {
        return ResponseEntity.ok(estadisticasDomainService.obtenerEstadisticasGenerales());
    }

    @GetMapping("/conductores/{conductorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
        summary = "Obtener estadísticas de un conductor específico",
        description = "Retorna métricas detalladas sobre el desempeño y actividad de un conductor específico. " +
                     "Accesible para usuarios con rol ADMIN o USER."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estadísticas del conductor obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EstadisticasDomainService.EstadisticasConductor.class),
                examples = @ExampleObject(value = """
                    {
                        "totalPedidosAsignados": 120,
                        "pedidosEntregados": 100,
                        "pedidosEnProceso": 20,
                        "kilometrosRecorridos": 5000.5,
                        "pesoTotalTransportado": 15000.75,
                        "promedioTiempoEntrega": 45.5,
                        "calificacionPromedio": 4.8,
                        "vehiculosAsignados": 2
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Conductor no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN o USER",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "ID de conductor inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<EstadisticasDomainService.EstadisticasConductor> obtenerEstadisticasConductor(
            @PathVariable Long conductorId) {
        if (conductorId <= 0) {
            throw new ValidationException("El ID del conductor debe ser un número positivo");
        }
        return ResponseEntity.ok(estadisticasDomainService.obtenerEstadisticasConductor(conductorId));
    }
} 