package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.application.VehiculoApplicationService;
import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PageDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.UpdateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.VehiculoDTO;
import org.gersystem.transporte.infrastructure.security.JwtAuthenticationFilter;
import org.gersystem.transporte.infrastructure.security.JwtTokenProvider;
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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VehiculoController.class)
@ActiveProfiles("test")
class VehiculoControllerTest {

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
        public JwtTokenProvider jwtTokenProvider() {
            return mock(JwtTokenProvider.class);
        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(jwtTokenProvider(), userDetailsService());
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
    private VehiculoApplicationService vehiculoApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private VehiculoDTO vehiculoDTO;
    private CreateVehiculoDTO createVehiculoDTO;
    private UpdateVehiculoDTO updateVehiculoDTO;

    @BeforeEach
    void setUp() {
        vehiculoDTO = new VehiculoDTO();
        vehiculoDTO.setId(1L);
        vehiculoDTO.setPlaca("ABC-123");
        vehiculoDTO.setCapacidad(new BigDecimal("1000.00"));
        vehiculoDTO.setActivo(true);

        createVehiculoDTO = new CreateVehiculoDTO();
        createVehiculoDTO.setPlaca("ABC-123");
        createVehiculoDTO.setCapacidad(new BigDecimal("1000.00"));

        updateVehiculoDTO = new UpdateVehiculoDTO();
        updateVehiculoDTO.setPlaca("XYZ-789");
        updateVehiculoDTO.setCapacidad(new BigDecimal("2000.00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe crear un vehículo exitosamente")
    void crearVehiculo_DebeCrearExitosamente() throws Exception {
        when(vehiculoApplicationService.crearVehiculo(any(CreateVehiculoDTO.class)))
                .thenReturn(vehiculoDTO);

        mockMvc.perform(post("/api/v1/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createVehiculoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.placa").value("ABC-123"))
                .andExpect(jsonPath("$.capacidad").value("1000.00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe actualizar un vehículo exitosamente")
    void actualizarVehiculo_DebeActualizarExitosamente() throws Exception {
        when(vehiculoApplicationService.actualizarVehiculo(eq(1L), any(UpdateVehiculoDTO.class)))
                .thenReturn(vehiculoDTO);

        mockMvc.perform(put("/api/v1/vehiculos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateVehiculoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Debe obtener un vehículo por ID exitosamente")
    void obtenerVehiculo_DebeObtenerExitosamente() throws Exception {
        given(vehiculoApplicationService.obtenerVehiculoPorId(1L)).willReturn(vehiculoDTO);

        mockMvc.perform(get("/api/v1/vehiculos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.placa").value("ABC-123"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Debe listar vehículos paginados")
    void listarVehiculos_DebeListarPaginado() throws Exception {
        Page<VehiculoDTO> page = new PageImpl<>(List.of(vehiculoDTO));
        PageDTO<VehiculoDTO> pageDTO = new PageDTO<>(page);
        when(vehiculoApplicationService.obtenerTodosLosVehiculos(any(), any(), any()))
                .thenReturn(pageDTO);

        mockMvc.perform(get("/api/v1/vehiculos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].placa").value("ABC-123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe eliminar un vehículo lógicamente")
    void eliminarVehiculo_DebeEliminarLogicamente() throws Exception {
        mockMvc.perform(delete("/api/v1/vehiculos/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe devolver 401 cuando no está autenticado")
    void accederSinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/v1/vehiculos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Debe devolver 403 cuando no tiene permisos")
    void accederSinAutorizacion_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/v1/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createVehiculoDTO)))
                .andExpect(status().isForbidden());
    }
} 