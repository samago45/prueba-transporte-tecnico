package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.gersystem.transporte.domain.model.Rol;
import java.util.List;

@Data
public class CreateUsuarioDTO {
    @NotBlank(message = "El username es requerido")
    private String username;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotNull(message = "Los roles son requeridos")
    private List<Rol> roles;
} 
