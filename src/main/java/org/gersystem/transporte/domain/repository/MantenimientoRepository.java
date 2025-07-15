package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.EstadoMantenimiento;
import org.gersystem.transporte.domain.model.Mantenimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    
    List<Mantenimiento> findByVehiculoIdAndEstadoAndFechaProgramadaBetween(
            Long vehiculoId,
            EstadoMantenimiento estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    
    List<Mantenimiento> findByVehiculoIdAndEstado(Long vehiculoId, EstadoMantenimiento estado);
    Page<Mantenimiento> findByVehiculoIdAndEstado(Long vehiculoId, EstadoMantenimiento estado, Pageable pageable);
    Page<Mantenimiento> findByVehiculoId(Long vehiculoId, Pageable pageable);
    Page<Mantenimiento> findByEstado(EstadoMantenimiento estado, Pageable pageable);
} 