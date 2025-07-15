package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gersystem.transporte.application.PedidoApplicationService;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Operaciones sobre pedidos de transporte")
public class PedidoController {

    private final PedidoApplicationService pedidoApplicationService;

    public PedidoController(PedidoApplicationService pedidoApplicationService) {
        this.pedidoApplicationService = pedidoApplicationService;
    }

    @Operation(summary = "Registrar un nuevo pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido registrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo o conductor no encontrado"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio violada (ej. capacidad excedida)")
    })
    @PostMapping
    public ResponseEntity<PedidoDTO> registrarPedido(@RequestBody CreatePedidoDTO createPedidoDTO) {
        PedidoDTO nuevoPedido = pedidoApplicationService.registrarPedido(createPedidoDTO);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar el estado de un pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del pedido actualizado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{pedidoId}/estado")
    public ResponseEntity<PedidoDTO> actualizarEstadoPedido(@PathVariable Long pedidoId, @RequestParam EstadoPedido nuevoEstado) {
        PedidoDTO pedidoActualizado = pedidoApplicationService.actualizarEstadoPedido(pedidoId, nuevoEstado);
        return ResponseEntity.ok(pedidoActualizado);
    }
} 