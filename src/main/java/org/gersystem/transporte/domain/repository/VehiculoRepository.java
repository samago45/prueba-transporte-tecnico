package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long>, JpaSpecificationExecutor<Vehiculo> {
    
    @Query("SELECT v FROM Vehiculo v WHERE v.activo = true AND v.conductor IS NULL")
    List<Vehiculo> findVehiculosLibres();
    
    boolean existsByPlaca(String placa);
    
    long countByActivoTrue();
} 