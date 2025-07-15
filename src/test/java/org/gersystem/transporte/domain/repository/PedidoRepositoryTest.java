package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.config.TestJpaConfig;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
class PedidoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Conductor conductor;
    private Vehiculo vehiculo;
    private Pedido pedido1;
    private Pedido pedido2;

    @BeforeEach
    void setUp() {
        conductor = new Conductor();
        conductor.setNombre("Juan Pérez");
        conductor.setActivo(true);
        entityManager.persist(conductor);

        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo.setConductor(conductor);
        entityManager.persist(vehiculo);

        pedido1 = new Pedido();
        pedido1.setDescripcion("Pedido 1");
        pedido1.setPeso(new BigDecimal("500.00"));
        pedido1.setEstado(EstadoPedido.PENDIENTE);
        pedido1.setVehiculo(vehiculo);
        pedido1.setConductor(conductor);
        entityManager.persist(pedido1);

        pedido2 = new Pedido();
        pedido2.setDescripcion("Pedido 2");
        pedido2.setPeso(new BigDecimal("300.00"));
        pedido2.setEstado(EstadoPedido.EN_PROCESO);
        pedido2.setVehiculo(vehiculo);
        pedido2.setConductor(conductor);
        entityManager.persist(pedido2);

        entityManager.flush();
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
    @DisplayName("Debe contar pedidos por estado")
    void countByEstado_DebeContarPedidos() {
        // Act
        Long cantidadPendientes = pedidoRepository.countByEstado(EstadoPedido.PENDIENTE);
        Long cantidadEnProceso = pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO);

        // Assert
        assertThat(cantidadPendientes).isEqualTo(1);
        assertThat(cantidadEnProceso).isEqualTo(1);
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
        assertThat(pedidosPendientes.getContent()).hasSize(1);
        assertThat(pedidosPendientes.getContent().get(0).getEstado())
                .isEqualTo(EstadoPedido.PENDIENTE);
    }

    @Test
    @DisplayName("Debe encontrar pedidos paginados por conductor")
    void findByConductorId_DebeRetornarPaginado() {
        // Act
        Page<Pedido> pedidosConductor = pedidoRepository.findByConductorId(
                conductor.getId(),
                PageRequest.of(0, 10)
        );

        // Assert
        assertThat(pedidosConductor.getContent()).hasSize(2);
        assertThat(pedidosConductor.getContent())
                .extracting(Pedido::getConductor)
                .allMatch(c -> c.getId().equals(conductor.getId()));
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
        entityManager.persist(pedidoEntregado);
        entityManager.flush();

        // Act
        BigDecimal pesoTotal = pedidoRepository.calcularPesoTotalTransportado();

        // Assert
        assertThat(pesoTotal).isEqualByComparingTo(new BigDecimal("800.00"));
    }
} 