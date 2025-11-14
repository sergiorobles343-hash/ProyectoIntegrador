package app.model;

public class Plaga {
    private int idPlaga;
    private String nombreCientifico;
    private String nombreComun;

    // Constructor vacío
    public Plaga() {
    }

    // Constructor completo
    public Plaga(int idPlaga, String nombreCientifico, String nombreComun) {
        this.idPlaga = idPlaga;
        this.nombreCientifico = nombreCientifico;
        this.nombreComun = nombreComun;
    }

    // Constructor sin ID (para crear nuevas)
    public Plaga(String nombreCientifico, String nombreComun) {
        this.nombreCientifico = nombreCientifico;
        this.nombreComun = nombreComun;
    }

    // Getters y Setters
    public int getIdPlaga() {
        return idPlaga;
    }

    public void setIdPlaga(int idPlaga) {
        this.idPlaga = idPlaga;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    @Override
    public String toString() {
        return nombreComun != null && !nombreComun.isEmpty() 
               ? nombreComun + " (" + nombreCientifico + ")"
               : nombreCientifico;
    }
}