package app.model;

/**
 * Clase base para todos los usuarios del sistema fitosanitario.
 * Compatible con la tabla USUARIO en Oracle.
 */
public class Usuario {

    private String id;
    private String nombreCompleto;
    private String documento;
    private String correo;
    private String password;
    private String rol;
    private boolean aprobado; // 🔹 Indica si el admin ya aprobó al usuario

    // 🔹 Constructor principal
    public Usuario(String id, String nombreCompleto, String documento,
                   String correo, String password, String rol) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.documento = documento;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.aprobado = false; // 🔹 Por defecto, el usuario NO está aprobado
    }

    // 🔹 Constructor alterno (por si lo necesitas al cargar desde BD)
    public Usuario(String id, String nombreCompleto, String documento,
                   String correo, String password, String rol, boolean aprobado) {
        this(id, nombreCompleto, documento, correo, password, rol);
        this.aprobado = aprobado;
    }

    // 🔹 Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }

    // 🔹 Devuelve el nombre del usuario (usado en pantallas de bienvenida)
    public String getNombre() { return nombreCompleto; }

    @Override
    public String toString() {
        return nombreCompleto + " - " + rol + (aprobado ? " ✅" : " ❌ Pendiente");
    }
}
