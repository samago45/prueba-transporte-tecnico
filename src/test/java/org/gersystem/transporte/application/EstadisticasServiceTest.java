package org.gersystem.transporte.application;

import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.EstadisticasDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadisticasServiceTest {

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private EstadisticasService estadisticasService;

    @BeforeEach
    void setUp() {
        // No se requiere configuración adicional
    }

    @Test
    @DisplayName("Debe obtener estadísticas generales correctamente")
    void obtenerEstadisticasGenerales_DebeCalcularCorrectamente() {
        // Arrange
        when(conductorRepository.count()).thenReturn(10L);
        when(conductorRepository.countByActivoTrue()).thenReturn(8L);
        when(vehiculoRepository.count()).thenReturn(15L);
        when(vehiculoRepository.countByActivoTrue()).thenReturn(12L);
        when(pedidoRepository.count()).thenReturn(100L);
        when(pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO)).thenReturn(20L);
        when(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(70L);
        when(pedidoRepository.calcularPesoTotalTransportado()).thenReturn(new BigDecimal("5000.00"));

        // Act
        EstadisticasDTO resultado = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalConductores()).isEqualTo(10L);
        assertThat(resultado.getConductoresActivos()).isEqualTo(8L);
        assertThat(resultado.getTotalVehiculos()).isEqualTo(15L);
        assertThat(resultado.getVehiculosActivos()).isEqualTo(12L);
        assertThat(resultado.getTotalPedidos()).isEqualTo(100L);
        assertThat(resultado.getPedidosEnProceso()).isEqualTo(20L);
        assertThat(resultado.getPedidosEntregados()).isEqualTo(70L);
        assertThat(resultado.getPesoTotalTransportado()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(resultado.getPromedioVehiculosPorConductor()).isEqualTo(1.50); // 12 vehículos activos / 8 conductores activos
    }

    @Test
    @DisplayName("Debe manejar correctamente el caso de cero conductores activos")
    void obtenerEstadisticasGenerales_SinConductoresActivos() {
        // Arrange
        when(conductorRepository.count()).thenReturn(5L);
        when(conductorRepository.countByActivoTrue()).thenReturn(0L);
        when(vehiculoRepository.count()).thenReturn(10L);
        when(vehiculoRepository.countByActivoTrue()).thenReturn(8L);
        when(pedidoRepository.count()).thenReturn(50L);
        when(pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO)).thenReturn(10L);
        when(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(35L);
        when(pedidoRepository.calcularPesoTotalTransportado()).thenReturn(new BigDecimal("2500.00"));

        // Act
        EstadisticasDTO resultado = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPromedioVehiculosPorConductor()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Debe manejar correctamente estadísticas con valores mínimos")
    void obtenerEstadisticasGenerales_ValoresMinimos() {
        // Arrange
        when(conductorRepository.count()).thenReturn(0L);
        when(conductorRepository.countByActivoTrue()).thenReturn(0L);
        when(vehiculoRepository.count()).thenReturn(0L);
        when(vehiculoRepository.countByActivoTrue()).thenReturn(0L);
        when(pedidoRepository.count()).thenReturn(0L);
        when(pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO)).thenReturn(0L);
        when(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(0L);
        when(pedidoRepository.calcularPesoTotalTransportado()).thenReturn(BigDecimal.ZERO);

        // Act
        EstadisticasDTO resultado = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalConductores()).isZero();
        assertThat(resultado.getConductoresActivos()).isZero();
        assertThat(resultado.getTotalVehiculos()).isZero();
        assertThat(resultado.getVehiculosActivos()).isZero();
        assertThat(resultado.getTotalPedidos()).isZero();
        assertThat(resultado.getPedidosEnProceso()).isZero();
        assertThat(resultado.getPedidosEntregados()).isZero();
        assertThat(resultado.getPesoTotalTransportado()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.getPromedioVehiculosPorConductor()).isZero();
    }

    @Test
    @DisplayName("Debe calcular correctamente las proporciones y porcentajes")
    void obtenerEstadisticasGenerales_CalculoProporciones() {
        // Arrange
        when(conductorRepository.count()).thenReturn(100L);
        when(conductorRepository.countByActivoTrue()).thenReturn(75L);
        when(vehiculoRepository.count()).thenReturn(150L);
        when(vehiculoRepository.countByActivoTrue()).thenReturn(120L);
        when(pedidoRepository.count()).thenReturn(1000L);
        when(pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO)).thenReturn(200L);
        when(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(700L);
        when(pedidoRepository.calcularPesoTotalTransportado()).thenReturn(new BigDecimal("10000.00"));

        // Act
        EstadisticasDTO resultado = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(resultado).isNotNull();
        // Proporción de conductores activos: 75%
        assertThat((double) resultado.getConductoresActivos() / resultado.getTotalConductores()).isEqualTo(0.75);
        // Proporción de vehículos activos: 80%
        assertThat((double) resultado.getVehiculosActivos() / resultado.getTotalVehiculos()).isEqualTo(0.80);
        // Proporción de pedidos completados: 70%
        assertThat((double) resultado.getPedidosEntregados() / resultado.getTotalPedidos()).isEqualTo(0.70);
        // Promedio de vehículos por conductor: 1.60
        assertThat(resultado.getPromedioVehiculosPorConductor()).isEqualTo(1.60);
    }

    @Test
    @DisplayName("Debe manejar correctamente valores grandes")
    void obtenerEstadisticasGenerales_ValoresGrandes() {
        // Arrange
        when(conductorRepository.count()).thenReturn(10000L);
        when(conductorRepository.countByActivoTrue()).thenReturn(9500L);
        when(vehiculoRepository.count()).thenReturn(15000L);
        when(vehiculoRepository.countByActivoTrue()).thenReturn(14000L);
        when(pedidoRepository.count()).thenReturn(1000000L);
        when(pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO)).thenReturn(50000L);
        when(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(900000L);
        when(pedidoRepository.calcularPesoTotalTransportado()).thenReturn(new BigDecimal("1000000.00"));

        // Act
        EstadisticasDTO resultado = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalConductores()).isEqualTo(10000L);
        assertThat(resultado.getVehiculosActivos()).isEqualTo(14000L);
        assertThat(resultado.getTotalPedidos()).isEqualTo(1000000L);
        assertThat(resultado.getPesoTotalTransportado()).isEqualByComparingTo(new BigDecimal("1000000.00"));
        assertThat(resultado.getPromedioVehiculosPorConductor()).isEqualTo(1.47); // Redondeado a 2 decimales
    }
} 