package org.gersystem.transporte.infrastructure.adapters.repository;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.gersystem.transporte.config.TestJpaConfig;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestJpaConfig.class)
class PedidoRepositoryComponentTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ConductorRepository conductorRepository;

    private Conductor conductor;
    private Vehiculo vehiculo;
    private Pedido pedido1;
    private Pedido pedido2;

    @BeforeEach
    void setUp() {
        // Limpiar datos anteriores
        pedidoRepository.deleteAll();
        vehiculoRepository.deleteAll();
        conductorRepository.deleteAll();

        // Crear conductor
        conductor = new Conductor();
        conductor.setNombre("Juan Pérez");
        conductor.setLicencia("ABC123456");
        conductor.setActivo(true);
        conductor = conductorRepository.save(conductor);

        // Crear vehículo
        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo.setConductor(conductor);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Crear pedidos
        pedido1 = new Pedido();
        pedido1.setDescripcion("Pedido 1");
        pedido1.setPeso(new BigDecimal("500.00"));
        pedido1.setEstado(EstadoPedido.PENDIENTE);
        pedido1.setVehiculo(vehiculo);
        pedido1.setConductor(conductor);
        pedido1 = pedidoRepository.save(pedido1);

        pedido2 = new Pedido();
        pedido2.setDescripcion("Pedido 2");
        pedido2.setPeso(new BigDecimal("300.00"));
        pedido2.setEstado(EstadoPedido.EN_PROCESO);
        pedido2.setVehiculo(vehiculo);
        pedido2.setConductor(conductor);
        pedido2 = pedidoRepository.save(pedido2);
    }

    @Test
    @DisplayName("Debe encontrar pedidos por vehículo y estados")
    void findByVehiculoAndEstadoIn_DebeEncontrarPedidos() {
        // Act
        List<Pedido> pedidos = pedidoRepository.findByVehiculoAndEstadoIn(
                vehiculo,
                List.of(EstadoPedido.PENDIENTE, EstadoPedido.EN_PROCESO)
        );

        // Assert
        assertThat(pedidos).hasSize(2);
        assertThat(pedidos).extracting(Pedido::getEstado)
                .containsExactlyInAnyOrder(EstadoPedido.PENDIENTE, EstadoPedido.EN_PROCESO);
    }

    @Test
    @DisplayName("Debe encontrar pedidos paginados por estado")
    void findByEstado_DebeRetornarPaginado() {
        // Act
        Page<Pedido> pedidosPendientes = pedidoRepository.findByEstado(
                EstadoPedido.PENDIENTE,
                PageRequest.of(0, 10)
        );

        // Assert
        assertThat(pedidosPendientes).isNotNull();
        assertThat(pedidosPendientes.getContent()).hasSize(1);
        assertThat(pedidosPendientes.getContent().get(0).getEstado())
                .isEqualTo(EstadoPedido.PENDIENTE);
    }

    @Test
    @DisplayName("Debe calcular peso total transportado")
    void calcularPesoTotalTransportado_DebeCalcularCorrectamente() {
        // Arrange
        Pedido pedidoEntregado = new Pedido();
        pedidoEntregado.setDescripcion("Pedido Entregado");
        pedidoEntregado.setPeso(new BigDecimal("800.00"));
        pedidoEntregado.setEstado(EstadoPedido.ENTREGADO);
        pedidoEntregado.setVehiculo(vehiculo);
        pedidoEntregado.setConductor(conductor);
        pedidoRepository.save(pedidoEntregado);

        // Act
        BigDecimal pesoTotal = pedidoRepository.calcularPesoTotalTransportado();

        // Assert
        assertThat(pesoTotal).isNotNull();
        assertThat(pesoTotal).isEqualByComparingTo(new BigDecimal("800.00"));
    }

    @Test
    @DisplayName("Debe encontrar pedidos por conductor")
    void findByConductorId_DebeEncontrarPedidos() {
        // Act
        Page<Pedido> pedidosConductor = pedidoRepository.findByConductorId(
                conductor.getId(),
                PageRequest.of(0, 10)
        );

        // Assert
        assertThat(pedidosConductor).isNotNull();
        assertThat(pedidosConductor.getContent()).hasSize(2);
        assertThat(pedidosConductor.getContent())
                .extracting(Pedido::getConductor)
                .allMatch(c -> c.getId().equals(conductor.getId()));
    }

    @Test
    @DisplayName("Debe contar pedidos por estado")
    void countByEstado_DebeContarCorrectamente() {
        // Act
        long pedidosPendientes = pedidoRepository.countByEstado(EstadoPedido.PENDIENTE);
        long pedidosEnProceso = pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO);
        long pedidosEntregados = pedidoRepository.countByEstado(EstadoPedido.ENTREGADO);

        // Assert
        assertThat(pedidosPendientes).isEqualTo(1);
        assertThat(pedidosEnProceso).isEqualTo(1);
        assertThat(pedidosEntregados).isZero();
    }
} 