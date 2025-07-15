package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.domain.service.VehiculoDomainService;
import org.gersystem.transporte.infrastructure.adapters.repository.VehiculoSpecification;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateVehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.VehiculoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.VehiculoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private CreateVehiculoDTO createVehiculoDTO;
    private VehiculoDTO vehiculoDTO;

    @BeforeEach
    void setUp() {
        createVehiculoDTO = new CreateVehiculoDTO();
        createVehiculoDTO.setPlaca("ABC-123");
        createVehiculoDTO.setCapacidad(new BigDecimal("1000.50"));

        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC-123");
        vehiculo.setCapacidad(new BigDecimal("1000.50"));

        vehiculoDTO = new VehiculoDTO();
        vehiculoDTO.setId(1L);
        vehiculoDTO.setPlaca("ABC-123");
        vehiculoDTO.setCapacidad(new BigDecimal("1000.50"));
        vehiculoDTO.setActivo(true);
    }

    @Test
    void debeCrearVehiculoExitosamente() {
        // Arrange
        when(vehiculoMapper.toEntity(any(CreateVehiculoDTO.class))).thenReturn(vehiculo);
        doNothing().when(vehiculoDomainService).validarPlaca(any(Vehiculo.class));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenAnswer(invocation -> {
            Vehiculo v = invocation.getArgument(0);
            v.setId(1L); // Simula la generación de ID por la BD
            v.setActivo(true);
            return v;
        });
        when(vehiculoMapper.toDto(any(Vehiculo.class))).thenReturn(vehiculoDTO);

        // Act
        VehiculoDTO resultado = vehiculoApplicationService.crearVehiculo(createVehiculoDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPlaca()).isEqualTo("ABC-123");

        verify(vehiculoDomainService, times(1)).validarPlaca(vehiculo);
        verify(vehiculoRepository, times(1)).save(vehiculo);
        verify(vehiculoMapper, times(1)).toEntity(createVehiculoDTO);
        verify(vehiculoMapper, times(1)).toDto(vehiculo);
    }
} 