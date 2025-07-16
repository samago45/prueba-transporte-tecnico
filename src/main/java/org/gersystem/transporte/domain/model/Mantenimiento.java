package org.gersystem.transporte.domain.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Mantenimiento extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    @NotNull(message = "El vehículo es requerido")
    private Vehiculo vehiculo;

    @NotNull(message = "La fecha programada es requerida")
    private LocalDateTime fechaProgramada;

    private LocalDateTime fechaRealizada;

    @Enumerated(EnumType.STRING)
    private TipoMantenimiento tipo;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoMantenimiento estado = EstadoMantenimiento.PENDIENTE;

    private String observaciones;
} 
