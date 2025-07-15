package org.gersystem.transporte.infrastructure.adapters.rest;

import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.domain.service.UsuarioDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.*;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.UsuarioMapper;
import org.gersystem.transporte.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UsuarioDomainService usuarioDomainService;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AuthController authController;

    private Usuario usuario;
    private LoginRequestDTO loginRequest;
    private CreateUsuarioDTO createUsuarioDTO;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USER);
        usuario.setActivo(true);

        loginRequest = new LoginRequestDTO();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        createUsuarioDTO = new CreateUsuarioDTO();
        createUsuarioDTO.setUsername("newuser");
        createUsuarioDTO.setEmail("newuser@example.com");
        createUsuarioDTO.setPassword("newpass123");
        createUsuarioDTO.setRol(Rol.USER);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setUsername("testuser");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setRol(Rol.USER);
        usuarioDTO.setActivo(true);
    }

    @Test
    @DisplayName("Debe autenticar usuario exitosamente")
    void login_DebeAutenticarUsuarioExitosamente() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuario, null, Collections.emptyList());
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(usuario)).thenReturn("test.jwt.token");
        when(usuarioDomainService.generarRefreshToken(usuario)).thenReturn("refresh.token");

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(JwtAuthenticationResponseDTO.class);
        JwtAuthenticationResponseDTO authResponse = (JwtAuthenticationResponseDTO) response.getBody();
        assertThat(authResponse.getAccessToken()).isEqualTo("test.jwt.token");
        assertThat(authResponse.getRefreshToken()).isEqualTo("refresh.token");
    }

    @Test
    @DisplayName("Debe manejar credenciales inválidas")
    void login_DebeManejarCredencialesInvalidas() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        ErrorResponseDTO errorResponse = (ErrorResponseDTO) response.getBody();
        assertThat(errorResponse.getMensaje()).isEqualTo("Credenciales inválidas");
    }

    @Test
    @DisplayName("Debe registrar nuevo usuario exitosamente")
    void register_DebeRegistrarNuevoUsuario() {
        // Arrange
        when(usuarioMapper.toEntity(createUsuarioDTO)).thenReturn(usuario);
        when(usuarioDomainService.crearUsuario(usuario)).thenReturn(usuario);
        when(usuarioMapper.toDto(usuario)).thenReturn(usuarioDTO);

        // Act
        ResponseEntity<UsuarioDTO> response = authController.register(createUsuarioDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("testuser");
        verify(usuarioDomainService).crearUsuario(usuario);
    }

    @Test
    @DisplayName("Debe refrescar token exitosamente")
    void refreshToken_DebeGenerarNuevoToken() {
        // Arrange
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("old.refresh.token");
        when(usuarioDomainService.validarRefreshToken("old.refresh.token")).thenReturn(usuario);
        when(jwtTokenProvider.generateToken(usuario)).thenReturn("new.jwt.token");
        when(usuarioDomainService.generarRefreshToken(usuario)).thenReturn("new.refresh.token");

        // Act
        ResponseEntity<JwtAuthenticationResponseDTO> response = authController.refreshToken(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("new.jwt.token");
        assertThat(response.getBody().getRefreshToken()).isEqualTo("new.refresh.token");
    }

    @Test
    @DisplayName("Debe cerrar sesión exitosamente")
    void logout_DebeRevocarToken() {
        // Arrange
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("refresh.token");
        doNothing().when(usuarioDomainService).revocarRefreshToken("refresh.token");

        // Act
        ResponseEntity<Void> response = authController.logout(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(usuarioDomainService).revocarRefreshToken("refresh.token");
    }
} 