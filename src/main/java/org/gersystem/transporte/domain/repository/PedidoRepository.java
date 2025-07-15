package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByVehiculoAndEstadoIn(Vehiculo vehiculo, List<EstadoPedido> estados);
    
    Long countByEstado(EstadoPedido estado);
    
    Long countByCreatedDateAfter(LocalDateTime fecha);
    
    Long countByConductorIdAndEstado(Long conductorId, EstadoPedido estado);
    
    Long countByConductorIdAndEstadoAndCreatedDateAfter(Long conductorId, EstadoPedido estado, LocalDateTime fecha);

    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);
    
    Page<Pedido> findByConductorId(Long conductorId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.peso), 0) FROM Pedido p WHERE p.estado = 'ENTREGADO'")
    BigDecimal calcularPesoTotalTransportado();
} 