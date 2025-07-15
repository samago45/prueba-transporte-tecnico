package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.config.TestJpaConfig;
import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario();
        usuario1.setUsername("testuser1");
        usuario1.setEmail("test1@example.com");
        usuario1.setPassword("password123");
        usuario1.setNombre("Test User One");
        usuario1.setRoles(List.of(Rol.CLIENTE));
        usuario1.setActivo(true);
        usuario1.setRefreshToken("refresh.token.1");
        usuario1.setRefreshTokenExpiryDate(LocalDateTime.now().plusDays(7));
        entityManager.persist(usuario1);

        usuario2 = new Usuario();
        usuario2.setUsername("testuser2");
        usuario2.setEmail("test2@example.com");
        usuario2.setPassword("password456");
        usuario2.setNombre("Test User Two");
        usuario2.setRoles(List.of(Rol.ADMIN));
        usuario2.setActivo(true);
        entityManager.persist(usuario2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar usuario por username")
    void findByUsername_DebeEncontrarUsuario() {
        // Act
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsername("testuser1");

        // Assert
        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("Debe encontrar usuario por email")
    void findByEmail_DebeEncontrarUsuario() {
        // Act
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("test1@example.com");

        // Assert
        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getUsername()).isEqualTo("testuser1");
    }

    @Test
    @DisplayName("Debe verificar existencia por username")
    void existsByUsername_DebeVerificarExistencia() {
        // Act
        boolean existe = usuarioRepository.existsByUsername("testuser1");
        boolean noExiste = usuarioRepository.existsByUsername("nonexistent");

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }

    @Test
    @DisplayName("Debe verificar existencia por email")
    void existsByEmail_DebeVerificarExistencia() {
        // Act
        boolean existe = usuarioRepository.existsByEmail("test1@example.com");
        boolean noExiste = usuarioRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }

    @Test
    @DisplayName("Debe encontrar usuario por refresh token")
    void findByRefreshToken_DebeEncontrarUsuario() {
        // Act
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByRefreshToken("refresh.token.1");

        // Assert
        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getUsername()).isEqualTo("testuser1");
    }

    @Test
    @DisplayName("Debe encontrar usuarios por rol")
    void findByRolesContains_DebeEncontrarUsuarios() {
        // Act
        Page<Usuario> usuariosAdmin = usuarioRepository.findByRolesContains(Rol.ADMIN, PageRequest.of(0, 10));
        Page<Usuario> usuariosCliente = usuarioRepository.findByRolesContains(Rol.CLIENTE, PageRequest.of(0, 10));

        // Assert
        assertThat(usuariosAdmin.getContent()).hasSize(1);
        assertThat(usuariosAdmin.getContent().get(0).getUsername()).isEqualTo("testuser2");
        assertThat(usuariosCliente.getContent()).hasSize(1);
        assertThat(usuariosCliente.getContent().get(0).getUsername()).isEqualTo("testuser1");
    }

    @Test
    @DisplayName("Debe encontrar usuarios activos")
    void findByActivo_DebeEncontrarUsuarios() {
        // Act
        Page<Usuario> usuariosActivos = usuarioRepository.findByActivo(true, PageRequest.of(0, 10));

        // Assert
        assertThat(usuariosActivos.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Debe encontrar por username o email")
    void findByUsernameOrEmail_DebeEncontrarUsuario() {
        // Act
        Optional<Usuario> porUsername = usuarioRepository.findByUsernameOrEmail("testuser1", null);
        Optional<Usuario> porEmail = usuarioRepository.findByUsernameOrEmail(null, "test1@example.com");

        // Assert
        assertThat(porUsername).isPresent();
        assertThat(porEmail).isPresent();
        assertThat(porUsername.get().getId()).isEqualTo(porEmail.get().getId());
    }

    @Test
    @DisplayName("Debe contar usuarios por rol")
    void countByRolesContains_DebeContarUsuarios() {
        // Act
        long cantidadAdmin = usuarioRepository.countByRolesContains(Rol.ADMIN);
        long cantidadCliente = usuarioRepository.countByRolesContains(Rol.CLIENTE);

        // Assert
        assertThat(cantidadAdmin).isEqualTo(1);
        assertThat(cantidadCliente).isEqualTo(1);
    }
} 