package app.ui;

import app.db.UserDatabase;
import app.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegisterFrame extends JFrame {

    private JTextField txtNombre, txtDocumento, txtCorreo;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JButton btnRegistrar, btnCancelar;

    public RegisterFrame() {
        setTitle("Registro - Sistema Fitosanitario");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal dividido
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel);

        // ========================= PANEL IZQUIERDO (FORMULARIO) =========================
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Crear Cuenta", JLabel.LEFT);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(25, 30, 40));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelForm.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("Completa los campos para registrarte");
        lblSub.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 100, 110));
        gbc.gridy = 1;
        panelForm.add(lblSub, gbc);

        // Campos
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(new JLabel("Nombre completo:"), gbc);
        txtNombre = new JTextField(); estilizarCampo(txtNombre);
        gbc.gridx = 1; panelForm.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panelForm.add(new JLabel("Documento:"), gbc);
        txtDocumento = new JTextField(); estilizarCampo(txtDocumento);
        gbc.gridx = 1; panelForm.add(txtDocumento, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panelForm.add(new JLabel("Correo:"), gbc);
        txtCorreo = new JTextField(); estilizarCampo(txtCorreo);
        gbc.gridx = 1; panelForm.add(txtCorreo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panelForm.add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField(); estilizarCampo(txtPassword);
        gbc.gridx = 1; panelForm.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 6; panelForm.add(new JLabel("Rol:"), gbc);
        cmbRol = new JComboBox<>(new String[]{"Productor", "Propietario", "Tecnico"});
        estilizarCombo(cmbRol);
        gbc.gridx = 1; panelForm.add(cmbRol, gbc);

        // Botones
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 5, 15);
        btnRegistrar = crearBoton("Registrar", new Color(70, 130, 255));
        panelForm.add(btnRegistrar, gbc);

        gbc.gridy = 8;
        btnCancelar = crearBotonSecundario("Cancelar");
        panelForm.add(btnCancelar, gbc);

        mainPanel.add(panelForm);

        // ========================= PANEL DERECHO (DECORATIVO ANIMADO) =========================
        AnimatedPanel panelDecorativo = new AnimatedPanel();
        panelDecorativo.setLayout(new GridBagLayout());
        JLabel lblMensaje = new JLabel("<html><div style='text-align:center; color:white;'>"
                + "<h2>Únete al Sistema Fitosanitario</h2>"
                + "<p style='font-size:13px;'>Administra y gestiona todo fácilmente<br>desde una plataforma moderna.</p>"
                + "</div></html>");
        lblMensaje.setFont(new Font("Poppins", Font.PLAIN, 16));
        panelDecorativo.add(lblMensaje);
        mainPanel.add(panelDecorativo);

        // ========================= EVENTOS =========================
        btnRegistrar.addActionListener(e -> registrarUsuario());
        btnCancelar.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    // ========================= ESTILOS =========================
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Poppins", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setBackground(new Color(250, 250, 252));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(new Font("Poppins", Font.PLAIN, 14));
        combo.setBackground(new Color(250, 250, 252));
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 15));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorFondo);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new RoundedBorder(25));
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 15));
        boton.setForeground(new Color(70, 130, 255));
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new RoundedBorder(25));
        return boton;
    }

    private static class RoundedBorder extends javax.swing.border.LineBorder {
        RoundedBorder(int radius) {
            super(new Color(200, 200, 200), 1, true);
        }
    }

    // ========================= PANEL ANIMADO =========================
    private static class AnimatedPanel extends JPanel {
        private final List<ShapeData> shapes = new ArrayList<>();
        private final Random random = new Random();

        AnimatedPanel() {
            setBackground(new Color(100, 160, 255));
            for (int i = 0; i < 12; i++) shapes.add(new ShapeData(getRandomShape(), random));
            javax.swing.Timer timer = new javax.swing.Timer(40, e -> {
                for (ShapeData s : shapes) s.update(getWidth(), getHeight());
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint grad = new GradientPaint(0, 0, new Color(130, 170, 255),
                    getWidth(), getHeight(), new Color(70, 130, 255));
            g2.setPaint(grad);
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (ShapeData s : shapes) {
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fill(s.shape);
            }
        }

        private Shape getRandomShape() {
            int t = random.nextInt(3);
            int size = 25 + random.nextInt(30);
            switch (t) {
                case 0: return new Ellipse2D.Double(random.nextInt(900), random.nextInt(600), size, size);
                case 1: return new Rectangle2D.Double(random.nextInt(900), random.nextInt(600), size, size);
                default:
                    Polygon p = new Polygon();
                    for (int i = 0; i < 5; i++)
                        p.addPoint((int) (Math.cos(i * 2 * Math.PI / 5) * size + 50),
                                   (int) (Math.sin(i * 2 * Math.PI / 5) * size + 50));
                    return p;
            }
        }

        private static class ShapeData {
            Shape shape;
            double dx, dy;

            ShapeData(Shape shape, Random r) {
                this.shape = shape;
                this.dx = (r.nextDouble() - 0.5) * 2;
                this.dy = (r.nextDouble() - 0.5) * 2;
            }

            void update(int w, int h) {
                if (shape instanceof RectangularShape rs) {
                    double x = rs.getX() + dx, y = rs.getY() + dy;
                    if (x < 0 || x > w - rs.getWidth()) dx *= -1;
                    if (y < 0 || y > h - rs.getHeight()) dy *= -1;
                    rs.setFrame(x, y, rs.getWidth(), rs.getHeight());
                }
            }
        }
    }

    // ========================= REGISTRO =========================
    private void registrarUsuario() {
        String nombre = txtNombre.getText().trim();
        String documento = txtDocumento.getText().trim();
        String correo = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword());
        String rol = (String) cmbRol.getSelectedItem();

        if (nombre.isEmpty() || documento.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        if (!documento.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El documento debe contener solo números.");
            return;
        }

        Usuario nuevoUsuario;
        switch (rol) {
            case "Productor" -> nuevoUsuario = new Productor("", nombre, documento, correo, password);
            case "Propietario" -> nuevoUsuario = new Propietario("", nombre, documento, correo, password);
            default -> nuevoUsuario = new Tecnico("", nombre, documento, correo, password);
        }

        if (UserDatabase.registrarUsuario(nuevoUsuario)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error: el documento o correo ya está registrado.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}
