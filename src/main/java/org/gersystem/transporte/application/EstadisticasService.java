package org.gersystem.transporte.application;

import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.EstadisticasDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.EstadisticasConductorDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.gersystem.transporte.domain.model.Conductor;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PedidoRepository pedidoRepository;

    private EstadisticasDTO.ConductorSimpleDTO mapToConductorSimpleDTO(Conductor conductor) {
        EstadisticasDTO.ConductorSimpleDTO dto = new EstadisticasDTO.ConductorSimpleDTO();
        dto.setId(conductor.getId());
        dto.setNombre(conductor.getNombre());
        dto.setLicencia(conductor.getLicencia());
        return dto;
    }

    @Transactional(readOnly = true)
    @Cacheable("estadisticas_generales")
    public EstadisticasDTO obtenerEstadisticasGenerales() {
        long totalConductores = conductorRepository.count();
        long conductoresActivos = conductorRepository.countByActivoTrue();
        long totalVehiculos = vehiculoRepository.count();
        long vehiculosActivos = vehiculoRepository.countByActivoTrue();
        long totalPedidos = pedidoRepository.count();
        long pedidosEnProceso = pedidoRepository.countByEstado(EstadoPedido.EN_PROCESO);
        long pedidosEntregados = pedidoRepository.countByEstado(EstadoPedido.ENTREGADO);
        
        BigDecimal pesoTotalTransportado = pedidoRepository.calcularPesoTotalTransportado();
        
        double promedioVehiculosPorConductor = conductoresActivos > 0 
            ? (double) vehiculosActivos / conductoresActivos 
            : 0.0;

        double porcentajeConductoresActivos = totalConductores > 0 
            ? (double) conductoresActivos * 100 / totalConductores 
            : 0.0;

        double porcentajeVehiculosActivos = totalVehiculos > 0 
            ? (double) vehiculosActivos * 100 / totalVehiculos 
            : 0.0;

        double porcentajePedidosEntregados = totalPedidos > 0 
            ? (double) pedidosEntregados * 100 / totalPedidos 
            : 0.0;

        List<EstadisticasDTO.ConductorSimpleDTO> conductoresSinVehiculos = 
            conductorRepository.findByVehiculosIsEmpty()
                .stream()
                .map(this::mapToConductorSimpleDTO)
                .collect(Collectors.toList());

        return EstadisticasDTO.builder()
                .totalConductores(totalConductores)
                .conductoresActivos(conductoresActivos)
                .totalVehiculos(totalVehiculos)
                .vehiculosActivos(vehiculosActivos)
                .totalPedidos(totalPedidos)
                .pedidosEnProceso(pedidosEnProceso)
                .pedidosEntregados(pedidosEntregados)
                .pesoTotalTransportado(pesoTotalTransportado)
                .promedioVehiculosPorConductor(BigDecimal.valueOf(promedioVehiculosPorConductor)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .porcentajeConductoresActivos(BigDecimal.valueOf(porcentajeConductoresActivos)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .porcentajeVehiculosActivos(BigDecimal.valueOf(porcentajeVehiculosActivos)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .porcentajePedidosEntregados(BigDecimal.valueOf(porcentajePedidosEntregados)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .conductoresSinVehiculos(conductoresSinVehiculos)
                .vehiculosPorConductor(conductorRepository.countVehiculosByConductor())
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "estadisticas_conductor", key = "#conductorId")
    public EstadisticasConductorDTO obtenerEstadisticasConductor(Long conductorId) {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        
        return EstadisticasConductorDTO.builder()
            .pedidosEntregadosMes(pedidoRepository.countByConductorIdAndEstadoAndCreatedDateAfter(
                conductorId, EstadoPedido.ENTREGADO, inicioMes))
            .totalPedidosEntregados(pedidoRepository.countByConductorIdAndEstado(
                conductorId, EstadoPedido.ENTREGADO))
            .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "promedio_capacidad", key = "#fechaInicio.toString() + #fechaFin.toString()")
    public Double obtenerPromedioCapacidadUtilizada(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.calcularPromedioCapacidadUtilizada(fechaInicio, fechaFin);
    }
} 