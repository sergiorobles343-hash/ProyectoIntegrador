package app.ui;

import javax.swing.*;
import java.awt.*;

public class DetalleInspeccionFrame extends JFrame {
// Frame que muestra detalles completos de una inspección en modo solo lectura.
// Organiza información general (predio, fecha, cultivo, plaga) y dos áreas de texto
// para observaciones generales y específicas. Usa BoxLayout vertical con paneles estilizados.
    public DetalleInspeccionFrame(String predio, String fecha, String cultivo, String plaga, 
                                   String observacionesGenerales, String observacionesEspecificas) {
        setTitle("Detalle de Inspección");
        setSize(600, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(Color.WHITE);

        // Título
        JLabel lblTitulo = new JLabel("Detalle de Inspección", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 102, 204));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));

        // Panel con información básica
        JPanel panelInfo = new JPanel(new GridLayout(4, 2, 10, 10));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información General"));

        panelInfo.add(crearLabelNegrita("Predio:"));
        panelInfo.add(new JLabel(predio));

        panelInfo.add(crearLabelNegrita("Fecha:"));
        panelInfo.add(new JLabel(fecha));

        panelInfo.add(crearLabelNegrita("Cultivo:"));
        panelInfo.add(new JLabel(cultivo));

        panelInfo.add(crearLabelNegrita("Plaga:"));
        panelInfo.add(new JLabel(plaga));

        panelPrincipal.add(panelInfo);
        panelPrincipal.add(Box.createVerticalStrut(15));

        // Panel observaciones GENERALES
        JPanel panelObsGenerales = new JPanel(new BorderLayout(5, 5));
        panelObsGenerales.setBackground(Color.WHITE);
        panelObsGenerales.setBorder(BorderFactory.createTitledBorder("Observaciones Generales"));

        JTextArea txtObsGenerales = new JTextArea(observacionesGenerales != null ? observacionesGenerales : "Sin observaciones generales");
        txtObsGenerales.setLineWrap(true);
        txtObsGenerales.setWrapStyleWord(true);
        txtObsGenerales.setEditable(false);
        txtObsGenerales.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObsGenerales.setBackground(new Color(245, 245, 245));
        JScrollPane scrollGenerales = new JScrollPane(txtObsGenerales);
        scrollGenerales.setPreferredSize(new Dimension(0, 100));
        panelObsGenerales.add(scrollGenerales, BorderLayout.CENTER);

        panelPrincipal.add(panelObsGenerales);
        panelPrincipal.add(Box.createVerticalStrut(10));

        // Panel observaciones ESPECÍFICAS
        JPanel panelObsEspecificas = new JPanel(new BorderLayout(5, 5));
        panelObsEspecificas.setBackground(Color.WHITE);
        panelObsEspecificas.setBorder(BorderFactory.createTitledBorder("Observaciones Específicas"));

        JTextArea txtObsEspecificas = new JTextArea(observacionesEspecificas != null ? observacionesEspecificas : "Sin observaciones específicas");
        txtObsEspecificas.setLineWrap(true);
        txtObsEspecificas.setWrapStyleWord(true);
        txtObsEspecificas.setEditable(false);
        txtObsEspecificas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObsEspecificas.setBackground(new Color(245, 245, 245));
        JScrollPane scrollEspecificas = new JScrollPane(txtObsEspecificas);
        scrollEspecificas.setPreferredSize(new Dimension(0, 100));
        panelObsEspecificas.add(scrollEspecificas, BorderLayout.CENTER);

        panelPrincipal.add(panelObsEspecificas);
        panelPrincipal.add(Box.createVerticalStrut(15));

        // Botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(52, 152, 219));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrar.addActionListener(e -> dispose());
        panelPrincipal.add(btnCerrar);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JLabel crearLabelNegrita(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }
}