package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long>, JpaSpecificationExecutor<Vehiculo> {
    @Query("SELECT v FROM Vehiculo v WHERE v.conductor IS NULL AND v.activo = true")
    List<Vehiculo> findVehiculosLibres();

    boolean existsByPlaca(String placa);
}