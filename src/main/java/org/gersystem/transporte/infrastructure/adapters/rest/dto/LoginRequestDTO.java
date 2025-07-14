package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
} 