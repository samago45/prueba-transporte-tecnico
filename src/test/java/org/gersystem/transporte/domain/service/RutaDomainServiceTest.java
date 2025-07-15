package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.Ruta;
import org.gersystem.transporte.domain.repository.RutaRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RutaDomainServiceTest {

    @Mock
    private RutaRepository rutaRepository;

    @InjectMocks
    private RutaDomainService rutaDomainService;

    private Ruta ruta;
    private final Long RUTA_ID = 1L;

    @BeforeEach
    void setUp() {
        ruta = new Ruta();
        ruta.setId(RUTA_ID);
        ruta.setNombre("Ruta Test");
        ruta.setPuntoOrigen("Origen");
        ruta.setPuntoDestino("Destino");
        ruta.setDistanciaKm(100.0);
        ruta.setTiempoEstimadoMinutos(120);
        ruta.setActiva(true);
    }

    @Test
    void crearRuta_DebeGuardarYRetornarRuta() {
        when(rutaRepository.save(any(Ruta.class))).thenReturn(ruta);

        Ruta rutaCreada = rutaDomainService.crearRuta(ruta);

        assertNotNull(rutaCreada);
        assertEquals(RUTA_ID, rutaCreada.getId());
        verify(rutaRepository).save(ruta);
    }

    @Test
    void activarRuta_DebeActivarRutaExistente() {
        ruta.setActiva(false);
        when(rutaRepository.findById(RUTA_ID)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(ruta);

        Ruta rutaActivada = rutaDomainService.activarRuta(RUTA_ID);

        assertTrue(rutaActivada.isActiva());
        verify(rutaRepository).save(ruta);
    }

    @Test
    void desactivarRuta_DebeDesactivarRutaExistente() {
        when(rutaRepository.findById(RUTA_ID)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(ruta);

        Ruta rutaDesactivada = rutaDomainService.desactivarRuta(RUTA_ID);

        assertFalse(rutaDesactivada.isActiva());
        verify(rutaRepository).save(ruta);
    }

    @Test
    void obtenerRuta_DebeRetornarRutaExistente() {
        when(rutaRepository.findById(RUTA_ID)).thenReturn(Optional.of(ruta));

        Ruta rutaEncontrada = rutaDomainService.obtenerRuta(RUTA_ID);

        assertNotNull(rutaEncontrada);
        assertEquals(RUTA_ID, rutaEncontrada.getId());
    }

    @Test
    void obtenerRuta_DebeLanzarExcepcionSiNoExiste() {
        when(rutaRepository.findById(RUTA_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            rutaDomainService.obtenerRuta(RUTA_ID)
        );
    }

    @Test
    void listarRutas_DebeRetornarTodasLasRutasSiFiltroEsNulo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ruta> page = new PageImpl<>(List.of(ruta));
        when(rutaRepository.findAll(pageable)).thenReturn(page);

        Page<Ruta> rutas = rutaDomainService.listarRutas(null, pageable);

        assertFalse(rutas.isEmpty());
        assertEquals(1, rutas.getTotalElements());
    }

    @Test
    void listarRutas_DebeRetornarRutasFiltradas() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ruta> page = new PageImpl<>(List.of(ruta));
        when(rutaRepository.findByActiva(true, pageable)).thenReturn(page);

        Page<Ruta> rutas = rutaDomainService.listarRutas(true, pageable);

        assertFalse(rutas.isEmpty());
        assertEquals(1, rutas.getTotalElements());
        verify(rutaRepository).findByActiva(true, pageable);
    }
} 