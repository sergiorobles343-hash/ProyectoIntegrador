package app;

import app.db.Conexion;
import app.ui.LoginFrame;

import java.sql.Connection;
// 
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try (Connection conn = Conexion.getConnection()) {
                if (conn != null) {
                    System.out.println(" Conexion exitosa con la base de datos Oracl2e.");
                    new LoginFrame().setVisible(true);
                } else {
                    System.err.println("No se pudo conectar con la base de datos.");
                }
            } catch (Exception e) {
                System.err.println("Error al verificar la conexión: " + e.getMessage());
            }
        });
    }
}