package app.db;

import java.sql.*;
import java.util.*;

/**
 * Clase para consultar las vistas (reportes) de la base de datos Oracle.
 * ✅ CORREGIDO: Compatible con las vistas SQL implementadas
 */
public class ReportesVistas {

    /**
     * 🔹 VISTA: v_historial_inspecciones_predio
     * Obtiene el historial completo de inspecciones de un predio.
     */
public static List<Map<String, Object>> obtenerHistorialInspeccionesPredio(String numeroPredial) {
    List<Map<String, Object>> resultados = new ArrayList<>();
    String sql = """
        SELECT * 
        FROM V_HISTORIAL_INSPECCIONES
        WHERE numero_predial = ? 
        ORDER BY fecha_inspeccion DESC
    """;

    try (Connection conn = Conexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, numeroPredial);
        ResultSet rs = pstmt.executeQuery();

        // Usar un Set para evitar duplicados por ID_INSPECCION
        Set<Integer> idsProcesados = new HashSet<>();
        
        while (rs.next()) {
            int idInspeccion = rs.getInt("ID_INSPECCION");
            
            // Solo procesar si no hemos visto este ID antes
            if (!idsProcesados.contains(idInspeccion)) {
                idsProcesados.add(idInspeccion);
                
                Map<String, Object> fila = new HashMap<>();
                fila.put("numero_predial", rs.getString("numero_predial"));
                fila.put("nombre_predio", rs.getString("nombre_predio"));
                fila.put("fecha", rs.getDate("fecha_inspeccion"));
                fila.put("tecnico", rs.getString("nombre_tecnico"));
                fila.put("cultivo", rs.getString("cultivo"));
                fila.put("plaga", rs.getString("plaga"));
                fila.put("observaciones_generales", rs.getString("observaciones_generales"));
                fila.put("observaciones_especificas", rs.getString("observaciones_especificas"));
                // También puedes incluir los nuevos campos si los necesitas:
                fila.put("porcentaje_infestacion", rs.getDouble("porcentaje_infestacion"));
                fila.put("cantidad_plantas_afectadas", rs.getInt("cantidad_plantas_afectadas"));
                
                resultados.add(fila);
                
                // Depuración
                System.out.println("✓ Agregada inspección ID: " + idInspeccion + 
                                 ", Fecha: " + rs.getDate("fecha_inspeccion") +
                                 ", Cultivo: " + rs.getString("cultivo"));
            } else {
                System.out.println("✗ Duplicado omitido - ID: " + idInspeccion);
            }
        }

        System.out.println("✓ Historial obtenido: " + resultados.size() + 
                         " inspecciones únicas de " + idsProcesados.size() + " IDs encontrados");

    } catch (SQLException e) {
        System.err.println("❌ Error al consultar V_HISTORIAL_INSPECCIONES: " + e.getMessage());
        e.printStackTrace();
    }

    return resultados;
}

    /**
     * 🔹 VISTA: v_inspecciones_por_tecnico
     * Obtiene todas las inspecciones realizadas por un técnico.
     */
    public static List<Map<String, Object>> obtenerInspeccionesPorTecnico(int idTecnico) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = """
            SELECT * 
            FROM v_inspecciones_por_tecnico 
            WHERE id_usuario = ?
        """;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTecnico);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_usuario", rs.getInt("id_usuario"));
                fila.put("tecnico", rs.getString("Tecnico"));
                fila.put("documento", rs.getString("documento"));
                fila.put("total_inspecciones", rs.getInt("total_inspecciones"));
                fila.put("primera_inspeccion", rs.getDate("primera_inspeccion"));
                fila.put("ultima_inspeccion", rs.getDate("ultima_inspeccion"));
                fila.put("predios_inspeccionados", rs.getInt("predios_inspeccionados"));
                fila.put("total_plagas_detectadas", rs.getInt("total_plagas_detectadas"));
                resultados.add(fila);
            }

            System.out.println("✓ Inspecciones del técnico: " + resultados.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar v_inspecciones_por_tecnico: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    /**
     * 🔹 VISTA: v_plagas_mas_frecuentes
     * Obtiene las plagas más frecuentes en el sistema.
     */
    public static List<Map<String, Object>> obtenerPlagasMasFrecuentes() {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = """
            SELECT * FROM v_plagas_mas_frecuentes 
            WHERE veces_detectada > 0
            ORDER BY veces_detectada DESC
        """;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_plaga", rs.getInt("id_plaga"));
                fila.put("nombre_comun", rs.getString("nombre_comun"));
                fila.put("nombre_cientifico", rs.getString("nombre_cientifico"));
                fila.put("veces_detectada", rs.getInt("veces_detectada"));
                fila.put("cultivos_afectados", rs.getInt("cultivos_afectados"));
                
                // ✅ Manejo de NULL para promedio_infestacion
                Double promedio = rs.getDouble("promedio_infestacion");
                if (rs.wasNull()) {
                    promedio = 0.0;
                }
                fila.put("promedio_infestacion", promedio);
                
                // ✅ Para compatibilidad con crearPanelReportes
                fila.put("plaga", rs.getString("nombre_comun"));
                fila.put("total_apariciones", rs.getInt("veces_detectada"));
                fila.put("predios_afectados", rs.getInt("cultivos_afectados"));
                fila.put("porcentaje", promedio);
                
                resultados.add(fila);
            }

            System.out.println("✓ Plagas más frecuentes: " + resultados.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar v_plagas_mas_frecuentes: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    /**
     * 🔹 VISTA: v_plagas_mas_frecuentes (con límite)
     * Obtiene las plagas más frecuentes en el sistema (usando ROWNUM para Oracle).
     */
    public static List<Map<String, Object>> obtenerPlagasMasFrecuentes(int limite) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = """
            SELECT * FROM (
                SELECT * FROM v_plagas_mas_frecuentes 
                WHERE veces_detectada > 0
                ORDER BY veces_detectada DESC
            ) WHERE ROWNUM <= ?
        """;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_plaga", rs.getInt("id_plaga"));
                fila.put("nombre_comun", rs.getString("nombre_comun"));
                fila.put("nombre_cientifico", rs.getString("nombre_cientifico"));
                fila.put("veces_detectada", rs.getInt("veces_detectada"));
                fila.put("cultivos_afectados", rs.getInt("cultivos_afectados"));
                
                // ✅ Manejo de NULL para promedio_infestacion
                Double promedio = rs.getDouble("promedio_infestacion");
                if (rs.wasNull()) {
                    promedio = 0.0;
                }
                fila.put("promedio_infestacion", promedio);
                
                // ✅ Para compatibilidad con crearPanelReportes
                fila.put("plaga", rs.getString("nombre_comun"));
                fila.put("total_apariciones", rs.getInt("veces_detectada"));
                fila.put("predios_afectados", rs.getInt("cultivos_afectados"));
                fila.put("porcentaje", promedio);
                
                resultados.add(fila);
            }

            System.out.println("✓ Plagas más frecuentes (TOP " + limite + "): " + resultados.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar v_plagas_mas_frecuentes: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    /**
     * 🔹 VISTA: v_productores_por_propietario
     * Obtiene todos los productores asociados a los predios de un propietario.
     */
    public static List<Map<String, Object>> obtenerProductoresPorPropietario(int idPropietario) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "SELECT * FROM v_productores_por_propietario WHERE id_propietario = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPropietario);
            ResultSet rs = pstmt.executeQuery();

            // ✅ Usamos un Set para evitar duplicados de productores
            Map<Integer, Map<String, Object>> productoresUnicos = new HashMap<>();

            while (rs.next()) {
                int idProductor = rs.getInt("id_productor");
                
                if (!productoresUnicos.containsKey(idProductor)) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_propietario", rs.getInt("id_propietario"));
                    fila.put("nombre_propietario", rs.getString("propietario"));
                    fila.put("id_productor", idProductor);
                    fila.put("nombre_productor", rs.getString("productor"));
                    fila.put("predio", rs.getString("predio"));
                    fila.put("total_cultivos_registrados", rs.getInt("total_cultivos_registrados"));
                    
                    // ✅ Contar predios asignados manualmente
                    fila.put("total_predios_asignados", 1);
                    
                    productoresUnicos.put(idProductor, fila);
                } else {
                    // Incrementar contador de predios
                    Map<String, Object> productor = productoresUnicos.get(idProductor);
                    int prediosActuales = (int) productor.get("total_predios_asignados");
                    productor.put("total_predios_asignados", prediosActuales + 1);
                }
            }

            resultados.addAll(productoresUnicos.values());
            System.out.println("✓ Productores del propietario: " + resultados.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar v_productores_por_propietario: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    /**
     * 🔹 VISTA: v_cultivos_por_predio
     * Obtiene todos los cultivos de un predio específico.
     */
    public static List<Map<String, Object>> obtenerCultivosPorPredio(String numeroPredial) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "SELECT * FROM v_cultivos_por_predio WHERE numero_predial = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numeroPredial);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("numero_predial", rs.getString("numero_predial"));
                fila.put("nombre_predio", rs.getString("predio")); // ✅ La vista usa 'predio'
                fila.put("especie_cientifica", rs.getString("especie_cientifica"));
                fila.put("nombre_comun", rs.getString("cultivo")); // ✅ La vista usa 'cultivo'
                fila.put("variedad", rs.getString("variedad"));
                fila.put("ciclo", rs.getString("ciclo"));
                
                // ✅ Manejo de NULL
                Integer inspecciones = rs.getInt("inspecciones_realizadas");
                if (rs.wasNull()) {
                    inspecciones = 0;
                }
                fila.put("inspecciones_realizadas", inspecciones);
                
                resultados.add(fila);
            }

            System.out.println("✓ Cultivos del predio: " + resultados.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar v_cultivos_por_predio: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    /**
     * 🔹 VISTA: v_usuarios_pendientes_aprobacion
     * Obtiene todos los usuarios pendientes de aprobación.
     */
    public static List<Map<String, Object>> obtenerUsuariosPendientesAprobacion() {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "SELECT * FROM V_USUARIOS_PENDIENTES_APROB ORDER BY id_usuario DESC";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_usuario", rs.getInt("id_usuario"));
                fila.put("nombre", rs.getString("nombre"));
                fila.put("documento", rs.getString("documento"));
                fila.put("correo", rs.getString("correo"));
                fila.put("rol", rs.getString("rol"));
                
                // ✅ La vista no tiene fecha_registro ni dias_pendiente
                // fila.put("fecha_registro", rs.getDate("fecha_registro"));
                // fila.put("dias_pendiente", rs.getInt("dias_pendiente"));
                
                fila.put("permisos_otorgados", rs.getString("permisos_otorgados"));
                resultados.add(fila);
            }

            System.out.println("✓ Usuarios pendientes: " + resultados.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar V_USUARIOS_PENDIENTES_APROB: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    /**
     * 🔹 VISTA: v_porcentaje_infestacion (NO EXISTE EN TUS VISTAS)
     * Si necesitas esta vista, créala primero en Oracle
     */
    public static List<Map<String, Object>> obtenerPorcentajeInfestacion() {
        List<Map<String, Object>> resultados = new ArrayList<>();
        
        // ✅ Consulta directa ya que no existe la vista
        String sql = """
            SELECT 
                p.nombre AS predio,
                c.nombre_comun AS cultivo,
                pl.nombre_comun AS plaga,
                di.porcentaje_infestacion,
                i.fecha
            FROM DETALLE_INSPECCION di
            JOIN INSPECCION i ON di.id_inspeccion = i.id_inspeccion
            JOIN PREDIO p ON i.id_predio = p.id_predio
            JOIN CULTIVO c ON di.id_cultivo = c.id_cultivo
            JOIN PLAGA pl ON di.id_plaga = pl.id_plaga
            WHERE di.porcentaje_infestacion IS NOT NULL
            ORDER BY di.porcentaje_infestacion DESC
        """;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("predio", rs.getString("predio"));
                fila.put("cultivo", rs.getString("cultivo"));
                fila.put("plaga", rs.getString("plaga"));
                fila.put("porcentaje_infestacion", rs.getDouble("porcentaje_infestacion"));
                fila.put("fecha", rs.getDate("fecha"));
                resultados.add(fila);
            }

            System.out.println("✓ Porcentaje de infestación consultado: " + resultados.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar porcentaje_infestacion: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    /**
     * 🔹 VISTA: v_cantidad_plagas_afectadas
     * Obtiene la cantidad de plagas que han afectado a cada cultivo o predio
     */
    public static List<Map<String, Object>> obtenerCantidadPlagasAfectadas() {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "SELECT * FROM v_cantidad_plagas_afectadas ORDER BY total_plagas DESC";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("predio", rs.getString("predio"));
                fila.put("cultivo", rs.getString("cultivo"));
                fila.put("total_plagas", rs.getInt("total_plagas"));
                
                // ✅ Manejo de NULL para ultima_inspeccion
                java.sql.Date ultimaInspeccion = rs.getDate("ultima_inspeccion");
                fila.put("ultima_inspeccion", ultimaInspeccion);
                
                resultados.add(fila);
            }

            System.out.println("✓ Cantidad de plagas afectadas consultada: " + resultados.size());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar v_cantidad_plagas_afectadas: " + e.getMessage());
            e.printStackTrace();
        }

        return resultados;
    }

    /**
     * 🧾 Método auxiliar: genera una cadena formateada del reporte.
     */
    public static String formatearReporte(List<Map<String, Object>> datos) {
        if (datos.isEmpty()) {
            return "No hay datos para mostrar.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("  REPORTE GENERADO - ").append(new java.util.Date()).append("\n");
        sb.append("═══════════════════════════════════════════════\n\n");

        for (int i = 0; i < datos.size(); i++) {
            sb.append("Registro #").append(i + 1).append(":\n");
            Map<String, Object> fila = datos.get(i);
            for (Map.Entry<String, Object> entry : fila.entrySet()) {
                Object valor = entry.getValue();
                // ✅ Manejo de valores null
                String valorStr = (valor != null) ? valor.toString() : "N/A";
                sb.append("  • ").append(entry.getKey()).append(": ").append(valorStr).append("\n");
            }
            sb.append("\n");
        }

        sb.append("═══════════════════════════════════════════════\n");
        sb.append("Total de registros: ").append(datos.size()).append("\n");
        return sb.toString();
    }
}