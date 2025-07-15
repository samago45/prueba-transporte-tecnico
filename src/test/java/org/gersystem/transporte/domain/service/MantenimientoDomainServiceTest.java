package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.*;
import org.gersystem.transporte.domain.repository.MantenimientoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MantenimientoDomainServiceTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private MantenimientoDomainService mantenimientoDomainService;

    private Mantenimiento mantenimiento;
    private Vehiculo vehiculo;
    private final Long MANTENIMIENTO_ID = 1L;
    private final Long VEHICULO_ID = 1L;
    private final String FECHA_PROGRAMADA = LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    @BeforeEach
    void setUp() {
        vehiculo = new Vehiculo();
        vehiculo.setId(VEHICULO_ID);
        vehiculo.setPlaca("ABC123");
        vehiculo.setActivo(true);

        mantenimiento = new Mantenimiento();
        mantenimiento.setId(MANTENIMIENTO_ID);
        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setFechaProgramada(LocalDateTime.now().plusDays(1));
        mantenimiento.setTipo(TipoMantenimiento.PREVENTIVO);
        mantenimiento.setEstado(EstadoMantenimiento.PENDIENTE);
    }

    @Test
    void programarMantenimiento_DebeGuardarMantenimiento() {
        when(vehiculoRepository.findById(VEHICULO_ID)).thenReturn(Optional.of(vehiculo));
        when(mantenimientoRepository.findByVehiculoIdAndEstadoAndFechaProgramadaBetween(
                eq(VEHICULO_ID), any(), any(), any())).thenReturn(Collections.emptyList());
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenReturn(mantenimiento);

        Mantenimiento resultado = mantenimientoDomainService.programarMantenimiento(mantenimiento, VEHICULO_ID, FECHA_PROGRAMADA);

        assertNotNull(resultado);
        assertEquals(MANTENIMIENTO_ID, resultado.getId());
        verify(mantenimientoRepository).save(mantenimiento);
    }

    @Test
    void programarMantenimiento_DebeFallarSiVehiculoNoExiste() {
        when(vehiculoRepository.findById(VEHICULO_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            mantenimientoDomainService.programarMantenimiento(mantenimiento, VEHICULO_ID, FECHA_PROGRAMADA)
        );
    }

    @Test
    void programarMantenimiento_DebeFallarSiVehiculoInactivo() {
        vehiculo.setActivo(false);
        when(vehiculoRepository.findById(VEHICULO_ID)).thenReturn(Optional.of(vehiculo));

        assertThrows(IllegalStateException.class, () ->
            mantenimientoDomainService.programarMantenimiento(mantenimiento, VEHICULO_ID, FECHA_PROGRAMADA)
        );
    }

    @Test
    void actualizarEstadoMantenimiento_DebeActualizarEstado() {
        when(mantenimientoRepository.findById(MANTENIMIENTO_ID)).thenReturn(Optional.of(mantenimiento));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenReturn(mantenimiento);

        Mantenimiento resultado = mantenimientoDomainService.actualizarEstadoMantenimiento(
            MANTENIMIENTO_ID, EstadoMantenimiento.EN_PROCESO);

        assertEquals(EstadoMantenimiento.EN_PROCESO, resultado.getEstado());
        verify(mantenimientoRepository).save(mantenimiento);
    }

    @Test
    void obtenerMantenimiento_DebeRetornarMantenimiento() {
        when(mantenimientoRepository.findById(MANTENIMIENTO_ID)).thenReturn(Optional.of(mantenimiento));

        Mantenimiento resultado = mantenimientoDomainService.obtenerMantenimiento(MANTENIMIENTO_ID);

        assertNotNull(resultado);
        assertEquals(MANTENIMIENTO_ID, resultado.getId());
    }

    @Test
    void listarMantenimientos_DebeRetornarTodosSiFiltrosNulos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Mantenimiento> page = new PageImpl<>(List.of(mantenimiento));
        when(mantenimientoRepository.findAll(pageable)).thenReturn(page);

        Page<Mantenimiento> resultado = mantenimientoDomainService.listarMantenimientos(null, null, pageable);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void listarMantenimientos_DebeRetornarFiltradosPorVehiculoYEstado() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Mantenimiento> page = new PageImpl<>(List.of(mantenimiento));
        when(mantenimientoRepository.findByVehiculoIdAndEstado(
            VEHICULO_ID, EstadoMantenimiento.PENDIENTE, pageable)).thenReturn(page);

        Page<Mantenimiento> resultado = mantenimientoDomainService.listarMantenimientos(
            VEHICULO_ID, EstadoMantenimiento.PENDIENTE, pageable);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        verify(mantenimientoRepository).findByVehiculoIdAndEstado(
            VEHICULO_ID, EstadoMantenimiento.PENDIENTE, pageable);
    }
} 