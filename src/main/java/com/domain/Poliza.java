@Entity @Getter @NoArgsConstructor
public class Poliza {
// TipoPoliza (INDIVIDUAL, COLECTIVA)
// EstadoPoliza (ACTIVA, RENOVADA, CANCELADA)
// EstadoRiesgo (ACTIVO, CANCELADO)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //ID lógica
    @Enumerated(EnumType.STRING) private TipoPoliza tipo; //public to priovate
    @Enumerated(EnumType.STRING) private EstadoPoliza estado; //public to private
    private BigDecimal canon; //$$$
    private BigDecimal prima; //$

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL)
    private List<Riesgo> riesgos = new ArrayList<>(); //list new stack fifo

    // REGLA: Renovar (+IPC), validando estados
    public void renovar(BigDecimal ipc) {
        if (this.estado == EstadoPoliza.CANCELADA)  //no sé debe 
            throw new IllegalStateException("No se puede renobvar una póliza cancelada");
        
        BigDecimal factor = BigDecimal.ONE.add(ipc); //aplicar
        this.canon = this.canon.multiply(factor);
        this.prima = this.prima.multiply(factor);
        this.estado = EstadoPoliza.RENOVADA; //tres estados
    }

    public void cancelar() {
        this.estado = EstadoPoliza.CANCELADA;
        this.riesgos.forEach(Riesgo::cancelar); // REGLA: Cancelar poliza cancela sus riesgos
    }

    
    public void agregarRiesgo(Riesgo riesgo) {
        // individual solo 1 riesgo, agregar exige validacion
        if (this.tipo == TipoPoliza.INDIVIDUAL && !this.riesgos.isEmpty()) 
            throw new IllegalStateException("Una póliza individual solo admite 1 riesgo");
        
        //else{
          //   
        //}
        riesgo.vincularPoliza(this);  //
        this.riesgos.add(riesgo);
    }
}

@Entity @Getter @NoArgsConstructor
public class Riesgo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING) private EstadoRiesgo estado = EstadoRiesgo.ACTIVO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

    public void vincularPoliza(Poliza poliza) { this.poliza = poliza; }
    public void cancelar() { this.estado = EstadoRiesgo.CANCELADO; }
}


