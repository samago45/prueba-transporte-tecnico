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
        // Configurar comportamiento base de los mocks
        when(conductorRepository.count()).thenReturn(10L);
        when(conductorRepository.countByActivoTrue()).thenReturn(8L);
        when(vehiculoRepository.count()).thenReturn(15L);
        when(vehiculoRepository.countByActivoTrue()).thenReturn(12L);
        when(pedidoRepository.count()).thenReturn(100L);
        when(pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO)).thenReturn(20L);
        when(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(70L);
        when(pedidoRepository.calcularPesoTotalTransportado()).thenReturn(new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("Debe calcular estadísticas generales correctamente")
    void obtenerEstadisticasGenerales_DebeCalcularCorrectamente() {
        // Act
        EstadisticasDTO estadisticas = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(estadisticas).isNotNull();
        assertThat(estadisticas.getTotalConductores()).isEqualTo(10L);
        assertThat(estadisticas.getConductoresActivos()).isEqualTo(8L);
        assertThat(estadisticas.getTotalVehiculos()).isEqualTo(15L);
        assertThat(estadisticas.getVehiculosActivos()).isEqualTo(12L);
        assertThat(estadisticas.getTotalPedidos()).isEqualTo(100L);
        assertThat(estadisticas.getPedidosEnProceso()).isEqualTo(20L);
        assertThat(estadisticas.getPedidosEntregados()).isEqualTo(70L);
        assertThat(estadisticas.getPesoTotalTransportado())
                .isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(estadisticas.getPromedioVehiculosPorConductor()).isEqualTo(1.5);
    }

    @Test
    @DisplayName("Debe manejar caso sin conductores activos")
    void obtenerEstadisticasGenerales_DebeManejarSinConductores() {
        // Arrange
        when(conductorRepository.countByActivoTrue()).thenReturn(0L);

        // Act
        EstadisticasDTO estadisticas = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(estadisticas).isNotNull();
        assertThat(estadisticas.getPromedioVehiculosPorConductor()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Debe manejar caso sin pedidos")
    void obtenerEstadisticasGenerales_DebeManejarSinPedidos() {
        // Arrange
        when(pedidoRepository.count()).thenReturn(0L);
        when(pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO)).thenReturn(0L);
        when(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(0L);
        when(pedidoRepository.calcularPesoTotalTransportado()).thenReturn(BigDecimal.ZERO);

        // Act
        EstadisticasDTO estadisticas = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(estadisticas).isNotNull();
        assertThat(estadisticas.getTotalPedidos()).isEqualTo(0L);
        assertThat(estadisticas.getPedidosEnProceso()).isEqualTo(0L);
        assertThat(estadisticas.getPedidosEntregados()).isEqualTo(0L);
        assertThat(estadisticas.getPesoTotalTransportado())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe calcular porcentajes correctamente")
    void obtenerEstadisticasGenerales_DebeCalcularPorcentajes() {
        // Act
        EstadisticasDTO estadisticas = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(estadisticas).isNotNull();
        // Porcentaje de conductores activos: 8/10 = 80%
        assertThat(estadisticas.getPorcentajeConductoresActivos()).isEqualTo(80.0);
        // Porcentaje de vehículos activos: 12/15 = 80%
        assertThat(estadisticas.getPorcentajeVehiculosActivos()).isEqualTo(80.0);
        // Porcentaje de pedidos entregados: 70/100 = 70%
        assertThat(estadisticas.getPorcentajePedidosEntregados()).isEqualTo(70.0);
    }

    @Test
    @DisplayName("Debe manejar caso sin vehículos")
    void obtenerEstadisticasGenerales_DebeManejarSinVehiculos() {
        // Arrange
        when(vehiculoRepository.count()).thenReturn(0L);
        when(vehiculoRepository.countByActivoTrue()).thenReturn(0L);

        // Act
        EstadisticasDTO estadisticas = estadisticasService.obtenerEstadisticasGenerales();

        // Assert
        assertThat(estadisticas).isNotNull();
        assertThat(estadisticas.getTotalVehiculos()).isEqualTo(0L);
        assertThat(estadisticas.getVehiculosActivos()).isEqualTo(0L);
        assertThat(estadisticas.getPorcentajeVehiculosActivos()).isEqualTo(0.0);
        assertThat(estadisticas.getPromedioVehiculosPorConductor()).isEqualTo(0.0);
    }
} 