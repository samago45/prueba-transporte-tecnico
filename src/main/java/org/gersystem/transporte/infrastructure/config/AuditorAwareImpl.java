package org.gersystem.transporte.infrastructure.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // PENDIENTE: Integrar con Spring Security para obtener el usuario autenticado
        // Optional.ofNullable(SecurityContextHolder.getContext())
        //         .map(SecurityContext::getAuthentication)
        //         .filter(Authentication::isAuthenticated)
        //         .map(Authentication::getName);
        return Optional.of("SYSTEM");
    }
} 
