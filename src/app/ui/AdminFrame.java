
package app.ui;

import app.model.Admin;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminFrame extends JFrame {

    private Admin admin;

    public AdminFrame() {
        admin = new Admin();
        setTitle("Panel de Administración - Sistema Fitosanitario");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel lblTitulo = new JLabel("Panel del Administrador");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblSubtitulo = new JLabel("Gestiona usuarios, predios, cultivos y plagas del sistema");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(120, 120, 130));

        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(Color.WHITE);
        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createVerticalStrut(5));
        panelTitulos.add(lblSubtitulo);

        JButton btnCerrarSesion = crearBotonSecundario("← Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "¿Deseas cerrar sesión?",
                    "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        panelSuperior.add(panelTitulos, BorderLayout.WEST);
        panelSuperior.add(btnCerrarSesion, BorderLayout.EAST);

        // Panel central con scroll
        JPanel panelCards = new JPanel(new GridBagLayout());
        panelCards.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // === SECCIÓN USUARIOS ===
        JLabel lblUsuarios = new JLabel("👥 GESTIÓN DE USUARIOS");
        lblUsuarios.setFont(new Font("Poppins", Font.BOLD, 16));
        lblUsuarios.setForeground(new Color(100, 100, 110));
        lblUsuarios.setBorder(new EmptyBorder(15, 10, 10, 0));
        gbc.gridy = 0;
        panelCards.add(lblUsuarios, gbc);

        // Card Técnicos
        JPanel cardTecnicos = crearCard(
            "👨‍🔧 Técnicos",
            "Gestiona el personal técnico del sistema",
            new Color(90, 140, 255)
        );
        gbc.gridy = 1;
        panelCards.add(cardTecnicos, gbc);
        cardTecnicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new GestionUsuarioFrame("Tecnico").setVisible(true);
            }
        });

        // Card Productores
        JPanel cardProductores = crearCard(
            "🌱 Productores",
            "Administra los productores agrícolas",
            new Color(80, 200, 120)
        );
        gbc.gridy = 2;
        panelCards.add(cardProductores, gbc);
        cardProductores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new GestionUsuarioFrame("Productor").setVisible(true);
            }
        });

        // Card Propietarios
        JPanel cardPropietarios = crearCard(
            "🏡 Propietarios",
            "Controla los propietarios de predios",
            new Color(255, 160, 100)
        );
        gbc.gridy = 3;
        panelCards.add(cardPropietarios, gbc);
        cardPropietarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new GestionUsuarioFrame("Propietario").setVisible(true);
            }
        });

        // === SECCIÓN DATOS ===
        JLabel lblDatos = new JLabel("📊 GESTIÓN DE DATOS");
        lblDatos.setFont(new Font("Poppins", Font.BOLD, 16));
        lblDatos.setForeground(new Color(100, 100, 110));
        lblDatos.setBorder(new EmptyBorder(25, 10, 10, 0));
        gbc.gridy = 4;
        panelCards.add(lblDatos, gbc);

        // Card Predios
        JPanel cardPredios = crearCard(
            "🏞️ Predios",
            "Administra todos los predios del sistema",
            new Color(120, 80, 200)
        );
        gbc.gridy = 5;
        panelCards.add(cardPredios, gbc);
        cardPredios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new GestionPredioFrame().setVisible(true);
            }
        });

        // Card Cultivos
        JPanel cardCultivos = crearCard(
            "🌾 Cultivos",
            "Gestiona los cultivos registrados",
            new Color(255, 180, 50)
        );
        gbc.gridy = 6;
        panelCards.add(cardCultivos, gbc);
        cardCultivos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new GestionCultivoFrame().setVisible(true);
            }
        });

        // Card Plagas
        JPanel cardPlagas = crearCard(
            "🐛 Plagas",
            "Administra el catálogo de plagas",
            new Color(220, 50, 80)
        );
        gbc.gridy = 7;
        panelCards.add(cardPlagas, gbc);
        cardPlagas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new GestionPlagaFrame().setVisible(true);
            }
        });

        // Scroll para el panel central
        JScrollPane scrollPane = new JScrollPane(panelCards);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Footer
        JPanel panelFooter = new JPanel();
        panelFooter.setBackground(Color.WHITE);
        panelFooter.setBorder(new EmptyBorder(20, 0, 30, 0));
        JLabel lblFooter = new JLabel("© 2025 Sistema Fitosanitario - Panel Administrativo");
        lblFooter.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(160, 160, 170));
        panelFooter.add(lblFooter);

        mainPanel.add(panelSuperior, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(panelFooter, BorderLayout.SOUTH);
    }

    private JPanel crearCard(String titulo, String descripcion, Color colorAccento) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 10));
        card.setBackground(new Color(250, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(25, 30, 25, 30)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(700, 100));
        card.setMaximumSize(new Dimension(900, 100));

        // Barra de color lateral
        JPanel barraColor = new JPanel();
        barraColor.setBackground(colorAccento);
        barraColor.setPreferredSize(new Dimension(5, 60));

        // Contenido del card
        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(120, 120, 130));

        panelTexto.add(lblTitulo);
        panelTexto.add(Box.createVerticalStrut(5));
        panelTexto.add(lblDesc);

        // Icono de flecha
        JLabel lblFlecha = new JLabel("→");
        lblFlecha.setFont(new Font("Poppins", Font.BOLD, 28));
        lblFlecha.setForeground(colorAccento);

        card.add(barraColor, BorderLayout.WEST);
        card.add(panelTexto, BorderLayout.CENTER);
        card.add(lblFlecha, BorderLayout.EAST);

        // Efecto hover
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(245, 247, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorAccento, 2, true),
                    new EmptyBorder(24, 29, 24, 29)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(250, 250, 252));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                    new EmptyBorder(25, 30, 25, 30)
                ));
            }
        });

        return card;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 14));
        boton.setForeground(new Color(70, 130, 255));
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(10, 20, 10, 20)
        ));
        boton.setPreferredSize(new Dimension(160, 42));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(245, 247, 250));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(Color.WHITE);
            }
        });

        return boton;
    }
}