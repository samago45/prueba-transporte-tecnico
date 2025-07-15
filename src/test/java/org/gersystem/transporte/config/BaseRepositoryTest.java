package org.gersystem.transporte.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Clase base para todas las pruebas de repositorio.
 * 
 * Configuración optimizada para:
 * - Uso de MySQL en pruebas con perfil "test"
 * - Aislamiento completo entre pruebas con @DirtiesContext
 * - Transacciones automáticas que se revierten al final de cada prueba
 * - Configuración de auditoría para pruebas
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "spring.jpa.properties.hibernate.format_sql=false",
    "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
    "spring.jpa.properties.hibernate.cache.use_query_cache=false"
})
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseRepositoryTest {
    
    // Esta clase base proporciona la configuración común para todas las pruebas de repositorio
    // Cada clase de prueba que extienda de esta tendrá:
    // - Base de datos limpia para cada método de prueba
    // - Transacciones que se revierten automáticamente
    // - Configuración optimizada para rendimiento en pruebas
    
} 