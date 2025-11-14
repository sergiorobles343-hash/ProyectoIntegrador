package app.db;

import app.model.Plaga;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlagaDatabase {

    // ==========================================
    // CREAR (Agregar una nueva plaga)
    // ==========================================
    /**
     * Agrega una nueva plaga a la base de datos.
     * @param plaga Objeto con los datos de la plaga.
     * @return true si la plaga fue agregada correctamente, false si ocurrió un error.
     */
    public static boolean agregarPlaga(Plaga plaga) {
    String sql = "{call sp_registrar_plaga_manual(?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {
        
        // ✅ Generar ID manualmente (igual que tu código original)
        int nuevoId = obtenerNuevoId();
        
        // Parámetros IN
        cs.setInt(1, nuevoId);
        cs.setString(2, plaga.getNombreCientifico());
        cs.setString(3, plaga.getNombreComun());
        
        // Parámetro OUT
        cs.registerOutParameter(4, Types.VARCHAR);  // p_mensaje

        // Ejecutar procedimiento
        cs.execute();

        String mensaje = cs.getString(4);

        if (mensaje.contains("exitosamente")) {
            System.out.println("✓ " + mensaje);
            return true;
        } else {
            System.err.println("❌ " + mensaje);
            return false;
        }

    } catch (SQLException e) {
        System.err.println("❌ Error al agregar plaga: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    // ==========================================
    // LEER (Obtener plaga por ID)
    // ==========================================
    /**
     * Busca y devuelve una plaga usando su ID.
     * @param idPlaga ID de la plaga.
     * @return Objeto Plaga encontrado o null si no existe.
     */
    public static Plaga obtenerPlagaPorId(int idPlaga) {
        String sql = "SELECT * FROM PLAGA WHERE ID_PLAGA = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPlaga);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return crearPlagaDesdeResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener plaga por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================
    // LEER (Obtener ID de plaga por nombre)
    // ==========================================
    /**
     * Obtiene el ID de una plaga a partir de su nombre común o científico.
     * @param nombre Nombre de la plaga.
     * @return ID de la plaga o -1 si no se encuentra.
     */
    public static int obtenerIdPlagaPorNombre(String nombre) {
        String sql = "SELECT ID_PLAGA FROM PLAGA WHERE NOMBRE_COMUN = ? OR NOMBRE_CIENTIFICO = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, nombre);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("ID_PLAGA");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de plaga: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // No encontrado
    }

    // ==========================================
    // LEER (Obtener todas las plagas)
    // ==========================================
    /**
     * Devuelve una lista con todas las plagas registradas.
     * @return Lista de plagas.
     */
    public static List<Plaga> obtenerTodasPlagas() {
        List<Plaga> plagas = new ArrayList<>();
        String sql = "SELECT * FROM PLAGA ORDER BY NOMBRE_COMUN";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                plagas.add(crearPlagaDesdeResultSet(rs));
            }
            
            System.out.println("✓ Se obtuvieron " + plagas.size() + " plagas");
            
        } catch (SQLException e) {
            System.err.println("Error al obtener plagas: " + e.getMessage());
            e.printStackTrace();
        }
        return plagas;
    }

    // ==========================================
    // LEER (Obtener nombres de plagas para combo)
    // ==========================================
    /**
     * Devuelve una lista con los nombres comunes de todas las plagas.
     * @return Lista de nombres de plagas.
     */
    public static List<String> obtenerNombresPlagas() {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT NOMBRE_COMUN FROM PLAGA ORDER BY NOMBRE_COMUN";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nombre = rs.getString("NOMBRE_COMUN");
                if (nombre != null && !nombre.isEmpty()) {
                    nombres.add(nombre);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener nombres de plagas: " + e.getMessage());
            e.printStackTrace();
        }
        return nombres;
    }

    // ==========================================
    // LEER (Obtener plagas asociadas a un cultivo)
    // ==========================================
    /**
     * Obtiene todas las plagas asociadas a un cultivo.
     * @param idCultivo ID del cultivo.
     * @return Lista de plagas relacionadas.
     */
    public static List<Plaga> obtenerPlagasPorCultivo(int idCultivo) {
    List<Plaga> plagas = new ArrayList<>();
    String sql = "{ ? = call fn_obtener_plagas_por_cultivo(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Registrar parámetro de retorno como CURSOR
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.setInt(2, idCultivo);
        
        // Ejecutar función
        cs.execute();
        
        // Obtener el cursor resultante
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                plagas.add(crearPlagaDesdeResultSet(rs));
            }
        }

        System.out.println("✓ Se obtuvieron " + plagas.size() + " plagas asociadas al cultivo");

    } catch (SQLException e) {
        System.err.println("Error al obtener plagas por cultivo: " + e.getMessage());
        e.printStackTrace();
    }
    return plagas;
}

    // ==========================================
    // ACTUALIZAR (Modificar una plaga)
    // ==========================================
    /**
     * Actualiza los datos de una plaga existente.
     * @param plaga Objeto con los nuevos datos.
     * @param idPlaga ID de la plaga a modificar.
     * @return true si se actualizó correctamente, false si no.
     */
    public static boolean actualizarPlaga(Plaga plaga, int idPlaga) {
        String sql = "UPDATE PLAGA SET NOMBRE_CIENTIFICO = ?, NOMBRE_COMUN = ? WHERE ID_PLAGA = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, plaga.getNombreCientifico());
            pstmt.setString(2, plaga.getNombreComun());
            pstmt.setInt(3, idPlaga);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Plaga actualizada exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar plaga: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // ELIMINAR (Borrar una plaga)
    // ==========================================
    /**
     * Elimina una plaga de la base de datos.
     * @param idPlaga ID de la plaga a eliminar.
     * @return true si fue eliminada correctamente, false si ocurrió un error.
     */
    public static boolean eliminarPlaga(int idPlaga) {
        String sql = "DELETE FROM PLAGA WHERE ID_PLAGA = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPlaga);
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Plaga eliminada exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar plaga: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // VERIFICAR si existe plaga por nombre
    // ==========================================
    /**
     * Verifica si una plaga existe por su nombre común o científico.
     * @param nombre Nombre de la plaga.
     * @return true si ya existe, false si no.
     */
    public static boolean existePlagaPorNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM PLAGA WHERE NOMBRE_COMUN = ? OR NOMBRE_CIENTIFICO = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, nombre);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de plaga: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // BUSCAR o CREAR plaga (útil para registros rápidos)
    // ==========================================
    /**
     * Busca una plaga por nombre. Si no existe, la crea automáticamente.
     * @param nombrePlaga Nombre de la plaga.
     * @return ID de la plaga existente o recién creada, o -1 si falla.
     */
    public static int buscarOCrearPlaga(String nombrePlaga) {
        int idExistente = obtenerIdPlagaPorNombre(nombrePlaga);
        if (idExistente != -1) {
            return idExistente;
        }
        
        Plaga nuevaPlaga = new Plaga();
        nuevaPlaga.setNombreComun(nombrePlaga);
        nuevaPlaga.setNombreCientifico(nombrePlaga);
        
        if (agregarPlaga(nuevaPlaga)) {
            return obtenerIdPlagaPorNombre(nombrePlaga);
        }
        
        return -1;
    }

    // ==========================================
    // OBTENER NUEVO ID
    // ==========================================
    /**
     * Genera un nuevo ID para una plaga incrementando el máximo existente.
     * @return Nuevo ID disponible.
     */
    private static int obtenerNuevoId() {
        String sql = "SELECT NVL(MAX(ID_PLAGA), 0) + 1 AS NUEVO_ID FROM PLAGA";
        
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("NUEVO_ID");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener nuevo ID de plaga: " + e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }

    // ==========================================
    // MÉTODO AUXILIAR: Crear Plaga desde ResultSet
    // ==========================================
    /**
     * Crea un objeto Plaga a partir de los datos de una fila del ResultSet.
     * @param rs Resultado de la consulta SQL.
     * @return Objeto Plaga.
     * @throws SQLException Si ocurre un error al leer los datos.
     */
    private static Plaga crearPlagaDesdeResultSet(ResultSet rs) throws SQLException {
        Plaga plaga = new Plaga();
        plaga.setIdPlaga(rs.getInt("ID_PLAGA"));
        plaga.setNombreCientifico(rs.getString("NOMBRE_CIENTIFICO"));
        plaga.setNombreComun(rs.getString("NOMBRE_COMUN"));
        return plaga;
}
    
public static List<Plaga> obtenerPlagas() {
    List<Plaga> lista = new ArrayList<>();
    String sql = "SELECT ID_PLAGA, NOMBRE_COMUN FROM PLAGA ORDER BY NOMBRE_COMUN";

    try (Connection conn = Conexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            Plaga pl = new Plaga();
            pl.setIdPlaga(rs.getInt("ID_PLAGA")); // ✅ Corregido
            pl.setNombreComun(rs.getString("NOMBRE_COMUN"));
            lista.add(pl);
        }

    } catch (Exception e) {
        System.err.println("Error obteniendo plagas: " + e.getMessage());
        e.printStackTrace();
    }

    return lista;
}
}
