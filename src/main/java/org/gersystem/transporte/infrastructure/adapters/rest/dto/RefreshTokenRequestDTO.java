package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    @NotBlank(message = "El refresh token es requerido")
    private String refreshToken;
} 