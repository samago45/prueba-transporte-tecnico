package org.gersystem.transporte.application.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    private final String entity;

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.entity = null;
    }

    public BusinessException(String message, String entity) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.entity = entity;
    }

    public BusinessException(String message, String code, String entity) {
        super(message);
        this.code = code;
        this.entity = entity;
    }
} 
