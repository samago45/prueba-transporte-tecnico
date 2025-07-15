package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.Ruta;
import org.gersystem.transporte.domain.service.RutaDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateRutaDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.RutaDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.RutaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rutas")
@RequiredArgsConstructor
@Tag(name = "Rutas", description = "API para gestión de rutas de transporte")
public class RutaController {

    private final RutaDomainService rutaDomainService;
    private final RutaMapper rutaMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear ruta", description = "Crea una nueva ruta de transporte")
    public ResponseEntity<RutaDTO> crearRuta(@Valid @RequestBody CreateRutaDTO createRutaDTO) {
        Ruta ruta = rutaMapper.toEntity(createRutaDTO);
        Ruta rutaCreada = rutaDomainService.crearRuta(ruta);
        return ResponseEntity.ok(rutaMapper.toDto(rutaCreada));
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar ruta", description = "Activa una ruta existente")
    public ResponseEntity<RutaDTO> activarRuta(@PathVariable Long id) {
        Ruta rutaActualizada = rutaDomainService.activarRuta(id);
        return ResponseEntity.ok(rutaMapper.toDto(rutaActualizada));
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar ruta", description = "Desactiva una ruta existente")
    public ResponseEntity<RutaDTO> desactivarRuta(@PathVariable Long id) {
        Ruta rutaActualizada = rutaDomainService.desactivarRuta(id);
        return ResponseEntity.ok(rutaMapper.toDto(rutaActualizada));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Obtener ruta", description = "Obtiene los detalles de una ruta por ID")
    public ResponseEntity<RutaDTO> obtenerRuta(@PathVariable Long id) {
        Ruta ruta = rutaDomainService.obtenerRuta(id);
        return ResponseEntity.ok(rutaMapper.toDto(ruta));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar rutas", description = "Lista las rutas con filtros opcionales")
    public ResponseEntity<Page<RutaDTO>> listarRutas(
            @RequestParam(required = false) Boolean activa,
            Pageable pageable) {
        Page<Ruta> rutas = rutaDomainService.listarRutas(activa, pageable);
        return ResponseEntity.ok(rutas.map(rutaMapper::toDto));
    }
} 