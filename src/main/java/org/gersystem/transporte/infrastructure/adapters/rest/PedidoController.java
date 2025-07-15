package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.application.PedidoApplicationService;
import org.gersystem.transporte.application.exception.BusinessException;
import org.gersystem.transporte.application.exception.ValidationException;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ErrorResponseDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API para gestión del ciclo de vida completo de pedidos en el sistema de transporte")
public class PedidoController {

    private final PedidoApplicationService pedidoApplicationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(
        summary = "Crear un nuevo pedido",
        description = """
            Crea un nuevo pedido en el sistema asignándolo a un vehículo específico.
            
            Reglas de negocio:
            - El vehículo debe estar activo
            - El vehículo debe tener capacidad suficiente para el pedido
            - El conductor asignado al vehículo debe estar activo
            - El pedido inicia en estado PENDIENTE
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pedido creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PedidoDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = """
                Error de validación:
                - Vehículo inactivo
                - Capacidad insuficiente
                - Conductor inactivo
                - Datos del pedido inválidos
                """,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN o CLIENTE",
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
    public ResponseEntity<PedidoDTO> crearPedido(
            @Valid @RequestBody CreatePedidoDTO createPedidoDTO,
            @Parameter(description = "ID del vehículo al que se asignará el pedido", required = true)
            @RequestParam Long vehiculoId) {
        try {
            if (vehiculoId <= 0) {
                throw new ValidationException("El ID del vehículo debe ser un número positivo");
            }
            return ResponseEntity.ok(pedidoApplicationService.crearPedido(createPedidoDTO, vehiculoId));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (IllegalStateException e) {
            throw new BusinessException("Error de validación: " + e.getMessage());
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR')")
    @Operation(
        summary = "Actualizar estado de pedido",
        description = "Actualiza el estado de un pedido existente. Los estados válidos son: " +
                     "PENDIENTE, EN_RUTA, ENTREGADO, CANCELADO. Solo ciertos roles pueden realizar ciertas transiciones de estado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estado actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PedidoDTO.class)
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
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN o CONDUCTOR",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pedido no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<PedidoDTO> actualizarEstado(
            @Parameter(description = "ID del pedido a actualizar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado del pedido", required = true, schema = @Schema(implementation = EstadoPedido.class))
            @RequestParam EstadoPedido nuevoEstado) {
        if (id <= 0) {
            throw new ValidationException("El ID del pedido debe ser un número positivo");
        }
        return ResponseEntity.ok(pedidoApplicationService.actualizarEstado(id, nuevoEstado));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'CONDUCTOR')")
    @Operation(
        summary = "Listar pedidos",
        description = "Obtiene una lista paginada de pedidos con filtros opcionales por estado y conductor. " +
                     "Los resultados se pueden ordenar por diferentes campos usando el parámetro sort (ej: sort=fechaCreacion,desc)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de pedidos obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN, CLIENTE o CONDUCTOR",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<Page<PedidoDTO>> listarPedidos(
            @Parameter(description = "Filtrar por estado del pedido")
            @RequestParam(required = false) EstadoPedido estado,
            @Parameter(description = "Filtrar por ID del conductor asignado")
            @RequestParam(required = false) Long conductorId,
            @Parameter(description = "Parámetros de paginación y ordenamiento (size, page, sort)")
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        if (conductorId != null && conductorId <= 0) {
            throw new ValidationException("El ID del conductor debe ser un número positivo");
        }
        return ResponseEntity.ok(pedidoApplicationService.listarPedidos(estado, conductorId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'CONDUCTOR')")
    @Operation(
        summary = "Obtener pedido por ID",
        description = "Obtiene los detalles completos de un pedido específico por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pedido encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PedidoDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Se requiere rol de ADMIN, CLIENTE o CONDUCTOR",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pedido no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<PedidoDTO> obtenerPedido(
            @Parameter(description = "ID del pedido a consultar", required = true)
            @PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("El ID del pedido debe ser un número positivo");
        }
        return ResponseEntity.ok(pedidoApplicationService.obtenerPedido(id));
    }
} 