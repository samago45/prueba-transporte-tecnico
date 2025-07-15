package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.EstadoMantenimiento;
import org.gersystem.transporte.domain.model.Mantenimiento;
import org.gersystem.transporte.domain.model.TipoMantenimiento;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    List<Mantenimiento> findByVehiculoAndEstado(Vehiculo vehiculo, EstadoMantenimiento estado);
    
    long countByEstado(EstadoMantenimiento estado);
    
    Page<Mantenimiento> findByVehiculo(Vehiculo vehiculo, Pageable pageable);
    
    List<Mantenimiento> findByTipo(TipoMantenimiento tipo);
    
    List<Mantenimiento> findByFechaProgramadaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    List<Mantenimiento> findByVehiculoAndTipo(Vehiculo vehiculo, TipoMantenimiento tipo);
    
    Mantenimiento findFirstByVehiculoOrderByFechaProgramadaDesc(Vehiculo vehiculo);

    Page<Mantenimiento> findByVehiculoIdAndEstado(Long vehiculoId, EstadoMantenimiento estado, Pageable pageable);
    List<Mantenimiento> findByVehiculoIdAndEstadoAndFechaProgramadaBetween(Long vehiculoId, EstadoMantenimiento estado, LocalDateTime inicio, LocalDateTime fin);
    Page<Mantenimiento> findByVehiculoId(Long vehiculoId, Pageable pageable);
    Page<Mantenimiento> findByEstado(EstadoMantenimiento estado, Pageable pageable);
} 