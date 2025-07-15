package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.domain.model.Ruta;
import org.gersystem.transporte.domain.service.RutaDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateRutaDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.RutaDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.RutaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RutaController.class)
class RutaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RutaDomainService rutaDomainService;

    @MockBean
    private RutaMapper rutaMapper;

    private Ruta ruta;
    private RutaDTO rutaDTO;
    private CreateRutaDTO createRutaDTO;

    @BeforeEach
    void setUp() {
        ruta = new Ruta();
        ruta.setId(1L);
        ruta.setNombre("Ruta Test");
        ruta.setPuntoOrigen("Origen");
        ruta.setPuntoDestino("Destino");
        ruta.setDistanciaKm(100.0);
        ruta.setTiempoEstimadoMinutos(120);
        ruta.setActiva(true);

        rutaDTO = new RutaDTO();
        rutaDTO.setId(1L);
        rutaDTO.setNombre("Ruta Test");
        rutaDTO.setPuntoOrigen("Origen");
        rutaDTO.setPuntoDestino("Destino");
        rutaDTO.setDistanciaKm(100.0);
        rutaDTO.setTiempoEstimadoMinutos(120);
        rutaDTO.setActiva(true);

        createRutaDTO = new CreateRutaDTO();
        createRutaDTO.setNombre("Ruta Test");
        createRutaDTO.setPuntoOrigen("Origen");
        createRutaDTO.setPuntoDestino("Destino");
        createRutaDTO.setDistanciaKm(100.0);
        createRutaDTO.setTiempoEstimadoMinutos(120);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearRuta_DebeRetornarRutaCreada() throws Exception {
        when(rutaMapper.toEntity(any(CreateRutaDTO.class))).thenReturn(ruta);
        when(rutaDomainService.crearRuta(any(Ruta.class))).thenReturn(ruta);
        when(rutaMapper.toDto(any(Ruta.class))).thenReturn(rutaDTO);

        mockMvc.perform(post("/api/v1/rutas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRutaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Ruta Test"));

        verify(rutaDomainService).crearRuta(any(Ruta.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void activarRuta_DebeRetornarRutaActivada() throws Exception {
        when(rutaDomainService.activarRuta(1L)).thenReturn(ruta);
        when(rutaMapper.toDto(any(Ruta.class))).thenReturn(rutaDTO);

        mockMvc.perform(put("/api/v1/rutas/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activa").value(true));

        verify(rutaDomainService).activarRuta(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void obtenerRuta_DebeRetornarRuta() throws Exception {
        when(rutaDomainService.obtenerRuta(1L)).thenReturn(ruta);
        when(rutaMapper.toDto(any(Ruta.class))).thenReturn(rutaDTO);

        mockMvc.perform(get("/api/v1/rutas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(rutaDomainService).obtenerRuta(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void listarRutas_DebeRetornarPaginaDeRutas() throws Exception {
        Page<Ruta> page = new PageImpl<>(List.of(ruta));
        when(rutaDomainService.listarRutas(any(), any(Pageable.class))).thenReturn(page);
        when(rutaMapper.toDto(any(Ruta.class))).thenReturn(rutaDTO);

        mockMvc.perform(get("/api/v1/rutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(rutaDomainService).listarRutas(any(), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void listarRutas_DebeRetornarPaginaDeRutasFiltradas() throws Exception {
        Page<Ruta> page = new PageImpl<>(List.of(ruta));
        when(rutaDomainService.listarRutas(eq(true), any(Pageable.class))).thenReturn(page);
        when(rutaMapper.toDto(any(Ruta.class))).thenReturn(rutaDTO);

        mockMvc.perform(get("/api/v1/rutas?activa=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].activa").value(true));

        verify(rutaDomainService).listarRutas(eq(true), any(Pageable.class));
    }
} 