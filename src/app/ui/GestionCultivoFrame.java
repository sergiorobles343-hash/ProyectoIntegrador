package app.ui;

import app.db.CultivoDatabase;
import app.db.PredioDatabase;
import app.model.Cultivo;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionCultivoFrame extends JFrame {

    private JTable tablaCultivos;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    public GestionCultivoFrame() {
        setTitle("Gestión de Cultivos - Sistema Fitosanitario");
        setSize(1400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
        cargarCultivos();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 180, 50));
        headerPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        JLabel lblTitulo = new JLabel("🌾 Gestión de Cultivos");
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
        btnBuscar.addActionListener(e -> buscarCultivo());

        JButton btnLimpiar = crearBotonAccion("↻ Limpiar", new Color(120, 120, 130));
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarCultivos();
        });

        panelBusqueda.add(new JLabel("Buscar por nombre:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        // Botones de acciones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(Color.WHITE);

        JButton btnAgregar = crearBotonAccion("➕ Agregar Cultivo", new Color(80, 200, 120));
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());

        JButton btnEditar = crearBotonAccion("✏️ Editar", new Color(255, 160, 100));
        btnEditar.addActionListener(e -> editarCultivoSeleccionado());

        JButton btnEliminar = crearBotonAccion("🗑️ Eliminar", new Color(220, 50, 80));
        btnEliminar.addActionListener(e -> eliminarCultivoSeleccionado());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        panelAcciones.add(panelBusqueda, BorderLayout.WEST);
        panelAcciones.add(panelBotones, BorderLayout.EAST);

        // Tabla
        String[] columnas = {"ID", "Especie Científica", "Nombre Común", "Variedad", "Ciclo", "ID Predio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCultivos = new JTable(modeloTabla);
        tablaCultivos.setFont(new Font("Poppins", Font.PLAIN, 13));
        tablaCultivos.setRowHeight(35);
        tablaCultivos.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
        tablaCultivos.getTableHeader().setBackground(new Color(250, 250, 252));
        tablaCultivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCultivos.setShowGrid(true);
        tablaCultivos.setGridColor(new Color(230, 230, 235));

        JScrollPane scrollPane = new JScrollPane(tablaCultivos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 35, 20, 35));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(panelAcciones, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
    }

    private void cargarCultivos() {
        modeloTabla.setRowCount(0);
        List<Cultivo> cultivos = CultivoDatabase.obtenerTodosCultivos();

        for (Cultivo c : cultivos) {
            modeloTabla.addRow(new Object[]{
                c.getIdCultivo(),
                c.getEspecieCientifica(),
                c.getNombreComun(),
                c.getVariedad(),
                c.getCiclo(),
                c.getIdPredio()
            });
        }
    }

    private void buscarCultivo() {
        String criterio = txtBuscar.getText().trim().toLowerCase();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un criterio de búsqueda");
            return;
        }

        modeloTabla.setRowCount(0);
        List<Cultivo> cultivos = CultivoDatabase.obtenerTodosCultivos();

        for (Cultivo c : cultivos) {
            if (c.getNombreComun().toLowerCase().contains(criterio) ||
                c.getEspecieCientifica().toLowerCase().contains(criterio)) {
                modeloTabla.addRow(new Object[]{
                    c.getIdCultivo(), c.getEspecieCientifica(), c.getNombreComun(),
                    c.getVariedad(), c.getCiclo(), c.getIdPredio()
                });
            }
        }

        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron cultivos con ese criterio");
        }
    }

    private void mostrarDialogoAgregar() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Cultivo", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JTextField txtEspecie = crearCampo();
        JTextField txtNombreComun = crearCampo();
        JTextField txtVariedad = crearCampo();
        JTextField txtCiclo = crearCampo();

        // ComboBox de predios
        JComboBox<String> comboPredio = new JComboBox<>();
        comboPredio.setFont(new Font("Poppins", Font.PLAIN, 14));
        List<String> predios = PredioDatabase.obtenerNombresPredios();
        for (String p : predios) {
            comboPredio.addItem(p);
        }

        agregarCampo(panel, gbc, "Especie Científica:", txtEspecie, 0);
        agregarCampo(panel, gbc, "Nombre Común:", txtNombreComun, 1);
        agregarCampo(panel, gbc, "Variedad:", txtVariedad, 2);
        agregarCampo(panel, gbc, "Ciclo:", txtCiclo, 3);
        
        gbc.gridy = 8;
        panel.add(crearLabel("Predio:"), gbc);
        gbc.gridy = 9;
        panel.add(comboPredio, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(Color.WHITE);

        JButton btnGuardar = crearBotonAccion("💾 Guardar", new Color(80, 200, 120));
        btnGuardar.addActionListener(e -> {
            String especie = txtEspecie.getText().trim();
            String nombreComun = txtNombreComun.getText().trim();
            String variedad = txtVariedad.getText().trim();
            String ciclo = txtCiclo.getText().trim();
            String predioNombre = (String) comboPredio.getSelectedItem();

            if (especie.isEmpty() || nombreComun.isEmpty() || predioNombre == null) {
                JOptionPane.showMessageDialog(dialog, "Los campos obligatorios no pueden estar vacíos");
                return;
            }

            int idPredio = PredioDatabase.obtenerIdPredioPorNombre(predioNombre);
            if (idPredio == -1) {
                JOptionPane.showMessageDialog(dialog, "❌ Error: Predio no encontrado");
                return;
            }

            Cultivo cultivo = new Cultivo();
            cultivo.setEspecieCientifica(especie);
            cultivo.setNombreComun(nombreComun);
            cultivo.setVariedad(variedad);
            cultivo.setCiclo(ciclo);
            cultivo.setIdPredio(String.valueOf(idPredio));

            if (CultivoDatabase.agregarCultivo(cultivo)) {
                JOptionPane.showMessageDialog(dialog, "✅ Cultivo agregado exitosamente");
                cargarCultivos();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Error al agregar el cultivo");
            }
        });

        JButton btnCancelar = crearBotonAccion("❌ Cancelar", new Color(220, 50, 80));
        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridy = 10;
        gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editarCultivoSeleccionado() {
        int fila = tablaCultivos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cultivo para editar");
            return;
        }

        int idCultivo = (int) modeloTabla.getValueAt(fila, 0);
        Cultivo cultivo = CultivoDatabase.obtenerCultivoPorId(idCultivo);

        if (cultivo == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el cultivo");
            return;
        }

        JDialog dialog = new JDialog(this, "Editar Cultivo", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JTextField txtEspecie = crearCampo();
        txtEspecie.setText(cultivo.getEspecieCientifica());

        JTextField txtNombreComun = crearCampo();
        txtNombreComun.setText(cultivo.getNombreComun());

        JTextField txtVariedad = crearCampo();
        txtVariedad.setText(cultivo.getVariedad());

        JTextField txtCiclo = crearCampo();
        txtCiclo.setText(cultivo.getCiclo());

        agregarCampo(panel, gbc, "Especie Científica:", txtEspecie, 0);
        agregarCampo(panel, gbc, "Nombre Común:", txtNombreComun, 1);
        agregarCampo(panel, gbc, "Variedad:", txtVariedad, 2);
        agregarCampo(panel, gbc, "Ciclo:", txtCiclo, 3);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(Color.WHITE);

        JButton btnGuardar = crearBotonAccion("💾 Guardar Cambios", new Color(80, 200, 120));
        btnGuardar.addActionListener(e -> {
            cultivo.setEspecieCientifica(txtEspecie.getText().trim());
            cultivo.setNombreComun(txtNombreComun.getText().trim());
            cultivo.setVariedad(txtVariedad.getText().trim());
            cultivo.setCiclo(txtCiclo.getText().trim());

            if (CultivoDatabase.actualizarCultivo(cultivo, idCultivo)) {
                JOptionPane.showMessageDialog(dialog, "✅ Cultivo actualizado");
                cargarCultivos();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Error al actualizar");
            }
        });

        JButton btnCancelar = crearBotonAccion("❌ Cancelar", new Color(220, 50, 80));
        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void eliminarCultivoSeleccionado() {
        int fila = tablaCultivos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cultivo para eliminar");
            return;
        }

        int idCultivo = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 2);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar el cultivo:\n" + nombre + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (CultivoDatabase.eliminarCultivo(idCultivo)) {
                JOptionPane.showMessageDialog(this, "✅ Cultivo eliminado correctamente");
                cargarCultivos();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al eliminar el cultivo");
            }
        }
    }

    // Métodos auxiliares
    private JButton crearBotonHeader(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(235, 160, 30));
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