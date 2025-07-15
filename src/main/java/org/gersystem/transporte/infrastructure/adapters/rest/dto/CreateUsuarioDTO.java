package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.gersystem.transporte.domain.model.Rol;

@Data
public class CreateUsuarioDTO {
    @NotBlank(message = "El username es requerido")
    private String username;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @NotNull(message = "El rol es requerido")
    private Rol rol;
} 