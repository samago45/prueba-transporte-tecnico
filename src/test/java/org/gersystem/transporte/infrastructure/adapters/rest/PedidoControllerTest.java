package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.application.PedidoApplicationService;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@ActiveProfiles("test")
class PedidoControllerTest {

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
    private PedidoApplicationService pedidoApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private PedidoDTO pedidoDTO;
    private CreatePedidoDTO createPedidoDTO;

    @BeforeEach
    void setUp() {
        pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setDescripcion("Pedido de prueba");
        pedidoDTO.setEstado(EstadoPedido.PENDIENTE);

        createPedidoDTO = new CreatePedidoDTO();
        createPedidoDTO.setDescripcion("Pedido de prueba");
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Crear pedido con datos válidos debe retornar 200 OK")
    void crearPedido_ConDatosValidos_DebeRetornar200() throws Exception {
        when(pedidoApplicationService.crearPedido(any(CreatePedidoDTO.class), eq(1L)))
                .thenReturn(pedidoDTO);

        mockMvc.perform(post("/api/v1/pedidos")
                        .param("vehiculoId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPedidoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descripcion").value("Pedido de prueba"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "CONDUCTOR")
    @DisplayName("Actualizar estado de pedido debe retornar 200 OK")
    void actualizarEstado_ConDatosValidos_DebeRetornar200() throws Exception {
        pedidoDTO.setEstado(EstadoPedido.EN_PROCESO);
        when(pedidoApplicationService.actualizarEstado(eq(1L), eq(EstadoPedido.EN_PROCESO)))
                .thenReturn(pedidoDTO);

        mockMvc.perform(patch("/api/v1/pedidos/1/estado")
                        .param("nuevoEstado", "EN_PROCESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Listar pedidos debe retornar 200 OK")
    void listarPedidos_DebeRetornar200() throws Exception {
        Page<PedidoDTO> page = new PageImpl<>(List.of(pedidoDTO));
        when(pedidoApplicationService.listarPedidos(any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/pedidos")
                        .param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Obtener pedido por ID debe retornar 200 OK")
    void obtenerPedido_ConIdValido_DebeRetornar200() throws Exception {
        when(pedidoApplicationService.obtenerPedido(1L))
                .thenReturn(pedidoDTO);

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Crear pedido con datos inválidos debe retornar 400 Bad Request")
    void crearPedido_ConDatosInvalidos_DebeRetornar400() throws Exception {
        CreatePedidoDTO pedidoInvalido = new CreatePedidoDTO();
        // No establecemos descripción ni peso para provocar error de validación

        mockMvc.perform(post("/api/v1/pedidos")
                        .param("vehiculoId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe devolver 401 cuando no está autenticado")
    void accederSinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe devolver 403 cuando no tiene permisos")
    void accederSinAutorizacion_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPedidoDTO)))
                .andExpect(status().isForbidden());
    }
} 