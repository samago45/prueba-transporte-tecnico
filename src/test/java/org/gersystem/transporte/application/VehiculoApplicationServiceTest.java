package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.domain.service.VehiculoDomainService;
import org.gersystem.transporte.infrastructure.adapters.repository.VehiculoSpecification;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PageDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.UpdateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.VehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ResourceNotFoundException;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.VehiculoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoApplicationServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private VehiculoDomainService vehiculoDomainService;

    @Mock
    private VehiculoMapper vehiculoMapper;

    @Mock
    private VehiculoSpecification vehiculoSpecification;

    @InjectMocks
    private VehiculoApplicationService vehiculoApplicationService;

    private Vehiculo vehiculo;
    private VehiculoDTO vehiculoDTO;
    private CreateVehiculoDTO createVehiculoDTO;
    private UpdateVehiculoDTO updateVehiculoDTO;

    @BeforeEach
    void setUp() {
        // Configuración de datos de prueba
        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);

        vehiculoDTO = new VehiculoDTO();
        vehiculoDTO.setId(1L);
        vehiculoDTO.setPlaca("ABC123");
        vehiculoDTO.setCapacidad(new BigDecimal("1000.00"));
        vehiculoDTO.setActivo(true);

        createVehiculoDTO = new CreateVehiculoDTO();
        createVehiculoDTO.setPlaca("ABC123");
        createVehiculoDTO.setCapacidad(new BigDecimal("1000.00"));

        updateVehiculoDTO = new UpdateVehiculoDTO();
        updateVehiculoDTO.setPlaca("XYZ789");
        updateVehiculoDTO.setCapacidad(new BigDecimal("2000.00"));
        updateVehiculoDTO.setActivo(true);
    }

    @Test
    @DisplayName("Debe crear un vehículo exitosamente")
    void crearVehiculo_DebeCrearExitosamente() {
        // Arrange
        when(vehiculoMapper.toEntity(any(CreateVehiculoDTO.class))).thenReturn(vehiculo);
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);
        when(vehiculoMapper.toDto(any(Vehiculo.class))).thenReturn(vehiculoDTO);
        doNothing().when(vehiculoDomainService).validarPlaca(any(Vehiculo.class));
        doNothing().when(vehiculoDomainService).validarCapacidad(any(Vehiculo.class));

        // Act
        VehiculoDTO resultado = vehiculoApplicationService.crearVehiculo(createVehiculoDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPlaca()).isEqualTo("ABC123");
        assertThat(resultado.getCapacidad()).isEqualByComparingTo(new BigDecimal("1000.00"));
        verify(vehiculoDomainService).validarPlaca(any(Vehiculo.class));
        verify(vehiculoDomainService).validarCapacidad(any(Vehiculo.class));
        verify(vehiculoRepository).save(any(Vehiculo.class));
    }

    @Test
    @DisplayName("Debe actualizar un vehículo exitosamente")
    void actualizarVehiculo_DebeActualizarExitosamente() {
        // Arrange
        Vehiculo vehiculoActualizado = new Vehiculo();
        vehiculoActualizado.setId(1L);
        vehiculoActualizado.setPlaca("XYZ789");
        vehiculoActualizado.setCapacidad(new BigDecimal("2000.00"));
        vehiculoActualizado.setActivo(true);

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculoActualizado);
        when(vehiculoMapper.toDto(any(Vehiculo.class))).thenReturn(vehiculoDTO);
        doNothing().when(vehiculoDomainService).validarPlaca(any(Vehiculo.class));
        doNothing().when(vehiculoDomainService).validarCapacidad(any(Vehiculo.class));

        // Act
        VehiculoDTO resultado = vehiculoApplicationService.actualizarVehiculo(1L, updateVehiculoDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(vehiculoDomainService).validarPlaca(any(Vehiculo.class));
        verify(vehiculoDomainService).validarCapacidad(any(Vehiculo.class));
        verify(vehiculoRepository).save(any(Vehiculo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar vehículo inexistente")
    void actualizarVehiculo_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehiculoApplicationService.actualizarVehiculo(999L, updateVehiculoDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehículo no encontrado");
    }

    @Test
    @DisplayName("Debe obtener un vehículo por ID exitosamente")
    void obtenerVehiculoPorId_DebeObtenerExitosamente() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoMapper.toDto(vehiculo)).thenReturn(vehiculoDTO);

        // Act
        VehiculoDTO resultado = vehiculoApplicationService.obtenerVehiculoPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPlaca()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener vehículo inexistente")
    void obtenerVehiculoPorId_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehiculoApplicationService.obtenerVehiculoPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehículo no encontrado");
    }

    @Test
    @DisplayName("Debe listar vehículos paginados")
    void obtenerTodosLosVehiculos_DebeListarPaginado() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehiculo> vehiculosPage = new PageImpl<>(List.of(vehiculo));
        
        when(vehiculoSpecification.placaContains(any())).thenReturn((root, query, cb) -> cb.conjunction());
        when(vehiculoSpecification.esActivo(any())).thenReturn((root, query, cb) -> cb.conjunction());
        when(vehiculoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(vehiculosPage);
        when(vehiculoMapper.toDto(any(Vehiculo.class))).thenReturn(vehiculoDTO);

        // Act
        PageDTO<VehiculoDTO> resultado = vehiculoApplicationService.obtenerTodosLosVehiculos(null, true, pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(vehiculoRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Debe obtener vehículos libres exitosamente")
    void obtenerVehiculosLibres_DebeObtenerExitosamente() {
        // Arrange
        List<Vehiculo> vehiculosLibres = Arrays.asList(vehiculo);
        when(vehiculoRepository.findVehiculosLibres()).thenReturn(vehiculosLibres);
        when(vehiculoMapper.toDto(any(Vehiculo.class))).thenReturn(vehiculoDTO);

        // Act
        List<VehiculoDTO> resultado = vehiculoApplicationService.obtenerVehiculosLibres();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        verify(vehiculoRepository).findVehiculosLibres();
    }

    @Test
    @DisplayName("Debe eliminar un vehículo lógicamente")
    void eliminarVehiculo_DebeEliminarLogicamente() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        // Act
        vehiculoApplicationService.eliminarVehiculo(1L);

        // Assert
        verify(vehiculoRepository).findById(1L);
        verify(vehiculoRepository).save(any(Vehiculo.class));
        assertThat(vehiculo.isActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar vehículo inexistente")
    void eliminarVehiculo_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehiculoApplicationService.eliminarVehiculo(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehículo no encontrado");
    }
} 
