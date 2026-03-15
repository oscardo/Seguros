package com.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoRiesgo estado = EstadoRiesgo.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

    // Campo de hora de tráfico - version 0.0.2
    private LocalTime horaTraffico;

    public void vincularPoliza(Poliza poliza) {
        this.poliza = poliza;
    }

    public void cancelar() {
        this.estado = EstadoRiesgo.CANCELADO;
    }
}
