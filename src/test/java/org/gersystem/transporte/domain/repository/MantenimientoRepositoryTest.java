package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.config.TestJpaConfig;
import org.gersystem.transporte.domain.model.*;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class MantenimientoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    private Vehiculo vehiculo;
    private Mantenimiento mantenimiento1;
    private Mantenimiento mantenimiento2;

    @BeforeEach
    void setUp() {
        // Crear y persistir un vehículo
        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        entityManager.persist(vehiculo);

        // Crear y persistir mantenimientos
        mantenimiento1 = new Mantenimiento();
        mantenimiento1.setDescripcion("Mantenimiento preventivo");
        mantenimiento1.setTipo(TipoMantenimiento.PREVENTIVO);
        mantenimiento1.setEstado(EstadoMantenimiento.PROGRAMADO);
        mantenimiento1.setFechaProgramada(LocalDateTime.now().plusDays(1));
        mantenimiento1.setVehiculo(vehiculo);
        entityManager.persist(mantenimiento1);

        mantenimiento2 = new Mantenimiento();
        mantenimiento2.setDescripcion("Mantenimiento correctivo");
        mantenimiento2.setTipo(TipoMantenimiento.CORRECTIVO);
        mantenimiento2.setEstado(EstadoMantenimiento.EN_PROCESO);
        mantenimiento2.setFechaProgramada(LocalDateTime.now().plusDays(2));
        mantenimiento2.setVehiculo(vehiculo);
        entityManager.persist(mantenimiento2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar mantenimientos por vehículo y estado")
    void findByVehiculoAndEstado_DebeEncontrarMantenimientos() {
        // Act
        List<Mantenimiento> mantenimientos = mantenimientoRepository
                .findByVehiculoAndEstado(vehiculo, EstadoMantenimiento.PROGRAMADO);

        // Assert
        assertThat(mantenimientos).hasSize(1);
        assertThat(mantenimientos.get(0).getDescripcion()).isEqualTo("Mantenimiento preventivo");
        assertThat(mantenimientos.get(0).getEstado()).isEqualTo(EstadoMantenimiento.PROGRAMADO);
    }

    @Test
    @DisplayName("Debe contar mantenimientos por estado")
    void countByEstado_DebeContarCorrectamente() {
        // Act
        long cantidadProgramados = mantenimientoRepository.countByEstado(EstadoMantenimiento.PROGRAMADO);
        long cantidadEnProceso = mantenimientoRepository.countByEstado(EstadoMantenimiento.EN_PROCESO);

        // Assert
        assertThat(cantidadProgramados).isEqualTo(1);
        assertThat(cantidadEnProceso).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe encontrar mantenimientos paginados por vehículo")
    void findByVehiculo_DebeRetornarPaginado() {
        // Act
        Page<Mantenimiento> mantenimientosPaginados = mantenimientoRepository
                .findByVehiculo(vehiculo, PageRequest.of(0, 10));

        // Assert
        assertThat(mantenimientosPaginados.getContent()).hasSize(2);
        assertThat(mantenimientosPaginados.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe encontrar mantenimientos por tipo")
    void findByTipo_DebeEncontrarMantenimientos() {
        // Act
        List<Mantenimiento> mantenimientosPreventivos = mantenimientoRepository
                .findByTipo(TipoMantenimiento.PREVENTIVO);

        // Assert
        assertThat(mantenimientosPreventivos).hasSize(1);
        assertThat(mantenimientosPreventivos.get(0).getTipo()).isEqualTo(TipoMantenimiento.PREVENTIVO);
    }

    @Test
    @DisplayName("Debe encontrar mantenimientos programados para fecha")
    void findByFechaProgramadaBetween_DebeEncontrarMantenimientos() {
        // Act
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = LocalDateTime.now().plusDays(3);
        List<Mantenimiento> mantenimientosProgramados = mantenimientoRepository
                .findByFechaProgramadaBetween(inicio, fin);

        // Assert
        assertThat(mantenimientosProgramados).hasSize(2);
    }

    @Test
    @DisplayName("Debe encontrar mantenimientos por vehículo y tipo")
    void findByVehiculoAndTipo_DebeEncontrarMantenimientos() {
        // Act
        List<Mantenimiento> mantenimientos = mantenimientoRepository
                .findByVehiculoAndTipo(vehiculo, TipoMantenimiento.PREVENTIVO);

        // Assert
        assertThat(mantenimientos).hasSize(1);
        assertThat(mantenimientos.get(0).getTipo()).isEqualTo(TipoMantenimiento.PREVENTIVO);
        assertThat(mantenimientos.get(0).getVehiculo().getId()).isEqualTo(vehiculo.getId());
    }

    @Test
    @DisplayName("Debe encontrar último mantenimiento por vehículo")
    void findFirstByVehiculoOrderByFechaProgramadaDesc_DebeEncontrarUltimo() {
        // Act
        Mantenimiento ultimoMantenimiento = mantenimientoRepository
                .findFirstByVehiculoOrderByFechaProgramadaDesc(vehiculo);

        // Assert
        assertThat(ultimoMantenimiento).isNotNull();
        assertThat(ultimoMantenimiento.getFechaProgramada())
                .isAfterOrEqualTo(mantenimiento1.getFechaProgramada());
    }
} 