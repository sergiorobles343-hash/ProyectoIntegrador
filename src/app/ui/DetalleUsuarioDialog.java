package app.ui;

import app.model.*;
import app.db.UserDatabase;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DetalleUsuarioDialog extends JDialog {

    private Usuario usuario;
    private JTextField txtNombre, txtDocumento, txtCorreo, txtEstado;
    private JLabel lblId;
    private JButton btnPermitir, btnEliminar, btnBloquear, btnDesbloquear;

    public DetalleUsuarioDialog(Frame owner, Usuario usuario) {
        super(owner, "Detalle de usuario - " + usuario.getNombreCompleto(), true);

        Usuario usuarioRefrescado = UserDatabase.obtenerUsuarioPorId(usuario.getId());
        this.usuario = (usuarioRefrescado != null) ? usuarioRefrescado : usuario;

        setSize(650, 520);
        setLocationRelativeTo(owner);
        setResizable(false);

        initUI();
        cargarDatosEnFormulario();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Panel superior - Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(Color.WHITE);
        panelTitulo.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel lblTitulo = new JLabel("Detalle del Usuario");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblSubtitulo = new JLabel("Gestiona el estado y permisos del usuario");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(120, 120, 130));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblSubtitulo);

        // Panel central - Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panelForm.add(crearLabel("ID:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        lblId = crearLabelCampo();
        panelForm.add(lblId, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panelForm.add(crearLabel("Nombre completo:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtNombre = crearCampoTexto();
        panelForm.add(txtNombre, gbc);

        // Documento
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panelForm.add(crearLabel("Documento:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDocumento = crearCampoTexto();
        panelForm.add(txtDocumento, gbc);

        // Correo
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        panelForm.add(crearLabel("Correo:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtCorreo = crearCampoTexto();
        panelForm.add(txtCorreo, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panelForm.add(crearLabel("Estado:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtEstado = crearCampoTexto();
        panelForm.add(txtEstado, gbc);

        // Panel inferior - Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnPermitir = crearBotonAccion("✓ Permitir", new Color(80, 200, 120));
        btnEliminar = crearBotonAccion("🗑️ Eliminar", new Color(220, 80, 80)); // Color rojo más intenso para eliminar
        btnBloquear = crearBotonAccion("⊘ Bloquear", new Color(255, 160, 100));
        btnDesbloquear = crearBotonAccion("⊙ Desbloquear", new Color(90, 140, 255));
        JButton btnCerrar = crearBotonSecundario("Cerrar");

        panelBotones.add(btnPermitir);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBloquear);
        panelBotones.add(btnDesbloquear);
        panelBotones.add(btnCerrar);

        // Agregar todo al panel principal
        mainPanel.add(panelTitulo, BorderLayout.NORTH);
        mainPanel.add(panelForm, BorderLayout.CENTER);
        mainPanel.add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnCerrar.addActionListener(e -> dispose());
        btnPermitir.addActionListener(e -> permitirUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnBloquear.addActionListener(e -> bloquearUsuario());
        btnDesbloquear.addActionListener(e -> desbloquearUsuario());

        setContentPane(mainPanel);
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(new Color(70, 70, 80));
        lbl.setFont(new Font("Poppins", Font.PLAIN, 14));
        return lbl;
    }

    private JLabel crearLabelCampo() {
        JLabel lbl = new JLabel();
        lbl.setForeground(new Color(30, 30, 30));
        lbl.setFont(new Font("Poppins", Font.BOLD, 14));
        return lbl;
    }

    private JTextField crearCampoTexto() {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        txt.setBackground(new Color(250, 250, 252));
        txt.setForeground(new Color(30, 30, 30));
        txt.setFont(new Font("Poppins", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return txt;
    }

    private JButton crearBotonAccion(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorFondo);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(9, 16, 9, 16));
        boton.setPreferredSize(new Dimension(120, 36)); // Un poco más ancho para el nuevo texto

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = colorFondo;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(originalColor);
            }
        });

        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 12));
        boton.setForeground(new Color(70, 130, 255));
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        boton.setPreferredSize(new Dimension(90, 36));

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

    private void cargarDatosEnFormulario() {
        lblId.setText(usuario.getId());
        txtNombre.setText(usuario.getNombreCompleto());
        txtDocumento.setText(usuario.getDocumento());
        txtCorreo.setText(usuario.getCorreo());
        actualizarEstadoVisual();
    }

    private void permitirUsuario() {
        usuario.setAprobado(true);
        actualizarUsuarioBD();
        actualizarEstadoVisual();
        JOptionPane.showMessageDialog(this, "Usuario aprobado correctamente.");
    }

    private void eliminarUsuario() {
        // Confirmación antes de eliminar
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de ELIMINAR permanentemente al usuario?\n\n" +
            "Usuario: " + usuario.getNombreCompleto() + "\n" +
            "Documento: " + usuario.getDocumento() + "\n\n" +
            "⚠️ Esta acción NO se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean eliminado = UserDatabase.eliminarUsuario(usuario.getId());
            if (eliminado) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Usuario eliminado correctamente.", 
                    "Eliminación Exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cerrar el diálogo después de eliminar
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Error al eliminar el usuario.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void bloquearUsuario() {
        usuario.setAprobado(false);
        actualizarUsuarioBD();
        actualizarEstadoVisual();
        JOptionPane.showMessageDialog(this, "Usuario bloqueado.");
    }

    private void desbloquearUsuario() {
        usuario.setAprobado(true);
        actualizarUsuarioBD();
        actualizarEstadoVisual();
        JOptionPane.showMessageDialog(this, "Usuario desbloqueado.");
    }

    private void actualizarEstadoVisual() {
        if (usuario.isAprobado()) {
            txtEstado.setText("Aprobado ✓");
            txtEstado.setForeground(new Color(80, 200, 120));
        } else {
            txtEstado.setText("Pendiente / Bloqueado ✗");
            txtEstado.setForeground(new Color(255, 100, 100));
        }
    }

    private void actualizarUsuarioBD() {
        boolean ok = UserDatabase.actualizarAprobado(usuario);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Error al actualizar usuario en BD.");
        }
    }
}