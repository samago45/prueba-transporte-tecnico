package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gersystem.transporte.application.AsignacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/asignaciones")
@Tag(name = "Asignaciones", description = "Operaciones para asignar y desasignar vehículos a conductores")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @Operation(summary = "Asignar un vehículo a un conductor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehículo asignado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Conductor o vehículo no encontrado"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio violada (ej. límite de vehículos)")
    })
    @PostMapping("/conductor/{conductorId}/vehiculo/{vehiculoId}")
    public ResponseEntity<Void> asignarVehiculo(@PathVariable Long conductorId, @PathVariable Long vehiculoId) {
        asignacionService.asignarVehiculo(conductorId, vehiculoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desasignar un vehículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehículo desasignado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @DeleteMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<Void> desasignarVehiculo(@PathVariable Long vehiculoId) {
        asignacionService.desasignarVehiculo(vehiculoId);
        return ResponseEntity.noContent().build();
    }
} 