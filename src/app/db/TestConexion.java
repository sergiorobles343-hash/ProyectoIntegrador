package app.db;

import java.sql.Connection;

/**
 * Clase de prueba para verificar la conexión con la base de datos Oracle.
 * Ejecuta el método {@code getConnection()} de la clase {@link Conexion}
 * y muestra en consola si la conexión fue exitosa o no.
 * 
 * Esta clase es útil para comprobar que los parámetros de conexión (URL, usuario, contraseña)
 * están configurados correctamente antes de ejecutar el resto de la aplicación.
 */

public class TestConexion {
    public static void main(String[] args) {
        Connection conn = Conexion.getConnection();
        if (conn != null) {
            System.out.println("Conectado correctamente ");
        } else {
            System.out.println("No se pudo consctar ");
        }
    }
}

