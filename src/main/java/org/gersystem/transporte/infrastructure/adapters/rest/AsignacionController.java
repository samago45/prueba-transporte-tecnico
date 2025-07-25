package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.persistence.EntityNotFoundException;
import org.gersystem.transporte.application.AsignacionService;
import org.gersystem.transporte.application.exception.BusinessException;
import org.gersystem.transporte.application.exception.ValidationException;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ErrorResponseDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/asignaciones")
@Tag(name = "Asignaciones", description = "API para gestionar la asignación de vehículos a conductores. Permite asignar un vehículo a un conductor, desasignar un vehículo de su conductor actual y validación automática de reglas de negocio (límites, disponibilidad).")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @PostMapping("/conductor/{conductorId}/vehiculo/{vehiculoId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Asignar un vehículo a un conductor",
        description = "Asigna un vehículo específico a un conductor. Reglas de negocio: El conductor debe estar activo, el vehículo debe estar activo y disponible, el conductor no debe exceder su límite de vehículos asignados, el vehículo no debe estar ya asignado a otro conductor. La asignación es exclusiva: un vehículo solo puede estar asignado a un conductor a la vez."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Vehículo asignado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Conductor o vehículo no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación: conductor inactivo, límite excedido o vehículo no disponible",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos para realizar asignaciones",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<Void> asignarVehiculo(
            @Parameter(description = "ID del conductor", required = true, example = "1")
            @PathVariable Long conductorId,
            @Parameter(description = "ID del vehículo a asignar", required = true, example = "1")
            @PathVariable Long vehiculoId) {
        try {
            asignacionService.asignarVehiculoAConductor(conductorId, vehiculoId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (IllegalStateException e) {
            throw new BusinessException("Error en la asignación: " + e.getMessage());
        }
    }

    @DeleteMapping("/vehiculo/{vehiculoId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Desasignar un vehículo de su conductor actual",
        description = "Elimina la asignación actual de un vehículo, dejándolo disponible para ser asignado a otro conductor. Consideraciones: El vehículo debe estar actualmente asignado a un conductor, se registrará la fecha de desasignación, se actualizará el historial de asignaciones."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Vehículo desasignado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Vehículo no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "El vehículo no está asignado a ningún conductor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos para realizar desasignaciones",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<Void> desasignarVehiculo(
            @Parameter(description = "ID del vehículo a desasignar", required = true, example = "1")
            @PathVariable Long vehiculoId) {
        try {
            asignacionService.desasignarVehiculo(vehiculoId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("No se puede realizar la desasignación: " + e.getMessage());
        }
    }
} 
