package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.service.PedidoDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
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

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    }

    @Test
    @DisplayName("Debe crear un pedido exitosamente")
    void crearPedido_DebeCrearExitosamente() {
        // Arrange
        when(pedidoDomainService.crearPedido(any(Pedido.class), eq(1L))).thenReturn(pedido);

        // Act
        Pedido resultado = pedidoApplicationService.crearPedido(pedido, 1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getDescripcion()).isEqualTo("Pedido de prueba");
        verify(pedidoDomainService).crearPedido(any(Pedido.class), eq(1L));
    }

    @Test
    @DisplayName("Debe actualizar el estado de un pedido exitosamente")
    void actualizarEstadoPedido_DebeActualizarExitosamente() {
        // Arrange
        pedido.setEstado(EstadoPedido.EN_PROCESO);
        when(pedidoDomainService.actualizarEstadoPedido(1L, EstadoPedido.EN_PROCESO)).thenReturn(pedido);

        // Act
        Pedido resultado = pedidoApplicationService.actualizarEstadoPedido(1L, EstadoPedido.EN_PROCESO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.EN_PROCESO);
        verify(pedidoDomainService).actualizarEstadoPedido(1L, EstadoPedido.EN_PROCESO);
    }

    @Test
    @DisplayName("Debe obtener un pedido por ID exitosamente")
    void obtenerPedido_DebeObtenerExitosamente() {
        // Arrange
        when(pedidoDomainService.obtenerPedido(1L)).thenReturn(pedido);

        // Act
        Pedido resultado = pedidoApplicationService.obtenerPedido(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(pedidoDomainService).obtenerPedido(1L);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando el pedido no existe")
    void obtenerPedido_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        when(pedidoDomainService.obtenerPedido(1L)).thenThrow(new EntityNotFoundException("Pedido no encontrado"));

        // Act & Assert
        assertThatThrownBy(() -> pedidoApplicationService.obtenerPedido(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Pedido no encontrado");
    }

    @Test
    @DisplayName("Debe buscar pedidos con filtros")
    void buscarPedidos_DebeListarConFiltros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> pedidosPage = new PageImpl<>(List.of(pedido));
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fechaFin = LocalDateTime.now();

        when(pedidoDomainService.buscarPedidos(
            EstadoPedido.PENDIENTE, 1L, null, fechaInicio, fechaFin, pageable)
        ).thenReturn(pedidosPage);

        // Act
        Page<Pedido> resultado = pedidoApplicationService.buscarPedidos(
            EstadoPedido.PENDIENTE, 1L, null, fechaInicio, fechaFin, pageable
        );

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        verify(pedidoDomainService).buscarPedidos(
            EstadoPedido.PENDIENTE, 1L, null, fechaInicio, fechaFin, pageable
        );
    }
} 
