package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConteoVehiculosDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Long> {
    Page<Conductor> findByActivo(Boolean activo, Pageable pageable);
    
    Page<Conductor> findByActivoAndNombreContaining(Boolean activo, String nombre, Pageable pageable);
    
    List<Conductor> findByVehiculosIsEmpty();
    
    @Query("SELECT new org.gersystem.transporte.infrastructure.adapters.rest.dto.ConteoVehiculosDTO(" +
           "c.id, c.nombre, COUNT(v)) " +
           "FROM Conductor c LEFT JOIN c.vehiculos v " +
           "GROUP BY c.id, c.nombre")
    List<ConteoVehiculosDTO> countVehiculosByConductor();
    
    long countByActivoTrue();
} 
