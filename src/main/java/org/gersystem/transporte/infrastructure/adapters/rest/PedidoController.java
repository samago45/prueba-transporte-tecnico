package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.gersystem.transporte.application.PedidoApplicationService;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "API para gestión de pedidos")
public class PedidoController {

    private final PedidoApplicationService pedidoApplicationService;

    public PedidoController(PedidoApplicationService pedidoApplicationService) {
        this.pedidoApplicationService = pedidoApplicationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Crear un nuevo pedido", description = "Crea un nuevo pedido asignándolo a un vehículo específico")
    public ResponseEntity<PedidoDTO> crearPedido(
            @Valid @RequestBody CreatePedidoDTO createPedidoDTO,
            @RequestParam Long vehiculoId) {
        return ResponseEntity.ok(pedidoApplicationService.crearPedido(createPedidoDTO, vehiculoId));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR')")
    @Operation(summary = "Actualizar estado de pedido", description = "Actualiza el estado de un pedido existente")
    public ResponseEntity<PedidoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido nuevoEstado) {
        return ResponseEntity.ok(pedidoApplicationService.actualizarEstado(id, nuevoEstado));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'CONDUCTOR')")
    @Operation(summary = "Listar pedidos", description = "Obtiene una lista paginada de pedidos con filtros opcionales")
    public ResponseEntity<Page<PedidoDTO>> listarPedidos(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) Long conductorId,
            Pageable pageable) {
        return ResponseEntity.ok(pedidoApplicationService.listarPedidos(estado, conductorId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'CONDUCTOR')")
    @Operation(summary = "Obtener pedido por ID", description = "Obtiene los detalles de un pedido específico")
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoApplicationService.obtenerPedido(id));
    }
} 