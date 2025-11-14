package app.db;

import app.model.Inspeccion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase que gestiona todas las operaciones CRUD (crear, leer, actualizar y eliminar)
 * sobre la tabla INSPECCION y su relación con DETALLE_INSPECCION.
 * Se conecta con la base de datos Oracle a través de la clase Conexion.
 */
public class InspeccionDatabase {

    // ==========================================
    // CREAR (Agregar una nueva inspección)
    // ==========================================
    /**
     * Inserta una nueva inspección en la base de datos junto con su detalle (si aplica).
     */
    public static boolean agregarInspeccion(Inspeccion inspeccion) {
        // ✅ 1. Generar ID manualmente (TU LÓGICA)
        int nuevoId = obtenerNuevoId();

        // ✅ 2. Obtener ID del predio
        int idPredio = PredioDatabase.obtenerIdPredioPorNumeroPredial(inspeccion.getPredio());
        if (idPredio == -1) {
            System.err.println("⚠ No se encontró el predio para número predial: " + inspeccion.getPredio());
            return false;
        }

        // ✅ 3. Usar procedimiento almacenado CON el ID generado
        boolean exito = StoredProcedures.registrarInspeccion(
            nuevoId,
            Integer.parseInt(inspeccion.getTecnicoId()),
            idPredio,
            inspeccion.getFecha(),
            inspeccion.getObservaciones()
        );

        if (exito) {
            System.out.println("✓ Inspección registrada exitosamente con ID: " + nuevoId);

            // ✅ 4. Agregar detalle si corresponde
            if (inspeccion.getCultivo() != null && !inspeccion.getCultivo().isEmpty()) {
                agregarDetalleInspeccion(nuevoId, inspeccion);
            }
        }

        return exito;
    }

 // ==========================================
// Agregar detalle de inspección CON PORCENTAJE
// ==========================================
/**
 * Inserta un registro en DETALLE_INSPECCION asociado a una inspección existente.
 * ✅ ACTUALIZADO: Ahora incluye el porcentaje de infestación
 */
private static boolean agregarDetalleInspeccion(int idInspeccion, Inspeccion inspeccion) {
    try {
        // Validar cultivo obligatorio
        if (inspeccion.getCultivo() == null || inspeccion.getCultivo().isEmpty()) {
            System.err.println("⚠ No se proporcionó cultivo");
            return false;
        }
        
        int idCultivo;
        try {
            idCultivo = Integer.parseInt(inspeccion.getCultivo());
        } catch (NumberFormatException e) {
            System.err.println("⚠ Error al parsear ID de cultivo: " + inspeccion.getCultivo());
            return false;
        }
        
        // Manejar plaga (puede ser null) - MÁS ROBUSTO
        Integer idPlaga = null; // Usar Integer para permitir null
        if (inspeccion.getPlaga() != null && !inspeccion.getPlaga().isEmpty()) {
            try {
                idPlaga = Integer.parseInt(inspeccion.getPlaga());
                System.out.println("✓ Plaga asignada con ID: " + idPlaga);
            } catch (NumberFormatException e) {
                System.err.println("⚠ Error al parsear ID de plaga: " + inspeccion.getPlaga());
                // Mantener como null
            }
        } else {
            System.out.println("⚠ No se seleccionó plaga");
        }
        
        // VALOR POR DEFECTO para cantidadPlantas
        int cantidadPlantas = 0;
        
        // ✅ LLAMAR AL SP de StoredProcedures - MANEJO EXPLÍCITO DE NULL
        int idDetalle = StoredProcedures.agregarDetalleInspeccion(
            idInspeccion, 
            idCultivo, 
            idPlaga != null ? idPlaga : -1, // Pasar -1 solo si el SP lo maneja
            cantidadPlantas,
            inspeccion.getPorcentajeInfestacion(),
            inspeccion.getObservacionesEspecificas() != null ? inspeccion.getObservacionesEspecificas() : ""
        );
        
        if (idDetalle > 0) {
            System.out.println("✓ Detalle de inspección agregado correctamente con ID: " + idDetalle);
            System.out.println("✓ Porcentaje infestación: " + inspeccion.getPorcentajeInfestacion() + "%");
            return true;
        } else {
            System.err.println("❌ No se pudo agregar el detalle de inspección");
            return false;
        }
        
    } catch (Exception e) {
        System.err.println("❌ Error al agregar detalle: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    // ==========================================
    // LEER (Obtener todas las inspecciones)
    // ==========================================
    /**
     * Retorna una lista de todas las inspecciones con su información general.
     */
    public static List<Inspeccion> getInspecciones() {
    List<Inspeccion> inspecciones = new ArrayList<>();
    String sql = "{ ? = call fn_obtener_inspecciones() }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        // Registrar parámetro de retorno como CURSOR
        cs.registerOutParameter(1, Types.REF_CURSOR);
        
        // Ejecutar función
        cs.execute();
        
        // Obtener el cursor resultante
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                // ✅ MISMO CONSTRUCTOR que tu código original
                Inspeccion inspeccion = new Inspeccion(
                    rs.getString("NOMBRE_PREDIO"),
                    rs.getDate("FECHA"),
                    rs.getString("NOMBRE_TECNICO"),
                    rs.getString("CULTIVO"),
                    rs.getString("PLAGA"),
                    rs.getString("OBSERVACIONES_GENERALES")
                );
                inspecciones.add(inspeccion);
            }
        }

        System.out.println("✓ Se obtuvieron " + inspecciones.size() + " inspecciones en total");

    } catch (SQLException e) {
        System.err.println("Error al obtener inspecciones: " + e.getMessage());
        e.printStackTrace();
    }

    return inspecciones;
}

    // ==========================================
    // LEER (Obtener inspecciones por técnico)
    // ==========================================
    /**
     * Retorna todas las inspecciones realizadas por un técnico específico.
     */
public static List<Inspeccion> getInspeccionesPorTecnico(String tecnicoId) {
    List<Inspeccion> inspecciones = new ArrayList<>();
    String sql = "{ ? = call FN_OBTENER_INSPEC_TECNICO(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {
        
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.setInt(2, Integer.parseInt(tecnicoId));
        cs.execute();
        
        try (ResultSet rs = (ResultSet) cs.getObject(1)) {
            while (rs != null && rs.next()) {
                // ✅ SOLO ID por ahora
                Inspeccion inspeccion = new Inspeccion();
                inspeccion.setId(rs.getInt("ID_INSPECCION"));
                inspeccion.setPredio(rs.getString("PREDIO"));
                inspeccion.setFecha(rs.getDate("FECHA"));
                inspeccion.setCultivo(rs.getString("CULTIVO"));
                inspeccion.setPlaga(rs.getString("PLAGA"));
                inspeccion.setObservacionesGenerales(rs.getString("OBSERVACIONES_GENERALES"));
                inspeccion.setObservacionesEspecificas(rs.getString("OBSERVACIONES_ESPECIFICAS"));
                inspecciones.add(inspeccion);
            }
        }
        
        System.out.println("✓ Se obtuvieron " + inspecciones.size() + " inspecciones del técnico ID: " + tecnicoId);
        
    } catch (SQLException e) {
        System.err.println("❌ Error al obtener inspecciones por técnico: " + e.getMessage());
        e.printStackTrace();
    }
    
    return inspecciones;
}

    // ==========================================
    // ACTUALIZAR (Observaciones)
    // ==========================================
    /**
     * Actualiza las observaciones generales de una inspección.
     */
    public static boolean actualizarObservacionesGenerales(int idInspeccion, String observacionesGenerales) {
        String sql = "UPDATE INSPECCION SET OBSERVACIONES_GENERALES = ? WHERE ID_INSPECCION = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, observacionesGenerales);
            pstmt.setInt(2, idInspeccion);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Observaciones generales actualizadas exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar observaciones generales: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza o crea observaciones específicas para una inspección.
     */
    public static boolean actualizarObservacionesEspecificas(int idInspeccion, String observaciones) {
    // ✅ Ahora llama al procedimiento en StoredProcedures
    return StoredProcedures.actualizarObservacionesEspecificas(idInspeccion, observaciones);
}

    // ==========================================
    // ELIMINAR
    // ==========================================
    /**
     * Elimina una inspección y todos sus detalles relacionados.
     */
    public static boolean eliminarInspeccion(int idInspeccion) {
        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);
            
            String sqlDetalle = "DELETE FROM DETALLE_INSPECCION WHERE ID_INSPECCION = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetalle)) {
                pstmt.setInt(1, idInspeccion);
                int detallesEliminados = pstmt.executeUpdate();
                System.out.println("✓ Detalles eliminados: " + detallesEliminados);
            }
            
            String sqlInspeccion = "DELETE FROM INSPECCION WHERE ID_INSPECCION = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInspeccion)) {
                pstmt.setInt(1, idInspeccion);
                int filasAfectadas = pstmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    conn.commit();
                    System.out.println("✓ Inspección eliminada exitosamente");
                    return true;
                }
            }
            
            conn.rollback();
            
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error al eliminar inspección: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    // ==========================================
    // OBTENER NUEVOS ID
    // ==========================================
    /**
     * Obtiene un nuevo ID disponible para INSPECCION.
     */
    private static int obtenerNuevoId() {
        String sql = "SELECT NVL(MAX(ID_INSPECCION), 0) + 1 AS NUEVO_ID FROM INSPECCION";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NUEVO_ID");
        } catch (SQLException e) {
            System.err.println("Error al obtener nuevo ID de inspección: " + e.getMessage());
        }
        return 1;
    }

    /**
     * Obtiene un nuevo ID disponible para DETALLE_INSPECCION.
     */
    

    // ==========================================
    // LEER (Inspecciones por predio)
    // ==========================================
    /**
     * Obtiene todas las inspecciones relacionadas con un número predial.
     */
 public static List<Inspeccion> obtenerInspeccionesPorPredio(String numeroPredial) {
    List<Inspeccion> inspecciones = new ArrayList<>();
    String sql = "{ ? = call FN_OBTENER_INSPECCIONES_PREDIO(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {
        
        cs.setString(2, numeroPredial);
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.execute();
        
        ResultSet rs = (ResultSet) cs.getObject(1);
        
        // ✅ MISMA LÓGICA EXACTA: Usar un Set para evitar duplicados por ID_INSPECCION
        Set<Integer> idsProcesados = new HashSet<>();
        
        while (rs.next()) {
            int idInspeccion = rs.getInt("ID_INSPECCION");
            
            // ✅ MISMA LÓGICA EXACTA: Evitar procesar la misma inspección múltiples veces
            if (!idsProcesados.contains(idInspeccion)) {
                idsProcesados.add(idInspeccion);
                
                Inspeccion inspeccion = new Inspeccion(
                    rs.getString("NOMBRE_PREDIO"),
                    rs.getDate("FECHA"),
                    rs.getString("NOMBRE_TECNICO"),
                    rs.getString("CULTIVO"),
                    rs.getString("PLAGA"),
                    rs.getString("OBSERVACIONES_GENERALES"),
                    rs.getString("OBSERVACIONES_ESPECIFICAS")
                );
                // ✅ MISMA LÓGICA EXACTA: ¡IMPORTANTE! Asignar el ID a la inspección
                inspeccion.setId(idInspeccion);
                
                inspecciones.add(inspeccion);
                
                // ✅ MISMO LOG EXACTO
                System.out.println("Procesando inspección ID: " + idInspeccion + 
                                 ", Cultivo: " + rs.getString("CULTIVO") +
                                 ", Plaga: " + rs.getString("PLAGA"));
            }
        }
        
        // ✅ MISMOS MENSAJES EXACTOS
        System.out.println("✓ Se obtuvieron " + inspecciones.size() + 
                           " inspecciones únicas para el predio: " + numeroPredial);
        System.out.println("IDs procesados: " + idsProcesados);
        
        rs.close();
        
    } catch (SQLException e) {
        System.err.println("Error al obtener inspecciones por predio: " + e.getMessage());
        e.printStackTrace();
    }
    return inspecciones;
    
}    // ==========================================
    // LEER (Obtener ID por nombre de predio y fecha)
    // ==========================================
    /**
     * Busca una inspección por nombre de predio y fecha.
     * Devuelve su ID o -1 si no existe.
     */
    public static int obtenerIdInspeccion(String nombrePredio, java.util.Date fecha) {
        String sql = "SELECT i.ID_INSPECCION FROM INSPECCION i " +
                     "JOIN PREDIO p ON i.ID_PREDIO = p.ID_PREDIO " +
                     "WHERE p.NOMBRE = ? AND i.FECHA = ?";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombrePredio);
            pstmt.setDate(2, new java.sql.Date(fecha.getTime()));
            
            System.out.println("🔍 Buscando inspección: predio=" + nombrePredio + ", fecha=" + fecha);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID_INSPECCION");
                System.out.println("✓ ID encontrado: " + id);
                return id;
            } else {
                System.out.println("⚠ No se encontró inspección con esos datos");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ID de inspección: " + e.getMessage());
        }
        return -1;
    }
}
