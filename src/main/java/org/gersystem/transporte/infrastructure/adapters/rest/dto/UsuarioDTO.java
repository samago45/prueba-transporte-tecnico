package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;
import org.gersystem.transporte.domain.model.Rol;
import java.util.List;

@Data
public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private List<Rol> roles;
    private boolean activo;
} 