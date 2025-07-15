package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.application.VehiculoApplicationService;
import org.gersystem.transporte.application.exception.ValidationException;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "API para gestión completa de vehículos, incluyendo registro, actualización, consulta y eliminación")
public class VehiculoController {

    private final VehiculoApplicationService vehiculoApplicationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear un nuevo vehículo",
        description = "Registra un nuevo vehículo en el sistema con la placa (formato AAA999), capacidad y otras características. " +
                     "El vehículo se crea inicialmente como activo pero sin conductor asignado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Vehículo creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VehiculoDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "id": 1,
                        "placa": "ABC123",
                        "capacidadCarga": 5000.0,
                        "volumenCarga": 20.5,
                        "activo": true,
                        "conductorId": null,
                        "fechaCreacion": "2024-03-20T10:30:00",
                        "ultimoMantenimiento": null,
                        "kilometraje": 0
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - La placa debe tener formato AAA999 y la capacidad debe ser positiva",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto - La placa ya está registrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<VehiculoDTO> crearVehiculo(@Valid @RequestBody CreateVehiculoDTO createVehiculoDTO) {
        VehiculoDTO nuevoVehiculo = vehiculoApplicationService.crearVehiculo(createVehiculoDTO);
        return new ResponseEntity<>(nuevoVehiculo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Actualizar un vehículo existente",
        description = "Actualiza la información de un vehículo existente. Permite modificar capacidad, estado y otros atributos, " +
                     "pero no la placa ni el conductor asignado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vehículo actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VehiculoDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Vehículo no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<VehiculoDTO> actualizarVehiculo(
            @Parameter(description = "ID del vehículo a actualizar", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehiculoDTO updateVehiculoDTO) {
        if (id <= 0) {
            throw new ValidationException("El ID del vehículo debe ser un número positivo");
        }
        return ResponseEntity.ok(vehiculoApplicationService.actualizarVehiculo(id, updateVehiculoDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR', 'CLIENTE')")
    @Operation(
        summary = "Obtener un vehículo por su ID",
        description = "Obtiene la información completa de un vehículo específico, incluyendo su estado actual, " +
                     "conductor asignado si tiene, y datos de mantenimiento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vehículo encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VehiculoDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN, CONDUCTOR o CLIENTE",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Vehículo no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<VehiculoDTO> obtenerVehiculoPorId(
            @Parameter(description = "ID del vehículo a consultar", required = true)
            @PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("El ID del vehículo debe ser un número positivo");
        }
        VehiculoDTO vehiculoDTO = vehiculoApplicationService.obtenerVehiculoPorId(id);
        return ResponseEntity.ok(vehiculoDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR', 'CLIENTE')")
    @Operation(
        summary = "Listar vehículos con filtros",
        description = "Obtiene una lista paginada de vehículos con opción de filtrar por placa y estado. " +
                     "Los resultados se pueden ordenar por diferentes campos usando el parámetro sort."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista paginada de vehículos obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN, CONDUCTOR o CLIENTE",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<PageDTO<VehiculoDTO>> obtenerTodosLosVehiculos(
            @Parameter(description = "Filtrar por placa (búsqueda parcial)")
            @RequestParam(required = false) String placa,
            @Parameter(description = "Filtrar por estado del vehículo (true=activo, false=inactivo)")
            @RequestParam(required = false) Boolean activo,
            @Parameter(description = "Parámetros de paginación y ordenamiento (size, page, sort)")
            @ParameterObject @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        PageDTO<VehiculoDTO> vehiculos = vehiculoApplicationService.obtenerTodosLosVehiculos(placa, activo, pageable);
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/libres")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR')")
    @Operation(
        summary = "Obtener vehículos disponibles",
        description = "Retorna una lista de vehículos que están activos y no tienen conductor asignado, " +
                     "disponibles para asignación."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de vehículos libres obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = List.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN o CONDUCTOR",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<List<VehiculoDTO>> obtenerVehiculosLibres() {
        return ResponseEntity.ok(vehiculoApplicationService.obtenerVehiculosLibres());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar un vehículo",
        description = "Realiza un borrado lógico del vehículo. El vehículo debe estar inactivo y sin pedidos pendientes. " +
                     "No se permite eliminar vehículos con conductor asignado o con pedidos en curso."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Vehículo eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "No se puede eliminar - Vehículo con conductor o pedidos pendientes",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Vehículo no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<Void> eliminarVehiculo(
            @Parameter(description = "ID del vehículo a eliminar", required = true)
            @PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("El ID del vehículo debe ser un número positivo");
        }
        vehiculoApplicationService.eliminarVehiculo(id);
        return ResponseEntity.noContent().build();
    }
} 