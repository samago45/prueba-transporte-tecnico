package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;
import org.gersystem.transporte.domain.model.Rol;

import java.time.LocalDateTime;

@Data
public class UsuarioDTO {
    private Long id;
    private String username;
    private String nombre;
    private String apellido;
    private Rol rol;
    private boolean activo;
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
} 