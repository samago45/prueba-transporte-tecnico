package org.gersystem.transporte.domain.service;

import org.gersystem.transporte.domain.model.Conductor;

import java.util.List;
import java.util.Optional;

public interface ConductorService {
    List<Conductor> findAll();
    Optional<Conductor> findById(Long id);
    Conductor save(Conductor conductor);
    void deleteById(Long id);
    List<Conductor> findByNombre(String nombre);
} 