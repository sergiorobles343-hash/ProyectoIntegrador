package app.ui;

import app.db.UserDatabase;
import app.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class GestionUsuarioFrame extends JFrame {

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private String rolActual;

    public GestionUsuarioFrame(String rol) {
        this.rolActual = rol;

        setTitle("Gestión de Usuarios - " + rol);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
        cargarUsuarios();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Panel superior con título
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Títulos
        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Gestión de " + rolActual + "s");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblSubtitulo = new JLabel("Administra todos los usuarios con este rol");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(120, 120, 130));

        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createVerticalStrut(5));
        panelTitulos.add(lblSubtitulo);

        // Botón actualizar
        JButton btnActualizar = crearBotonPrimario("↻ Actualizar lista");
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBackground(Color.WHITE);
        panelBoton.add(btnActualizar);

        panelSuperior.add(panelTitulos, BorderLayout.WEST);
        panelSuperior.add(panelBoton, BorderLayout.EAST);

        // Tabla
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Documento", "Correo", "Rol"}, 0
        ) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setFont(new Font("Poppins", Font.PLAIN, 13));
        tablaUsuarios.setRowHeight(50);
        tablaUsuarios.setShowVerticalLines(false);
        tablaUsuarios.setShowHorizontalLines(true);
        tablaUsuarios.setGridColor(new Color(240, 240, 245));
        tablaUsuarios.setBackground(Color.WHITE);
        tablaUsuarios.setForeground(new Color(30, 30, 30));
        tablaUsuarios.setSelectionBackground(new Color(245, 247, 250));
        tablaUsuarios.setSelectionForeground(new Color(25, 30, 40));
        tablaUsuarios.setIntercellSpacing(new Dimension(10, 0));

        // Header personalizado
        JTableHeader header = tablaUsuarios.getTableHeader();
        header.setFont(new Font("Poppins", Font.BOLD, 13));
        header.setBackground(new Color(250, 250, 252));
        header.setForeground(new Color(70, 70, 80));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 48));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(240, 240, 245)));

        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablaUsuarios.getColumnCount(); i++) {
            tablaUsuarios.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        // Nota informativa
        JPanel panelNota = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNota.setBackground(Color.WHITE);
        panelNota.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel lblNota = new JLabel("💡 Haz doble clic en una fila para ver los detalles del usuario");
        lblNota.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblNota.setForeground(new Color(120, 120, 130));
        panelNota.add(lblNota);

        // Eventos (funcionalidad original)
        btnActualizar.addActionListener(e -> cargarUsuarios());

        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaUsuarios.getSelectedRow() != -1) {
                    int fila = tablaUsuarios.getSelectedRow();
                    String idUsuario = tablaUsuarios.getValueAt(fila, 0).toString();

                    Usuario usuario = UserDatabase.obtenerUsuarioPorId(idUsuario);
                    if (usuario != null) {
                        new DetalleUsuarioDialog(GestionUsuarioFrame.this, usuario).setVisible(true);
                        cargarUsuarios();
                    } else {
                        JOptionPane.showMessageDialog(GestionUsuarioFrame.this,
                                "No se pudo cargar la información del usuario.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        mainPanel.add(panelSuperior, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(panelNota, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton crearBotonPrimario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(90, 140, 255));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(10, 20, 10, 20));
        boton.setPreferredSize(new Dimension(160, 38));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(70, 120, 235));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(90, 140, 255));
            }
        });

        return boton;
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        List<Usuario> usuarios = UserDatabase.obtenerUsuariosPorRol(rolActual);
        for (Usuario u : usuarios) {
            modeloTabla.addRow(new Object[]{
                    u.getId(),
                    u.getNombreCompleto(),
                    u.getDocumento(),
                    u.getCorreo(),
                    u.getRol()
            });
        }
    }
}