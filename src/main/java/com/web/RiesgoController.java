package com.web;

import com.domain.Riesgo;
import com.service.PolizaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
@RequiredArgsConstructor
public class RiesgoController {

    private final PolizaService service;

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Riesgo> cancelar(@PathVariable Long id) {
        Riesgo riesgo = service.cancelarRiesgo(id);
        return ResponseEntity.ok(riesgo);
    }
}
