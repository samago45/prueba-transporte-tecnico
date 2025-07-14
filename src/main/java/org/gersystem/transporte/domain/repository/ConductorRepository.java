package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ConteoVehiculosDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Long>, JpaSpecificationExecutor<Conductor> {
    @Query("SELECT c FROM Conductor c WHERE c.activo = true AND c.vehiculos IS EMPTY")
    List<Conductor> findConductoresSinVehiculos();

    @Query("SELECT new org.gersystem.transporte.infrastructure.adapters.rest.dto.ConteoVehiculosDTO(c.id, c.nombre, size(c.vehiculos)) FROM Conductor c WHERE c.activo = true")
    List<ConteoVehiculosDTO> countVehiculosByConductor();
} 