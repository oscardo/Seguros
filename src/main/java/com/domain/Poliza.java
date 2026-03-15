package com.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    private EstadoPoliza estado;

    private BigDecimal canon;

    private BigDecimal prima;

    // Campos de fecha - version 0.0.2
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Integer diaEnvioInformacion;

    // Campo de hora de tráfico - version 0.0.2
    private LocalTime horaTraffico;

    // Seguimiento del paso del proceso - version 0.0.2
    @Enumerated(EnumType.STRING)
    private PasoProceso pasoProceso = PasoProceso.INICIADO;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();

    public Poliza(TipoPoliza tipo, BigDecimal canon, BigDecimal prima, EstadoPoliza estado,
                  LocalDate fechaInicio, LocalDate fechaFin, Integer diaEnvioInformacion) {
        this.tipo = tipo;
        this.canon = canon;
        this.prima = prima;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diaEnvioInformacion = diaEnvioInformacion;
    }

    /**
     * Paso 1: Listar pólizas por tipo y estado
     */
    public void listar(TipoPoliza tipo, EstadoPoliza estado) {
        this.pasoProceso = PasoProceso.LISTADO;
    }

    /**
     * Paso 2: Obtener riesgos de la póliza
     */
    public void obtenerRiesgos() {
        if (this.pasoProceso.ordinal() < PasoProceso.LISTADO.ordinal()) {
            throw new IllegalStateException("Debe listar la póliza primero");
        }
        this.pasoProceso = PasoProceso.RIESGOS_OBTENIDOS;
    }

    /**
     * Paso 3: Renovar póliza (+IPC), validando estados
     */
    public void renovar(BigDecimal ipc) {
        if (this.pasoProceso.ordinal() < PasoProceso.RIESGOS_OBTENIDOS.ordinal()) {
            throw new IllegalStateException("Debe obtener riesgos antes de renovar");
        }
        if (this.estado == EstadoPoliza.CANCELADA) {
            throw new IllegalStateException("No se puede renovar una póliza cancelada");
        }

        BigDecimal factor = BigDecimal.ONE.add(ipc);
        this.canon = this.canon.multiply(factor);
        this.prima = this.prima.multiply(factor);
        this.estado = EstadoPoliza.RENOVADA;
        this.pasoProceso = PasoProceso.RENOVADA;
    }

    /**
     * Paso 4: Cancelar póliza
     */
    public void cancelar() {
        if (this.pasoProceso.ordinal() < PasoProceso.RIESGOS_OBTENIDOS.ordinal()) {
            throw new IllegalStateException("Debe obtener riesgos antes de cancelar");
        }
        this.estado = EstadoPoliza.CANCELADA;
        this.riesgos.forEach(Riesgo::cancelar);
        this.pasoProceso = PasoProceso.CANCELADA;
    }

    /**
     * Paso 5: Agregar riesgo (solo si tipo = COLECTIVA)
     */
    public void agregarRiesgo(Riesgo riesgo) {
        if (this.pasoProceso.ordinal() < PasoProceso.RIESGOS_OBTENIDOS.ordinal()) {
            throw new IllegalStateException("Debe obtener riesgos antes de agregar");
        }
        if (this.tipo == TipoPoliza.INDIVIDUAL && !this.riesgos.isEmpty()) {
            throw new IllegalStateException("Una póliza individual solo admite 1 riesgo");
        }
        riesgo.vincularPoliza(this);
        this.riesgos.add(riesgo);
        this.pasoProceso = PasoProceso.RIESGO_AGREGADO;
    }

    /**
     * Paso 6: Cancelar riesgo
     */
    public void cancelarRiesgo(Riesgo riesgo) {
        if (!this.riesgos.contains(riesgo)) {
            throw new IllegalStateException("El riesgo no pertenece a esta póliza");
        }
        riesgo.cancelar();
        this.pasoProceso = PasoProceso.RIESGO_CANCELADO;
    }

    /**
     * Paso 6: Cancelar riesgo por ID
     */
    public void cancelarRiesgoById(Long riesgoId) {
        Riesgo riesgo = this.riesgos.stream()
                .filter(r -> r.getId().equals(riesgoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Riesgo no encontrado"));
        cancelarRiesgo(riesgo);
    }

    /**
     * Avanzar al siguiente paso del proceso
     */
    public void avanzarProceso() {
        if (this.pasoProceso != PasoProceso.COMPLETADO) {
            this.pasoProceso = PasoProceso.values()[this.pasoProceso.ordinal() + 1];
        }
    }
}
