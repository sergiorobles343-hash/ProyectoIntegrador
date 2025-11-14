package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que permite conectarse a una base de datos Oracle.
 * 
 * Contiene un método estático para crear y devolver una conexión 
 * a la base de datos usando JDBC.
 */
public class Conexion {

    // Dirección del servidor Oracle (IP, puerto y servicio)
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/xe"; 
    
    // Usuario de la base de datos
    private static final String USER = "destroy";
    
    // Contraseña del usuario
    private static final String PASSWORD = "destroy";


    /**
     * Establece una conexión con la base de datos Oracle.
     * 
     * @return un objeto Connection si la conexión fue exitosa, 
     *         o null si ocurrió un error.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Cargar el driver JDBC de Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");
            
            // Intentar conectar con la base de datos
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a Oracle");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar con Oracle: " + e.getMessage());
        }
        return conn;
    }
}
