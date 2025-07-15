package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.application.PedidoApplicationService;
import org.gersystem.transporte.application.exception.BusinessException;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.PedidoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API para gestión de pedidos")
public class PedidoController {

    private final PedidoApplicationService pedidoService;
    private final PedidoMapper pedidoMapper;

    private static final Set<String> CAMPOS_ORDENAMIENTO_VALIDOS = Set.of(
            "id", "descripcion", "peso", "estado", "createdDate", "lastModifiedDate",
            "conductor.nombre", "vehiculo.placa"
    );

    @GetMapping
    @Operation(summary = "Listar pedidos", 
               description = "Obtiene una lista paginada de pedidos con filtros opcionales")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR')")
    public ResponseEntity<Page<PedidoDTO>> listarPedidos(
            @Parameter(description = "Filtrar por estado del pedido")
            @RequestParam(required = false) EstadoPedido estado,
            
            @Parameter(description = "Filtrar por ID del conductor")
            @RequestParam(required = false) Long conductorId,
            
            @Parameter(description = "Filtrar por ID del vehículo")
            @RequestParam(required = false) Long vehiculoId,
            
            @Parameter(description = "Fecha inicio para filtrar (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            
            @Parameter(description = "Fecha fin para filtrar (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        validarOrdenamiento(pageable);
        
        Page<PedidoDTO> pedidos = pedidoService.buscarPedidos(
                estado, conductorId, vehiculoId, fechaInicio, fechaFin, pageable
        ).map(pedidoMapper::toDto);
        
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody CreatePedidoDTO createPedidoDTO) {
        return ResponseEntity.ok(
            pedidoMapper.toDto(
                pedidoService.crearPedido(
                    pedidoMapper.toEntity(createPedidoDTO),
                    createPedidoDTO.getVehiculoId()
                )
            )
        );
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado", description = "Actualiza el estado de un pedido")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado
    ) {
        return ResponseEntity.ok(
            pedidoMapper.toDto(
                pedidoService.actualizarEstadoPedido(id, estado)
            )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido", description = "Obtiene un pedido por su ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONDUCTOR')")
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(
            pedidoMapper.toDto(
                pedidoService.obtenerPedido(id)
            )
        );
    }

    private void validarOrdenamiento(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return;
        }
        
        pageable.getSort().forEach(order -> {
            if (!CAMPOS_ORDENAMIENTO_VALIDOS.contains(order.getProperty())) {
                throw new BusinessException(
                    String.format("La propiedad '%s' no es válida para ordenamiento. Propiedades válidas: %s",
                        order.getProperty(),
                        String.join(", ", CAMPOS_ORDENAMIENTO_VALIDOS)
                    )
                );
            }
        });
    }
} 