package app.ui;

import app.db.UserDatabase;
import app.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoginFrame extends JFrame {
    private JTextField txtDocumento;
    private JPasswordField txtContrasena;
    private JButton btnLogin, btnRegistro;

    public LoginFrame() {
        setTitle("Inicio de Sesión - Sistema Fitosanitario");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel);

        // ------------------- IZQUIERDA -------------------
        JPanel panelLogin = new JPanel(new GridBagLayout());
        panelLogin.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Login", JLabel.LEFT);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(25, 30, 40));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelLogin.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("Accede con tu cuenta para continuar");
        lblSub.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblSub.setForeground(new Color(120, 120, 130));
        gbc.gridy = 1;
        panelLogin.add(lblSub, gbc);

        // Documento
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridx = 0;
        panelLogin.add(new JLabel("Documento:"), gbc);
        txtDocumento = new JTextField();
        estilizarCampo(txtDocumento);
        gbc.gridx = 1;
        panelLogin.add(txtDocumento, gbc);

        // Contraseña
        gbc.gridy = 3; gbc.gridx = 0;
        panelLogin.add(new JLabel("Contraseña:"), gbc);
        txtContrasena = new JPasswordField();
        estilizarCampo(txtContrasena);
        gbc.gridx = 1;
        panelLogin.add(txtContrasena, gbc);

        // Botones
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 5, 15);
        btnLogin = crearBoton("Iniciar Sesión", new Color(90, 140, 255));
        panelLogin.add(btnLogin, gbc);

        gbc.gridy = 5;
        btnRegistro = crearBotonSecundario("Crear Cuenta");
        panelLogin.add(btnRegistro, gbc);

        gbc.gridy = 6;
        JLabel lblFooter = new JLabel("© 2025 Sistema Fitosanitario", JLabel.CENTER);
        lblFooter.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(160, 160, 170));
        panelLogin.add(lblFooter, gbc);

        mainPanel.add(panelLogin);

        // ------------------- DERECHA: PANEL ANIMADO -------------------
        AnimatedPanel panelDecorativo = new AnimatedPanel();
        panelDecorativo.setLayout(new GridBagLayout());

        JLabel lblMensaje = new JLabel("<html><div style='text-align:center; color:white;'>"
                + "<h2>Convierte tus ideas<br>en realidad</h2>"
                + "<p style='font-size:13px;'>Gestiona tus predios y usuarios fácilmente<br>"
                + "desde una sola plataforma.</p></div></html>");
        lblMensaje.setFont(new Font("Poppins", Font.PLAIN, 16));
        panelDecorativo.add(lblMensaje);

        mainPanel.add(panelDecorativo);

        // ------------------- EVENTOS -------------------
        btnLogin.addActionListener(e -> login());
        btnRegistro.addActionListener(e -> abrirRegistro());
        txtDocumento.addActionListener(e -> login());
        txtContrasena.addActionListener(e -> login());
    }

    // ======== CLASE PANEL ANIMADO ========
    private static class AnimatedPanel extends JPanel {
        private final List<ShapeData> shapes = new ArrayList<>();
        private final Random random = new Random();

        public AnimatedPanel() {
            setBackground(new Color(80, 140, 255));
            for (int i = 0; i < 20; i++) {
                shapes.add(new ShapeData(getWidth(), getHeight(), random));
            }

            Timer timer = new Timer(30, e -> {
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

            GradientPaint grad = new GradientPaint(0, 0,
                    new Color(140, 180, 255),
                    getWidth(), getHeight(),
                    new Color(80, 140, 255));
            g2.setPaint(grad);
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (ShapeData s : shapes) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2.setColor(s.color);
                g2.fill(s.shape);
            }
        }

        private static class ShapeData {
            Shape shape;
            Color color;
            float x, y, dx, dy, size;
            int type;

            ShapeData(int w, int h, Random r) {
                x = r.nextInt(Math.max(w, 1000));
                y = r.nextInt(Math.max(h, 700));
                dx = (r.nextFloat() - 0.5f) * 1.5f;
                dy = (r.nextFloat() - 0.5f) * 1.5f;
                size = 20 + r.nextFloat() * 40;
                color = new Color(255, 255, 255, 180);
                type = r.nextInt(3);
                crearForma();
            }

            void crearForma() {
                switch (type) {
                    case 0 -> shape = new Ellipse2D.Float(x, y, size, size);
                    case 1 -> {
                        Polygon p = new Polygon();
                        for (int i = 0; i < 3; i++) {
                            p.addPoint((int) (x + size * Math.cos(i * 2 * Math.PI / 3)),
                                       (int) (y + size * Math.sin(i * 2 * Math.PI / 3)));
                        }
                        shape = p;
                    }
                    case 2 -> shape = new Rectangle((int) x, (int) y, (int) size, (int) size);
                }
            }

            void update(int w, int h) {
                x += dx;
                y += dy;
                if (x < -50 || x > w + 50) dx *= -1;
                if (y < -50 || y > h + 50) dy *= -1;
                crearForma();
            }
        }
    }

    // ======== DIÁLOGO PERSONALIZADO ========
    private void mostrarMensaje(String titulo, String mensaje, boolean esError) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(esError ? new Color(255, 100, 100) : new Color(90, 140, 255), 2),
            new EmptyBorder(20, 25, 20, 25)
        ));

        // Icono y título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setOpaque(false);

        JLabel lblIcono = new JLabel(esError ? "✕" : "✓");
        lblIcono.setFont(new Font("Poppins", Font.BOLD, 32));
        lblIcono.setForeground(esError ? new Color(255, 100, 100) : new Color(80, 200, 120));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(25, 30, 40));

        panelTitulo.add(lblIcono);
        panelTitulo.add(lblTitulo);

        // Mensaje
        JLabel lblMensaje = new JLabel("<html><div style='margin-top:10px;'>" + mensaje + "</div></html>");
        lblMensaje.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblMensaje.setForeground(new Color(70, 70, 80));
        lblMensaje.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Botón
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setFont(new Font("Poppins", Font.BOLD, 13));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setBackground(esError ? new Color(255, 100, 100) : new Color(90, 140, 255));
        btnAceptar.setFocusPainted(false);
        btnAceptar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAceptar.setBorder(new EmptyBorder(10, 30, 10, 30));
        btnAceptar.addActionListener(e -> dialog.dispose());

        btnAceptar.addMouseListener(new MouseAdapter() {
            Color original = btnAceptar.getBackground();
            @Override
            public void mouseEntered(MouseEvent e) {
                btnAceptar.setBackground(original.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnAceptar.setBackground(original);
            }
        });

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setOpaque(false);
        panelBoton.add(btnAceptar);

        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);
        panelContenido.add(lblMensaje, BorderLayout.CENTER);
        panelContenido.add(panelBoton, BorderLayout.SOUTH);

        mainPanel.add(panelTitulo, BorderLayout.NORTH);
        mainPanel.add(panelContenido, BorderLayout.CENTER);

        dialog.add(mainPanel);
        
        // Animación de shake si es error
        if (esError) {
            Toolkit.getDefaultToolkit().beep(); // Sonido de error
            animarShake(dialog);
        }
        
        dialog.setVisible(true);
    }

    // Animación de sacudida para errores
    private void animarShake(JDialog dialog) {
        Point originalLocation = dialog.getLocation();
        Timer timer = new Timer(50, null);
        final int[] counter = {0};
        
        timer.addActionListener(e -> {
            int offset = counter[0] % 2 == 0 ? 10 : -10;
            dialog.setLocation(originalLocation.x + offset, originalLocation.y);
            counter[0]++;
            
            if (counter[0] >= 6) {
                dialog.setLocation(originalLocation);
                timer.stop();
            }
        });
        timer.start();
    }

    // Animación de fade in para éxito del admin
    private void animarAdminSuccess() {
        JWindow splash = new JWindow(this);
        splash.setSize(400, 200);
        splash.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(80, 200, 120));
        
        JLabel lblMensaje = new JLabel("✓ Acceso Concedido");
        lblMensaje.setFont(new Font("Poppins", Font.BOLD, 32));
        lblMensaje.setForeground(Color.WHITE);
        
        panel.add(lblMensaje);
        splash.setContentPane(panel);
        splash.setOpacity(0f);
        splash.setVisible(true);
        
        // Fade in
        Timer fadeIn = new Timer(30, null);
        final float[] opacity = {0f};
        fadeIn.addActionListener(e -> {
            opacity[0] += 0.05f;
            if (opacity[0] >= 1f) {
                opacity[0] = 1f;
                fadeIn.stop();
                
                // Esperar un poco y hacer fade out
                Timer wait = new Timer(800, ev -> {
                    Timer fadeOut = new Timer(30, null);
                    fadeOut.addActionListener(ev2 -> {
                        opacity[0] -= 0.05f;
                        if (opacity[0] <= 0f) {
                            splash.dispose();
                            fadeOut.stop();
                        } else {
                            splash.setOpacity(opacity[0]);
                        }
                    });
                    fadeOut.start();
                });
                wait.setRepeats(false);
                wait.start();
            } else {
                splash.setOpacity(opacity[0]);
            }
        });
        fadeIn.start();
    }

    // ======== ESTILOS ========
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Poppins", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setBackground(new Color(250, 250, 252));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 15));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorFondo);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new RoundedBorder(25));
        boton.setOpaque(true);
        boton.setPreferredSize(new Dimension(200, 45));
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
        boton.setPreferredSize(new Dimension(200, 45));
        return boton;
    }

    private static class RoundedBorder extends javax.swing.border.LineBorder {
        RoundedBorder(int radius) {
            super(new Color(200, 200, 200), 1, true);
            this.roundedCorners = true;
        }
    }

    // ======== LOGIN ========
    private void login() {
        String documento = txtDocumento.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (documento.isEmpty() || contrasena.isEmpty()) {
            mostrarMensaje("Error de autenticación", "Por favor complete todos los campos", true);
            return;
        }
        if ("000".equals(documento)) {
        Admin admin = new Admin();
        if (admin.autenticar(documento, contrasena)) {
            animarAdminSuccess();
            Timer timer = new Timer(1500, e -> {
                new AdminFrame().setVisible(true);
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
            return;
        } else {
            // ✅ Específico para cuando documento es "000" pero contraseña falla
            mostrarMensaje("Error de administrador", "Credenciales de administrador incorrectas", true);
            return;
        }
    }

        Admin admin = new Admin();
        if (admin.autenticar(documento, contrasena)) {
            animarAdminSuccess();
            Timer timer = new Timer(1500, e -> {
                new AdminFrame().setVisible(true);
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
            return;
        }

        String rol = UserDatabase.validarUsuarioPorDocumento(documento, contrasena);
        if (rol == null) {
            mostrarMensaje("Error de autenticación", "Documento o contraseña incorrectos", true);
            return;
        }
        if (rol.equals("NO_APROBADO")) {
            mostrarMensaje("Acceso denegado", "Tu cuenta está pendiente de aprobación por el administrador", true);
            return;
        }

        Usuario usuarioActual = UserDatabase.obtenerUsuarioPorDocumento(documento);
        if (usuarioActual == null) {
            mostrarMensaje("Error", "No se pudo cargar el usuario", true);
            return;
        }

        UserDatabase.setCurrentUser(usuarioActual);

        if (usuarioActual instanceof Productor) {
            new ProductorFrame((Productor) usuarioActual).setVisible(true);
        } else if (usuarioActual instanceof Propietario) {
            Propietario prop = (Propietario) usuarioActual;
            java.util.List<Predio> predios = app.db.PredioDatabase.obtenerPrediosPorPropietario(Integer.parseInt(prop.getId()));
            if (predios != null) predios.forEach(prop::agregarPredio);
            new PropietarioFrame(prop).setVisible(true);
        } else if (usuarioActual instanceof Tecnico) {
            new TecnicoFrame((Tecnico) usuarioActual).setVisible(true);
        } else {
            mostrarMensaje("Error", "Rol desconocido", true);
            return;
        }

        dispose();
    }

    private void abrirRegistro() {
        new RegisterFrame().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}