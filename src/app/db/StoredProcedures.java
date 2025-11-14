package app.db;

import app.model.*;
import java.sql.*;

/**
 * Procedimientos almacenados que RECIBEN el ID desde Java
 * Mantiene tu lógica de generación manual de IDs
 */
public class StoredProcedures {

    /**
     * SP: sp_registrar_usuario_manual
     * Recibe el ID generado desde Java
     */
    public static boolean registrarUsuario(int idUsuario, String nombre, String documento, 
                                          String correo, String contrasena, String rol) {
        String sql = "{CALL sp_registrar_usuario_manual(?, ?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Parámetros IN (incluye el ID)
            cstmt.setInt(1, idUsuario);
            cstmt.setString(2, nombre);
            cstmt.setString(3, documento);
            cstmt.setString(4, correo);
            cstmt.setString(5, contrasena);
            cstmt.setString(6, rol);
            
            // Parámetro OUT
            cstmt.registerOutParameter(7, java.sql.Types.VARCHAR);  // P_MENSAJE
            
            cstmt.execute();
            
            String mensaje = cstmt.getString(7);
            
            if (mensaje.contains("exitosamente")) {
                System.out.println("✓ " + mensaje);
                return true;
            } else {
                System.err.println("⚠️ " + mensaje);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error en sp_registrar_usuario_manual: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SP: sp_asignar_productor_manual
     * Recibe el ID de asignación generado desde Java
     */
    public static boolean asignarProductor(int idAsignacion, int idPropietario, 
                                          int idProductor, int idPredio) {
        String sql = "{CALL sp_asignar_productor_manual(?, ?, ?, ?, ?)}";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Parámetros IN
            cstmt.setInt(1, idAsignacion);
            cstmt.setInt(2, idPropietario);
            cstmt.setInt(3, idProductor);
            cstmt.setInt(4, idPredio);
            
            // Parámetro OUT
            cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);  // P_MENSAJE
            
            cstmt.execute();
            
            String mensaje = cstmt.getString(5);
            
            if (mensaje.contains("exitosamente")) {
                System.out.println("✓ " + mensaje);
                return true;
            } else {
                System.err.println("⚠️ " + mensaje);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error en sp_asignar_productor_manual: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SP: sp_registrar_cultivo_manual
     * Recibe el ID de cultivo generado desde Java
     */
    public static boolean registrarCultivo(int idCultivo, int idProductor, int idPredio,
                                          String especie, String nombreComun, 
                                          String variedad, String ciclo) {
        String sql = "{CALL sp_registrar_cultivo_manual(?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Parámetros IN
            cstmt.setInt(1, idCultivo);
            cstmt.setInt(2, idProductor);
            cstmt.setInt(3, idPredio);
            cstmt.setString(4, especie);
            cstmt.setString(5, nombreComun);
            cstmt.setString(6, variedad);
            cstmt.setString(7, ciclo);
            
            // Parámetro OUT
            cstmt.registerOutParameter(8, java.sql.Types.VARCHAR);  // P_MENSAJE
            
            cstmt.execute();
            
            String mensaje = cstmt.getString(8);
            
            if (mensaje.contains("exitosamente")) {
                System.out.println("✓ " + mensaje);
                return true;
            } else {
                System.err.println("⚠️ " + mensaje);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error en sp_registrar_cultivo_manual: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SP: sp_registrar_inspeccion_manual
     * Recibe el ID de inspección generado desde Java
     */
    public static boolean registrarInspeccion(int idInspeccion, int idTecnico, int idPredio,
                                             java.util.Date fecha, String observaciones) {
        String sql = "{CALL sp_registrar_inspeccion_manual(?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = Conexion.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Parámetros IN
            cstmt.setInt(1, idInspeccion);
            cstmt.setInt(2, idTecnico);
            cstmt.setInt(3, idPredio);
            cstmt.setDate(4, new java.sql.Date(fecha.getTime()));
            cstmt.setString(5, observaciones);
            
            // Parámetro OUT
            cstmt.registerOutParameter(6, java.sql.Types.VARCHAR);  // P_MENSAJE
            
            cstmt.execute();
            
            String mensaje = cstmt.getString(6);
            
            if (mensaje.contains("exitosamente")) {
                System.out.println("✓ " + mensaje);
                return true;
            } else {
                System.err.println("⚠️ " + mensaje);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error en sp_registrar_inspeccion_manual: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

   /**
 * SP: sp_agregar_detalle_inspeccion_manual
 * Este sí genera su propio ID (puedes mantenerlo así o modificarlo)
 */
public static int agregarDetalleInspeccion(int idInspeccion, int idCultivo, int idPlaga,
                                           int cantidadPlantas, double porcentaje, 
                                           String observaciones) {
    String sql = "{CALL SP_AGREGAR_DETALLE_INSPEC(?, ?, ?, ?, ?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {
        
        cstmt.setInt(1, idInspeccion);
        cstmt.setInt(2, idCultivo);
        cstmt.setInt(3, idPlaga);
        cstmt.setInt(4, cantidadPlantas);
        cstmt.setDouble(5, porcentaje);
        cstmt.setString(6, observaciones);
        
        cstmt.registerOutParameter(7, java.sql.Types.NUMERIC);  // P_ID_DETALLE
        cstmt.registerOutParameter(8, java.sql.Types.VARCHAR);  // P_MENSAJE
        
        cstmt.execute();
        
        int idDetalle = cstmt.getInt(7);
        String mensaje = cstmt.getString(8);
        
        // ✅ CORREGIDO: Mostrar mensaje aunque idDetalle sea 0
        if (idDetalle > 0) {
            System.out.println("✓ " + mensaje);
        } else {
            System.err.println("❌ " + mensaje);  // Mostrar error cuando idDetalle es 0
        }
        
        return idDetalle;
        
    } catch (SQLException e) {
        System.err.println("❌ Error en SP_AGREGAR_DETALLE_INSPEC: " + e.getMessage());
        e.printStackTrace();
        return -1;
    }
}
    /**
 * SP: sp_asociar_cultivo_plaga_manual
 * Asocia un cultivo con una plaga en una inspección con ID manual
 */
public static boolean asociarCultivoPlaga(int idDetalle, int idInspeccion, int idCultivo, int idPlaga) {
    String sql = "{CALL SP_ASOCIAR_CULTIVO_PLAGA(?, ?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {
        
        // Parámetros IN
        cstmt.setInt(1, idDetalle);
        cstmt.setInt(2, idInspeccion);
        cstmt.setInt(3, idCultivo);
        cstmt.setInt(4, idPlaga);
        
        // Parámetro OUT
        cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);  // P_MENSAJE
        
        cstmt.execute();
        
        String mensaje = cstmt.getString(5);
        
        if (mensaje.contains("exitosamente")) {
            System.out.println("✓ " + mensaje);
            return true;
        } else {
            System.err.println("❌ " + mensaje);
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Error en SP_ASOCIAR_CULTIVO_PLAGA: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}/**
 * SP: sp_actualizar_observaciones_especificas
 * Actualiza o crea observaciones específicas en detalle de inspección
 */
public static boolean actualizarObservacionesEspecificas(int idInspeccion, String observaciones) {
    String sql = "{call SP_ACTUALIZAR_OBSERVAESPE(?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {
        
        // Parámetros IN
        cstmt.setInt(1, idInspeccion);
        cstmt.setString(2, observaciones);
        
        // Parámetros OUT
        cstmt.registerOutParameter(3, java.sql.Types.NUMERIC);  // P_RESULTADO
        cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);  // P_MENSAJE
        
        cstmt.execute();
        
        int resultado = cstmt.getInt(3);
        String mensaje = cstmt.getString(4);
        
        if (resultado == 1) {
            System.out.println("✓ " + mensaje);
            return true;
        } else {
            System.err.println("❌ " + mensaje);
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Error en SP_ACTUALIZAR_OBSERVAESPE: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
/**
 * SP: sp_registrar_plaga_manual
 * Recibe el ID de plaga generado desde Java
 */
public static boolean registrarPlaga(int idPlaga, String nombreCientifico, String nombreComun) {
    String sql = "{call sp_registrar_plaga_manual(?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {
        
        // Parámetros IN
        cstmt.setInt(1, idPlaga);
        cstmt.setString(2, nombreCientifico);
        cstmt.setString(3, nombreComun);
        
        // Parámetro OUT
        cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);  // P_MENSAJE
        
        cstmt.execute();
        
        String mensaje = cstmt.getString(4);
        
        if (mensaje.contains("exitosamente")) {
            System.out.println("✓ " + mensaje);
            return true;
        } else {
            System.err.println("⚠️ " + mensaje);
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Error en sp_registrar_plaga_manual: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
/**
 * SP: sp_registrar_predio_completo
 * Registra predio y asigna automáticamente al propietario como productor
 */
public static boolean registrarPredioCompleto(int idPredio, String numeroPredial, String nombre, 
                                             String departamento, String municipio, String vereda,
                                             double latitud, double longitud, int idPropietario) {
    String sql = "{call sp_registrar_predio_completo(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {
        
        // Parámetros IN
        cstmt.setInt(1, idPredio);
        cstmt.setString(2, numeroPredial);
        cstmt.setString(3, nombre);
        cstmt.setString(4, departamento);
        cstmt.setString(5, municipio);
        cstmt.setString(6, vereda);
        cstmt.setDouble(7, latitud);
        cstmt.setDouble(8, longitud);
        cstmt.setInt(9, idPropietario);
        
        // Parámetro OUT
        cstmt.registerOutParameter(10, java.sql.Types.VARCHAR);  // P_MENSAJE
        
        cstmt.execute();
        
        String mensaje = cstmt.getString(10);
        
        if (mensaje.contains("agregado")) {
            System.out.println("✅ " + mensaje);
            return true;
        } else {
            System.err.println("❌ " + mensaje);
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Error en sp_registrar_predio_completo: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
/**
 * SP: sp_actualizar_predio
 * Actualiza la información de un predio
 */
public static boolean actualizarPredio(String numeroPredial, String nombre, String departamento, 
                                      String municipio, String vereda, double latitud, 
                                      double longitud, int idPropietario) {
    String sql = "{call sp_actualizar_predio(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {
        
        // Parámetros IN
        cstmt.setString(1, numeroPredial);
        cstmt.setString(2, nombre);
        cstmt.setString(3, departamento);
        cstmt.setString(4, municipio);
        cstmt.setString(5, vereda);
        cstmt.setDouble(6, latitud);
        cstmt.setDouble(7, longitud);
        cstmt.setInt(8, idPropietario);
        
        // Parámetro OUT
        cstmt.registerOutParameter(9, java.sql.Types.VARCHAR);  // P_MENSAJE
        
        cstmt.execute();
        
        String mensaje = cstmt.getString(9);
        
        if (mensaje.contains("exitosamente")) {
            System.out.println("✓ " + mensaje);
            return true;
        } else {
            System.err.println("❌ " + mensaje);
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Error en sp_actualizar_predio: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

}