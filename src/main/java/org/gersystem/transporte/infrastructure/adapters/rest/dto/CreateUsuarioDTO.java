package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.gersystem.transporte.domain.model.Rol;

import java.util.List;

@Data
public class CreateUsuarioDTO {
    
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String username;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    
    private String apellido;
    
    private List<Rol> roles;
} 