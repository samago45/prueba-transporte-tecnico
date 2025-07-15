package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.config.TestJpaConfig;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class VehiculoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    private Conductor conductor;
    private Vehiculo vehiculoLibre;
    private Vehiculo vehiculoAsignado;
    private Vehiculo vehiculoInactivo;

    @BeforeEach
    void setUp() {
        conductor = new Conductor();
        conductor.setNombre("Juan Perez");
        conductor.setLicencia("L12345");
        conductor.setActivo(true);
        entityManager.persist(conductor);

        vehiculoLibre = new Vehiculo();
        vehiculoLibre.setPlaca("ABC123"); // Formato correcto AAA999
        vehiculoLibre.setCapacidad(new BigDecimal("1200"));
        vehiculoLibre.setActivo(true);
        vehiculoLibre.setConductor(null);
        entityManager.persist(vehiculoLibre);

        vehiculoAsignado = new Vehiculo();
        vehiculoAsignado.setPlaca("XYZ789"); // Formato correcto AAA999
        vehiculoAsignado.setCapacidad(new BigDecimal("800"));
        vehiculoAsignado.setActivo(true);
        vehiculoAsignado.setConductor(conductor);
        entityManager.persist(vehiculoAsignado);

        vehiculoInactivo = new Vehiculo();
        vehiculoInactivo.setPlaca("DEF456"); // Formato correcto AAA999
        vehiculoInactivo.setCapacidad(new BigDecimal("1500"));
        vehiculoInactivo.setActivo(false);
        vehiculoInactivo.setConductor(null);
        entityManager.persist(vehiculoInactivo);

        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar solo vehículos libres")
    void findVehiculosLibres_DebeEncontrarSoloVehiculosLibres() {
        // Act
        List<Vehiculo> vehiculosLibres = vehiculoRepository.findVehiculosLibres();

        // Assert
        assertThat(vehiculosLibres).isNotNull();
        assertThat(vehiculosLibres).hasSize(1);
        assertThat(vehiculosLibres.get(0).getPlaca()).isEqualTo("ABC123");
        assertThat(vehiculosLibres.get(0).getConductor()).isNull();
        assertThat(vehiculosLibres.get(0).isActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe encontrar vehículo por placa")
    void findByPlaca_DebeEncontrarVehiculo() {
        // Act
        Optional<Vehiculo> vehiculo = vehiculoRepository.findByPlaca("ABC123");

        // Assert
        assertThat(vehiculo).isPresent();
        assertThat(vehiculo.get().getPlaca()).isEqualTo("ABC123");
        assertThat(vehiculo.get().isActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe encontrar vehículos por conductor")
    void findByConductor_DebeEncontrarVehiculos() {
        // Act
        List<Vehiculo> vehiculos = vehiculoRepository.findByConductor(conductor);

        // Assert
        assertThat(vehiculos).hasSize(1);
        assertThat(vehiculos.get(0).getPlaca()).isEqualTo("XYZ789");
        assertThat(vehiculos.get(0).getConductor().getId()).isEqualTo(conductor.getId());
    }

    @Test
    @DisplayName("Debe encontrar vehículos activos paginados")
    void findByActivo_DebeRetornarPaginado() {
        // Act
        Page<Vehiculo> vehiculosActivos = vehiculoRepository.findByActivo(true, PageRequest.of(0, 10));

        // Assert
        assertThat(vehiculosActivos.getContent()).hasSize(2);
        assertThat(vehiculosActivos.getContent())
                .extracting(Vehiculo::isActivo)
                .containsOnly(true);
    }

    @Test
    @DisplayName("Debe encontrar vehículos por capacidad mayor que")
    void findByCapacidadGreaterThanEqual_DebeEncontrarVehiculos() {
        // Act
        List<Vehiculo> vehiculosGranCapacidad = vehiculoRepository.findByCapacidadGreaterThanEqualAndActivo(
                new BigDecimal("1000"), true);

        // Assert
        assertThat(vehiculosGranCapacidad).hasSize(1);
        assertThat(vehiculosGranCapacidad.get(0).getPlaca()).isEqualTo("ABC123");
        assertThat(vehiculosGranCapacidad.get(0).getCapacidad())
                .isGreaterThanOrEqualTo(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("Debe verificar existencia por placa")
    void existsByPlaca_DebeVerificarExistencia() {
        // Act
        boolean existe = vehiculoRepository.existsByPlaca("ABC123");
        boolean noExiste = vehiculoRepository.existsByPlaca("XXX999");

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
} 