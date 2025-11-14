package app.db;

import app.model.*;
import javax.swing.*;
import java.sql.*;

public class UserDatabase {
    
    // 🔹 GUARDAR USUARIO ACTUAL EN SESIÓN
    private static Usuario currentUser = null;
    
    /**
     * Registra un nuevo usuario en la base de datos asignándole un ID automático.
     * 
     * Este método:
     * 1. Calcula el siguiente ID disponible en la tabla {@code USUARIO}.
     * 2. Inserta un nuevo registro con los datos del usuario.
     * 3. Retorna {@code true} si la operación fue exitosa, o {@code false} si ocurrió un error.
     * 
     * En caso de que el documento o correo ya existan en la base de datos,
     * se muestra un mensaje de advertencia al usuario.
     * 
     * @param usuario Objeto {@link Usuario} con los datos del nuevo registro.
     * @return {@code true} si el usuario fue registrado correctamente; {@code false} en caso contrario.
     */
    
    // 🔹 Cache temporal de usuario cargado desde BD
    // (para asegurar que los estados de aprobación se mantengan)
    private static java.util.Map<String, Boolean> cacheAprobados = new java.util.HashMap<>();
    
    // 🔹 REGISTRAR USUARIO (genera ID automáticamente)
    public static boolean registrarUsuario(Usuario usuario) {
    Connection conn = null;
    try {
        conn = Conexion.getConnection();

        // ✅ USAR FUNCIÓN DE BD para obtener nuevo ID
        String sql = "SELECT fn_obtener_nuevo_id_usuario() AS NUEVO_ID FROM DUAL";
        int nextId = 1;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                nextId = rs.getInt("NUEVO_ID");
            }
        }

        System.out.println("🔢 Nuevo ID generado: " + nextId);
        usuario.setId(String.valueOf(nextId));

        // ✅ Usar procedimiento almacenado con el ID generado
        boolean exito = StoredProcedures.registrarUsuario(
            nextId,
            usuario.getNombreCompleto(),
            usuario.getDocumento(),
            usuario.getCorreo(),
            usuario.getPassword(),
            usuario.getRol()
        );

        return exito;

    } catch (SQLException e) {
        System.err.println("Error al registrar usuario: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
    
    /**
     * Valida las credenciales de un usuario utilizando su ID y contraseña.  
     * 
     * Este método está marcado como {@code @Deprecated} porque ha sido reemplazado
     * por una versión más actual que usa el correo electrónico en lugar del ID.  
     * Sin embargo, se mantiene por compatibilidad con versiones anteriores del sistema.  
     * 
     * Si las credenciales son correctas, devuelve el rol del usuario (por ejemplo, "ADMIN" o "PRODUCTOR").  
     * Si no coinciden, retorna {@code null}.
     * 
     * @param idUsuario Identificador único del usuario.
     * @param contrasena Contraseña asociada al usuario.
     * @return El rol del usuario si las credenciales son válidas, o {@code null} si no lo son.
     */
    
    // 🔹 VALIDAR LOGIN CON ID (método antiguo - mantener por compatibilidad)
    @Deprecated
    public static String validarUsuario(String idUsuario, String contrasena) {
        String sql = "SELECT ROL FROM USUARIO WHERE ID_USUARIO = ? AND CONTRASENA = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idUsuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("ROL");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error al validar usuario: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Valida las credenciales de un usuario usando su documento y contraseña.  
     * 
     * Realiza una búsqueda en la tabla {@code USUARIO} para verificar si existe un registro 
     * con el documento y la contraseña proporcionados.  
     * Si encuentra coincidencia, devuelve el rol del usuario (por ejemplo, "ADMIN" o "PRODUCTOR");  
     * de lo contrario, retorna {@code null}.  
     * 
     * @param documento Documento de identidad del usuario.
     * @param contrasena Contraseña del usuario.
     * @return El rol del usuario si las credenciales son válidas, o {@code null} si no hay coincidencia.
     */
    
// 🔹 VALIDAR LOGIN CON DOCUMENTO
public static String validarUsuarioPorDocumento(String documento, String contrasena) {
    String sql = "SELECT ROL, APROBADO FROM USUARIO WHERE DOCUMENTO = ? AND CONTRASENA = ?";
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, documento);
        ps.setString(2, contrasena);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            boolean aprobado = rs.getInt("APROBADO") == 1;
            if (!aprobado) {
                return "NO_APROBADO"; // ✅ Retorna código especial en vez de mostrar diálogo
            }
            return rs.getString("ROL"); // ✅ Solo retorna si está aprobado
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Error al validar usuario: " + e.getMessage());
        e.printStackTrace();
    }
    return null; // ✅ Credenciales incorrectas
}

    
    /**
     * Obtiene la información de un usuario a partir de su ID.  
     * 
     * Este método busca en la tabla {@code USUARIO} un registro con el ID especificado.  
     * Según el rol obtenido ("Productor", "Propietario" o "Tecnico"),  
     * crea y devuelve una instancia del tipo correspondiente.  
     * 
     * Si el usuario es un propietario, también se cargan sus predios asociados.  
     * 
     * @param idUsuario ID del usuario que se desea buscar.  
     * @return Un objeto {@link Usuario} correspondiente al registro encontrado,  
     *         o {@code null} si no existe ningún usuario con ese ID.  
     * @deprecated Este método se mantiene solo por compatibilidad con versiones anteriores.  
     */
    
    // 🔹 OBTENER USUARIO POR ID (método antiguo - mantener por compatibilidad)
    @Deprecated
    public static Usuario obtenerUsuarioPorId(String idUsuario) {
    String sql = "SELECT * FROM USUARIO WHERE ID_USUARIO = ?";
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, idUsuario);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String id = String.valueOf(rs.getInt("ID_USUARIO"));
            String nombre = rs.getString("NOMBRE");
            String documento = rs.getString("DOCUMENTO");
            String correo = rs.getString("CORREO");
            String contrasena = rs.getString("CONTRASENA");
            String rol = rs.getString("ROL");
            boolean aprobado = rs.getInt("APROBADO") == 1; // ✅ aquí tomamos el estado real

            System.out.println("🔍 ROL detectado: [" + rol + "] para usuario: " + id);
            System.out.println("🔍 Estado aprobado: " + aprobado);

            switch (rol.trim()) {
                case "Productor":
                    Productor productor = new Productor(id, nombre, documento, correo, contrasena);
                    productor.setAprobado(aprobado);
                    return productor;
                case "Propietario":
                    Propietario propietario = new Propietario(id, nombre, documento, correo, contrasena);
                    propietario.setAprobado(aprobado);
                    cargarPrediosDelPropietario(propietario);
                    return propietario;
                case "Tecnico":
                    Tecnico tecnico = new Tecnico(id, nombre, documento, correo, contrasena);
                    tecnico.setAprobado(aprobado);
                    return tecnico;
                default:
                    return null;
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

    
    /**
     * Obtiene la información de un usuario a partir de su documento de identidad.  
     * 
     * Este método consulta la tabla {@code USUARIO} para buscar un registro que coincida con  
     * el documento proporcionado. Según el rol obtenido ("Productor", "Propietario" o "Tecnico"),  
     * crea y devuelve una instancia del tipo correspondiente.  
     * 
     * Si el usuario es un propietario, también se cargan automáticamente sus predios asociados.  
     * 
     * @param documento Documento de identidad del usuario a buscar.  
     * @return Un objeto {@link Usuario} con los datos del usuario encontrado,  
     *         o {@code null} si no existe ningún registro con ese documento.  
     */
    
public static Usuario obtenerUsuarioPorDocumento(String documento) {
    String sql = "SELECT * FROM USUARIO WHERE DOCUMENTO = ?";
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, documento);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String id = String.valueOf(rs.getInt("ID_USUARIO"));
            String nombre = rs.getString("NOMBRE");
            String doc = rs.getString("DOCUMENTO");
            String correo = rs.getString("CORREO");
            String contrasena = rs.getString("CONTRASENA");
            String rol = rs.getString("ROL");
            boolean aprobado = rs.getInt("APROBADO") == 1; // ✅ Lee desde BD

            cacheAprobados.put(id, aprobado); // Guarda en cache

            Usuario usuario;
            switch (rol.trim()) {
                case "Productor":
                    usuario = new Productor(id, nombre, doc, correo, contrasena);
                    break;
                case "Propietario":
                    Propietario propietario = new Propietario(id, nombre, doc, correo, contrasena);
                    propietario.setAprobado(aprobado);
                    usuario = propietario;
                    break;
                case "Tecnico":
                    usuario = new Tecnico(id, nombre, doc, correo, contrasena);
                    break;
                default:
                    usuario = new Usuario(id, nombre, doc, correo, contrasena, rol);
            }

            usuario.setAprobado(aprobado); // ✅ Aplica el valor real
            return usuario;
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "❌ Error al obtener usuario: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}

    
    /**
     * Maneja la sesión del usuario actualmente autenticado en el sistema.  
     * 
     * Estos métodos permiten guardar y recuperar el usuario que ha iniciado sesión,  
     * manteniendo su información accesible durante la ejecución del programa.  
     * 
     * {@code setCurrentUser()} asigna el usuario activo a la variable estática {@code currentUser},  
     * mientras que {@code getCurrentUser()} devuelve el usuario actualmente en sesión.  
     * 
     * @param usuario Usuario que inicia sesión (para {@code setCurrentUser}).  
     * @return El usuario actualmente guardado en sesión (para {@code getCurrentUser}).  
     */
    
    public static void setCurrentUser(Usuario usuario) {
        currentUser = usuario;
    }
    
    public static Usuario getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Carga todos los predios asociados a un propietario desde la base de datos.
     *
     * Este método realiza una consulta SQL sobre la tabla {@code PREDIO} para
     * obtener los registros vinculados al propietario mediante su {@code ID_USUARIO}.
     * Luego, por cada registro encontrado, crea un objeto {@link Predio} y lo
     * asocia al objeto {@link Propietario} recibido.
     *
     * @param propietario El propietario cuyo listado de predios se desea cargar.
     *
     * Manejo de errores:
     * - Si el ID del propietario no es numérico, se captura un {@link NumberFormatException}.
     * - Si ocurre un error en la consulta SQL, se captura un {@link SQLException}.
     */
    
    // 🔹 CARGAR PREDIOS DE UN PROPIETARIO DESDE LA BD
    private static void cargarPrediosDelPropietario(Propietario propietario) {
        String sql = "SELECT * FROM PREDIO WHERE ID_USUARIO = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int idUsuario = Integer.parseInt(propietario.getId());
            ps.setInt(1, idUsuario);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                int idPredio = rs.getInt("ID_PREDIO");
                String numeroPredial = rs.getString("NUMERO_PREDIAL");
                String nombre = rs.getString("NOMBRE");
                String departamento = rs.getString("DEPARTAMENTO");
                String municipio = rs.getString("MUNICIPIO");
                String vereda = rs.getString("VEREDA");
                double latitud = rs.getDouble("COORDENADAS_LATITUD");
                double longitud = rs.getDouble("COORDENADAS_LONGITUD");
                
                Predio predio = new Predio(idPredio, numeroPredial, nombre, 
                                          departamento, municipio, vereda, 
                                          latitud, longitud, idUsuario);
                propietario.agregarPredio(predio);
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠ Error: El ID del usuario no es un número válido");
        } catch (SQLException e) {
            System.err.println("⚠ Error al cargar predios: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene una lista de usuarios filtrados por su rol desde la base de datos.
     *
     * Este método ejecuta una consulta SQL sobre la tabla {@code USUARIO}, 
     * seleccionando todos los registros cuyo campo {@code ROL} coincida con 
     * el parámetro proporcionado. Los resultados se ordenan alfabéticamente por nombre.
     *
     * @param rol Rol de usuario a filtrar (por ejemplo: "Productor", "Propietario", "Tecnico").
     * @return Una lista de objetos {@link Usuario} que pertenecen al rol especificado.
     *
     * Manejo de errores:
     * - Si ocurre un error durante la conexión o ejecución del SQL, se captura un {@link SQLException}.
     */
    
    // 🔹 OBTENER USUARIOS POR ROL
    public static java.util.List<Usuario> obtenerUsuariosPorRol(String rol) {
    java.util.List<Usuario> usuarios = new java.util.ArrayList<>();
    String sql = "SELECT * FROM USUARIO WHERE ROL = ? ORDER BY NOMBRE";

    try (Connection conn = Conexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, rol);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int idUsuario = rs.getInt("ID_USUARIO");
            String nombre = rs.getString("NOMBRE");
            String documento = rs.getString("DOCUMENTO");
            String correo = rs.getString("CORREO");
            String contrasena = rs.getString("CONTRASENA");
            String rolUsuario = rs.getString("ROL");
            boolean aprobado = rs.getInt("APROBADO") == 1; // ✅ nuevo

            Usuario usuario = new Usuario(
                String.valueOf(idUsuario),
                nombre,
                documento,
                correo,
                contrasena,
                rolUsuario,
                aprobado
            );

            cacheAprobados.put(String.valueOf(idUsuario), aprobado); // 🔹 Guardar estado
            usuarios.add(usuario);
        }

        System.out.println("✓ Se obtuvieron " + usuarios.size() + " usuarios con rol: " + rol);

    } catch (SQLException e) {
        System.err.println("Error al obtener usuarios por rol: " + e.getMessage());
        e.printStackTrace();
    }

    return usuarios;
}

    
    // 🔹 OBTENER NOMBRES DE USUARIOS POR ROL (para combos)
    public static java.util.List<String> obtenerNombresUsuariosPorRol(String rol) {
    java.util.List<String> nombres = new java.util.ArrayList<>();
    String sql = "{ ? = call FN_OBTENER_NOM_USUARIOS_ROL(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Registrar parámetro de retorno como CURSOR
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.setString(2, rol);
        
        // Ejecutar función
        cs.execute();
        
        // Obtener el cursor resultante
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                nombres.add(rs.getString("NOMBRE"));
            }
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener nombres de usuarios por rol: " + e.getMessage());
        e.printStackTrace();
    }
    
    return nombres;
}
 /**
     * Actualiza el estado de aprobación (activo/bloqueado) de un usuario.
     * 
     * Este método cambia el campo APROBADO en la tabla USUARIO.
     * Si APROBADO = 1 → usuario activo
     * Si APROBADO = 0 → usuario bloqueado o pendiente
     *
     * @param usuario Objeto Usuario con el nuevo estado de aprobación.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public static boolean actualizarAprobado(Usuario usuario) {
        String sql = "UPDATE USUARIO SET APROBADO = ? WHERE ID_USUARIO = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuario.isAprobado() ? 1 : 0);
            ps.setInt(2, Integer.parseInt(usuario.getId()));
            if ("999".equals(usuario.getId()) && !usuario.isAprobado()) {
            System.err.println("🚫 No se puede desaprobar al administrador del sistema");
            return false;
    }
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado de usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        }
    }
}