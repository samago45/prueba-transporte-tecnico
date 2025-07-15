package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.gersystem.transporte.domain.model.Ruta;
import org.gersystem.transporte.domain.repository.RutaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RutaDomainService {

    private final RutaRepository rutaRepository;

    public RutaDomainService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    @Transactional
    public Ruta crearRuta(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta activarRuta(Long id) {
        Ruta ruta = obtenerRuta(id);
        ruta.setActiva(true);
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta desactivarRuta(Long id) {
        Ruta ruta = obtenerRuta(id);
        ruta.setActiva(false);
        return rutaRepository.save(ruta);
    }

    @Transactional(readOnly = true)
    public Ruta obtenerRuta(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
    }

    @Transactional(readOnly = true)
    public Page<Ruta> listarRutas(Boolean activa, Pageable pageable) {
        if (activa != null) {
            return rutaRepository.findByActiva(activa, pageable);
        }
        return rutaRepository.findAll(pageable);
    }
} 