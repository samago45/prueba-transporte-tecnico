package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.gersystem.transporte.application.ConductorApplicationService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConteoVehiculosDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateConductorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conductores")
public class ConductorController {

    private final ConductorApplicationService conductorApplicationService;

    public ConductorController(ConductorApplicationService conductorApplicationService) {
        this.conductorApplicationService = conductorApplicationService;
    }

    @Operation(summary = "Crear un nuevo conductor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conductor creado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConductorDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<ConductorDTO> crearConductor(@RequestBody CreateConductorDTO createConductorDTO) {
        ConductorDTO nuevoConductor = conductorApplicationService.crearConductor(createConductorDTO);
        return new ResponseEntity<>(nuevoConductor, HttpStatus.CREATED);
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
    public ResponseEntity<ConductorDTO> obtenerConductorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(conductorApplicationService.obtenerConductorPorId(id));
    }

    @Operation(summary = "Obtener todos los conductores con paginación y filtros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de conductores",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)) })
    })
    @GetMapping
    public ResponseEntity<Page<ConductorDTO>> obtenerTodosLosConductores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            Pageable pageable) {
        return ResponseEntity.ok(conductorApplicationService.obtenerTodosLosConductores(nombre, activo, pageable));
    }

    @Operation(summary = "Obtener todos los conductores sin vehículos asignados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de conductores sin vehículos")
    })
    @GetMapping("/sin-vehiculos")
    public ResponseEntity<List<ConductorDTO>> obtenerConductoresSinVehiculos() {
        return ResponseEntity.ok(conductorApplicationService.obtenerConductoresSinVehiculos());
    }

    @Operation(summary = "Contar los vehículos asignados a cada conductor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo de vehículos por conductor")
    })
    @GetMapping("/conteo-vehiculos")
    public ResponseEntity<List<ConteoVehiculosDTO>> contarVehiculosPorConductor() {
        return ResponseEntity.ok(conductorApplicationService.contarVehiculosPorConductor());
    }

    @Operation(summary = "Eliminar un conductor (borrado lógico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conductor eliminado exitosamente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Conductor no encontrado",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConductor(@PathVariable Long id) {
        conductorApplicationService.eliminarConductor(id);
        return ResponseEntity.noContent().build();
    }
} 