package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.config.TestJpaConfig;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConteoVehiculosDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class ConductorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConductorRepository conductorRepository;

    private Conductor conductor1;
    private Conductor conductor2;
    private Vehiculo vehiculo1;
    private Vehiculo vehiculo2;

    @BeforeEach
    void setUp() {
        // Crear conductores
        conductor1 = new Conductor();
        conductor1.setNombre("Juan Pérez");
        conductor1.setLicencia("A12345");
        conductor1.setActivo(true);
        conductor1 = entityManager.persist(conductor1);

        conductor2 = new Conductor();
        conductor2.setNombre("María López");
        conductor2.setLicencia("B67890");
        conductor2.setActivo(false);
        conductor2 = entityManager.persist(conductor2);

        // Crear vehículos
        vehiculo1 = new Vehiculo();
        vehiculo1.setPlaca("ABC123");
        vehiculo1.setCapacidad(new BigDecimal("1000.00"));
        vehiculo1.setActivo(true);
        vehiculo1.setConductor(conductor1);
        vehiculo1 = entityManager.persist(vehiculo1);

        vehiculo2 = new Vehiculo();
        vehiculo2.setPlaca("XYZ789");
        vehiculo2.setCapacidad(new BigDecimal("2000.00"));
        vehiculo2.setActivo(true);
        vehiculo2.setConductor(conductor1);
        vehiculo2 = entityManager.persist(vehiculo2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar conductor por ID")
    void findById_DebeEncontrarConductor() {
        // Act
        Conductor conductorEncontrado = conductorRepository.findById(conductor1.getId()).orElse(null);

        // Assert
        assertThat(conductorEncontrado).isNotNull();
        assertThat(conductorEncontrado.getNombre()).isEqualTo("Juan Pérez");
        assertThat(conductorEncontrado.getLicencia()).isEqualTo("A12345");
    }

    @Test
    @DisplayName("Debe encontrar conductores por estado activo")
    void findByActivo_DebeEncontrarConductores() {
        // Act
        Page<Conductor> conductoresActivos = conductorRepository.findByActivo(true, PageRequest.of(0, 10));
        Page<Conductor> conductoresInactivos = conductorRepository.findByActivo(false, PageRequest.of(0, 10));

        // Assert
        assertThat(conductoresActivos.getContent()).hasSize(1);
        assertThat(conductoresActivos.getContent().get(0).getNombre()).isEqualTo("Juan Pérez");
        
        assertThat(conductoresInactivos.getContent()).hasSize(1);
        assertThat(conductoresInactivos.getContent().get(0).getNombre()).isEqualTo("María López");
    }

    @Test
    @DisplayName("Debe encontrar conductores por nombre conteniendo")
    void findByActivoAndNombreContaining_DebeEncontrarConductores() {
        // Act
        Page<Conductor> conductores = conductorRepository.findByActivoAndNombreContaining(
                true, "Juan", PageRequest.of(0, 10));

        // Assert
        assertThat(conductores.getContent()).hasSize(1);
        assertThat(conductores.getContent().get(0).getNombre()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("Debe encontrar conductores sin vehículos")
    void findByVehiculosIsEmpty_DebeEncontrarConductores() {
        // Act
        List<Conductor> conductoresSinVehiculos = conductorRepository.findByVehiculosIsEmpty();

        // Assert
        assertThat(conductoresSinVehiculos).hasSize(1);
        assertThat(conductoresSinVehiculos.get(0).getNombre()).isEqualTo("María López");
    }

    @Test
    @DisplayName("Debe contar vehículos por conductor")
    void countVehiculosByConductor_DebeContarCorrectamente() {
        // Act
        List<ConteoVehiculosDTO> conteos = conductorRepository.countVehiculosByConductor();

        // Assert
        assertThat(conteos).hasSize(2); // Debe encontrar ambos conductores
        
        // Convertir la lista a un Map para facilitar las aserciones
        Map<String, ConteoVehiculosDTO> conteoPorNombre = conteos.stream()
            .collect(Collectors.toMap(ConteoVehiculosDTO::getNombreConductor, dto -> dto));
        
        // Verificar conductor con vehículos
        ConteoVehiculosDTO conteoJuan = conteoPorNombre.get("Juan Pérez");
        assertThat(conteoJuan).isNotNull();
        assertThat(conteoJuan.getCantidadVehiculos()).isEqualTo(2L);
        
        // Verificar conductor sin vehículos
        ConteoVehiculosDTO conteoMaria = conteoPorNombre.get("María López");
        assertThat(conteoMaria).isNotNull();
        assertThat(conteoMaria.getCantidadVehiculos()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Debe contar conductores activos")
    void countByActivoTrue_DebeContarCorrectamente() {
        // Act
        long cantidadActivos = conductorRepository.countByActivoTrue();

        // Assert
        assertThat(cantidadActivos).isEqualTo(1);
    }
} 