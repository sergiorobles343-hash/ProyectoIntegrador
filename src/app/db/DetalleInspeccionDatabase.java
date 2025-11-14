package app.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase encargada de manejar todas las operaciones relacionadas con la tabla DETALLE_INSPECCION.
 * 
 * Contiene métodos para crear, leer, actualizar y eliminar registros, así como obtener estadísticas
 * y asociaciones entre cultivos y plagas.
 */
public class DetalleInspeccionDatabase {

    // ==========================================
    // CREAR (Agregar un detalle de inspección completo)
    // ==========================================

    /**
     * Agrega un nuevo registro en la tabla DETALLE_INSPECCION.
     * 
     * @param idInspeccion ID de la inspección asociada.
     * @param idCultivo ID del cultivo asociado.
     * @param idPlaga ID de la plaga asociada (puede ser 0 si no aplica).
     * @param cantidadPlantasAfectadas Número de plantas afectadas.
     * @param porcentajeInfestacion Porcentaje de infestación registrado.
     * @param observacionesEspecificas Observaciones adicionales.
     * @return true si el registro se insertó correctamente, false en caso de error.
     */
    public static boolean agregarDetalleInspeccion(int idInspeccion, int idCultivo, int idPlaga,
                                               int cantidadPlantasAfectadas, double porcentajeInfestacion, 
                                               String observacionesEspecificas) {
    
    // ✅ LLAMAR DIRECTAMENTE al SP de StoredProcedures
    int idDetalle = StoredProcedures.agregarDetalleInspeccion(
        idInspeccion, idCultivo, idPlaga,
        cantidadPlantasAfectadas, porcentajeInfestacion, observacionesEspecificas
    );
    
    return idDetalle > 0;
}

    // ==========================================
    // CREAR (Asociar cultivo con plaga en inspección específica)
    // ==========================================

    /**
     * Asocia un cultivo con una plaga dentro de una inspección específica.
     * 
     * @param nombreCultivo Nombre del cultivo.
     * @param nombrePlaga Nombre de la plaga.
     * @param idInspeccion ID de la inspección.
     * @return true si la asociación fue creada correctamente, false si hubo error.
     */
    public static boolean asociarCultivoConPlaga(String nombreCultivo, String nombrePlaga, int idInspeccion) {
        int idCultivo = CultivoDatabase.obtenerIdCultivoPorNombre(nombreCultivo);
        if (idCultivo == -1) {
            System.err.println("⚠ Cultivo no encontrado: " + nombreCultivo);
            return false;
        }
        
        int idPlaga = PlagaDatabase.buscarOCrearPlaga(nombrePlaga);
        if (idPlaga == -1) {
            System.err.println("⚠ No se pudo crear/encontrar la plaga: " + nombrePlaga);
            return false;
        }
        
        return agregarDetalleInspeccion(idInspeccion, idCultivo, idPlaga, 0, 0.0, 
                                       "Asociación: " + nombreCultivo + " - " + nombrePlaga);
    }

    // ==========================================
    // LEER (Obtener detalles por inspección)
    // ==========================================

    /**
     * Obtiene todos los detalles de inspección asociados a una inspección específica.
     * 
     * @param idInspeccion ID de la inspección.
     * @return Lista de mapas con la información de los detalles.
     */
    public static List<Map<String, Object>> obtenerDetallesPorInspeccion(int idInspeccion) {
    List<Map<String, Object>> detalles = new ArrayList<>();
    String sql = "{ ? = call fn_obtener_detalles_inspeccion(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Registrar parámetro de retorno como CURSOR
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.setInt(2, idInspeccion);
        
        // Ejecutar función
        cs.execute();
        
        // Obtener el cursor resultante
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                Map<String, Object> detalle = new HashMap<>();
                detalle.put("id_detalle", rs.getInt("ID_DETALLE"));
                detalle.put("cultivo", rs.getString("CULTIVO"));
                detalle.put("plaga", rs.getString("PLAGA"));
                detalle.put("cantidad_plantas", rs.getInt("CANTIDAD_PLANTAS_AFECTADAS"));
                detalle.put("porcentaje", rs.getDouble("PORCENTAJE_INFESTACION"));
                detalle.put("observaciones", rs.getString("OBSERVACIONES_ESPECIFICAS"));
                detalles.add(detalle);
            }
        }

        System.out.println("✓ Se obtuvieron " + detalles.size() + " detalles de la inspección");

    } catch (SQLException e) {
        System.err.println("Error al obtener detalles de inspección: " + e.getMessage());
        e.printStackTrace();
    }
    return detalles;
}

    // ==========================================
    // LEER (Obtener asociaciones cultivo-plaga)
    // ==========================================

    /**
     * Obtiene una lista con todas las asociaciones únicas entre cultivos y plagas registradas.
     * 
     * @return Lista de asociaciones en formato texto.
     */
    public static List<String> obtenerAsociacionesCultivoPlaga() {
    List<String> asociaciones = new ArrayList<>();
    String sql = "{ ? = call FN_OBTENER_CULTIVO_PLAGA() }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Registrar parámetro de retorno como CURSOR
        cs.registerOutParameter(1, Types.REF_CURSOR);
        
        // Ejecutar función
        cs.execute();
        
        // Obtener el cursor resultante
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                // ✅ Mismo formato que tu código original: "Cultivo → Plaga"
                String asociacion = rs.getString("CULTIVO") + " → " + rs.getString("PLAGA");
                asociaciones.add(asociacion);
            }
        }

        System.out.println("✓ Se obtuvieron " + asociaciones.size() + " asociaciones cultivo-plaga");

    } catch (SQLException e) {
        System.err.println("Error al obtener asociaciones: " + e.getMessage());
        e.printStackTrace();
    }
    return asociaciones;
}

    // ==========================================
    // LEER (Obtener plagas de un cultivo específico)
    // ==========================================

    /**
     * Obtiene todas las plagas asociadas a un cultivo específico.
     * 
     * @param nombreCultivo Nombre del cultivo.
     * @return Lista de nombres de plagas asociadas.
     */
    public static List<String> obtenerPlagasDeCultivo(String nombreCultivo) {
    List<String> plagas = new ArrayList<>();
    String sql = "{ ? = call fn_obtener_plagas_de_cultivo(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Registrar parámetro de retorno como CURSOR
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.setString(2, nombreCultivo);
        
        // Ejecutar función
        cs.execute();
        
        // Obtener el cursor resultante
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                plagas.add(rs.getString("NOMBRE_COMUN"));
            }
        }

        System.out.println("✓ Se obtuvieron " + plagas.size() + " plagas para el cultivo: " + nombreCultivo);

    } catch (SQLException e) {
        System.err.println("Error al obtener plagas del cultivo: " + e.getMessage());
        e.printStackTrace();
    }
    return plagas;
}

    // ==========================================
    // ACTUALIZAR (Modificar cantidad de plantas y porcentaje)
    // ==========================================

    /**
     * Actualiza los datos de cantidad de plantas afectadas y porcentaje de infestación.
     * 
     * @param idDetalle ID del detalle a modificar.
     * @param cantidadPlantas Nueva cantidad de plantas afectadas.
     * @param porcentaje Nuevo porcentaje de infestación.
     * @return true si la actualización fue exitosa, false si no.
     */
    public static boolean actualizarDatosDetalle(int idDetalle, int cantidadPlantas, double porcentaje) {
        String sql = "UPDATE DETALLE_INSPECCION SET CANTIDAD_PLANTAS_AFECTADAS = ?, " +
                     "PORCENTAJE_INFESTACION = ? WHERE ID_DETALLE = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cantidadPlantas);
            pstmt.setDouble(2, porcentaje);
            pstmt.setInt(3, idDetalle);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Detalle actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar detalle: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // ACTUALIZAR (Modificar observaciones específicas)
    // ==========================================

    /**
     * Actualiza las observaciones específicas de un detalle.
     * 
     * @param idDetalle ID del detalle a modificar.
     * @param observaciones Texto con las nuevas observaciones.
     * @return true si la actualización fue exitosa, false si no.
     */
    public static boolean actualizarObservacionesEspecificas(int idDetalle, String observaciones) {
        String sql = "UPDATE DETALLE_INSPECCION SET OBSERVACIONES_ESPECIFICAS = ? WHERE ID_DETALLE = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, observaciones);
            pstmt.setInt(2, idDetalle);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Observaciones actualizadas exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar observaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // ELIMINAR (Borrar un detalle de inspección)
    // ==========================================

    /**
     * Elimina un registro de detalle de inspección.
     * 
     * @param idDetalle ID del detalle a eliminar.
     * @return true si se eliminó correctamente, false si ocurrió error.
     */
    public static boolean eliminarDetalle(int idDetalle) {
        String sql = "DELETE FROM DETALLE_INSPECCION WHERE ID_DETALLE = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idDetalle);
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Detalle eliminado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalle: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // ELIMINAR (Borrar todos los detalles de una inspección)
    // ==========================================

    /**
     * Elimina todos los detalles asociados a una inspección.
     * 
     * @param idInspeccion ID de la inspección.
     * @return true si la operación fue exitosa, false si hubo error.
     */
    public static boolean eliminarDetallesPorInspeccion(int idInspeccion) {
        String sql = "DELETE FROM DETALLE_INSPECCION WHERE ID_INSPECCION = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idInspeccion);
            int filasAfectadas = pstmt.executeUpdate();
            
            System.out.println("✓ Eliminados " + filasAfectadas + " detalles de la inspección");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalles: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // VERIFICAR si existe asociación cultivo-plaga
    // ==========================================

    /**
     * Verifica si ya existe una asociación entre un cultivo y una plaga.
     * 
     * @param idCultivo ID del cultivo.
     * @param idPlaga ID de la plaga.
     * @return true si la asociación existe, false si no.
     */
    public static boolean existeAsociacion(int idCultivo, int idPlaga) {
        String sql = "SELECT COUNT(*) FROM DETALLE_INSPECCION WHERE ID_CULTIVO = ? AND ID_PLAGA = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCultivo);
            pstmt.setInt(2, idPlaga);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar asociación: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // OBTENER estadísticas de plagas por cultivo
    // ==========================================

    /**
     * Obtiene estadísticas del número de plagas registradas por cultivo.
     * 
     * @param idCultivo ID del cultivo.
     * @return Mapa con nombre de plaga y cantidad de ocurrencias.
     */
    public static Map<String, Integer> obtenerEstadisticasPlagasPorCultivo(int idCultivo) {
        Map<String, Integer> estadisticas = new HashMap<>();
        String sql = "{ ? = call FN_OBTENER_ESTADI_PLAG_CULTI(?) }";

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
                    estadisticas.put(rs.getString("NOMBRE_COMUN"), rs.getInt("CANTIDAD"));
                }
            }

            System.out.println("✓ Se obtuvieron estadísticas de " + estadisticas.size() + " plagas para el cultivo ID: " + idCultivo);

        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            e.printStackTrace();
        }
        return estadisticas;
    }
}
