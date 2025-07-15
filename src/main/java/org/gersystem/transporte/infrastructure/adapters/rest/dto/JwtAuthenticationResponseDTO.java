package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    
    public JwtAuthenticationResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
} 