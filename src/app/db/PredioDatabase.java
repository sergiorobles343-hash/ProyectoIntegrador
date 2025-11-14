package app.db;

import app.model.Predio;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de manejar las operaciones de base de datos relacionadas con los predios.
 * Incluye métodos para agregar, actualizar, eliminar y consultar registros en la tabla PREDIO.
 */
public class PredioDatabase {
    
// 🔹 AGREGAR NUEVO PREDIO
public static boolean agregarPredio(Predio predio) {
    String sql = "{call sp_registrar_predio_completo(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // 🔹 Obtener el siguiente ID disponible para PREDIO
        int nextId = obtenerNuevoIdPredio();

        // Parámetros IN
        cs.setInt(1, nextId);
        cs.setString(2, predio.getNumeroPredial());
        cs.setString(3, predio.getNombre());
        cs.setString(4, predio.getDepartamento());
        cs.setString(5, predio.getMunicipio());
        cs.setString(6, predio.getVereda());
        cs.setDouble(7, predio.getLatitud());
        cs.setDouble(8, predio.getLongitud());
        cs.setInt(9, predio.getIdPropietario());
        
        // Parámetro OUT
        cs.registerOutParameter(10, Types.VARCHAR);  // p_mensaje

        // Ejecutar procedimiento
        cs.execute();

        String mensaje = cs.getString(10);

        if (mensaje.contains("agregado")) {
            System.out.println("✅ " + mensaje);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, mensaje);
            return false;
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "❌ Error de conexión: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
private static int obtenerNuevoIdPredio() {
    String sql = "SELECT NVL(MAX(ID_PREDIO), 0) + 1 AS NUEVO_ID FROM PREDIO";
    try (Connection conn = Conexion.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) return rs.getInt("NUEVO_ID");
    } catch (SQLException e) {
        System.err.println("Error al obtener nuevo ID de predio: " + e.getMessage());
    }
    return 1;
}

    
     /**
     * Obtiene todos los predios registrados en la base de datos.
     * <p>
     * Este método realiza una consulta SQL para traer todos los registros de la tabla PREDIO,
     * los ordena por nombre y los convierte en objetos
     *  */
    
    // 🔹 OBTENER TODOS LOS PREDIOS
    public static List<Predio> obtenerPredios() {
        List<Predio> predios = new ArrayList<>();
        String sql = "SELECT * FROM PREDIO ORDER BY NOMBRE";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int idPredio = rs.getInt("ID_PREDIO");
                String numeroPredial = rs.getString("NUMERO_PREDIAL");
                String nombre = rs.getString("NOMBRE");
                String departamento = rs.getString("DEPARTAMENTO");
                String municipio = rs.getString("MUNICIPIO");
                String vereda = rs.getString("VEREDA");
                double latitud = rs.getDouble("COORDENADAS_LATITUD");
                double longitud = rs.getDouble("COORDENADAS_LONGITUD");
                int idUsuario = rs.getInt("ID_USUARIO");
                
                Predio predio = new Predio(idPredio, numeroPredial, nombre, 
                                          departamento, municipio, vereda, 
                                          latitud, longitud, idUsuario);
                predios.add(predio);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "❌ Error al obtener predios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return predios;
    }
    
    /**
     * Obtiene todos los predios registrados por un usuario específico
     * Busca en la base de datos todos los predios cuyo ID de usuario coincida con el indicado
     * y los devuelve en una lista ordenada por nombre.
     */
    
    // 🔹 OBTENER PREDIOS POR USUARIO (PRODUCTOR)
    public static List<Predio> obtenerPrediosPorPropietario(int idUsuario) {
        List<Predio> predios = new ArrayList<>();
        String sql = "SELECT * FROM PREDIO WHERE ID_PROPIETARIO = ? ORDER BY NOMBRE";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
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
                int idPropietario = rs.getInt("ID_PROPIETARIO");
                
                Predio predio = new Predio(idPredio, numeroPredial, nombre, 
                          departamento, municipio, vereda, 
                          latitud, longitud, idPropietario);
                predios.add(predio);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "❌ Error al obtener predios del usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return predios;
    }
    
// 🔹 OBTENER PREDIOS ASIGNADOS A UN PRODUCTOR (usando tabla intermedia)
public static List<Predio> obtenerPrediosPorProductor(int idProductor) {
    List<Predio> predios = new ArrayList<>();
    String sql = "{ ? = call FN_OBTENER_PREDIOS_PRODUCTOR(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Registrar parámetro de retorno como CURSOR
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.setInt(2, idProductor);
        
        // Ejecutar función
        cs.execute();
        
        // Obtener el cursor resultante
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                Predio predio = new Predio(
                    rs.getInt("ID_PREDIO"),
                    rs.getString("NUMERO_PREDIAL"),
                    rs.getString("NOMBRE"),
                    rs.getString("DEPARTAMENTO"),
                    rs.getString("MUNICIPIO"),
                    rs.getString("VEREDA"),
                    rs.getDouble("COORDENADAS_LATITUD"),
                    rs.getDouble("COORDENADAS_LONGITUD"),
                    rs.getInt("ID_PROPIETARIO") // se mantiene el dueño real
                );
                predios.add(predio);
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null,
                "❌ Error al obtener predios del productor: " + e.getMessage());
        e.printStackTrace();
    }

    return predios;
}

     /**
     * Busca un predio en la base de datos usando su número predial.
     * Realiza una consulta en la tabla PREDIO para encontrar el registro que coincida
     * con el número predial ingresado. Si lo encuentra, devuelve un objeto {@link Predio}
     * con todos sus datos; si no, retorna {@code null}.
     */
    
    // 🔹 BUSCAR PREDIO POR NÚMERO PREDIAL
    public static Predio buscarPredioPorNumeroPredial(String numeroPredial) {
        String sql = "SELECT * FROM PREDIO WHERE NUMERO_PREDIAL = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, numeroPredial);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int idPredio = rs.getInt("ID_PREDIO");
                String nombre = rs.getString("NOMBRE");
                String departamento = rs.getString("DEPARTAMENTO");
                String municipio = rs.getString("MUNICIPIO");
                String vereda = rs.getString("VEREDA");
                double latitud = rs.getDouble("COORDENADAS_LATITUD");
                double longitud = rs.getDouble("COORDENADAS_LONGITUD");
                int idUsuario = rs.getInt("ID_USUARIO");
                
                return new Predio(idPredio, numeroPredial, nombre, 
                                departamento, municipio, vereda, 
                                latitud, longitud, idUsuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar predio: " + e.getMessage());
        }
        
        return null;
    }
    /**
     * Busca un predio en la base de datos por su nombre.
   
     * Realiza una búsqueda en la tabla PREDIO ignorando mayúsculas y minúsculas
     * para encontrar un registro cuyo nombre coincida con el proporcionado.
     * Si se encuentra, devuelve un objeto {@link Predio} con toda su información.
     * @param nombre Nombre del predio a buscar.
     * @return Objeto {@link Predio} con los datos del predio encontrado,
     *         o {@code null} si no existe ningún registro con ese nombre.
     */
    
    // 🔹 BUSCAR PREDIO POR NOMBRE
    public static Predio buscarPredioPorNombre(String nombre) {
        String sql = "SELECT * FROM PREDIO WHERE UPPER(NOMBRE) = UPPER(?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int idPredio = rs.getInt("ID_PREDIO");
                String numeroPredial = rs.getString("NUMERO_PREDIAL");
                String departamento = rs.getString("DEPARTAMENTO");
                String municipio = rs.getString("MUNICIPIO");
                String vereda = rs.getString("VEREDA");
                double latitud = rs.getDouble("COORDENADAS_LATITUD");
                double longitud = rs.getDouble("COORDENADAS_LONGITUD");
                int idUsuario = rs.getInt("ID_USUARIO");
                
                return new Predio(idPredio, numeroPredial, nombre, 
                                departamento, municipio, vereda, 
                                latitud, longitud, idUsuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar predio: " + e.getMessage());
        }
        
        return null;
    }

       /**
     * Actualiza la información de un predio existente en la base de datos.
     * <p>
     * Modifica los datos del predio según el número predial, incluyendo nombre,
     * ubicación, coordenadas y propietario. Si el predio existe, sus valores
     * se reemplazan con los nuevos proporcionados.
     * </p>
     *
     * @param predio Objeto {@link Predio} con los datos actualizados del predio.
     * @return {@code true} si la actualización fue exitosa, o {@code false} si ocurrió un error
     *         o no se encontró el registro correspondiente.
     */
    

// 🔹 ACTUALIZAR PREDIO (incluyendo propietario)
public static boolean actualizarPredio(Predio predio) {
    String sql = "{call sp_actualizar_predio(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Parámetros IN
        cs.setString(1, predio.getNumeroPredial());
        cs.setString(2, predio.getNombre());
        cs.setString(3, predio.getDepartamento());
        cs.setString(4, predio.getMunicipio());
        cs.setString(5, predio.getVereda());
        cs.setDouble(6, predio.getLatitud());
        cs.setDouble(7, predio.getLongitud());
        cs.setInt(8, predio.getIdUsuario());  // ID_PROPIETARIO
        
        // Parámetro OUT
        cs.registerOutParameter(9, Types.VARCHAR);  // p_mensaje

        // Ejecutar procedimiento
        cs.execute();

        String mensaje = cs.getString(9);

        if (mensaje.contains("exitosamente")) {
            System.out.println("✓ " + mensaje);
            return true;
        } else {
            System.err.println("❌ " + mensaje);
            return false;
        }

    } catch (SQLException e) {
        System.err.println("❌ Error al actualizar el predio: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
    
    // 🔹 ELIMINAR PREDIO POR NÚMERO PREDIAL
public static boolean eliminarPredio(String numeroPredial) {
    Connection conn = null;
    try {
        conn = Conexion.getConnection();
        conn.setAutoCommit(false); // Iniciar transacción
        
        // 1. Obtener ID del predio
        String sqlId = "SELECT ID_PREDIO FROM PREDIO WHERE NUMERO_PREDIAL = ?";
        int idPredio = -1;
        
        try (PreparedStatement ps = conn.prepareStatement(sqlId)) {
            ps.setString(1, numeroPredial);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idPredio = rs.getInt("ID_PREDIO");
            }
        }
        
        if (idPredio == -1) {
            conn.rollback();
            return false;
        }
        // 🔹 Eliminar asignaciones del predio (si existen)
        String sqlAsignacion = "DELETE FROM ASIGNACION_PREDIO_PRODUCTOR WHERE ID_PREDIO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlAsignacion)) {
            ps.setInt(1, idPredio);
            ps.executeUpdate();
        }
        
        // 2. Eliminar detalles de inspecciones relacionadas
        String sqlDetalles = "DELETE FROM DETALLE_INSPECCION WHERE ID_INSPECCION IN " +
                            "(SELECT ID_INSPECCION FROM INSPECCION WHERE ID_PREDIO = ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlDetalles)) {
            ps.setInt(1, idPredio);
            ps.executeUpdate();
        }
        
        // 3. Eliminar inspecciones del predio
        String sqlInspeccion = "DELETE FROM INSPECCION WHERE ID_PREDIO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlInspeccion)) {
            ps.setInt(1, idPredio);
            ps.executeUpdate();
        }
        
        // 4. Eliminar cultivos del predio
        String sqlCultivo = "DELETE FROM CULTIVO WHERE ID_PREDIO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlCultivo)) {
            ps.setInt(1, idPredio);
            ps.executeUpdate();
        }
        
        // 5. Finalmente eliminar el predio
        String sqlPredio = "DELETE FROM PREDIO WHERE NUMERO_PREDIAL = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlPredio)) {
            ps.setString(1, numeroPredial);
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                conn.commit();
                return true;
            }
        }
        
        conn.rollback();
        return false;
        
    } catch (SQLException e) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, 
            "❌ Error al eliminar el predio: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
     /**
     * Elimina un predio de la base de datos usando su número predial.
     * <p>
     * Este método elimina de forma segura todas las dependencias del predio,
     * incluyendo inspecciones, detalles de inspección y cultivos asociados,
     * antes de eliminar el registro principal del predio.  
     * Utiliza transacciones para garantizar la integridad de los datos:
     * si ocurre un error, se realiza un rollback de todos los cambios.
     */

    // 🔹 ELIMINAR PREDIO POR NOMBRE (método legacy para compatibilidad)
    @Deprecated
    public static boolean eliminarPredioPorNombre(String nombre) {
        String sql = "DELETE FROM PREDIO WHERE UPPER(NOMBRE) = UPPER(?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "❌ Error al eliminar el predio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }   
    
     /**
     * Cuenta la cantidad total de predios asociados a un usuario específico.
     * Realiza una consulta a la base de datos para obtener el número de registros
     * en la tabla {@code PREDIO} que pertenecen al usuario indicado por su ID.
     */
    
    // 🔹 CONTAR PREDIOS DE UN USUARIO
    public static int contarPrediosPorPropietario(int idPropietario) {
        String sql = "SELECT COUNT(*) FROM PREDIO WHERE ID_PROPIETARIO = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPropietario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar predios: " + e.getMessage());
        }
        
        return 0;
    }
    
     /**
     * Obtiene el identificador (ID) de un predio a partir de su nombre o número predial.  
     * Busca coincidencias en la tabla {@code PREDIO}, sin distinguir entre mayúsculas y minúsculas.  
     * Si el predio existe, devuelve su ID; en caso contrario, retorna {@code -1}.  
     */
    
    // 🔹 OBTENER ID DE PREDIO POR NOMBRE (usado por InspeccionDatabase)
public static int obtenerIdPredioPorNombre(String nombre) {
    String sql = "SELECT ID_PREDIO FROM PREDIO WHERE UPPER(NOMBRE) = UPPER(?) OR NUMERO_PREDIAL = ?";
    
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, nombre);
        ps.setString(2, nombre);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("ID_PREDIO");
        } else {
            System.err.println("⚠ No se encontró predio con nombre o número predial: " + nombre);
        }
        
    } catch (SQLException e) {
        System.err.println("Error al obtener ID de predio: " + e.getMessage());
        e.printStackTrace();
    }
    
    return -1; // Retorna -1 si no se encuentra
}

    /**
     * Obtiene una lista con los nombres de todos los predios registrados en la base de datos.  
     * Los nombres se ordenan alfabéticamente y se utilizan, por ejemplo, para llenar listas o menús desplegables.  
     * Devuelve una lista vacía si ocurre un error o no hay registros.  
     */

// 🔹 OBTENER NOMBRES DE PREDIOS (para llenar combos en RegistrarInspeccionFrame)
public static List<String> obtenerNombresPredios() {
    List<String> nombres = new ArrayList<>();
    String sql = "SELECT NOMBRE FROM PREDIO ORDER BY NOMBRE";
    
    try (Connection conn = Conexion.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            nombres.add(rs.getString("NOMBRE"));
        }
        
    } catch (SQLException e) {
        System.err.println("Error al obtener nombres de predios: " + e.getMessage());
        e.printStackTrace();
    }
    
    return nombres;
}
   /**
     * Obtiene los nombres de los predios asociados a un usuario específico.  
     * Realiza una consulta filtrando por el ID del usuario y devuelve los nombres ordenados alfabéticamente.  
     * Retorna una lista vacía si el usuario no tiene predios registrados o ocurre un error.  
     */

// 🔹 OBTENER NOMBRES DE PREDIOS POR USUARIO (para productores específicos)
public static List<String> obtenerNombresPrediosPorUsuario(int idUsuario) {
    List<String> nombres = new ArrayList<>();
    String sql = "SELECT NOMBRE FROM PREDIO WHERE ID_PROPIETARIO = ? ORDER BY NOMBRE";
    
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setInt(1, idUsuario);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            nombres.add(rs.getString("NOMBRE"));
        }
        
    } catch (SQLException e) {
        System.err.println("Error al obtener nombres de predios del usuario: " + e.getMessage());
        e.printStackTrace();
    }
    
    return nombres;
}

 // 🔹 ASIGNAR PRODUCTOR A PREDIO (con ID autogenerado y fecha)
 // 🔹 ASIGNAR PRODUCTOR A PREDIO (con ID autogenerado y fecha)
public static boolean asignarProductorAPredio(int idProductor, String numeroPredial) {
    // ✅ 1. Obtener ID del predio
    int idPredio = obtenerIdPredioPorNumeroPredial(numeroPredial);
    if (idPredio == -1) {
        System.err.println("⚠ No se encontró el predio con número: " + numeroPredial);
        return false;
    }
    
    // ✅ 2. Obtener ID del propietario del predio
    int idPropietario = obtenerPropietarioDelPredio(idPredio);
    if (idPropietario == -1) {
        System.err.println("⚠ No se pudo obtener el propietario del predio");
        return false;
    }
    
    // ✅ 3. Generar ID de asignación manualmente (TU LÓGICA)
    String sqlMax = "SELECT NVL(MAX(ID_ASIGNACION), 0) + 1 AS NEXT_ID FROM ASIGNACION_PREDIO_PRODUCTOR";
    int nextId = 1;
    
    try (Connection conn = Conexion.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sqlMax)) {
        if (rs.next()) {
            nextId = rs.getInt("NEXT_ID");
        }
    } catch (SQLException e) {
        System.err.println("Error al generar ID: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
    
    // ✅ 4. Usar procedimiento almacenado CON el ID generado
    boolean exito = StoredProcedures.asignarProductor(
        nextId,
        idPropietario,
        idProductor,
        idPredio
    );
    
    if (exito) {
        System.out.println("✓ Productor asignado correctamente al predio " + numeroPredial);
    }
    
    return exito;
}

/**
 * Método auxiliar para obtener el ID del propietario de un predio
 */
private static int obtenerPropietarioDelPredio(int idPredio) {
    String sql = "SELECT ID_PROPIETARIO FROM PREDIO WHERE ID_PREDIO = ?";
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idPredio);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("ID_PROPIETARIO");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1;
}

    /**
     * Método de compatibilidad que devuelve la lista completa de predios.  
     * Está marcado como {@code @Deprecated} ya que su uso se mantiene solo por compatibilidad con versiones anteriores.  
     * Se recomienda utilizar el método {@link #obtenerPredios()} en su lugar.  
     */

    // 🔹 MÉTODO LEGACY (mantener para compatibilidad)
    @Deprecated
    public static List<Predio> getPredios() {
        return obtenerPredios();
    }
// 🔹 OBTENER ID DE PREDIO POR NÚMERO PREDIAL
public static int obtenerIdPredioPorNumeroPredial(String numeroPredial) {
    String sql = "SELECT ID_PREDIO FROM PREDIO WHERE NUMERO_PREDIAL = ?";
    
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, numeroPredial);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("ID_PREDIO");
        }
        
    } catch (SQLException e) {
        System.err.println("Error al buscar ID del predio por número predial: " + e.getMessage());
    }
    
    return -1; // No encontrado
}
// 🔹 OBTENER PREDIOS ASIGNADOS A UN PRODUCTOR (con tabla intermedia)
public static List<Predio> obtenerPrediosPorUsuario(int idProductor) {
    List<Predio> predios = new ArrayList<>();
    String sql = "{ ? = call fn_obtener_predios_por_usuario(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Registrar parámetro de retorno como CURSOR
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.setInt(2, idProductor);
        
        // Ejecutar función
        cs.execute();
        
        // Obtener el cursor resultante
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                Predio predio = new Predio(
                    rs.getInt("ID_PREDIO"),
                    rs.getString("NUMERO_PREDIAL"),
                    rs.getString("NOMBRE"),
                    rs.getString("DEPARTAMENTO"),
                    rs.getString("MUNICIPIO"),
                    rs.getString("VEREDA"),
                    rs.getDouble("COORDENADAS_LATITUD"),
                    rs.getDouble("COORDENADAS_LONGITUD"),
                    0 // Ya no usamos ID_USUARIO del predio
                );
                predios.add(predio);
            }
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "❌ Error al obtener predios: " + e.getMessage());
        e.printStackTrace();
    }
    
    return predios;
}
// 🔹 DESASIGNAR PRODUCTOR DE UN PREDIO
public static boolean desasignarProductorDePredio(int idProductor, String numeroPredial) {
    int idPredio = obtenerIdPredioPorNumeroPredial(numeroPredial);
    if (idPredio == -1) {
        JOptionPane.showMessageDialog(null, "⚠ No se encontró el predio con número: " + numeroPredial);
        return false;
    }

    // Verificar si este productor es el propietario original del predio
    String sqlVerificar = "SELECT ID_PROPIETARIO FROM PREDIO WHERE ID_PREDIO = ?";
    try (Connection conn = Conexion.getConnection();
         PreparedStatement psCheck = conn.prepareStatement(sqlVerificar)) {

        psCheck.setInt(1, idPredio);
        ResultSet rs = psCheck.executeQuery();
        if (rs.next()) {
            int idPropietario = rs.getInt("ID_PROPIETARIO");
            if (idPropietario == idProductor) {
                JOptionPane.showMessageDialog(null,
                    "⚠ No puedes desasignar al propietario original del predio.");
                return false;
            }
        }
    } catch (SQLException e) {
        System.err.println("❌ Error al verificar propietario: " + e.getMessage());
        return false;
    }

    // Eliminar la relación de la tabla intermedia
    String sqlDelete = "DELETE FROM ASIGNACION_PREDIO_PRODUCTOR WHERE ID_PREDIO = ? AND ID_PRODUCTOR = ?";
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlDelete)) {

        ps.setInt(1, idPredio);
        ps.setInt(2, idProductor);

        int filas = ps.executeUpdate();
        if (filas > 0) {
            JOptionPane.showMessageDialog(null, "✅ Productor desasignado correctamente del predio.");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "⚠ No se encontró asignación para eliminar.");
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "❌ Error al desasignar productor: " + e.getMessage());
        e.printStackTrace();
    }

    return false;
}


}