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
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.createdDate >= :fecha")
    long countByCreatedDateAfter(LocalDateTime fecha);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    long countByEstado(EstadoPedido estado);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.conductor.id = :conductorId AND p.estado = :estado")
    long countByConductorIdAndEstado(Long conductorId, EstadoPedido estado);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.conductor.id = :conductorId AND p.estado = :estado AND p.createdDate >= :fecha")
    long countByConductorIdAndEstadoAndCreatedDateAfter(Long conductorId, EstadoPedido estado, LocalDateTime fecha);
    
    @Query("SELECT COALESCE(SUM(p.peso), 0) FROM Pedido p WHERE p.estado = 'ENTREGADO'")
    BigDecimal calcularPesoTotalTransportado();
    
    @Query("""
        SELECT AVG((p.peso / v.capacidad) * 100)
        FROM Pedido p
        JOIN p.vehiculo v
        WHERE p.createdDate BETWEEN :fechaInicio AND :fechaFin
        AND p.estado = 'ENTREGADO'
    """)
    Double calcularPromedioCapacidadUtilizada(LocalDateTime fechaInicio, LocalDateTime fechaFin);
} 