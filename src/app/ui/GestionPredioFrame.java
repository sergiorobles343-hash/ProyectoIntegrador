package app.ui;

import app.db.PredioDatabase;
import app.db.UserDatabase;
import app.model.Predio;
import app.model.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionPredioFrame extends JFrame {

    private JTable tablaPredios;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    public GestionPredioFrame() {
        setTitle("Gestión de Predios - Sistema Fitosanitario");
        setSize(1600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
        cargarPredios();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(120, 80, 200));
        headerPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        JLabel lblTitulo = new JLabel("🏞️ Gestión de Predios");
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
        btnBuscar.addActionListener(e -> buscarPredio());

        JButton btnLimpiar = crearBotonAccion("↻ Limpiar", new Color(120, 120, 130));
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarPredios();
        });

        panelBusqueda.add(new JLabel("Buscar por nombre o número predial:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        // Botones de acciones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(Color.WHITE);

        JButton btnAgregar = crearBotonAccion("➕ Agregar Predio", new Color(80, 200, 120));
        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());
        JButton btnEliminar = crearBotonAccion("🗑️ Eliminar", new Color(220, 50, 80));
        btnEliminar.addActionListener(e -> eliminarPredioSeleccionado());
        JButton btnVerMapa = crearBotonAccion("🗺️ Ver en Mapa", new Color(70, 130, 255));
        btnVerMapa.addActionListener(e -> mostrarPredioEnMapa());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVerMapa);


        panelAcciones.add(panelBusqueda, BorderLayout.WEST);
        panelAcciones.add(panelBotones, BorderLayout.EAST);

            
        // Tabla
        String[] columnas = {"ID", "Número Predial", "Nombre", "Departamento", "Municipio", 
                             "Vereda", "Latitud", "Longitud", "ID Propietario"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPredios = new JTable(modeloTabla);
        tablaPredios.setFont(new Font("Poppins", Font.PLAIN, 13));
        tablaPredios.setRowHeight(35);
        tablaPredios.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
        tablaPredios.getTableHeader().setBackground(new Color(250, 250, 252));
        tablaPredios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPredios.setShowGrid(true);
        tablaPredios.setGridColor(new Color(230, 230, 235));

        JScrollPane scrollPane = new JScrollPane(tablaPredios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 35, 20, 35));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(panelAcciones, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
    }

    private void cargarPredios() {
        modeloTabla.setRowCount(0);
        List<Predio> predios = PredioDatabase.obtenerPredios();

        for (Predio p : predios) {
            modeloTabla.addRow(new Object[]{
                p.getIdPredio(),
                p.getNumeroPredial(),
                p.getNombre(),
                p.getDepartamento(),
                p.getMunicipio(),
                p.getVereda(),
                p.getLatitud(),
                p.getLongitud(),
                p.getIdPropietario()
            });
        }
    }

    private void buscarPredio() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un criterio de búsqueda");
            return;
        }

        modeloTabla.setRowCount(0);
        List<Predio> predios = PredioDatabase.obtenerPredios();

        for (Predio p : predios) {
            if (p.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                p.getNumeroPredial().toLowerCase().contains(criterio.toLowerCase())) {
                modeloTabla.addRow(new Object[]{
                    p.getIdPredio(), p.getNumeroPredial(), p.getNombre(),
                    p.getDepartamento(), p.getMunicipio(), p.getVereda(),
                    p.getLatitud(), p.getLongitud(), p.getIdPropietario()
                });
            }
        }

        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron predios con ese criterio");
        }
    }
private void mostrarDialogoAgregar() {
    JDialog dialog = new JDialog(this, "Agregar Nuevo Predio", true);
    dialog.setSize(600, 700);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(new EmptyBorder(25, 30, 25, 30));
    panel.setBackground(Color.WHITE);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 5, 8, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;

    // ===== CAMPOS =====
    JTextField txtNumeroPredial = crearCampo();
    JTextField txtNombre = crearCampo();
    JTextField txtVereda = crearCampo();
    JTextField txtLatitud = crearCampo();
    JTextField txtLongitud = crearCampo();

    JComboBox<String> cbDepartamento = new JComboBox<>();
    JComboBox<String> cbMunicipio = new JComboBox<>();
    JComboBox<String> comboPropietario = new JComboBox<>();

    // Estilo de combos
    Font fuente = new Font("Poppins", Font.PLAIN, 13);
    for (JComboBox<String> cb : new JComboBox[]{cbDepartamento, cbMunicipio, comboPropietario}) {
        cb.setFont(fuente);
        cb.setPreferredSize(new Dimension(350, 35));
    }

    cbDepartamento.addItem("Cargando...");
    cbMunicipio.addItem("Seleccione un departamento primero");

    // ===== Cargar datos asincrónicamente =====
    SwingWorker<Void, Void> workerDepto = new SwingWorker<>() {
        List<String> departamentos;
        @Override
        protected Void doInBackground() {
            departamentos = app.db.ColombiaAPI.obtenerDepartamentos();
            return null;
        }
        @Override
        protected void done() {
            cbDepartamento.removeAllItems();
            cbDepartamento.addItem("Seleccione...");
            if (departamentos != null) for (String d : departamentos) cbDepartamento.addItem(d);
        }
    };
    workerDepto.execute();

    cbDepartamento.addActionListener(e -> {
        String depto = (String) cbDepartamento.getSelectedItem();
        if (depto != null && !depto.equals("Seleccione...")) {
            cbMunicipio.removeAllItems();
            cbMunicipio.addItem("Cargando municipios...");
            SwingWorker<Void, Void> workerM = new SwingWorker<>() {
                List<String> municipios;
                @Override
                protected Void doInBackground() {
                    municipios = app.db.ColombiaAPI.obtenerMunicipiosPorDepartamento(depto);
                    return null;
                }
                @Override
                protected void done() {
                    cbMunicipio.removeAllItems();
                    cbMunicipio.addItem("Seleccione...");
                    if (municipios != null) for (String m : municipios) cbMunicipio.addItem(m);
                }
            };
            workerM.execute();
        }
    });

    // ===== Propietarios =====
    List<Usuario> propietarios = UserDatabase.obtenerUsuariosPorRol("Propietario");
    for (Usuario u : propietarios) {
        comboPropietario.addItem(u.getId() + " - " + u.getNombreCompleto());
    }

    // ===== Diseño limpio =====
    gbc.gridy = 0; panel.add(crearLabel("Número Predial:"), gbc);
    gbc.gridy++; panel.add(txtNumeroPredial, gbc);

    gbc.gridy++; panel.add(crearLabel("Nombre:"), gbc);
    gbc.gridy++; panel.add(txtNombre, gbc);

    gbc.gridy++; panel.add(crearLabel("Departamento:"), gbc);
    gbc.gridy++; panel.add(cbDepartamento, gbc);

    gbc.gridy++; panel.add(crearLabel("Municipio:"), gbc);
    gbc.gridy++; panel.add(cbMunicipio, gbc);

    gbc.gridy++; panel.add(crearLabel("Vereda:"), gbc);
    gbc.gridy++; panel.add(txtVereda, gbc);

    gbc.gridy++; panel.add(crearLabel("Latitud:"), gbc);
    gbc.gridy++; panel.add(txtLatitud, gbc);

    gbc.gridy++; panel.add(crearLabel("Longitud:"), gbc);
    gbc.gridy++; panel.add(txtLongitud, gbc);

    gbc.gridy++; panel.add(crearLabel("Propietario:"), gbc);
    gbc.gridy++; panel.add(comboPropietario, gbc);

    // ===== BOTONES =====
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    panelBotones.setBackground(Color.WHITE);

    JButton btnGuardar = crearBotonAccion("💾 Guardar", new Color(80, 200, 120));
    JButton btnCancelar = crearBotonAccion("❌ Cancelar", new Color(220, 50, 80));

    panelBotones.add(btnGuardar);
    panelBotones.add(btnCancelar);

    gbc.gridy++;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(panelBotones, gbc);

    // ===== ACCIONES =====
    btnGuardar.addActionListener(e -> {
        try {
            String numPredial = txtNumeroPredial.getText().trim();
            String nombre = txtNombre.getText().trim();
            String depto = (String) cbDepartamento.getSelectedItem();
            String muni = (String) cbMunicipio.getSelectedItem();
            String vereda = txtVereda.getText().trim();
            double lat = Double.parseDouble(txtLatitud.getText().trim());
            double lon = Double.parseDouble(txtLongitud.getText().trim());
            String seleccion = (String) comboPropietario.getSelectedItem();
            int idProp = Integer.parseInt(seleccion.split(" - ")[0]);

            if (numPredial.isEmpty() || nombre.isEmpty() ||
                depto.equals("Seleccione...") || muni.equals("Seleccione...")) {
                JOptionPane.showMessageDialog(dialog, "⚠️ Todos los campos obligatorios deben completarse");
                return;
            }

            Predio predio = new Predio(0, numPredial, nombre, depto, muni, vereda, lat, lon, idProp);
            if (PredioDatabase.agregarPredio(predio)) {
                JOptionPane.showMessageDialog(dialog, "✅ Predio agregado exitosamente");
                cargarPredios();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Error al agregar el predio");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "⚠️ Las coordenadas deben ser números válidos");
        }
    });

    btnCancelar.addActionListener(e -> dialog.dispose());

    // ===== SCROLL =====
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(20);
    dialog.add(scrollPane);
    dialog.setVisible(true);
}






    private void eliminarPredioSeleccionado() {
        int fila = tablaPredios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un predio para eliminar");
            return;
        }

        String numeroPredial = (String) modeloTabla.getValueAt(fila, 1);
        String nombre = (String) modeloTabla.getValueAt(fila, 2);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar el predio:\n" + nombre + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (PredioDatabase.eliminarPredio(numeroPredial)) {
                JOptionPane.showMessageDialog(this, "✅ Predio eliminado correctamente");
                cargarPredios();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al eliminar el predio");
            }
        }
    }
    
    private void mostrarPredioEnMapa() {
    int fila = tablaPredios.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un predio para ver en el mapa");
        return;
    }

    // Obtener datos del predio seleccionado
    String nombrePredio = (String) modeloTabla.getValueAt(fila, 2);
    double lat = Double.parseDouble(modeloTabla.getValueAt(fila, 6).toString());
    double lon = Double.parseDouble(modeloTabla.getValueAt(fila, 7).toString());

    // Abrir mapa
    SwingUtilities.invokeLater(() -> new MapaFrameSwing(lat, lon, nombrePredio));
}


    // Métodos auxiliares
    private JButton crearBotonHeader(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(100, 60, 180));
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
        campo.setFont(new Font("Poppins", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(350, 35));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return campo;
    }

    private JTextField crearCampoSoloLectura() {
        JTextField campo = crearCampo();
        campo.setEditable(false);
        campo.setBackground(new Color(245, 245, 250));
        campo.setForeground(new Color(100, 100, 110));
        return campo;
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Poppins", Font.BOLD, 14));
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