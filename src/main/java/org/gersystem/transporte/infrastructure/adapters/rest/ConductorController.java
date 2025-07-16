package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.gersystem.transporte.application.ConductorApplicationService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.*;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.mapping.PropertyReferenceException;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ValidationException;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/conductores")
@Tag(name = "Conductores", description = "API para la gestión de conductores. Permite crear, actualizar y eliminar conductores, consultar conductores por ID o con filtros, obtener conductores sin vehículos asignados y obtener estadísticas de vehículos por conductor.")
public class ConductorController {

    private final ConductorApplicationService conductorApplicationService;

    public ConductorController(ConductorApplicationService conductorApplicationService) {
        this.conductorApplicationService = conductorApplicationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear un nuevo conductor",
        description = "Crea un nuevo conductor con la información proporcionada. Reglas de validación: La licencia debe seguir el formato de una letra mayúscula seguida de 5 dígitos (ejemplo: A12345), el nombre y apellidos son obligatorios, el email debe ser único en el sistema, la fecha de nacimiento debe ser válida (conductor mayor de edad)."
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
    public ResponseEntity<ConductorDTO> crearConductor(
            @Parameter(
                description = "Datos del nuevo conductor",
                required = true,
                schema = @Schema(implementation = CreateConductorDTO.class)
            )
            @Valid @RequestBody CreateConductorDTO createConductorDTO) {
        try {
            ConductorDTO nuevoConductor = conductorApplicationService.crearConductor(createConductorDTO);
            return new ResponseEntity<>(nuevoConductor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Error en los datos del conductor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Actualizar un conductor existente",
        description = "Actualiza la información de un conductor existente. Solo se pueden actualizar los campos proporcionados en el DTO. La licencia y el email deben seguir siendo únicos en el sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Conductor actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ConductorDTO.class)
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
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<ConductorDTO> actualizarConductor(
            @Parameter(description = "ID del conductor", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(
                description = "Datos a actualizar del conductor",
                required = true,
                schema = @Schema(implementation = UpdateConductorDTO.class)
            )
            @Valid @RequestBody UpdateConductorDTO updateConductorDTO) {
        try {
            return ResponseEntity.ok(conductorApplicationService.actualizarConductor(id, updateConductorDTO));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Error en los datos del conductor: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR')")
    @Operation(
        summary = "Obtener un conductor por su ID",
        description = "Obtiene la información detallada de un conductor específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Conductor encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ConductorDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Conductor no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<ConductorDTO> obtenerConductorPorId(
            @Parameter(description = "ID del conductor", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(conductorApplicationService.obtenerConductorPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Obtener todos los conductores con paginación y filtros",
        description = "Lista los conductores con filtros opcionales y paginación. Parámetros de ordenamiento válidos (sort): id, nombre, licencia, fechaContratacion. Ejemplos de uso: /api/v1/conductores?page=0&size=10, /api/v1/conductores?nombre=Juan&activo=true, /api/v1/conductores?sort=fechaContratacion,desc"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista paginada de conductores",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageDTO.class)
            )
        )
    })
    public ResponseEntity<PageDTO<ConductorDTO>> obtenerTodosLosConductores(
            @Parameter(description = "Nombre o parte del nombre para filtrar", example = "Juan")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar por estado activo/inactivo")
            @RequestParam(required = false) Boolean activo,
            @Parameter(
                description = "Paginación y ordenamiento. Formato: page=0&size=10&sort=propiedad,direccion. Propiedades válidas: id, nombre, licencia, fechaContratacion. Direcciones válidas: asc, desc"
            )
            Pageable pageable) {
        try {
            return ResponseEntity.ok(conductorApplicationService.obtenerTodosLosConductores(nombre, activo, pageable));
        } catch (PropertyReferenceException e) {
            throw new ValidationException(
                "Error en parámetros de ordenamiento - La propiedad '" + e.getPropertyName() + 
                "' no es válida. Use: id, nombre, licencia, fechaContratacion"
            );
        }
    }

    @GetMapping("/sin-vehiculos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Obtener todos los conductores sin vehículos asignados",
        description = "Retorna una lista de conductores que no tienen vehículos asignados actualmente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de conductores sin vehículos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ConductorDTO.class)
            )
        )
    })
    public ResponseEntity<List<ConductorDTO>> obtenerConductoresSinVehiculos() {
        return ResponseEntity.ok(conductorApplicationService.obtenerConductoresSinVehiculos());
    }

    @GetMapping("/conteo-vehiculos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Contar los vehículos asignados a cada conductor",
        description = "Retorna una lista con el conteo de vehículos asignados a cada conductor"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Conteo de vehículos por conductor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ConteoVehiculosDTO.class)
            )
        )
    })
    public ResponseEntity<List<ConteoVehiculosDTO>> contarVehiculosPorConductor() {
        return ResponseEntity.ok(conductorApplicationService.contarVehiculosPorConductor());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar un conductor (borrado lógico)",
        description = "Realiza un borrado lógico del conductor. El conductor quedará marcado como inactivo pero sus datos se mantienen en el sistema. Solo se pueden eliminar conductores que no tengan vehículos asignados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Conductor eliminado exitosamente"
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
            responseCode = "400",
            description = "No se puede eliminar un conductor con vehículos asignados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<Void> eliminarConductor(
            @Parameter(description = "ID del conductor", required = true, example = "1")
            @PathVariable Long id) {
        try {
            conductorApplicationService.eliminarConductor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            throw new ValidationException("No se puede eliminar el conductor: " + e.getMessage());
        }
    }
} 
