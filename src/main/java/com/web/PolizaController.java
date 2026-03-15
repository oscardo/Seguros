package com.web;

import com.domain.Poliza;
import com.domain.Riesgo;
import com.domain.TipoPoliza;
import com.service.PolizaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolizaController {

    private final PolizaService service;

    /**
     * Paso 1: Listar pólizas por tipo y estado
     */
    @GetMapping
    public List<Poliza> listar(
            @RequestParam TipoPoliza tipo,
            @RequestParam String estado) {
        return service.listar(tipo, estado);
    }

    /**
     * Paso 2: Obtener riesgos de una póliza
     */
    @GetMapping("/{id}/riesgos")
    public List<Riesgo> obtenerRiesgos(@PathVariable Long id) {
        return service.obtenerRiesgosDePoliza(id);
    }

    /**
     * Paso 3: Renovar póliza (+IPC 5%)
     */
    @PostMapping("/{id}/renovar")
    public ResponseEntity<Poliza> renovar(@PathVariable Long id) {
        Poliza poliza = service.renovar(id);
        return ResponseEntity.ok(poliza);
    }

    /**
     * Paso 4: Cancelar póliza (y sus riesgos)
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Poliza> cancelar(@PathVariable Long id) {
        Poliza poliza = service.cancelar(id);
        return ResponseEntity.ok(poliza);
    }

    /**
     * Paso 5: Agregar riesgo (solo COLECTIVA)
     */
    @PostMapping("/{id}/riesgos")
    public ResponseEntity<Poliza> agregarRiesgo(
            @PathVariable Long id,
            @RequestBody Riesgo riesgo) {
        Poliza poliza = service.agregarRiesgo(id, riesgo);
        return ResponseEntity.ok(poliza);
    }

    /**
     * Obtener póliza por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Poliza> obtenerPoliza(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPoliza(id));
    }
}
