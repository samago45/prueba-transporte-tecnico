package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gersystem.transporte.application.VehiculoApplicationService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PageDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.VehiculoDTO;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@Tag(name = "Vehículos", description = "Operaciones sobre vehículos")
public class VehiculoController {

    private final VehiculoApplicationService vehiculoApplicationService;

    public VehiculoController(VehiculoApplicationService vehiculoApplicationService) {
        this.vehiculoApplicationService = vehiculoApplicationService;
    }

    @Operation(summary = "Crear un nuevo vehículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehículo creado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehiculoDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehiculoDTO> crearVehiculo(@RequestBody CreateVehiculoDTO createVehiculoDTO) {
        VehiculoDTO nuevoVehiculo = vehiculoApplicationService.crearVehiculo(createVehiculoDTO);
        return new ResponseEntity<>(nuevoVehiculo, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un vehículo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehiculoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR', 'CLIENTE')")
    public ResponseEntity<VehiculoDTO> obtenerVehiculoPorId(@PathVariable Long id) {
        VehiculoDTO vehiculoDTO = vehiculoApplicationService.obtenerVehiculoPorId(id);
        if (vehiculoDTO != null) {
            return ResponseEntity.ok(vehiculoDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todos los vehículos con paginación y filtros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de vehículos",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageDTO.class)) })
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR', 'CLIENTE')")
    public ResponseEntity<PageDTO<VehiculoDTO>> obtenerTodosLosVehiculos(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) Boolean activo,
            @ParameterObject Pageable pageable) {
        PageDTO<VehiculoDTO> vehiculos = vehiculoApplicationService.obtenerTodosLosVehiculos(placa, activo, pageable);
        return ResponseEntity.ok(vehiculos);
    }

    @Operation(summary = "Obtener todos los vehículos libres (activos y sin conductor)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vehículos libres")
    })
    @GetMapping("/libres")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR')")
    public ResponseEntity<List<VehiculoDTO>> obtenerVehiculosLibres() {
        return ResponseEntity.ok(vehiculoApplicationService.obtenerVehiculosLibres());
    }

    @Operation(summary = "Eliminar un vehículo (borrado lógico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehículo eliminado exitosamente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {
        vehiculoApplicationService.eliminarVehiculo(id);
        return ResponseEntity.noContent().build();
    }
} 