@Service @RequiredArgsConstructor @Slf4j
public class PolizaService {
    private final PolizaRepository polizaRepo; //poliza 
    private final RiesgoRepository riesgoRepo; //repo 
    private final RestTemplate restTemplate; // Para el mock externo
    private static final BigDecimal IPC_ACTUAL = new BigDecimal("0.05"); // 5% o 10%

    @Transactional(readOnly = true)
    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepo.findAllByTipoAndEstado(tipo, estado); //
    }

    @Transactional
    public void renovar(Long id) {
        Poliza poliza = getPoliza(id);
        poliza.renovar(IPC_ACTUAL);
        notificarCoreMock("ACTUALIZACION", id);
    }

    @Transactional
    public void cancelar(Long id) {
        getPoliza(id).cancelar();
        notificarCoreMock("CANCELACION", id);
    }

    @Transactional
    public void agregarRiesgo(Long polizaId, Riesgo riesgo) {
        Poliza poliza = getPoliza(polizaId);
        poliza.agregarRiesgo(riesgo);
        polizaRepo.save(poliza);
    }

    @Transactional
    public void cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepo.findById(riesgoId).orElseThrow();
        riesgo.cancelar();
    }

    private Poliza getPoliza(Long id) {
        return polizaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrada"));
    }

    // Integracion Mock Core
    private void notificarCoreMock(String evento, Long polizaId) {
        try {
            var payload = Map.of("evento", evento, "polizaId", polizaId);
            restTemplate.postForLocation("http://localhost:8080/core-mock/evento", payload);
            log.info("Notificado al CORE: {}", payload);
        } catch (Exception e) {
            log.error("Fallo mock core pero la transaccon sigue", e);
        }
    }
}