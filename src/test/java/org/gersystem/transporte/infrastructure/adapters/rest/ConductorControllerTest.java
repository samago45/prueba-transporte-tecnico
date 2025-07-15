package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.application.ConductorApplicationService;
import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConteoVehiculosDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.UpdateConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConductorController.class)
class ConductorControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    static class SecurityTestConfig {
        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                    Usuario testUser = new Usuario();
                    testUser.setId(1L);
                    testUser.setUsername(username);
                    testUser.setPassword(passwordEncoder().encode("test123"));
                    testUser.setEmail("test@example.com");
                    testUser.setRoles(List.of(Rol.ADMIN));
                    testUser.setActivo(true);
                    return testUser;
                }
            };
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .anyRequest().authenticated()
                );

            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConductorApplicationService conductorApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

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
        conductorDTO.setVehiculos(new ArrayList<>());

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
                .andExpect(jsonPath("$.id").value(1L))
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
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe obtener un conductor por ID exitosamente")
    void obtenerConductor_DebeObtenerExitosamente() throws Exception {
        when(conductorApplicationService.obtenerConductorPorId(1L)).thenReturn(conductorDTO);

        mockMvc.perform(get("/api/v1/conductores/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe listar conductores paginados")
    void listarConductores_DebeListarPaginado() throws Exception {
        Page<ConductorDTO> page = new PageImpl<>(List.of(conductorDTO));
        PageDTO<ConductorDTO> pageDTO = new PageDTO<>(page);
        when(conductorApplicationService.obtenerTodosLosConductores(any(), any(), any()))
                .thenReturn(pageDTO);

        mockMvc.perform(get("/api/v1/conductores")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].nombre").value("Juan Pérez"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe eliminar un conductor lógicamente")
    void eliminarConductor_DebeEliminarLogicamente() throws Exception {
        mockMvc.perform(delete("/api/v1/conductores/{id}", 1L))
                .andExpect(status().isOk());
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
    @DisplayName("Debe obtener conductores sin vehículos exitosamente")
    void obtenerConductoresSinVehiculos_DebeObtenerExitosamente() throws Exception {
        when(conductorApplicationService.obtenerConductoresSinVehiculos())
                .thenReturn(List.of(conductorDTO));

        mockMvc.perform(get("/api/v1/conductores/sin-vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe contar vehículos por conductor exitosamente")
    void contarVehiculosPorConductor_DebeContarExitosamente() throws Exception {
        ConteoVehiculosDTO conteoDTO = new ConteoVehiculosDTO();
        conteoDTO.setConductorId(1L);
        conteoDTO.setCantidadVehiculos(5L);

        when(conductorApplicationService.contarVehiculosPorConductor())
                .thenReturn(List.of(conteoDTO));

        mockMvc.perform(get("/api/v1/conductores/conteo-vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].conductorId").value(1L))
                .andExpect(jsonPath("$[0].cantidadVehiculos").value(5));
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