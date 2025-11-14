package app.model;

/**
 * Clase que representa un predio o finca en el sistema fitosanitario
 * @author sergi
 */
public class Predio {
    private int idPredio;
    private String numeroPredial;
    private String nombre;
    private String departamento;
    private String municipio;
    private String vereda;
    private double latitud;
    private double longitud;
    private int idUsuario;
    private int idPropietario;
    
    // Constructor completo (para cargar desde BD)
public Predio(int idPredio, String numeroPredial, String nombre, 
              String departamento, String municipio, String vereda,
              double latitud, double longitud, int idPropietario) {
    this.idPredio = idPredio;
    this.numeroPredial = numeroPredial;
    this.nombre = nombre;
    this.departamento = departamento;
    this.municipio = municipio;
    this.vereda = vereda;
    this.latitud = latitud;
    this.longitud = longitud;
    this.idPropietario = idPropietario;
    this.idUsuario = 0; // NULL/sin asignar inicialmente
}
    
    // Constructor simplificado (para crear nuevos predios)
public Predio(String numeroPredial, String nombre, String departamento, 
              String municipio, String vereda, double latitud, 
              double longitud, int idPropietario) {
    this.numeroPredial = numeroPredial;
    this.nombre = nombre;
    this.departamento = departamento;
    this.municipio = municipio;
    this.vereda = vereda;
    this.latitud = latitud;
    this.longitud = longitud;
    this.idPropietario = idPropietario;
    this.idUsuario = 0; // NULL/sin asignar inicialmente
}
    
    // Getters y Setters
    public int getIdPredio() {
        return idPredio;
    }
    
    public void setIdPredio(int idPredio) {
        this.idPredio = idPredio;
    }
    
    public String getNumeroPredial() {
        return numeroPredial;
    }
    
    public void setNumeroPredial(String numeroPredial) {
        this.numeroPredial = numeroPredial;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public String getMunicipio() {
        return municipio;
    }
    
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
    
    public String getVereda() {
        return vereda;
    }
    
    public void setVereda(String vereda) {
        this.vereda = vereda;
    }
    
    public double getLatitud() {
        return latitud;
    }
    
    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
    
    public double getLongitud() {
        return longitud;
    }
    
    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    public int getIdPropietario() {
    return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }
    
    // Métodos de utilidad
    public String getUbicacionCompleta() {
        StringBuilder ubicacion = new StringBuilder();
        if (vereda != null && !vereda.isEmpty()) {
            ubicacion.append("Vereda ").append(vereda).append(", ");
        }
        ubicacion.append(municipio).append(", ").append(departamento);
        return ubicacion.toString();
    }
    
    public String getCoordenadas() {
        return String.format("%.6f, %.6f", latitud, longitud);
    }
    
    @Override
    public String toString() {
        return nombre + " (" + numeroPredial + ")";
    }
    
public String toStringDetallado() {
    return "Predio{" +
            "idPredio=" + idPredio +
            ", numeroPredial='" + numeroPredial + '\'' +
            ", nombre='" + nombre + '\'' +
            ", departamento='" + departamento + '\'' +
            ", municipio='" + municipio + '\'' +
            ", vereda='" + vereda + '\'' +
            ", latitud=" + latitud +
            ", longitud=" + longitud +
            ", idPropietario=" + idPropietario +
            ", idUsuario=" + idUsuario +
            '}';
}
}