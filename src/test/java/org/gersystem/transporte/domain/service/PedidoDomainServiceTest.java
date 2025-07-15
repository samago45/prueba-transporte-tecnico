package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoDomainServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private ConductorDomainService conductorDomainService;

    @InjectMocks
    private PedidoDomainService pedidoDomainService;

    private Vehiculo vehiculo;
    private Conductor conductor;
    private Pedido pedido;

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
    }

    @Test
    @DisplayName("Crear pedido exitosamente cuando todos los datos son válidos")
    void crearPedido_ConDatosValidos_DebeCrearPedido() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(pedidoRepository.findByVehiculoAndEstadoIn(any(), any())).thenReturn(List.of());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        Pedido resultado = pedidoDomainService.crearPedido(pedido, 1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(resultado.getVehiculo()).isEqualTo(vehiculo);
        assertThat(resultado.getConductor()).isEqualTo(conductor);
    }

    @Test
    @DisplayName("Crear pedido debe fallar cuando el vehículo no existe")
    void crearPedido_CuandoVehiculoNoExiste_DebeLanzarExcepcion() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pedidoDomainService.crearPedido(pedido, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vehículo no encontrado");
    }

    @Test
    @DisplayName("Crear pedido debe fallar cuando el vehículo no está activo")
    void crearPedido_CuandoVehiculoNoActivo_DebeLanzarExcepcion() {
        // Arrange
        vehiculo.setActivo(false);
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act & Assert
        assertThatThrownBy(() -> pedidoDomainService.crearPedido(pedido, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El vehículo no está activo");
    }

    @Test
    @DisplayName("Crear pedido debe fallar cuando excede la capacidad del vehículo")
    void crearPedido_CuandoExcedeCapacidad_DebeLanzarExcepcion() {
        // Arrange
        pedido.setPeso(new BigDecimal("2000.00"));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(pedidoRepository.findByVehiculoAndEstadoIn(any(), any())).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> pedidoDomainService.crearPedido(pedido, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El vehículo no tiene capacidad suficiente");
    }

    @Test
    @DisplayName("Actualizar estado de pedido exitosamente")
    void actualizarEstadoPedido_ConEstadoValido_DebeActualizarEstado() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        Pedido resultado = pedidoDomainService.actualizarEstadoPedido(1L, EstadoPedido.EN_PROCESO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.EN_PROCESO);
    }

    @Test
    @DisplayName("Actualizar estado debe fallar para pedidos completados")
    void actualizarEstadoPedido_CuandoPedidoCompletado_DebeLanzarExcepcion() {
        // Arrange
        pedido.setEstado(EstadoPedido.ENTREGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        assertThatThrownBy(() -> pedidoDomainService.actualizarEstadoPedido(1L, EstadoPedido.EN_PROCESO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se puede cambiar el estado de un pedido completado o cancelado");
    }
} 