package app.model;

public class Cultivo {
    private int idCultivo;
    private String especieCientifica;
    private String nombreComun;
    private String variedad;
    private String ciclo; // Corto, Medio, Largo
    private String idPredio;

    // Constructor vacío
    public Cultivo() {
    }

    // Constructor completo
    public Cultivo(int idCultivo, String especieCientifica, String nombreComun, 
                   String variedad, String ciclo, String idPredio) {
        this.idCultivo = idCultivo;
        this.especieCientifica = especieCientifica;
        this.nombreComun = nombreComun;
        this.variedad = variedad;
        this.ciclo = ciclo;
        this.idPredio = idPredio;
    }

    // Constructor sin ID (para crear nuevos)
    public Cultivo(String especieCientifica, String nombreComun, 
                   String variedad, String ciclo, String idPredio) {
        this.especieCientifica = especieCientifica;
        this.nombreComun = nombreComun;
        this.variedad = variedad;
        this.ciclo = ciclo;
        this.idPredio = idPredio;
    }

    // Getters y Setters
    public int getIdCultivo() {
        return idCultivo;
    }

    public void setIdCultivo(int idCultivo) {
        this.idCultivo = idCultivo;
    }

    public String getEspecieCientifica() {
        return especieCientifica;
    }

    public void setEspecieCientifica(String especieCientifica) {
        this.especieCientifica = especieCientifica;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    public String getVariedad() {
        return variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(String idPredio) {
        this.idPredio = idPredio;
    }

    @Override
    public String toString() {
        return idCultivo + " - " + nombreComun;
    }
    
}
