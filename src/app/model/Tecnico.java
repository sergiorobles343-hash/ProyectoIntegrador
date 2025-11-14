package app.model;


public class Tecnico extends Usuario {
    private String especialidad;
    
    public Tecnico(String id, String nombreCompleto, String documento, String correo, String password) {
        super(id, nombreCompleto, documento, correo, password, "Tecnico");
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public String getNombre() {
        return super.getNombreCompleto();
    }
}

