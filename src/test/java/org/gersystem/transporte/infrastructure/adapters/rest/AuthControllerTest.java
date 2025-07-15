package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.domain.service.UsuarioDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateUsuarioDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.JwtAuthenticationResponseDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.LoginRequestDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.RefreshTokenRequestDTO;
import org.gersystem.transporte.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

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
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UsuarioDomainService usuarioDomainService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDTO loginRequest;
    private CreateUsuarioDTO createUsuarioDTO;
    private RefreshTokenRequestDTO refreshTokenRequest;
    private Usuario usuario;
    private JwtAuthenticationResponseDTO jwtResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        createUsuarioDTO = new CreateUsuarioDTO();
        createUsuarioDTO.setUsername("newuser");
        createUsuarioDTO.setPassword("password123");
        createUsuarioDTO.setEmail("newuser@example.com");

        refreshTokenRequest = new RefreshTokenRequestDTO();
        refreshTokenRequest.setRefreshToken("refresh_token");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("password");
        usuario.setEmail("test@example.com");
        usuario.setRoles(List.of(Rol.CLIENTE));
        usuario.setActivo(true);

        jwtResponse = new JwtAuthenticationResponseDTO();
        jwtResponse.setAccessToken("access_token");
        jwtResponse.setRefreshToken("refresh_token");
    }

    @Test
    void login_DebeAutenticarUsuarioExitosamente() throws Exception {
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(usuario, null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("access_token");
        when(usuarioDomainService.generarRefreshToken(usuario)).thenReturn("refresh_token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token"));
    }

    @Test
    void login_DebeManejarCredencialesInvalidas() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_DebeRegistrarNuevoUsuario() throws Exception {
        when(usuarioDomainService.crearUsuario(any(Usuario.class))).thenReturn(usuario);
        when(jwtTokenProvider.generateToken(any(Usuario.class))).thenReturn("access_token");
        when(usuarioDomainService.generarRefreshToken(usuario)).thenReturn("refresh_token");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUsuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token"));
    }

    @Test
    void refreshToken_DebeGenerarNuevoToken() throws Exception {
        when(usuarioDomainService.validarRefreshToken("refresh_token")).thenReturn(usuario);
        when(jwtTokenProvider.generateToken(any(Usuario.class))).thenReturn("new_access_token");
        when(usuarioDomainService.generarRefreshToken(usuario)).thenReturn("new_refresh_token");

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new_access_token"))
                .andExpect(jsonPath("$.refreshToken").value("new_refresh_token"));
    }

    @Test
    void logout_DebeRevocarToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk());
    }
} 