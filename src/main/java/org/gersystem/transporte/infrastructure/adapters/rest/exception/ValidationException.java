package org.gersystem.transporte.infrastructure.adapters.rest.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
} 