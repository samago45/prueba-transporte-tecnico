package org.gersystem.transporte.domain.service;

import javax.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioDomainService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final long REFRESH_TOKEN_VALIDITY = 7L; // días

    public UsuarioDomainService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalStateException("El nombre de usuario ya existe");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalStateException("El email ya está registrado");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public String generarRefreshToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        usuario.setRefreshToken(token);
        usuario.setRefreshTokenExpiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY));
        usuarioRepository.save(usuario);
        return token;
    }

    @Transactional
    public Usuario validarRefreshToken(String refreshToken) {
        Usuario usuario = usuarioRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalStateException("Refresh token inválido"));

        if (usuario.getRefreshTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Refresh token expirado");
        }

        return usuario;
    }

    @Transactional
    public void revocarRefreshToken(String refreshToken) {
        Usuario usuario = usuarioRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("Token no encontrado"));
        
        usuario.setRefreshToken(null);
        usuario.setRefreshTokenExpiryDate(null);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario cambiarPassword(Long id, String oldPassword, String newPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        return usuarioRepository.save(usuario);
    }
} 
