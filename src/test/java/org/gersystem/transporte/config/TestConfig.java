package org.gersystem.transporte.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Value;

@TestConfiguration
@TestPropertySource(properties = {
    "conductor.max.vehiculos=3"
})
public class TestConfig {
} 