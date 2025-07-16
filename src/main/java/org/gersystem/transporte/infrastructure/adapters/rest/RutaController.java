package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.application.exception.ValidationException;
import org.gersystem.transporte.domain.model.Ruta;
import org.gersystem.transporte.domain.service.RutaDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateRutaDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ErrorResponseDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.RutaDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.RutaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rutas")
@RequiredArgsConstructor
@Tag(name = "Rutas", description = "API para gestión de rutas de transporte, incluyendo creación, activación/desactivación y consulta")
public class RutaController {

    private final RutaDomainService rutaDomainService;
    private final RutaMapper rutaMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear nueva ruta de transporte",
        description = "Crea una nueva ruta de transporte con los puntos de origen y destino especificados, " +
                     "distancia, tiempo estimado y otros parámetros relevantes. La ruta se crea inicialmente como inactiva."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ruta creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RutaDTO.class),
                examples = @ExampleObject(value = "{\"id\": 1,\"nombre\": \"Ruta Norte-Sur\",\"origen\": \"Terminal Norte\",\"destino\": \"Terminal Sur\",\"distanciaKm\": 25.5,\"tiempoEstimadoMinutos\": 45,\"activa\": false,\"fechaCreacion\": \"2024-03-20T10:30:00\",\"ultimaModificacion\": \"2024-03-20T10:30:00\"}")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de la ruta inválidos",
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
        )
    })
    public ResponseEntity<RutaDTO> crearRuta(@Valid @RequestBody CreateRutaDTO createRutaDTO) {
        Ruta ruta = rutaMapper.toEntity(createRutaDTO);
        Ruta rutaCreada = rutaDomainService.crearRuta(ruta);
        return ResponseEntity.ok(rutaMapper.toDto(rutaCreada));
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Activar ruta existente",
        description = "Activa una ruta previamente creada para que pueda ser utilizada en la asignación de pedidos. " +
                     "Solo rutas inactivas pueden ser activadas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ruta activada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RutaDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "La ruta ya está activa",
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
            description = "Ruta no encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<RutaDTO> activarRuta(
            @Parameter(description = "ID de la ruta a activar", required = true)
            @PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("El ID de la ruta debe ser un número positivo");
        }
        Ruta rutaActualizada = rutaDomainService.activarRuta(id);
        return ResponseEntity.ok(rutaMapper.toDto(rutaActualizada));
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Desactivar ruta existente",
        description = "Desactiva una ruta activa para que no pueda ser utilizada en nuevas asignaciones de pedidos. " +
                     "Solo rutas activas pueden ser desactivadas. Las rutas con pedidos en curso no pueden ser desactivadas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ruta desactivada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RutaDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "La ruta ya está inactiva o tiene pedidos en curso",
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
            description = "Ruta no encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<RutaDTO> desactivarRuta(
            @Parameter(description = "ID de la ruta a desactivar", required = true)
            @PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("El ID de la ruta debe ser un número positivo");
        }
        Ruta rutaActualizada = rutaDomainService.desactivarRuta(id);
        return ResponseEntity.ok(rutaMapper.toDto(rutaActualizada));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
        summary = "Obtener detalles de una ruta",
        description = "Obtiene la información completa de una ruta específica por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ruta encontrada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RutaDTO.class)
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
            responseCode = "404",
            description = "Ruta no encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<RutaDTO> obtenerRuta(
            @Parameter(description = "ID de la ruta a consultar", required = true)
            @PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("El ID de la ruta debe ser un número positivo");
        }
        Ruta ruta = rutaDomainService.obtenerRuta(id);
        return ResponseEntity.ok(rutaMapper.toDto(ruta));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
        summary = "Listar rutas con filtros",
        description = "Obtiene una lista paginada de rutas con opción de filtrar por estado (activa/inactiva). " +
                     "Los resultados se pueden ordenar por diferentes campos usando el parámetro sort."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de rutas obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN o USER",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<Page<RutaDTO>> listarRutas(
            @Parameter(description = "Filtrar por estado de la ruta (true=activa, false=inactiva)")
            @RequestParam(required = false) Boolean activa,
            @Parameter(description = "Parámetros de paginación y ordenamiento (size, page, sort)")
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        Page<Ruta> rutas = rutaDomainService.listarRutas(activa, pageable);
        return ResponseEntity.ok(rutas.map(rutaMapper::toDto));
    }
} 
