package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.application.exception.BusinessException;
import org.gersystem.transporte.domain.model.EstadoMantenimiento;
import org.gersystem.transporte.domain.model.Mantenimiento;
import org.gersystem.transporte.domain.service.MantenimientoDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateMantenimientoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.MantenimientoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.MantenimientoMapper;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.mapping.PropertyReferenceException;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ValidationException;

@RestController
@RequestMapping("/api/v1/mantenimientos")
@RequiredArgsConstructor
@Tag(name = "Mantenimientos", description = "API para gestión de mantenimientos de vehículos. Permite programar, actualizar y consultar mantenimientos.")
public class MantenimientoController {

    private final MantenimientoDomainService mantenimientoDomainService;
    private final MantenimientoMapper mantenimientoMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Programar mantenimiento",
        description = """
            Programa un nuevo mantenimiento para un vehículo. Solo administradores pueden programar mantenimientos.
            El tipo de mantenimiento puede ser PREVENTIVO o CORRECTIVO.
            La fecha puede enviarse en formato 'YYYY-MM-DD' o 'YYYY-MM-DDTHH:mm:ss'.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mantenimiento programado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MantenimientoDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos en la solicitud",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos para programar mantenimientos",
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
    public ResponseEntity<MantenimientoDTO> programarMantenimiento(
            @Parameter(
                description = "Datos del mantenimiento a programar",
                required = true,
                schema = @Schema(implementation = CreateMantenimientoDTO.class)
            )
            @Valid @RequestBody CreateMantenimientoDTO createMantenimientoDTO) {
        try {
            Mantenimiento mantenimiento = mantenimientoMapper.toEntity(createMantenimientoDTO);
            Mantenimiento mantenimientoCreado = mantenimientoDomainService.programarMantenimiento(
                mantenimiento, 
                createMantenimientoDTO.getVehiculoId(),
                createMantenimientoDTO.getFechaProgramada()
            );
            return ResponseEntity.ok(mantenimientoMapper.toDto(mantenimientoCreado));
        } catch (BusinessException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Actualizar estado",
        description = """
            Actualiza el estado de un mantenimiento.
            Los estados posibles son:
            - PENDIENTE: El mantenimiento está pendiente de programación
            - PROGRAMADO: El mantenimiento está programado pero no iniciado
            - EN_PROCESO: El mantenimiento está siendo realizado
            - COMPLETADO: El mantenimiento ha sido completado
            - CANCELADO: El mantenimiento ha sido cancelado
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MantenimientoDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Estado inválido o transición no permitida",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mantenimiento no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<MantenimientoDTO> actualizarEstado(
            @Parameter(description = "ID del mantenimiento", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(
                description = "Nuevo estado del mantenimiento",
                required = true,
                schema = @Schema(implementation = EstadoMantenimiento.class)
            )
            @RequestParam EstadoMantenimiento nuevoEstado) {
        try {
            Mantenimiento mantenimientoActualizado = mantenimientoDomainService.actualizarEstadoMantenimiento(id, nuevoEstado);
            return ResponseEntity.ok(mantenimientoMapper.toDto(mantenimientoActualizado));
        } catch (BusinessException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
        summary = "Obtener mantenimiento",
        description = "Obtiene los detalles de un mantenimiento específico por su ID. Accesible para administradores y usuarios."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mantenimiento encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MantenimientoDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mantenimiento no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<MantenimientoDTO> obtenerMantenimiento(
            @Parameter(description = "ID del mantenimiento", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Mantenimiento mantenimiento = mantenimientoDomainService.obtenerMantenimiento(id);
            return ResponseEntity.ok(mantenimientoMapper.toDto(mantenimiento));
        } catch (BusinessException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
        summary = "Listar mantenimientos",
        description = """
            Lista los mantenimientos con filtros opcionales y paginación.
            
            Parámetros de ordenamiento válidos (sort):
            - id: Ordenar por identificador
            - fechaProgramada: Ordenar por fecha programada
            - fechaRealizada: Ordenar por fecha de realización
            - tipo: Ordenar por tipo de mantenimiento
            - estado: Ordenar por estado
            
            Ejemplos de uso:
            - /api/v1/mantenimientos?page=0&size=10
            - /api/v1/mantenimientos?vehiculoId=1&estado=PENDIENTE
            - /api/v1/mantenimientos?sort=fechaProgramada,desc
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de mantenimientos recuperada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros de paginación o filtros inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<PageDTO<MantenimientoDTO>> listarMantenimientos(
            @Parameter(description = "ID del vehículo para filtrar", example = "1")
            @RequestParam(required = false) Long vehiculoId,
            
            @Parameter(
                description = "Estado del mantenimiento",
                schema = @Schema(implementation = EstadoMantenimiento.class)
            )
            @RequestParam(required = false) EstadoMantenimiento estado,
            
            @Parameter(
                description = """
                    Paginación y ordenamiento.
                    Formato: page=0&size=10&sort=propiedad,direccion
                    Propiedades válidas: id, fechaProgramada, fechaRealizada, tipo, estado
                    Direcciones válidas: asc, desc
                    """
            )
            Pageable pageable) {
        try {
            Page<Mantenimiento> mantenimientos = mantenimientoDomainService.listarMantenimientos(vehiculoId, estado, pageable);
            Page<MantenimientoDTO> dtoPage = mantenimientos.map(mantenimientoMapper::toDto);
            return ResponseEntity.ok(new PageDTO<>(dtoPage));
        } catch (PropertyReferenceException e) {
            throw new ValidationException(
                "Error en parámetros de ordenamiento - La propiedad '" + e.getPropertyName() + 
                "' no es válida. Use: id, fechaProgramada, fechaRealizada, tipo, estado"
            );
        } catch (BusinessException e) {
            throw new ValidationException(e.getMessage());
        }
    }
} 