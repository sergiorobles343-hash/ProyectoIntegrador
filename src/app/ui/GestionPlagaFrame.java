package app.ui;

import app.db.PlagaDatabase;
import app.model.Plaga;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionPlagaFrame extends JFrame {

    private JTable tablaPlagas;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    public GestionPlagaFrame() {
        setTitle("Gestión de Plagas - Sistema Fitosanitario");
        setSize(1400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
        cargarPlagas();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(220, 50, 80));
        headerPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        JLabel lblTitulo = new JLabel("🐛 Gestión de Plagas");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);

        JButton btnVolver = crearBotonHeader("← Volver");
        btnVolver.addActionListener(e -> dispose());

        headerPanel.add(lblTitulo, BorderLayout.WEST);
        headerPanel.add(btnVolver, BorderLayout.EAST);

        // Panel de búsqueda y acciones
        JPanel panelAcciones = new JPanel(new BorderLayout(15, 0));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(new EmptyBorder(20, 35, 20, 35));

        // Búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusqueda.setBackground(Color.WHITE);

        txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("Poppins", Font.PLAIN, 14));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JButton btnBuscar = crearBotonAccion("🔍 Buscar", new Color(70, 130, 255));
        btnBuscar.addActionListener(e -> buscarPlaga());

        JButton btnLimpiar = crearBotonAccion("↻ Limpiar", new Color(120, 120, 130));
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarPlagas();
        });

        panelBusqueda.add(new JLabel("Buscar por nombre:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        // Botones de acciones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(Color.WHITE);

        JButton btnAgregar = crearBotonAccion("➕ Agregar Plaga", new Color(80, 200, 120));
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());

        JButton btnEditar = crearBotonAccion("✏️ Editar", new Color(255, 160, 100));
        btnEditar.addActionListener(e -> editarPlagaSeleccionada());

        JButton btnEliminar = crearBotonAccion("🗑️ Eliminar", new Color(220, 50, 80));
        btnEliminar.addActionListener(e -> eliminarPlagaSeleccionada());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        panelAcciones.add(panelBusqueda, BorderLayout.WEST);
        panelAcciones.add(panelBotones, BorderLayout.EAST);

        // Tabla
        String[] columnas = {"ID", "Nombre Científico", "Nombre Común"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPlagas = new JTable(modeloTabla);
        tablaPlagas.setFont(new Font("Poppins", Font.PLAIN, 13));
        tablaPlagas.setRowHeight(35);
        tablaPlagas.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
        tablaPlagas.getTableHeader().setBackground(new Color(250, 250, 252));
        tablaPlagas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPlagas.setShowGrid(true);
        tablaPlagas.setGridColor(new Color(230, 230, 235));

        // Ajustar anchos de columnas
        tablaPlagas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaPlagas.getColumnModel().getColumn(1).setPreferredWidth(350);
        tablaPlagas.getColumnModel().getColumn(2).setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(tablaPlagas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 35, 20, 35));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(panelAcciones, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
    }

    private void cargarPlagas() {
        modeloTabla.setRowCount(0);
        List<Plaga> plagas = PlagaDatabase.obtenerPlagas();

        for (Plaga p : plagas) {
            modeloTabla.addRow(new Object[]{
                p.getIdPlaga(),
                p.getNombreCientifico(),
                p.getNombreComun()
            });
        }
    }

    private void buscarPlaga() {
        String criterio = txtBuscar.getText().trim().toLowerCase();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un criterio de búsqueda");
            return;
        }

        modeloTabla.setRowCount(0);
        List<Plaga> plagas = PlagaDatabase.obtenerPlagas();

        for (Plaga p : plagas) {
            String nombreCientifico = p.getNombreCientifico() != null ? p.getNombreCientifico() : "";
            String nombreComun = p.getNombreComun() != null ? p.getNombreComun() : "";
            
            if (nombreCientifico.toLowerCase().contains(criterio) ||
                nombreComun.toLowerCase().contains(criterio)) {
                modeloTabla.addRow(new Object[]{
                    p.getIdPlaga(), p.getNombreCientifico(), p.getNombreComun()
                });
            }
        }

        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron plagas con ese criterio");
        }
    }

    private void mostrarDialogoAgregar() {
        JDialog dialog = new JDialog(this, "Agregar Nueva Plaga", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JTextField txtNombreCientifico = crearCampo();
        JTextField txtNombreComun = crearCampo();

        agregarCampo(panel, gbc, "Nombre Científico:", txtNombreCientifico, 0);
        agregarCampo(panel, gbc, "Nombre Común:", txtNombreComun, 1);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(Color.WHITE);

        JButton btnGuardar = crearBotonAccion("💾 Guardar", new Color(80, 200, 120));
        btnGuardar.addActionListener(e -> {
            String nombreCientifico = txtNombreCientifico.getText().trim();
            String nombreComun = txtNombreComun.getText().trim();

            if (nombreCientifico.isEmpty() || nombreComun.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios");
                return;
            }

            // Verificar si ya existe
            if (PlagaDatabase.existePlagaPorNombre(nombreComun)) {
                JOptionPane.showMessageDialog(dialog, "⚠️ Ya existe una plaga con ese nombre");
                return;
            }

            Plaga plaga = new Plaga();
            plaga.setNombreCientifico(nombreCientifico);
            plaga.setNombreComun(nombreComun);

            if (PlagaDatabase.agregarPlaga(plaga)) {
                JOptionPane.showMessageDialog(dialog, "✅ Plaga agregada exitosamente");
                cargarPlagas();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Error al agregar la plaga");
            }
        });

        JButton btnCancelar = crearBotonAccion("❌ Cancelar", new Color(220, 50, 80));
        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editarPlagaSeleccionada() {
        int fila = tablaPlagas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una plaga para editar");
            return;
        }

        int idPlaga = (int) modeloTabla.getValueAt(fila, 0);
        Plaga plaga = PlagaDatabase.obtenerPlagaPorId(idPlaga);

        if (plaga == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la plaga");
            return;
        }

        JDialog dialog = new JDialog(this, "Editar Plaga", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JTextField txtNombreCientifico = crearCampo();
        txtNombreCientifico.setText(plaga.getNombreCientifico());

        JTextField txtNombreComun = crearCampo();
        txtNombreComun.setText(plaga.getNombreComun());

        agregarCampo(panel, gbc, "Nombre Científico:", txtNombreCientifico, 0);
        agregarCampo(panel, gbc, "Nombre Común:", txtNombreComun, 1);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(Color.WHITE);

        JButton btnGuardar = crearBotonAccion("💾 Guardar Cambios", new Color(80, 200, 120));
        btnGuardar.addActionListener(e -> {
            plaga.setNombreCientifico(txtNombreCientifico.getText().trim());
            plaga.setNombreComun(txtNombreComun.getText().trim());

            if (PlagaDatabase.actualizarPlaga(plaga, idPlaga)) {
                JOptionPane.showMessageDialog(dialog, "✅ Plaga actualizada");
                cargarPlagas();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Error al actualizar");
            }
        });

        JButton btnCancelar = crearBotonAccion("❌ Cancelar", new Color(220, 50, 80));
        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void eliminarPlagaSeleccionada() {
        int fila = tablaPlagas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una plaga para eliminar");
            return;
        }

        int idPlaga = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 2);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar la plaga:\n" + nombre + "?\n\n" +
                "⚠️ Esto puede afectar registros de inspecciones relacionadas.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (PlagaDatabase.eliminarPlaga(idPlaga)) {
                JOptionPane.showMessageDialog(this, "✅ Plaga eliminada correctamente");
                cargarPlagas();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ No se pudo eliminar la plaga.\n" +
                    "Puede estar asociada a inspecciones existentes.");
            }
        }
    }

    // Métodos auxiliares
    private JButton crearBotonHeader(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(200, 40, 70));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 38));
        return btn;
    }

    private JButton crearBotonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        return btn;
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Poppins", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return campo;
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Poppins", Font.BOLD, 13));
        label.setForeground(new Color(60, 60, 70));
        return label;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, String label, JTextField campo, int fila) {
        gbc.gridy = fila * 2;
        panel.add(crearLabel(label), gbc);
        gbc.gridy = fila * 2 + 1;
        panel.add(campo, gbc);
    }
}