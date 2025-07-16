package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.config.BaseRepositoryTest;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para ConductorRepository.
 * 
 * Estas pruebas verifican la funcionalidad completa del repositorio
 * incluyendo operaciones CRUD, consultas personalizadas y especificaciones.
 */
class ConductorRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    private Conductor conductor1;
    private Conductor conductor2;
    private Conductor conductor3;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos
        vehiculoRepository.deleteAll();
        conductorRepository.deleteAll();

        // Crear conductores de prueba
        conductor1 = new Conductor();
        conductor1.setNombre("Juan Pérez");
        conductor1.setLicencia("A12345");
        conductor1.setActivo(true);
        conductor1 = conductorRepository.save(conductor1);

        conductor2 = new Conductor();
        conductor2.setNombre("María García");
        conductor2.setLicencia("B67890");
        conductor2.setActivo(true);
        conductor2 = conductorRepository.save(conductor2);

        conductor3 = new Conductor();
        conductor3.setNombre("Carlos López");
        conductor3.setLicencia("C11111");
        conductor3.setActivo(false);
        conductor3 = conductorRepository.save(conductor3);
    }

    @Test
    @DisplayName("Debe guardar un conductor exitosamente")
    void save_DebeGuardarConductorExitosamente() {
        // Arrange
        Conductor nuevoConductor = new Conductor();
        nuevoConductor.setNombre("Nuevo Conductor");
        nuevoConductor.setLicencia("D99999");
        nuevoConductor.setActivo(true);

        // Act
        Conductor conductorGuardado = conductorRepository.save(nuevoConductor);

        // Assert
        assertThat(conductorGuardado).isNotNull();
        assertThat(conductorGuardado.getId()).isNotNull();
        assertThat(conductorGuardado.getNombre()).isEqualTo("Nuevo Conductor");
        assertThat(conductorGuardado.getLicencia()).isEqualTo("D99999");
        assertThat(conductorGuardado.isActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe encontrar un conductor por ID")
    void findById_DebeEncontrarConductor() {
        // Act
        Optional<Conductor> conductorEncontrado = conductorRepository.findById(conductor1.getId());

        // Assert
        assertThat(conductorEncontrado).isPresent();
        assertThat(conductorEncontrado.get().getNombre()).isEqualTo("Juan Pérez");
        assertThat(conductorEncontrado.get().getLicencia()).isEqualTo("A12345");
    }

    @Test
    @DisplayName("Debe retornar empty cuando no encuentra conductor por ID")
    void findById_DebeRetornarEmpty_CuandoNoExiste() {
        // Act
        Optional<Conductor> conductorEncontrado = conductorRepository.findById(999L);

        // Assert
        assertThat(conductorEncontrado).isEmpty();
    }

    @Test
    @DisplayName("Debe encontrar todos los conductores")
    void findAll_DebeEncontrarTodosLosConductores() {
        // Act
        List<Conductor> conductores = conductorRepository.findAll();

        // Assert
        assertThat(conductores).hasSize(3);
        assertThat(conductores).extracting("nombre")
                .containsExactlyInAnyOrder("Juan Pérez", "María García", "Carlos López");
    }

    @Test
    @DisplayName("Debe encontrar conductores paginados")
    void findAll_DebeEncontrarConductoresPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);

        // Act
        Page<Conductor> conductoresPage = conductorRepository.findAll(pageable);

        // Assert
        assertThat(conductoresPage.getContent()).hasSize(2);
        assertThat(conductoresPage.getTotalElements()).isEqualTo(3);
        assertThat(conductoresPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe encontrar conductores activos paginados")
    void findByActivo_DebeEncontrarConductoresActivosPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Conductor> conductoresActivos = conductorRepository.findByActivo(true, pageable);

        // Assert
        assertThat(conductoresActivos.getContent()).hasSize(2);
        assertThat(conductoresActivos.getContent()).extracting("nombre")
                .containsExactlyInAnyOrder("Juan Pérez", "María García");
        assertThat(conductoresActivos.getContent()).allMatch(Conductor::isActivo);
    }

    @Test
    @DisplayName("Debe encontrar conductores por nombre y estado")
    void findByActivoAndNombreContaining_DebeEncontrarConductores() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Conductor> conductores = conductorRepository.findByActivoAndNombreContaining(true, "Juan", pageable);

        // Assert
        assertThat(conductores.getContent()).hasSize(1);
        assertThat(conductores.getContent().get(0).getNombre()).isEqualTo("Juan Pérez");
        assertThat(conductores.getContent().get(0).isActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe encontrar conductores sin vehículos")
    void findByVehiculosIsEmpty_DebeEncontrarConductores() {
        // Crear un vehículo para conductor1
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo.setConductor(conductor1);
        vehiculoRepository.save(vehiculo);

        // Act
        List<Conductor> conductoresSinVehiculos = conductorRepository.findByVehiculosIsEmpty();

        // Assert
        assertThat(conductoresSinVehiculos).hasSize(2);
        assertThat(conductoresSinVehiculos).extracting("nombre")
                .containsExactlyInAnyOrder("María García", "Carlos López");
    }

    @Test
    @DisplayName("Debe actualizar un conductor exitosamente")
    void save_DebeActualizarConductorExitosamente() {
        // Arrange
        conductor1.setNombre("Juan Pérez Actualizado");
        conductor1.setLicencia("A12345_NUEVA");

        // Act
        Conductor conductorActualizado = conductorRepository.save(conductor1);

        // Assert
        assertThat(conductorActualizado.getNombre()).isEqualTo("Juan Pérez Actualizado");
        assertThat(conductorActualizado.getLicencia()).isEqualTo("A12345_NUEVA");

        // Verificar que se guardó en la base de datos
        Optional<Conductor> conductorGuardado = conductorRepository.findById(conductor1.getId());
        assertThat(conductorGuardado).isPresent();
        assertThat(conductorGuardado.get().getNombre()).isEqualTo("Juan Pérez Actualizado");
    }

    @Test
    @DisplayName("Debe eliminar un conductor exitosamente")
    void delete_DebeEliminarConductorExitosamente() {
        // Act
        conductorRepository.delete(conductor1);

        // Assert
        assertThat(conductorRepository.findById(conductor1.getId())).isEmpty();
        assertThat(conductorRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Debe eliminar conductor por ID exitosamente")
    void deleteById_DebeEliminarConductorExitosamente() {
        // Act
        conductorRepository.deleteById(conductor1.getId());

        // Assert
        assertThat(conductorRepository.findById(conductor1.getId())).isEmpty();
        assertThat(conductorRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Debe contar conductores activos")
    void countByActivoTrue_DebeContarConductoresActivos() {
        // Act
        long count = conductorRepository.countByActivoTrue();

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe contar vehículos por conductor")
    void countVehiculosByConductor_DebeContarVehiculos() {
        // Crear un vehículo para conductor1
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo.setConductor(conductor1);
        vehiculoRepository.save(vehiculo);

        // Act
        var conteoVehiculos = conductorRepository.countVehiculosByConductor();

        // Assert
        assertThat(conteoVehiculos).hasSize(3);
        assertThat(conteoVehiculos).anyMatch(cv -> 
            cv.getConductorId().equals(conductor1.getId()) && cv.getCantidadVehiculos() == 1);
        assertThat(conteoVehiculos).anyMatch(cv -> 
            cv.getConductorId().equals(conductor2.getId()) && cv.getCantidadVehiculos() == 0);
    }
} 
