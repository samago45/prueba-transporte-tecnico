package org.gersystem.transporte.infrastructure.adapters.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;
    private String detalle;
    private String path;

    public ErrorResponseDTO(String mensaje, String detalle) {
        this.timestamp = LocalDateTime.now();
        this.mensaje = mensaje;
        this.detalle = detalle;
    }
} 