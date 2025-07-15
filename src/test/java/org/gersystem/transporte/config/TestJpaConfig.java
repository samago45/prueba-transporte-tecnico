package org.gersystem.transporte.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class TestJpaConfig {

    @Bean(name = "auditorAware")
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("test-user");
    }
} 