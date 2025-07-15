package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByRefreshToken(String refreshToken);
    Optional<Usuario> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<Usuario> findByRolesContains(Rol rol, Pageable pageable);
    Page<Usuario> findByActivo(boolean activo, Pageable pageable);
    long countByRolesContains(Rol rol);
} 