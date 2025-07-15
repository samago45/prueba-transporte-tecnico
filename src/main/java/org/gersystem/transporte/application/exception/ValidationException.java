package org.gersystem.transporte.application.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String code;
    private final String field;

    public ValidationException(String message) {
        super(message);
        this.code = "VALIDATION_ERROR";
        this.field = null;
    }

    public ValidationException(String message, String field) {
        super(message);
        this.code = "VALIDATION_ERROR";
        this.field = field;
    }

    public ValidationException(String message, String code, String field) {
        super(message);
        this.code = code;
        this.field = field;
    }
} 