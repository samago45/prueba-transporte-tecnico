package org.gersystem.transporte.domain.repository;

import org.gersystem.transporte.domain.model.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    Page<Ruta> findByActiva(boolean activa, Pageable pageable);
} 