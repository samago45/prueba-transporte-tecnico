package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long>, JpaSpecificationExecutor<Vehiculo> {
    
    @Query("SELECT v FROM Vehiculo v WHERE v.activo = true AND v.conductor IS NULL")
    List<Vehiculo> findVehiculosLibres();
    
    boolean existsByPlaca(String placa);
    
    long countByActivoTrue();

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByConductor(Conductor conductor);

    Page<Vehiculo> findByActivo(boolean activo, Pageable pageable);

    List<Vehiculo> findByCapacidadGreaterThanEqualAndActivo(BigDecimal capacidad, boolean activo);
} 