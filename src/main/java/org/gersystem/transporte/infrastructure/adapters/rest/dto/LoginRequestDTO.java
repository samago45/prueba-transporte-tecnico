package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "El usuario o email es requerido")
    private String usernameOrEmail;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
} 