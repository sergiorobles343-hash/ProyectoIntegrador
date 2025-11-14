package app.model;
import java.util.Date;
/**
 * Clase que representa una inspección fitosanitaria
 * @author sergi
 */
public class Inspeccion {
    private int id;
    private String predio;
    private Date fecha;
    private String tecnicoId;
    private String cultivo;
    private String plaga;
    private String observaciones; // Observaciones generales
    private String observacionesEspecificas;
    private double porcentajeInfestacion; // ✅ NUEVO CAMPO
    
    // ✅ Constructor completo con porcentaje de infestación
    public Inspeccion(String predio, Date fecha, String tecnicoId, String cultivo, String plaga, 
                      String observacionesGenerales, String observacionesEspecificas, 
                      double porcentajeInfestacion) {
        this.predio = predio;
        this.fecha = fecha;
        this.tecnicoId = tecnicoId;
        this.cultivo = cultivo;
        this.plaga = plaga;
        this.observaciones = observacionesGenerales;
        this.observacionesEspecificas = observacionesEspecificas;
        this.porcentajeInfestacion = porcentajeInfestacion;
    }
    
    // ✅ Constructor con porcentaje (para RegistrarInspeccionFrame)
    public Inspeccion(String predio, Date fecha, String tecnicoId, String cultivo, String plaga, 
                      String observaciones, double porcentajeInfestacion) {
        this(predio, fecha, tecnicoId, cultivo, plaga, observaciones, null, porcentajeInfestacion);
    }
    
    // ✅ Constructor con observaciones separadas (mantener por compatibilidad)
    public Inspeccion(String predio, Date fecha, String tecnicoId, String cultivo, String plaga, 
                      String observacionesGenerales, String observacionesEspecificas) {
        this(predio, fecha, tecnicoId, cultivo, plaga, observacionesGenerales, observacionesEspecificas, 0.0);
    }
    
    // ✅ Constructor anterior (mantener por compatibilidad)
    public Inspeccion(String predio, Date fecha, String tecnicoId, String cultivo, String plaga, String observaciones) {
        this(predio, fecha, tecnicoId, cultivo, plaga, observaciones, null, 0.0);
    }
    
    // ✅ Constructor vacío
    public Inspeccion() {
        this.porcentajeInfestacion = 0.0;
    }
    
    // ========== GETTERS ==========
    public int getId() {
        return id;
    }
    
    public String getPredio() {
        return predio;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public String getTecnicoId() {
        return tecnicoId;
    }
    
    public String getCultivo() {
        return cultivo;
    }
    
    public String getPlaga() {
        return plaga;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public String getObservacionesGenerales() {
        return observaciones;
    }
    
    public String getObservacionesEspecificas() {
        return observacionesEspecificas;
    }
    
    // ✅ NUEVO: Getter para porcentaje de infestación
    public double getPorcentajeInfestacion() {
        return porcentajeInfestacion;
    }
    
    // ========== SETTERS ==========
    public void setId(int id) {
        this.id = id;
    }
    
    public void setPredio(String predio) {
        this.predio = predio;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public void setTecnicoId(String tecnicoId) {
        this.tecnicoId = tecnicoId;
    }
    
    public void setCultivo(String cultivo) {
        this.cultivo = cultivo;
    }
    
    public void setPlaga(String plaga) {
        this.plaga = plaga;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public void setObservacionesGenerales(String observacionesGenerales) {
        this.observaciones = observacionesGenerales;
    }
    
    public void setObservacionesEspecificas(String observacionesEspecificas) {
        this.observacionesEspecificas = observacionesEspecificas;
    }
    
    // ✅ NUEVO: Setter para porcentaje de infestación
    public void setPorcentajeInfestacion(double porcentajeInfestacion) {
        this.porcentajeInfestacion = porcentajeInfestacion;
    }
    
    @Override
    public String toString() {
        return "Inspeccion{" +
                "id=" + id +
                ", predio='" + predio + '\'' +
                ", fecha=" + fecha +
                ", tecnicoId='" + tecnicoId + '\'' +
                ", cultivo='" + cultivo + '\'' +
                ", plaga='" + plaga + '\'' +
                ", observacionesGenerales='" + observaciones + '\'' +
                ", observacionesEspecificas='" + observacionesEspecificas + '\'' +
                ", porcentajeInfestacion=" + porcentajeInfestacion + "%" +
                '}';
    }
}