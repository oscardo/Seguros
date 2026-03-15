package com.service;

import com.domain.Poliza;
import com.domain.Riesgo;
import com.domain.TipoPoliza;
import com.repository.PolizaRepository;
import com.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolizaService {

    private final PolizaRepository polizaRepo;
    private final RiesgoRepository riesgoRepo;
    private final RestTemplate restTemplate;

    private static final BigDecimal IPC_ACTUAL = new BigDecimal("0.05");

    @Transactional(readOnly = true)
    public List<Poliza> listar(TipoPoliza tipo, String estado) {
        return polizaRepo.findAllByTipoAndEstado(tipo, com.domain.EstadoPoliza.valueOf(estado));
    }

    @Transactional
    public Poliza renovar(Long id) {
        Poliza poliza = getPoliza(id);
        poliza.obtenerRiesgos(); // Paso 2
        poliza.renovar(IPC_ACTUAL); // Paso 3
        polizaRepo.save(poliza);
        notificarCoreMock("ACTUALIZACION", id);
        return poliza;
    }

    @Transactional
    public Poliza cancelar(Long id) {
        Poliza poliza = getPoliza(id);
        poliza.obtenerRiesgos(); // Paso 2
        poliza.cancelar(); // Paso 4
        polizaRepo.save(poliza);
        notificarCoreMock("CANCELACION", id);
        return poliza;
    }

    @Transactional
    public Poliza agregarRiesgo(Long polizaId, Riesgo riesgo) {
        Poliza poliza = getPoliza(polizaId);
        poliza.obtenerRiesgos(); // Paso 2
        poliza.agregarRiesgo(riesgo); // Paso 5
        riesgoRepo.save(riesgo);
        return poliza;
    }

    @Transactional
    public Riesgo cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepo.findById(riesgoId)
                .orElseThrow(() -> new IllegalArgumentException("Riesgo no encontrado"));
        riesgo.cancelar(); // Paso 6
        return riesgo;
    }

    @Transactional(readOnly = true)
    public List<Riesgo> obtenerRiesgosDePoliza(Long polizaId) {
        Poliza poliza = getPoliza(polizaId);
        poliza.obtenerRiesgos(); // Paso 2
        return poliza.getRiesgos();
    }

    @Transactional(readOnly = true)
    public Poliza obtenerPoliza(Long id) {
        return getPoliza(id);
    }

    private Poliza getPoliza(Long id) {
        return polizaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Poliza no encontrada"));
    }

    private void notificarCoreMock(String evento, Long polizaId) {
        try {
            var payload = Map.of("evento", evento, "polizaId", polizaId);
            restTemplate.postForLocation("http://localhost:8080/core-mock/evento", payload);
            log.info("Notificado al CORE: {}", payload);
        } catch (Exception e) {
            log.warn("Falló la notificación al CORE mock: {}", e.getMessage());
        }
    }
}
