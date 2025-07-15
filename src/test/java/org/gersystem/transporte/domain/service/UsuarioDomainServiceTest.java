package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDomainServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioDomainService usuarioDomainService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USER);
        usuario.setActivo(true);
    }

    @Test
    @DisplayName("Debe crear usuario exitosamente")
    void crearUsuario_DebeCrearExitosamente() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioDomainService.crearUsuario(usuario);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("testuser");
        assertThat(resultado.getEmail()).isEqualTo("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe validar username único al crear usuario")
    void crearUsuario_DebeValidarUsernameUnico() {
        // Arrange
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioDomainService.crearUsuario(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El username ya está en uso");
    }

    @Test
    @DisplayName("Debe validar email único al crear usuario")
    void crearUsuario_DebeValidarEmailUnico() {
        // Arrange
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioDomainService.crearUsuario(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El email ya está en uso");
    }

    @Test
    @DisplayName("Debe generar refresh token exitosamente")
    void generarRefreshToken_DebeGenerarExitosamente() {
        // Arrange
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        String refreshToken = usuarioDomainService.generarRefreshToken(usuario);

        // Assert
        assertThat(refreshToken).isNotNull();
        assertThat(usuario.getRefreshToken()).isNotNull();
        assertThat(usuario.getRefreshTokenExpiryDate()).isAfter(LocalDateTime.now());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe validar refresh token exitosamente")
    void validarRefreshToken_DebeValidarExitosamente() {
        // Arrange
        usuario.setRefreshToken("valid.refresh.token");
        usuario.setRefreshTokenExpiryDate(LocalDateTime.now().plusDays(7));
        when(usuarioRepository.findByRefreshToken("valid.refresh.token"))
                .thenReturn(Optional.of(usuario));

        // Act
        Usuario resultado = usuarioDomainService.validarRefreshToken("valid.refresh.token");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Debe rechazar refresh token expirado")
    void validarRefreshToken_DebeRechazarTokenExpirado() {
        // Arrange
        usuario.setRefreshToken("expired.refresh.token");
        usuario.setRefreshTokenExpiryDate(LocalDateTime.now().minusDays(1));
        when(usuarioRepository.findByRefreshToken("expired.refresh.token"))
                .thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> usuarioDomainService.validarRefreshToken("expired.refresh.token"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Refresh token expirado");
    }

    @Test
    @DisplayName("Debe revocar refresh token exitosamente")
    void revocarRefreshToken_DebeRevocarExitosamente() {
        // Arrange
        usuario.setRefreshToken("token.to.revoke");
        when(usuarioRepository.findByRefreshToken("token.to.revoke"))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioDomainService.revocarRefreshToken("token.to.revoke");

        // Assert
        assertThat(usuario.getRefreshToken()).isNull();
        assertThat(usuario.getRefreshTokenExpiryDate()).isNull();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe cambiar contraseña exitosamente")
    void cambiarPassword_DebeCambiarExitosamente() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldPassword", usuario.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioDomainService.cambiarPassword(1L, "oldPassword", "newPassword");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPassword()).isEqualTo("encodedNewPassword");
        verify(passwordEncoder).matches("oldPassword", usuario.getPassword());
        verify(passwordEncoder).encode("newPassword");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe rechazar contraseña actual incorrecta")
    void cambiarPassword_DebeRechazarPasswordIncorrecta() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", usuario.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> usuarioDomainService.cambiarPassword(1L, "wrongPassword", "newPassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Contraseña actual incorrecta");
    }

    @Test
    @DisplayName("Debe rechazar usuario no encontrado al cambiar contraseña")
    void cambiarPassword_DebeRechazarUsuarioNoEncontrado() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioDomainService.cambiarPassword(999L, "oldPassword", "newPassword"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
} 