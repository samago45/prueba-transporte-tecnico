package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {
    List<Pedido> findByVehiculoAndEstadoIn(Vehiculo vehiculo, List<EstadoPedido> estados);
    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);
    Page<Pedido> findByConductorId(Long conductorId, Pageable pageable);
    
    long countByCreatedDateAfter(LocalDateTime fecha);
    long countByEstado(EstadoPedido estado);
    long countByConductorIdAndEstado(Long conductorId, EstadoPedido estado);
    long countByConductorIdAndEstadoAndCreatedDateAfter(Long conductorId, EstadoPedido estado, LocalDateTime fecha);
    
    @Query("SELECT COALESCE(SUM(p.peso), 0) FROM Pedido p WHERE p.estado = 'ENTREGADO'")
    BigDecimal calcularPesoTotalTransportado();
} 