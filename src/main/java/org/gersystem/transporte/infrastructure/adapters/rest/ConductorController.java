package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.gersystem.transporte.application.ConductorApplicationService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conductores")
@Tag(name = "Conductores", description = "Operaciones sobre conductores")
public class ConductorController {

    private final ConductorApplicationService conductorApplicationService;

    public ConductorController(ConductorApplicationService conductorApplicationService) {
        this.conductorApplicationService = conductorApplicationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear un nuevo conductor",
        description = "Crea un nuevo conductor con la información proporcionada. " +
                "La licencia debe seguir el formato de una letra mayúscula seguida de 5 dígitos (ejemplo: A12345)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Conductor creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ConductorDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - La licencia debe tener el formato correcto (A12345)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Se requiere autenticación",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Prohibido - Se requiere rol de ADMIN",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<ConductorDTO> crearConductor(@Valid @RequestBody CreateConductorDTO createConductorDTO) {
        ConductorDTO nuevoConductor = conductorApplicationService.crearConductor(createConductorDTO);
        return new ResponseEntity<>(nuevoConductor, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un conductor existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conductor actualizado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConductorDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Conductor no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConductorDTO> actualizarConductor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateConductorDTO updateConductorDTO) {
        return ResponseEntity.ok(conductorApplicationService.actualizarConductor(id, updateConductorDTO));
    }

    @Operation(summary = "Obtener un conductor por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conductor encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConductorDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Conductor no encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR')")
    public ResponseEntity<ConductorDTO> obtenerConductorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(conductorApplicationService.obtenerConductorPorId(id));
    }

    @Operation(summary = "Obtener todos los conductores con paginación y filtros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de conductores",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageDTO.class)) })
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageDTO<ConductorDTO>> obtenerTodosLosConductores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(conductorApplicationService.obtenerTodosLosConductores(nombre, activo, pageable));
    }

    @Operation(summary = "Obtener todos los conductores sin vehículos asignados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de conductores sin vehículos")
    })
    @GetMapping("/sin-vehiculos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConductorDTO>> obtenerConductoresSinVehiculos() {
        return ResponseEntity.ok(conductorApplicationService.obtenerConductoresSinVehiculos());
    }

    @Operation(summary = "Contar los vehículos asignados a cada conductor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo de vehículos por conductor")
    })
    @GetMapping("/conteo-vehiculos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConteoVehiculosDTO>> contarVehiculosPorConductor() {
        return ResponseEntity.ok(conductorApplicationService.contarVehiculosPorConductor());
    }

    @Operation(summary = "Eliminar un conductor (borrado lógico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conductor eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Conductor no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarConductor(@PathVariable Long id) {
        conductorApplicationService.eliminarConductor(id);
        return ResponseEntity.noContent().build();
    }
} 