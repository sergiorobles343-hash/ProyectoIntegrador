/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.ui;

/**
 *
 * @author SALA-404
 */
import app.db.Conexion;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import app.db.InspeccionDatabase;
// Frame para editar los datos de una inspección agrícola existente
public class ActualizarInspeccionFrame extends JFrame {

    private JTextField txtPredio, txtCultivo, txtPlaga;
    private JTextArea txtObsGenerales, txtObsEspecificas;
    private JFormattedTextField txtFecha;
    private JButton btnGuardar;
    private int idInspeccion;
// Frame para actualizar inspecciones agrícolas existentes. Contiene formulario con campos
// de texto para predio, cultivo, plaga y fecha, más áreas de texto para observaciones.
// Utiliza GridBagLayout para organizar componentes y valida entrada de usuario antes de guardar.

    public ActualizarInspeccionFrame(int id, String predio, String fecha, String cultivo,
                                     String plaga, String obsGenerales, String obsEspecificas) {
        this.idInspeccion = id;

        setTitle("Actualizar Inspección - ID " + id);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblPredio = new JLabel("Predio:");
        txtPredio = new JTextField(predio);

        JLabel lblFecha = new JLabel("Fecha (YYYY-MM-DD):");
        txtFecha = new JFormattedTextField(fecha);

        JLabel lblCultivo = new JLabel("Cultivo:");
        txtCultivo = new JTextField(cultivo);

        JLabel lblPlaga = new JLabel("Plaga:");
        txtPlaga = new JTextField(plaga);

        JLabel lblObsG = new JLabel("Observaciones Generales:");
        txtObsGenerales = new JTextArea(obsGenerales);
        txtObsGenerales.setRows(3);
        txtObsGenerales.setLineWrap(true);
        txtObsGenerales.setWrapStyleWord(true);

        JLabel lblObsE = new JLabel("Observaciones Específicas:");
        txtObsEspecificas = new JTextArea(obsEspecificas);
        txtObsEspecificas.setRows(3);
        txtObsEspecificas.setLineWrap(true);
        txtObsEspecificas.setWrapStyleWord(true);

        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(52, 152, 219));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);

        // --- Agregar componentes ---
        gbc.gridy = 0; panel.add(lblPredio, gbc);
        gbc.gridy = 1; panel.add(txtPredio, gbc);
        gbc.gridy = 2; panel.add(lblFecha, gbc);
        gbc.gridy = 3; panel.add(txtFecha, gbc);
        gbc.gridy = 4; panel.add(lblCultivo, gbc);
        gbc.gridy = 5; panel.add(txtCultivo, gbc);
        gbc.gridy = 6; panel.add(lblPlaga, gbc);
        gbc.gridy = 7; panel.add(txtPlaga, gbc);
        gbc.gridy = 8; panel.add(lblObsG, gbc);
        gbc.gridy = 9; panel.add(new JScrollPane(txtObsGenerales), gbc);
        gbc.gridy = 10; panel.add(lblObsE, gbc);
        gbc.gridy = 11; panel.add(new JScrollPane(txtObsEspecificas), gbc);
        gbc.gridy = 12; panel.add(btnGuardar, gbc);

        add(panel, BorderLayout.CENTER);

        // --- Acción del botón ---
        btnGuardar.addActionListener(e -> guardarCambios());
    }
// Valida y guarda los cambios de la inspección en la base de datos. Obtiene datos de campos,
// verifica que predio y fecha no estén vacíos, actualiza tabla INSPECCION y DETALLE_INSPECCION.
// Maneja excepciones de formato de fecha y errores SQL, mostrando mensajes al usuario.
private void guardarCambios() {
    String predio = txtPredio.getText().trim();
    String fechaStr = txtFecha.getText().trim();
    String cultivo = txtCultivo.getText().trim();
    String plaga = txtPlaga.getText().trim();
    String obsG = txtObsGenerales.getText().trim();
    String obsE = txtObsEspecificas.getText().trim();

    if (predio.isEmpty() || fechaStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Predio y Fecha son obligatorios.");
        return;
    }

    String sql = "UPDATE INSPECCION SET FECHA=?, OBSERVACIONES_GENERALES=? WHERE ID_INSPECCION=?";

    try (Connection conn = Conexion.getConnection()) {
        java.sql.Date fechaSQL = java.sql.Date.valueOf(fechaStr);
        
        // Actualiza tabla INSPECCION
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, fechaSQL);
            pstmt.setString(2, obsG);
            pstmt.setInt(3, idInspeccion);
            pstmt.executeUpdate();
        }

        //Actualizar DETALLE_INSPECCION solo si hay texto
        // O crear el registro si no existe
        if (!obsE.isEmpty()) {
            boolean actualizado = InspeccionDatabase.actualizarObservacionesEspecificas(idInspeccion, obsE);
            
            if (!actualizado) {
                System.out.println("No se pudo actualizar observaciones específicas");
            }
        } else {
            // Si está vacío, intentar borrar o dejar NULL
            String sqlDetalle = "UPDATE DETALLE_INSPECCION SET OBSERVACIONES_ESPECIFICAS = NULL WHERE ID_INSPECCION = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlDetalle)) {
                pstmt2.setInt(1, idInspeccion);
                int filas = pstmt2.executeUpdate();
                
                if (filas == 0) {
                    System.out.println("No existe detalle para limpiar observaciones específicas");
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Inspección actualizada correctamente.");
        dispose();

    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, 
            "Formato de fecha inválido. Use: YYYY-MM-DD (ej: 2025-10-08)", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        e.printStackTrace();
    }
}
}