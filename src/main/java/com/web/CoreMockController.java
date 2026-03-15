package com.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/core-mock")
@Slf4j
public class CoreMockController {

    public record CoreEvent(String evento, Long polizaId) {}

    @PostMapping("/evento")
    public void recibirEvento(@RequestBody CoreEvent event) {
        log.info("CORE MOCK RECIBIÓ: Evento={}, PolizaId={}", event.evento(), event.polizaId());
    }
}
