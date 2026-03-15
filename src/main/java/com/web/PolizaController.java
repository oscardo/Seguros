@RestController @RequestMapping("/polizas") @RequiredArgsConstructor
public class PolizaController {
    private final PolizaService service;

    @GetMapping
    public List<Poliza> listar(@RequestParam TipoPoliza tipo, @RequestParam EstadoPoliza estado) {
        return service.listar(tipo, estado);
    }

    @PostMapping("/{id}/renovar")
    public void renovar(@PathVariable Long id) { service.renovar(id); }

    @PostMapping("/{id}/cancelar")
    public void cancelar(@PathVariable Long id) { service.cancelar(id); }

    @PostMapping("/{id}/riesgos") //riego to riesgios
    public void agregarRiesgo(@PathVariable Long id, @RequestBody Riesgo riesgo) { 
        service.agregarRiesgo(id, riesgo); 
    }
}

@RestController @RequestMapping("/riesgos") @RequiredArgsConstructor
public class RiesgoController {
    private final PolizaService service;

    @PostMapping("/{id}/cancelar")
    public void cancelar(@PathVariable Long id) { service.cancelarRiesgo(id); }
}

@RestController @RequestMapping("/core-mock") @Slf4j
public class CoreMockController {
    public record CoreEvent(String evento, Long polizaId) {}

    @PostMapping("/evento")
    public void recibirEvento(@RequestBody CoreEvent event) {
        log.info("CORE MOCK RECIBÓ: Evento={}, PolizaId={}", event.evento(), event.polizaId());
    }
}