package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.domain.model.EstadoMantenimiento;
import org.gersystem.transporte.domain.model.Mantenimiento;
import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.TipoMantenimiento;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.service.MantenimientoDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateMantenimientoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.MantenimientoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.MantenimientoMapper;
import org.gersystem.transporte.infrastructure.security.JwtAuthenticationFilter;
import org.gersystem.transporte.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MantenimientoController.class)
@ActiveProfiles("test")
class MantenimientoControllerTest {

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
                    testUser.setRol(Rol.ADMIN);
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

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MantenimientoDomainService mantenimientoDomainService;

    @MockBean
    private MantenimientoMapper mantenimientoMapper;

    private Mantenimiento mantenimiento;
    private MantenimientoDTO mantenimientoDTO;
    private CreateMantenimientoDTO createMantenimientoDTO;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC123");

        mantenimiento = new Mantenimiento();
        mantenimiento.setId(1L);
        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setFechaProgramada(LocalDateTime.now().plusDays(1));
        mantenimiento.setTipo(TipoMantenimiento.PREVENTIVO);
        mantenimiento.setEstado(EstadoMantenimiento.PENDIENTE);

        mantenimientoDTO = new MantenimientoDTO();
        mantenimientoDTO.setId(1L);
        mantenimientoDTO.setVehiculoId(1L);
        mantenimientoDTO.setVehiculoPlaca("ABC123");
        mantenimientoDTO.setFechaProgramada(LocalDateTime.now().plusDays(1));
        mantenimientoDTO.setTipo(TipoMantenimiento.PREVENTIVO);
        mantenimientoDTO.setEstado(EstadoMantenimiento.PENDIENTE);

        createMantenimientoDTO = new CreateMantenimientoDTO();
        createMantenimientoDTO.setVehiculoId(1L);
        createMantenimientoDTO.setFechaProgramada(LocalDateTime.now().plusDays(1));
        createMantenimientoDTO.setTipo(TipoMantenimiento.PREVENTIVO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void programarMantenimiento_DebeRetornarMantenimientoCreado() throws Exception {
        when(mantenimientoMapper.toEntity(any(CreateMantenimientoDTO.class))).thenReturn(mantenimiento);
        when(mantenimientoDomainService.programarMantenimiento(any(Mantenimiento.class))).thenReturn(mantenimiento);
        when(mantenimientoMapper.toDto(any(Mantenimiento.class))).thenReturn(mantenimientoDTO);

        mockMvc.perform(post("/api/v1/mantenimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMantenimientoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vehiculoId").value(1L));

        verify(mantenimientoDomainService).programarMantenimiento(any(Mantenimiento.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarEstado_DebeRetornarMantenimientoActualizado() throws Exception {
        when(mantenimientoDomainService.actualizarEstadoMantenimiento(eq(1L), any(EstadoMantenimiento.class)))
                .thenReturn(mantenimiento);
        when(mantenimientoMapper.toDto(any(Mantenimiento.class))).thenReturn(mantenimientoDTO);

        mockMvc.perform(put("/api/v1/mantenimientos/1/estado")
                .param("nuevoEstado", "EN_PROCESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(mantenimientoDomainService).actualizarEstadoMantenimiento(eq(1L), any(EstadoMantenimiento.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void obtenerMantenimiento_DebeRetornarMantenimiento() throws Exception {
        when(mantenimientoDomainService.obtenerMantenimiento(1L)).thenReturn(mantenimiento);
        when(mantenimientoMapper.toDto(any(Mantenimiento.class))).thenReturn(mantenimientoDTO);

        mockMvc.perform(get("/api/v1/mantenimientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(mantenimientoDomainService).obtenerMantenimiento(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void listarMantenimientos_DebeRetornarPaginaDeMantenimientos() throws Exception {
        Page<Mantenimiento> page = new PageImpl<>(List.of(mantenimiento));
        when(mantenimientoDomainService.listarMantenimientos(any(), any(), any(Pageable.class))).thenReturn(page);
        when(mantenimientoMapper.toDto(any(Mantenimiento.class))).thenReturn(mantenimientoDTO);

        mockMvc.perform(get("/api/v1/mantenimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(mantenimientoDomainService).listarMantenimientos(any(), any(), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void listarMantenimientos_DebeRetornarPaginaFiltrada() throws Exception {
        Page<Mantenimiento> page = new PageImpl<>(List.of(mantenimiento));
        when(mantenimientoDomainService.listarMantenimientos(
            eq(1L), eq(EstadoMantenimiento.PENDIENTE), any(Pageable.class))).thenReturn(page);
        when(mantenimientoMapper.toDto(any(Mantenimiento.class))).thenReturn(mantenimientoDTO);

        mockMvc.perform(get("/api/v1/mantenimientos")
                .param("vehiculoId", "1")
                .param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].estado").value("PENDIENTE"));

        verify(mantenimientoDomainService).listarMantenimientos(
            eq(1L), eq(EstadoMantenimiento.PENDIENTE), any(Pageable.class));
    }

    @Test
    @DisplayName("Debe devolver 401 cuando no está autenticado")
    void accederSinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/v1/mantenimientos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe devolver 403 cuando no tiene permisos")
    void accederSinAutorizacion_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/v1/mantenimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMantenimientoDTO)))
                .andExpect(status().isForbidden());
    }
} 