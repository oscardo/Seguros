package com.repository;

import com.domain.EstadoPoliza;
import com.domain.Poliza;
import com.domain.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    List<Poliza> findAllByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);
}
