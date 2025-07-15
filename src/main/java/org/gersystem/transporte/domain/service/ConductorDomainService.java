package org.gersystem.transporte.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConductorDomainService {

    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;

    @Value("${conductor.max.vehiculos:3}")
    private int maxVehiculosPorConductor;

    @Value("${conductor.horario.inicio:06:00}")
    private String horarioInicio;

    @Value("${conductor.horario.fin:22:00}")
    private String horarioFin;

    @Transactional
    public Conductor crearConductor(Conductor conductor) {
        validarFormatoLicencia(conductor.getLicencia());
        if (!conductor.isActivo()) {
            conductor.setActivo(true);
        }
        return conductorRepository.save(conductor);
    }

    private void validarFormatoLicencia(String licencia) {
        if (licencia == null || !licencia.matches("^[A-Z]\\d{5}$")) {
            throw new IllegalArgumentException("Formato de licencia inválido");
        }
    }

    @Transactional
    public Conductor actualizarConductor(Long id, Conductor conductorActualizado) {
        return conductorRepository.findById(id)
            .map(conductor -> {
                conductor.setNombre(conductorActualizado.getNombre());
                conductor.setLicencia(conductorActualizado.getLicencia());
                return conductorRepository.save(conductor);
            })
            .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado"));
    }

    @Transactional
    public Conductor activarConductor(Long id) {
        return conductorRepository.findById(id)
            .map(conductor -> {
                conductor.setActivo(true);
                return conductorRepository.save(conductor);
            })
            .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado"));
    }

    @Transactional
    public Conductor desactivarConductor(Long id) {
        return conductorRepository.findById(id)
            .map(conductor -> {
                conductor.setActivo(false);
                return conductorRepository.save(conductor);
            })
            .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado"));
    }

    @Transactional
    public Conductor asignarVehiculo(Long conductorId, Long vehiculoId) {
        Conductor conductor = conductorRepository.findById(conductorId)
                .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado"));

        if (!conductor.isActivo()) {
            throw new IllegalStateException("El conductor no está activo");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));

        if (!vehiculo.isActivo()) {
            throw new IllegalStateException("El vehículo no está activo");
        }

        if (vehiculo.getConductor() != null) {
            throw new IllegalStateException("El vehículo ya está asignado a otro conductor");
        }

        validarLimiteDeVehiculos(conductor);

        if (conductor.getVehiculos() == null) {
            conductor.setVehiculos(new ArrayList<>());
        }

        conductor.getVehiculos().add(vehiculo);
        vehiculo.setConductor(conductor);

        conductorRepository.save(conductor);
        vehiculoRepository.save(vehiculo);

        return conductor;
    }

    public void validarLimiteDeVehiculos(Conductor conductor) {
        if (conductor.getVehiculos() == null) {
            conductor.setVehiculos(new ArrayList<>());
        }

        List<Vehiculo> vehiculosActivos = conductor.getVehiculos().stream()
                .filter(Vehiculo::isActivo)
                .toList();

        if (vehiculosActivos.size() >= maxVehiculosPorConductor) {
            throw new IllegalStateException(
                "El conductor ya tiene el máximo de vehículos permitidos"
            );
        }
    }

    public void validarHorarioServicio() {
        LocalTime horaActual = LocalTime.now();
        LocalTime inicio = LocalTime.parse(horarioInicio);
        LocalTime fin = LocalTime.parse(horarioFin);

        if (horaActual.isBefore(inicio) || horaActual.isAfter(fin)) {
            throw new IllegalStateException(
                    String.format("Fuera de horario de servicio. Horario permitido: %s - %s", horarioInicio, horarioFin)
            );
        }
    }

    public void validarZonaCobertura(String zonaActual, List<String> zonasPermitidas) {
        if (!zonasPermitidas.contains(zonaActual)) {
            throw new IllegalStateException(
                    String.format("La zona %s está fuera del área de cobertura permitida", zonaActual)
            );
        }
    }

    public void validarDisponibilidadConductor(Conductor conductor) {
        if (!conductor.isActivo()) {
            throw new IllegalStateException("El conductor no está activo");
        }

    }
} 