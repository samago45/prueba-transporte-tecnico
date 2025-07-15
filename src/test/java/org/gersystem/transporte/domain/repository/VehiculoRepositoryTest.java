package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan("org.gersystem.transporte.domain.model")
@Import(JpaRepositoriesAutoConfiguration.class)
class VehiculoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    private Conductor conductor;

    @BeforeEach
    void setUp() {
        conductor = new Conductor();
        conductor.setNombre("Juan Perez");
        entityManager.persist(conductor);

        Vehiculo vehiculoLibre = new Vehiculo();
        vehiculoLibre.setPlaca("LIB-001");
        vehiculoLibre.setCapacidad(new BigDecimal("1200"));
        vehiculoLibre.setConductor(null);
        entityManager.persist(vehiculoLibre);

        Vehiculo vehiculoAsignado = new Vehiculo();
        vehiculoAsignado.setPlaca("ASG-002");
        vehiculoAsignado.setCapacidad(new BigDecimal("800"));
        vehiculoAsignado.setConductor(conductor);
        entityManager.persist(vehiculoAsignado);

        Vehiculo vehiculoInactivo = new Vehiculo();
        vehiculoInactivo.setPlaca("INC-003");
        vehiculoInactivo.setCapacidad(new BigDecimal("1500"));
        vehiculoInactivo.setActivo(false);
        entityManager.persist(vehiculoInactivo);
    }

    @Test
    void debeEncontrarSoloVehiculosLibres() {
        // Act
        List<Vehiculo> vehiculosLibres = vehiculoRepository.findVehiculosLibres();

        // Assert
        assertThat(vehiculosLibres).isNotNull();
        assertThat(vehiculosLibres).hasSize(1);
        assertThat(vehiculosLibres.get(0).getPlaca()).isEqualTo("LIB-001");
        assertThat(vehiculosLibres.get(0).getConductor()).isNull();
        assertThat(vehiculosLibres.get(0).isActivo()).isTrue();
    }
} 