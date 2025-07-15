package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.service.PedidoDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.PedidoMapper;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoApplicationServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoDomainService pedidoDomainService;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoApplicationService pedidoApplicationService;

    private Pedido pedido;
    private PedidoDTO pedidoDTO;
    private CreatePedidoDTO createPedidoDTO;
    private Conductor conductor;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        conductor = new Conductor();
        conductor.setId(1L);
        conductor.setNombre("Juan Pérez");
        conductor.setActivo(true);

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo.setConductor(conductor);

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setDescripcion("Pedido de prueba");
        pedido.setPeso(new BigDecimal("500.00"));
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setVehiculo(vehiculo);
        pedido.setConductor(conductor);

        createPedidoDTO = new CreatePedidoDTO();
        createPedidoDTO.setDescripcion("Pedido de prueba");
        createPedidoDTO.setPeso(new BigDecimal("500.00"));

        pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setDescripcion("Pedido de prueba");
        pedidoDTO.setPeso(new BigDecimal("500.00"));
        pedidoDTO.setEstado(EstadoPedido.PENDIENTE);
    }

    @Test
    @DisplayName("Debe crear un pedido exitosamente")
    void crearPedido_DebeCrearExitosamente() {
        // Arrange
        when(pedidoMapper.toEntity(any(CreatePedidoDTO.class))).thenReturn(pedido);
        when(pedidoDomainService.crearPedido(any(Pedido.class), any(Long.class))).thenReturn(pedido);
        when(pedidoMapper.toDto(any(Pedido.class))).thenReturn(pedidoDTO);

        // Act
        PedidoDTO resultado = pedidoApplicationService.crearPedido(createPedidoDTO, 1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getDescripcion()).isEqualTo("Pedido de prueba");
        verify(pedidoDomainService).crearPedido(any(Pedido.class), eq(1L));
    }

    @Test
    @DisplayName("Debe actualizar el estado de un pedido exitosamente")
    void actualizarEstado_DebeActualizarExitosamente() {
        // Arrange
        pedido.setEstado(EstadoPedido.EN_PROCESO);
        pedidoDTO.setEstado(EstadoPedido.EN_PROCESO);
        when(pedidoDomainService.actualizarEstadoPedido(1L, EstadoPedido.EN_PROCESO)).thenReturn(pedido);
        when(pedidoMapper.toDto(pedido)).thenReturn(pedidoDTO);

        // Act
        PedidoDTO resultado = pedidoApplicationService.actualizarEstado(1L, EstadoPedido.EN_PROCESO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.EN_PROCESO);
        verify(pedidoDomainService).actualizarEstadoPedido(1L, EstadoPedido.EN_PROCESO);
    }

    @Test
    @DisplayName("Debe obtener un pedido por ID exitosamente")
    void obtenerPedido_DebeObtenerExitosamente() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toDto(pedido)).thenReturn(pedidoDTO);

        // Act
        PedidoDTO resultado = pedidoApplicationService.obtenerPedido(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(pedidoRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando el pedido no existe")
    void obtenerPedido_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pedidoApplicationService.obtenerPedido(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Pedido no encontrado");
    }

    @Test
    @DisplayName("Debe listar pedidos paginados por estado")
    void listarPedidos_DebeListarPorEstado() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> pedidosPage = new PageImpl<>(List.of(pedido));
        Page<PedidoDTO> expectedPage = new PageImpl<>(List.of(pedidoDTO));

        when(pedidoRepository.findByEstado(EstadoPedido.PENDIENTE, pageable)).thenReturn(pedidosPage);
        when(pedidoMapper.toDto(pedido)).thenReturn(pedidoDTO);

        // Act
        Page<PedidoDTO> resultado = pedidoApplicationService.listarPedidos(EstadoPedido.PENDIENTE, null, pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        verify(pedidoRepository).findByEstado(EstadoPedido.PENDIENTE, pageable);
    }

    @Test
    @DisplayName("Debe listar pedidos paginados por conductor")
    void listarPedidos_DebeListarPorConductor() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> pedidosPage = new PageImpl<>(List.of(pedido));
        Page<PedidoDTO> expectedPage = new PageImpl<>(List.of(pedidoDTO));

        when(pedidoRepository.findByConductorId(1L, pageable)).thenReturn(pedidosPage);
        when(pedidoMapper.toDto(pedido)).thenReturn(pedidoDTO);

        // Act
        Page<PedidoDTO> resultado = pedidoApplicationService.listarPedidos(null, 1L, pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(pedidoRepository).findByConductorId(1L, pageable);
    }
} 