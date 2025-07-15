package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {
    
    List<Pedido> findByVehiculoAndEstadoIn(Vehiculo vehiculo, List<EstadoPedido> estados);
    
    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);
    
    Page<Pedido> findByConductorId(Long conductorId, Pageable pageable);
} 