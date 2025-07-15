package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.application.ConductorApplicationService;
import org.gersystem.transporte.config.TestSecurityConfig;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConductorController.class)
@Import(TestSecurityConfig.class)
class ConductorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConductorApplicationService conductorApplicationService;

    private ConductorDTO conductorDTO;
    private CreateConductorDTO createConductorDTO;
    private UpdateConductorDTO updateConductorDTO;

    @BeforeEach
    void setUp() {
        conductorDTO = new ConductorDTO();
        conductorDTO.setId(1L);
        conductorDTO.setNombre("Juan Pérez");
        conductorDTO.setLicencia("A12345");
        conductorDTO.setActivo(true);

        createConductorDTO = new CreateConductorDTO();
        createConductorDTO.setNombre("Juan Pérez");
        createConductorDTO.setLicencia("A12345");

        updateConductorDTO = new UpdateConductorDTO();
        updateConductorDTO.setNombre("Juan Pérez Actualizado");
        updateConductorDTO.setLicencia("B67890");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe crear un conductor exitosamente")
    void crearConductor_DebeCrearExitosamente() throws Exception {
        when(conductorApplicationService.crearConductor(any(CreateConductorDTO.class)))
                .thenReturn(conductorDTO);

        mockMvc.perform(post("/api/v1/conductores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createConductorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.licencia").value("A12345"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe actualizar un conductor exitosamente")
    void actualizarConductor_DebeActualizarExitosamente() throws Exception {
        when(conductorApplicationService.actualizarConductor(eq(1L), any(UpdateConductorDTO.class)))
                .thenReturn(conductorDTO);

        mockMvc.perform(put("/api/v1/conductores/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateConductorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe activar un conductor exitosamente")
    void activarConductor_DebeActivarExitosamente() throws Exception {
        when(conductorApplicationService.activarConductor(1L)).thenReturn(conductorDTO);

        mockMvc.perform(put("/api/v1/conductores/{id}/activar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe desactivar un conductor exitosamente")
    void desactivarConductor_DebeDesactivarExitosamente() throws Exception {
        conductorDTO.setActivo(false);
        when(conductorApplicationService.desactivarConductor(1L)).thenReturn(conductorDTO);

        mockMvc.perform(put("/api/v1/conductores/{id}/desactivar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe listar conductores paginados")
    void listarConductores_DebeListarPaginado() throws Exception {
        Page<ConductorDTO> page = new PageImpl<>(List.of(conductorDTO));
        when(conductorApplicationService.listarConductores(any(), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/conductores")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Juan Pérez"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe obtener conductores sin vehículos")
    void obtenerConductoresSinVehiculos_DebeObtenerExitosamente() throws Exception {
        when(conductorApplicationService.obtenerConductoresSinVehiculos())
                .thenReturn(Arrays.asList(conductorDTO));

        mockMvc.perform(get("/api/v1/conductores/sin-vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe contar vehículos por conductor")
    void contarVehiculosPorConductor_DebeContarExitosamente() throws Exception {
        ConteoVehiculosDTO conteo = new ConteoVehiculosDTO(1L, "Juan Pérez", 2L);
        when(conductorApplicationService.contarVehiculosPorConductor())
                .thenReturn(Arrays.asList(conteo));

        mockMvc.perform(get("/api/v1/conductores/conteo-vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].conductorId").value(1))
                .andExpect(jsonPath("$[0].cantidadVehiculos").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe eliminar un conductor lógicamente")
    void eliminarConductor_DebeEliminarLogicamente() throws Exception {
        mockMvc.perform(delete("/api/v1/conductores/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe devolver 401 cuando no está autenticado")
    void accederSinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/v1/conductores"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe devolver 403 cuando no tiene permisos")
    void accederSinAutorizacion_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/v1/conductores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createConductorDTO)))
                .andExpect(status().isForbidden());
    }
} 