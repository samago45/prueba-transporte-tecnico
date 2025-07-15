package org.gersystem.transporte.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "testAuditorAware")
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class TestJpaConfig {

    @Bean(name = "testAuditorAware")
    @Primary
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("test-user");
    }
} 