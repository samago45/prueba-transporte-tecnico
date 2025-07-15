package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.service.ConductorDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.UpdateConductorDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.ConductorMapper;
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
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConductorApplicationServiceTest {

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private ConductorDomainService conductorDomainService;

    @Mock
    private ConductorMapper conductorMapper;

    @InjectMocks
    private ConductorApplicationService conductorApplicationService;

    private Conductor conductor;
    private ConductorDTO conductorDTO;
    private CreateConductorDTO createConductorDTO;
    private UpdateConductorDTO updateConductorDTO;

    @BeforeEach
    void setUp() {
        conductor = new Conductor();
        conductor.setId(1L);
        conductor.setNombre("Juan Pérez");
        conductor.setLicencia("A12345");
        conductor.setActivo(true);

        conductorDTO = new ConductorDTO();
        conductorDTO.setId(1L);
        conductorDTO.setNombre("Juan Pérez");

        conductorDTO.setActivo(true);

        createConductorDTO = new CreateConductorDTO();
        createConductorDTO.setNombre("Juan Pérez");
 

        updateConductorDTO = new UpdateConductorDTO();
        updateConductorDTO.setNombre("Juan Pérez Actualizado");
        updateConductorDTO.setLicencia("B67890");
    }

    @Test
    @DisplayName("Debe crear un conductor exitosamente")
    void crearConductor_DebeCrearExitosamente() {
        // Arrange
        when(conductorMapper.toEntity(any(CreateConductorDTO.class))).thenReturn(conductor);
        when(conductorDomainService.crearConductor(any(Conductor.class))).thenReturn(conductor);
        when(conductorMapper.toDto(any(Conductor.class))).thenReturn(conductorDTO);

        // Act
        ConductorDTO resultado = conductorApplicationService.crearConductor(createConductorDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan Pérez");
        assertThat(resultado.getLicencia()).isEqualTo("A12345");
        verify(conductorDomainService).crearConductor(any(Conductor.class));
    }

    @Test
    @DisplayName("Debe validar formato de licencia al crear conductor")
    void crearConductor_DebeValidarFormatoLicencia() {
        // Arrange
        createConductorDTO.setLicencia("123"); // Formato inválido

        // Act & Assert
        assertThatThrownBy(() -> conductorApplicationService.crearConductor(createConductorDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("formato de licencia");
    }

    @Test
    @DisplayName("Debe actualizar un conductor exitosamente")
    void actualizarConductor_DebeActualizarExitosamente() {
        // Arrange
        Conductor conductorActualizado = new Conductor();
        conductorActualizado.setId(1L);
        conductorActualizado.setNombre("Juan Pérez Actualizado");
        conductorActualizado.setLicencia("B67890");
        conductorActualizado.setActivo(true);

        when(conductorDomainService.actualizarConductor(eq(1L), any(Conductor.class)))
                .thenReturn(conductorActualizado);
        when(conductorMapper.toDto(conductorActualizado))
                .thenReturn(conductorDTO);

        // Act
        ConductorDTO resultado = conductorApplicationService.actualizarConductor(1L, updateConductorDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(conductorDomainService).actualizarConductor(eq(1L), any(Conductor.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar conductor inexistente")
    void actualizarConductor_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        when(conductorDomainService.actualizarConductor(eq(999L), any(Conductor.class)))
                .thenThrow(new EntityNotFoundException("Conductor no encontrado"));

        // Act & Assert
        assertThatThrownBy(() -> conductorApplicationService.actualizarConductor(999L, updateConductorDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Conductor no encontrado");
    }

    @Test
    @DisplayName("Debe activar un conductor exitosamente")
    void activarConductor_DebeActivarExitosamente() {
        // Arrange
        conductor.setActivo(true);
        when(conductorDomainService.activarConductor(1L)).thenReturn(conductor);
        when(conductorMapper.toDto(conductor)).thenReturn(conductorDTO);

        // Act
        ConductorDTO resultado = conductorApplicationService.activarConductor(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isActivo()).isTrue();
        verify(conductorDomainService).activarConductor(1L);
    }

    @Test
    @DisplayName("Debe desactivar un conductor exitosamente")
    void desactivarConductor_DebeDesactivarExitosamente() {
        // Arrange
        conductor.setActivo(false);
        conductorDTO.setActivo(false);
        when(conductorDomainService.desactivarConductor(1L)).thenReturn(conductor);
        when(conductorMapper.toDto(conductor)).thenReturn(conductorDTO);

        // Act
        ConductorDTO resultado = conductorApplicationService.desactivarConductor(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.isActivo()).isFalse();
        verify(conductorDomainService).desactivarConductor(1L);
    }

    @Test
    @DisplayName("Debe listar conductores paginados")
    void listarConductores_DebeListarPaginado() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Conductor> conductoresPage = new PageImpl<>(List.of(conductor));
        when(conductorRepository.findAll(pageable)).thenReturn(conductoresPage);
        when(conductorMapper.toDto(conductor)).thenReturn(conductorDTO);

        // Act
        Page<ConductorDTO> resultado = conductorApplicationService.listarConductores(null, pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(conductorRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Debe filtrar conductores activos")
    void listarConductores_DebeFiltrarActivos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Conductor> conductoresPage = new PageImpl<>(List.of(conductor));
        when(conductorRepository.findByActivo(true, pageable)).thenReturn(conductoresPage);
        when(conductorMapper.toDto(conductor)).thenReturn(conductorDTO);

        // Act
        Page<ConductorDTO> resultado = conductorApplicationService.listarConductores(true, pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).isActivo()).isTrue();
        verify(conductorRepository).findByActivo(true, pageable);
    }
} 