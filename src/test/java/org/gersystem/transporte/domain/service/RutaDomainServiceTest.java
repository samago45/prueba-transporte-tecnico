package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.Ruta;
import org.gersystem.transporte.domain.repository.RutaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RutaDomainServiceTest {

    @Mock
    private RutaRepository rutaRepository;

    @InjectMocks
    private RutaDomainService rutaDomainService;

    private Ruta ruta;

    @BeforeEach
    void setUp() {
        ruta = new Ruta();
        ruta.setId(1L);
        ruta.setNombre("Ruta Test");
        ruta.setPuntoOrigen("Origen Test");
        ruta.setPuntoDestino("Destino Test");
        ruta.setDistanciaKm(100.0);
        ruta.setTiempoEstimadoMinutos(120);
        ruta.setActiva(true);
    }

    @Test
    @DisplayName("Debe crear una ruta exitosamente")
    void crearRuta_DebeCrearExitosamente() {
        // Arrange
        when(rutaRepository.save(any(Ruta.class))).thenReturn(ruta);

        // Act
        Ruta rutaCreada = rutaDomainService.crearRuta(ruta);

        // Assert
        assertThat(rutaCreada).isNotNull();
        assertThat(rutaCreada.getId()).isEqualTo(1L);
        assertThat(rutaCreada.getNombre()).isEqualTo("Ruta Test");
        assertThat(rutaCreada.getPuntoOrigen()).isEqualTo("Origen Test");
        assertThat(rutaCreada.getPuntoDestino()).isEqualTo("Destino Test");
        assertThat(rutaCreada.getDistanciaKm()).isEqualTo(100.0);
        assertThat(rutaCreada.getTiempoEstimadoMinutos()).isEqualTo(120);
        assertThat(rutaCreada.isActiva()).isTrue();
    }

    @Test
    @DisplayName("Debe activar una ruta exitosamente")
    void activarRuta_DebeActivarExitosamente() {
        // Arrange
        ruta.setActiva(false);
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(ruta);

        // Act
        Ruta rutaActivada = rutaDomainService.activarRuta(1L);

        // Assert
        assertThat(rutaActivada).isNotNull();
        assertThat(rutaActivada.isActiva()).isTrue();
    }

    @Test
    @DisplayName("Debe desactivar una ruta exitosamente")
    void desactivarRuta_DebeDesactivarExitosamente() {
        // Arrange
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(ruta);

        // Act
        Ruta rutaDesactivada = rutaDomainService.desactivarRuta(1L);

        // Assert
        assertThat(rutaDesactivada).isNotNull();
        assertThat(rutaDesactivada.isActiva()).isFalse();
    }

    @Test
    @DisplayName("Debe lanzar excepción al no encontrar la ruta")
    void obtenerRuta_DebeLanzarExcepcion() {
        // Arrange
        when(rutaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> rutaDomainService.obtenerRuta(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ruta no encontrada");
    }

    @Test
    @DisplayName("Debe listar rutas activas paginadas")
    void listarRutas_DebeListarRutasActivasPaginadas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ruta> rutaPage = new PageImpl<>(List.of(ruta));
        when(rutaRepository.findByActiva(true, pageable)).thenReturn(rutaPage);

        // Act
        Page<Ruta> resultado = rutaDomainService.listarRutas(true, pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).isActiva()).isTrue();
    }

    @Test
    @DisplayName("Debe listar todas las rutas cuando no se especifica estado")
    void listarRutas_DebeListarTodasLasRutas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ruta> rutaPage = new PageImpl<>(List.of(ruta));
        when(rutaRepository.findAll(pageable)).thenReturn(rutaPage);

        // Act
        Page<Ruta> resultado = rutaDomainService.listarRutas(null, pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }
} 