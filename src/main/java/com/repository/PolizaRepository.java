//esta informacion es relavante par progrmaarla 
public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    List<Poliza> findAllByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);
}
public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {}