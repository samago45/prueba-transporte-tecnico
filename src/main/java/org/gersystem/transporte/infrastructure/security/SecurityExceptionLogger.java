package org.gersystem.transporte.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityExceptionLogger {

    public void logSecurityException(String path, Exception ex) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        
        if (ex instanceof AccessDeniedException) {
            if (log.isDebugEnabled()) {
                log.debug("Acceso denegado para usuario '{}' en ruta '{}': {}", 
                    username, path, ex.getMessage());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Error de seguridad para usuario '{}' en ruta '{}': {}", 
                    username, path, ex.getMessage());
            }
        }
    }
} 
