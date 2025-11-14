    package app.db;

    import app.model.Cultivo;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    


    /**
     * Clase que maneja todas las operaciones de base de datos relacionadas con la tabla CULTIVO.
     * 
     * Permite crear, leer, actualizar, eliminar y asociar cultivos con plagas dentro del sistema.
     */
    public class CultivoDatabase {

        /**
         * Agrega un nuevo cultivo a la base de datos.
         * @param cultivo Objeto de tipo Cultivo con los datos a registrar.
         * @return true si el cultivo fue agregado correctamente, false en caso de error.
         */
    public static boolean agregarCultivo(Cultivo cultivo) {
        // ✅ 1. Generar ID manualmente (TU LÓGICA)
        int nuevoId = obtenerNuevoId();

        // ✅ 2. Obtener ID del productor asignado al predio
        int idProductor = obtenerProductorDelPredio(Integer.parseInt(cultivo.getIdPredio()));
        if (idProductor == -1) {
            System.err.println("⚠ No hay productor asignado a este predio");
            return false;
        }

        // ✅ 3. Usar procedimiento almacenado CON el ID generado
        boolean exito = StoredProcedures.registrarCultivo(
            nuevoId,
            idProductor,
            Integer.parseInt(cultivo.getIdPredio()),
            cultivo.getEspecieCientifica(),
            cultivo.getNombreComun(),
            cultivo.getVariedad(),
            cultivo.getCiclo()
        );

        if (exito) {
            System.out.println("✓ Cultivo creado exitosamente con ID: " + nuevoId);
        }

        return exito;
    }

    /**
     * Método auxiliar para obtener el ID del productor asignado a un predio
     */
  private static int obtenerProductorDelPredio(int idPredio) {
    String sql = "{call sp_obtener_productor_predio(?, ?, ?)}";
    
    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {
        
        // Validar parámetro de entrada
        if (idPredio <= 0) {
            System.err.println("❌ ID de predio inválido: " + idPredio);
            return -1;
        }
        
        // Configurar parámetros
        cs.setInt(1, idPredio);                    // p_id_predio IN
        cs.registerOutParameter(2, Types.NUMERIC); // p_id_productor OUT
        cs.registerOutParameter(3, Types.VARCHAR); // p_mensaje OUT
        
        // Ejecutar procedimiento almacenado
        cs.execute();
        
        // Obtener resultados
        int idProductor = cs.getInt(2);
        String mensaje = cs.getString(3);
        
        // Log del resultado
        if (idProductor != -1) {
            System.out.println("✅ " + mensaje);
            return idProductor;
        } else {
            System.err.println("❌ " + mensaje);
            return -1;
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Error de base de datos al obtener productor del predio:");
        System.err.println("   - Código: " + e.getErrorCode());
        System.err.println("   - Estado: " + e.getSQLState());
        System.err.println("   - Mensaje: " + e.getMessage());
        return -1;
    }
}        /**
         * Obtiene un cultivo por su ID.
         * @param idCultivo ID del cultivo.
         * @return Objeto Cultivo si se encuentra, o null si no existe.
         */
        public static Cultivo obtenerCultivoPorId(int idCultivo) {
        String sql = "{ ? = call fn_obtener_cultivo_por_id(?) }";

        try (Connection conn = Conexion.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setInt(2, idCultivo);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs != null && rs.next()) {
                    return crearCultivoDesdeResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cultivo por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

        /**
         * Obtiene el ID de un cultivo usando su nombre común o especie científica.
         * @param nombreComun Nombre del cultivo.
         * @return ID del cultivo o -1 si no se encuentra.
         */
        public static int obtenerIdCultivoPorNombre(String nombreComun) {
            String sql = "SELECT ID_CULTIVO FROM CULTIVO WHERE NOMBRE_COMUN = ? OR ESPECIE_CIENTIFICA = ?";

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombreComun);
                pstmt.setString(2, nombreComun);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("ID_CULTIVO");
                }

            } catch (SQLException e) {
                System.err.println("Error al obtener ID de cultivo: " + e.getMessage());
                e.printStackTrace();
            }
            return -1;
        }

        /**
         * Obtiene todos los cultivos registrados.
         * @return Lista de objetos Cultivo.
         */
        public static List<Cultivo> obtenerTodosCultivos() {
            List<Cultivo> cultivos = new ArrayList<>();
            String sql = "SELECT * FROM CULTIVO ORDER BY NOMBRE_COMUN";

            try (Connection conn = Conexion.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    cultivos.add(crearCultivoDesdeResultSet(rs));
                }

                System.out.println("✓ Se obtuvieron " + cultivos.size() + " cultivos");

            } catch (SQLException e) {
                System.err.println("Error al obtener cultivos: " + e.getMessage());
                e.printStackTrace();
            }
            return cultivos;
        }

        /**
         * Obtiene los cultivos que pertenecen a un predio.
         * @param idPredio ID del predio.
         * @return Lista de cultivos de ese predio.
         */
        public static List<Cultivo> obtenerCultivosPorPredio(int idPredio) {
            List<Cultivo> cultivos = new ArrayList<>();
            String sql = "SELECT * FROM CULTIVO WHERE ID_PREDIO = ? ORDER BY NOMBRE_COMUN";

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idPredio);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    cultivos.add(crearCultivoDesdeResultSet(rs));
                }

                System.out.println("✓ Se obtuvieron " + cultivos.size() + " cultivos del predio");

            } catch (SQLException e) {
                System.err.println("Error al obtener cultivos por predio: " + e.getMessage());
                e.printStackTrace();
            }
            return cultivos;
        }

        /**
         * Obtiene solo los nombres de los cultivos.
         * @return Lista con los nombres comunes de los cultivos.
         */
        public static List<String> obtenerNombresCultivos() {
        List<String> nombres = new ArrayList<>();
        String sql = "{ ? = call fn_obtener_nombres_cultivos() }";

        try (Connection conn = Conexion.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // Registrar parámetro de retorno como CURSOR
            cs.registerOutParameter(1, Types.REF_CURSOR);

            // Ejecutar función
            cs.execute();

            // Obtener el cursor resultante
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs != null && rs.next()) {
                    nombres.add(rs.getString("NOMBRE_COMUN"));
                }
            }

            System.out.println("✓ Se obtuvieron " + nombres.size() + " nombres de cultivos");

        } catch (SQLException e) {
            System.err.println("Error al obtener nombres de cultivos: " + e.getMessage());
            e.printStackTrace();
        }
        return nombres;
    }

        /**
         * Obtiene los nombres de cultivos de un predio específico.
         * @param idPredio ID del predio.
         * @return Lista con los nombres comunes de los cultivos.
         */
        public static List<String> obtenerNombresCultivosPorPredio(int idPredio) {
            List<String> nombres = new ArrayList<>();
            String sql = "SELECT NOMBRE_COMUN FROM CULTIVO WHERE ID_PREDIO = ? ORDER BY NOMBRE_COMUN";

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idPredio);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    nombres.add(rs.getString("NOMBRE_COMUN"));
                }

            } catch (SQLException e) {
                System.err.println("Error al obtener nombres de cultivos: " + e.getMessage());
                e.printStackTrace();
            }
            return nombres;
        }

        /**
         * Actualiza la información de un cultivo.
         * @param cultivo Objeto con los nuevos datos.
         * @param idCultivo ID del cultivo a actualizar.
         * @return true si se actualizó correctamente, false en caso contrario.
         */
        public static boolean actualizarCultivo(Cultivo cultivo, int idCultivo) {
            String sql = "UPDATE CULTIVO SET ESPECIE_CIENTIFICA = ?, NOMBRE_COMUN = ?, " +
                         "VARIEDAD = ?, CICLO = ? WHERE ID_CULTIVO = ?";

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, cultivo.getEspecieCientifica());
                pstmt.setString(2, cultivo.getNombreComun());
                pstmt.setString(3, cultivo.getVariedad());
                pstmt.setString(4, cultivo.getCiclo());
                pstmt.setInt(5, idCultivo);

                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("✓ Cultivo actualizado exitosamente");
                    return true;
                }

            } catch (SQLException e) {
                System.err.println("Error al actualizar cultivo: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }

        /**
         * Elimina un cultivo de la base de datos.
         * @param idCultivo ID del cultivo a eliminar.
         * @return true si fue eliminado correctamente, false en caso de error.
         */
        public static boolean eliminarCultivo(int idCultivo) {
            String sql = "DELETE FROM CULTIVO WHERE ID_CULTIVO = ?";

            try (Connection conn = Conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idCultivo);
                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("✓ Cultivo eliminado exitosamente");
                    return true;
                }

            } catch (SQLException e) {
                System.err.println("Error al eliminar cultivo: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }

        /**
         * Asocia un cultivo con una plaga en una inspección.
         * @param idCultivo ID del cultivo.
         * @param idPlaga ID de la plaga.
         * @param idInspeccion ID de la inspección.
         * @return true si se asoció correctamente, false en caso contrario.
         */
        public static boolean asociarCultivoConPlaga(int idCultivo, int idPlaga, int idInspeccion) {
        int nuevoIdDetalle = obtenerNuevoIdDetalle();
        return StoredProcedures.asociarCultivoPlaga(nuevoIdDetalle, idInspeccion, idCultivo, idPlaga);
    }

        /**
         * Obtiene un nuevo ID para un cultivo.
         * @return El nuevo ID disponible.
         */
        private static int obtenerNuevoId() {
            String sql = "SELECT NVL(MAX(ID_CULTIVO), 0) + 1 AS NUEVO_ID FROM CULTIVO";

            try (Connection conn = Conexion.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                if (rs.next()) {
                    return rs.getInt("NUEVO_ID");
                }

            } catch (SQLException e) {
                System.err.println("Error al obtener nuevo ID de cultivo: " + e.getMessage());
                e.printStackTrace();
            }
            return 1;
        }

        /**
         * Obtiene un nuevo ID para el detalle de inspección.
         * @return El nuevo ID disponible.
         */
        private static int obtenerNuevoIdDetalle() {
            String sql = "SELECT NVL(MAX(ID_DETALLE), 0) + 1 AS NUEVO_ID FROM DETALLE_INSPECCION";

            try (Connection conn = Conexion.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                if (rs.next()) {
                    return rs.getInt("NUEVO_ID");
                }

            } catch (SQLException e) {
                System.err.println("Error al obtener nuevo ID de detalle: " + e.getMessage());
                e.printStackTrace();
            }
            return 1;
        }

        /**
         * Crea un objeto Cultivo a partir de un registro de la base de datos.
         * @param rs ResultSet con los datos del cultivo.
         * @return Objeto Cultivo.
         * @throws SQLException si ocurre un error al leer los datos.
         */
      private static Cultivo crearCultivoDesdeResultSet(ResultSet rs) throws SQLException {
    Cultivo cultivo = new Cultivo();
    cultivo.setIdCultivo(rs.getInt("ID_CULTIVO")); // ✅ AGREGAR
    cultivo.setEspecieCientifica(rs.getString("ESPECIE_CIENTIFICA"));
    cultivo.setNombreComun(rs.getString("NOMBRE_COMUN"));
    cultivo.setVariedad(rs.getString("VARIEDAD"));
    cultivo.setCiclo(rs.getString("CICLO"));
    cultivo.setIdPredio(String.valueOf(rs.getInt("ID_PREDIO")));
    return cultivo;
}
    }
