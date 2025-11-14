package app.ui;

import app.db.PredioDatabase;
import app.db.InspeccionDatabase;
import app.db.ColombiaAPI;
import app.db.ReportesVistas;
import app.model.Predio;
import app.model.Propietario;
import app.model.Inspeccion;
import app.db.UserDatabase;
import app.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class PropietarioFrame extends JFrame {
    private JTextField txtNumeroPredial, txtNombre, txtVereda, txtLatitud, txtLongitud;
    private JComboBox<String> cbDepartamento, cbMunicipio;
    private JTable tablaPredios, tablaInspecciones;
    private DefaultTableModel modeloTablaPredios, modeloTablaInspecciones;
    private Propietario propietario;

    public PropietarioFrame(Propietario propietario) {
        this.propietario = propietario;
        setTitle("Panel del Propietario - " + propietario.getNombre());

        setSize(1200, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        cargarPredios();
        cargarHistorialInspecciones();
    }

    // TEMPORAL PA QUE FUNCIONE
    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.white);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 35));
        btn.setOpaque(true);
        return btn;
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Barra superior
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(Color.WHITE);
        barraSuperior.setBorder(new EmptyBorder(25, 35, 20, 35));

        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Panel del Propietario");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblUsuario = new JLabel("Bienvenido, " + propietario.getNombre());
        lblUsuario.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblUsuario.setForeground(new Color(120, 120, 130));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblUsuario);

        JButton btnCerrarSesion = crearBotonSecundario("← Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });

        barraSuperior.add(panelTitulo, BorderLayout.WEST);
        barraSuperior.add(btnCerrarSesion, BorderLayout.EAST);

        // Pestañas
        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(new Font("Poppins", Font.BOLD, 13));
        pestañas.setBackground(Color.WHITE);
        pestañas.setBorder(new EmptyBorder(0, 25, 25, 25));

        pestañas.addTab("📋 Mis Predios", crearPanelPredios());
        pestañas.addTab("📊 Historial de Inspecciones", crearPanelHistorial());
        pestañas.addTab("👥 Asignar Productores", crearPanelAsignarProductores());
        pestañas.addTab("Reportes", crearPanelReportes());

        mainPanel.add(barraSuperior, BorderLayout.NORTH);
        mainPanel.add(pestañas, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel crearPanelPredios() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Formulario con scroll
        JPanel panelFormContainer = new JPanel(new BorderLayout());
        panelFormContainer.setBackground(Color.WHITE);
        panelFormContainer.setPreferredSize(new Dimension(400, 0));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(250, 250, 252));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblFormTitulo = new JLabel("Agregar Nuevo Predio");
        lblFormTitulo.setFont(new Font("Poppins", Font.BOLD, 16));
        lblFormTitulo.setForeground(new Color(25, 30, 40));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        panelForm.add(lblFormTitulo, gbc);

        gbc.insets = new Insets(8, 0, 8, 0);

        txtNumeroPredial = crearCampoTexto();
        txtNombre = crearCampoTexto();
        txtVereda = crearCampoTexto();
        txtLatitud = crearCampoTexto();
        txtLongitud = crearCampoTexto();

        cbDepartamento = new JComboBox<>();
        cbMunicipio = new JComboBox<>();
        estilizarCombo(cbDepartamento);
        estilizarCombo(cbMunicipio);

        // Llenar departamentos
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<String> departamentos;
            @Override
            protected Void doInBackground() {
                departamentos = ColombiaAPI.obtenerDepartamentos();
                return null;
            }
            @Override
            protected void done() {
                if (departamentos != null) {
                    cbDepartamento.addItem("Seleccione...");
                    for (String d : departamentos) cbDepartamento.addItem(d);
                }
            }
        };
        worker.execute();

        // Evento cambio de departamento
        cbDepartamento.addActionListener(e -> {
            String depto = (String) cbDepartamento.getSelectedItem();
            if (depto != null && !depto.equals("Seleccione...")) {
                cbMunicipio.removeAllItems();
                cbMunicipio.addItem("Cargando...");
                SwingWorker<Void, Void> workerM = new SwingWorker<>() {
                    List<String> municipios;
                    @Override
                    protected Void doInBackground() {
                        municipios = ColombiaAPI.obtenerMunicipiosPorDepartamento(depto);
                        return null;
                    }
                    @Override
                    protected void done() {
                        cbMunicipio.removeAllItems();
                        cbMunicipio.addItem("Seleccione...");
                        if (municipios != null) {
                            for (String m : municipios) cbMunicipio.addItem(m);
                        }
                    }
                };
                workerM.execute();
            }
        });

        gbc.gridy = 1;
        panelForm.add(crearLabel("Número Predial:"), gbc);
        gbc.gridy = 2;
        panelForm.add(txtNumeroPredial, gbc);

        gbc.gridy = 3;
        panelForm.add(crearLabel("Nombre del predio:"), gbc);
        gbc.gridy = 4;
        panelForm.add(txtNombre, gbc);

        gbc.gridy = 5;
        panelForm.add(crearLabel("Departamento:"), gbc);
        gbc.gridy = 6;
        panelForm.add(cbDepartamento, gbc);

        gbc.gridy = 7;
        panelForm.add(crearLabel("Municipio:"), gbc);
        gbc.gridy = 8;
        panelForm.add(cbMunicipio, gbc);

        gbc.gridy = 9;
        panelForm.add(crearLabel("Vereda:"), gbc);
        gbc.gridy = 10;
        panelForm.add(txtVereda, gbc);

        gbc.gridy = 11;
        panelForm.add(crearLabel("Latitud:"), gbc);
        gbc.gridy = 12;
        panelForm.add(txtLatitud, gbc);

        gbc.gridy = 13;
        panelForm.add(crearLabel("Longitud:"), gbc);
        gbc.gridy = 14;
        panelForm.add(txtLongitud, gbc);

        // ========== MINIMAPA ==========
        JPanel panelMapa = new JPanel(new BorderLayout(5, 5));
        panelMapa.setBackground(new Color(250, 250, 252));
        panelMapa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 255), 2),
            new EmptyBorder(12, 12, 12, 12)
        ));
        panelMapa.setPreferredSize(new Dimension(360, 350));

        JLabel lblMapaTitulo = new JLabel("🗺️ Vista Previa del Predio");
        lblMapaTitulo.setFont(new Font("Poppins", Font.BOLD, 14));
        lblMapaTitulo.setForeground(new Color(25, 30, 40));

        JEditorPane miniMapa = new JEditorPane();
        miniMapa.setContentType("text/html");
        miniMapa.setEditable(false);
        miniMapa.setBackground(new Color(240, 245, 250));
        
        miniMapa.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                // No hacer nada - deshabilita clics en enlaces
            }
        });

        miniMapa.setText("<html><body style='font-family: Segoe UI; padding: 40px 20px; text-align: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);'>"
            + "<div style='background: white; border-radius: 15px; padding: 30px; box-shadow: 0 4px 15px rgba(0,0,0,0.2);'>"
            + "<div style='font-size: 48px; margin-bottom: 15px;'>📍</div>"
            + "<h3 style='color: #1976D2; margin: 10px 0;'>Vista Previa del Predio</h3>"
            + "<p style='color: #666; font-size: 13px; margin: 10px 0;'>"
            + "Ingresa las coordenadas (Latitud y Longitud)<br>y presiona <b style='color: #1976D2;'>Ver en Mapa</b></p>"
            + "</div></body></html>");

        JPanel contenedorMapa = new JPanel(new BorderLayout());
        contenedorMapa.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        contenedorMapa.setPreferredSize(new Dimension(340, 250));
        contenedorMapa.add(miniMapa, BorderLayout.CENTER);

        JButton btnVerMapa = crearBotonPrimario("🗺️ Ver en Mapa", new Color(70, 130, 255));
        btnVerMapa.addActionListener(e -> {
            String latStr = txtLatitud.getText().trim();
            String lonStr = txtLongitud.getText().trim();
            String nombrePredio = txtNombre.getText().trim();

            if (latStr.isEmpty() || lonStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Primero ingresa las coordenadas (Latitud y Longitud)", 
                    "Datos incompletos", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);

                if (lat < -4.5 || lat > 13 || lon < -79 || lon > -66) {
                    JOptionPane.showMessageDialog(this,
                        "⚠️ Las coordenadas no corresponden a Colombia\n" +
                        "Latitud debe estar entre -4.5° y 13°\n" +
                        "Longitud debe estar entre -79° y -66°",
                        "Coordenadas fuera de rango",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nombreMarcador = nombrePredio.isEmpty() ? "Predio sin nombre" : nombrePredio;

                SwingUtilities.invokeLater(() -> {
                    new MapaFrameSwing(lat, lon, nombreMarcador);
                });

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Las coordenadas deben ser valores numéricos válidos.", 
                    "Error de formato", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        panelMapa.add(lblMapaTitulo, BorderLayout.NORTH);
        panelMapa.add(contenedorMapa, BorderLayout.CENTER);
        panelMapa.add(btnVerMapa, BorderLayout.SOUTH);

        gbc.gridy = 15;
        gbc.insets = new Insets(15, 0, 15, 0);
        panelForm.add(panelMapa, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnAgregar = crearBotonPrimario("+ Agregar", new Color(80, 200, 120));
        JButton btnEliminar = crearBotonPrimario("- Eliminar", new Color(255, 100, 100));

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);

        gbc.gridy = 16;
        gbc.insets = new Insets(20, 0, 20, 0);
        panelForm.add(panelBotones, gbc);

        // Scroll para formulario
        JScrollPane scrollFormulario = new JScrollPane(panelForm);
        scrollFormulario.setBorder(null);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);
        scrollFormulario.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFormulario.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        
        panelFormContainer.add(scrollFormulario, BorderLayout.CENTER);
        panel.add(panelFormContainer, BorderLayout.WEST);

        // Tabla
        String[] columnas = {"N° Predial", "Nombre", "Ubicación", "Coordenadas"};
        modeloTablaPredios = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPredios = new JTable(modeloTablaPredios);
        estilizarTabla(tablaPredios);

        tablaPredios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaPredios.getSelectedRow();
                    if (fila != -1 && propietario != null) {
                        // Crear copia segura para evitar ConcurrentModificationException
                        List<Predio> copiaPredios = new ArrayList<>(propietario.getPredios());
                        if (fila < copiaPredios.size()) {
                            Predio predioSeleccionado = copiaPredios.get(fila);
                            new EditarPredioFrame(predioSeleccionado, PropietarioFrame.this).setVisible(true);
                        }
                    }
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaPredios);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 240), 1));
        scrollTabla.getViewport().setBackground(Color.WHITE);

        JPanel panelTablaContainer = new JPanel(new BorderLayout());
        panelTablaContainer.setBackground(Color.WHITE);

        JLabel lblTablaTitulo = new JLabel("📋 Lista de Predios");
        lblTablaTitulo.setFont(new Font("Poppins", Font.BOLD, 15));
        lblTablaTitulo.setForeground(new Color(25, 30, 40));
        lblTablaTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));

        panelTablaContainer.add(lblTablaTitulo, BorderLayout.NORTH);
        panelTablaContainer.add(scrollTabla, BorderLayout.CENTER);

        panel.add(panelTablaContainer, BorderLayout.CENTER);

        btnAgregar.addActionListener(e -> agregarPredio());
        btnEliminar.addActionListener(e -> eliminarPredio());

        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("📊 Historial de Inspecciones");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(25, 30, 40));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));

        String[] columnas = {"Predio", "Fecha", "Técnico", "Cultivo", "Plaga", "Obs. Generales", "Obs. Específicas"};
        modeloTablaInspecciones = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInspecciones = new JTable(modeloTablaInspecciones);
        estilizarTabla(tablaInspecciones);

        JScrollPane scroll = new JScrollPane(tablaInspecciones);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 240), 1));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelAsignarProductores() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(250, 250, 252));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        panelForm.setPreferredSize(new Dimension(600, 350));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("👥 Asignar Productor a Predio");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(25, 30, 40));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        panelForm.add(lblTitulo, gbc);

        gbc.insets = new Insets(10, 0, 10, 0);

        JComboBox<String> cmbProductores = new JComboBox<>();
        JComboBox<String> cmbPredios = new JComboBox<>();
        estilizarCombo(cmbProductores);
        estilizarCombo(cmbPredios);

        // Método para cargar todos los datos
        Runnable cargarTodosLosDatos = () -> {
            cmbProductores.removeAllItems();
            cmbPredios.removeAllItems();
            cmbProductores.addItem("🔄 Cargando...");
            cmbPredios.addItem("🔄 Cargando...");
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    List<Usuario> productores = UserDatabase.obtenerUsuariosPorRol("Productor");
                    List<Predio> prediosActualizados = PredioDatabase.obtenerPrediosPorPropietario(
                        Integer.parseInt(propietario.getId())
                    );
                    
                    SwingUtilities.invokeLater(() -> {
                        cmbProductores.removeAllItems();
                        cmbProductores.addItem("-- Seleccione un productor --");
                        for (Usuario u : productores) {
                            cmbProductores.addItem(u.getId() + " - " + u.getNombre());
                        }
                        
                        cmbPredios.removeAllItems();
                        cmbPredios.addItem("-- Seleccione un predio --");
                        // Usar copia para evitar ConcurrentModificationException
                        List<Predio> copiaPredios = new ArrayList<>(prediosActualizados);
                        for (Predio p : copiaPredios) {
                            cmbPredios.addItem(p.getNumeroPredial() + " - " + p.getNombre());
                        }
                        
                        cargarPredios();
                    });
                    return null;
                }
            };
            worker.execute();
        };

        cargarTodosLosDatos.run();

        gbc.gridy = 1;
        panelForm.add(crearLabel("Seleccionar Productor:"), gbc);
        gbc.gridy = 2;
        panelForm.add(cmbProductores, gbc);

        gbc.gridy = 3;
        panelForm.add(crearLabel("Seleccionar Predio:"), gbc);
        gbc.gridy = 4;
        panelForm.add(cmbPredios, gbc);

        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnAsignar = crearBotonPrimario("✓ Asignar", new Color(80, 200, 120));
        JButton btnDesasignar = crearBotonPrimario("✗ Desasignar", new Color(255, 100, 100));
        JButton btnActualizar = crearBotonPrimario("🔄 Actualizar", new Color(70, 130, 255));

        panelBotones.add(btnAsignar);
        panelBotones.add(btnDesasignar);
        panelBotones.add(btnActualizar);

        gbc.gridy = 5;
        gbc.insets = new Insets(25, 0, 0, 0);
        panelForm.add(panelBotones, gbc);

        JPanel containerForm = new JPanel(new GridBagLayout());
        containerForm.setBackground(Color.WHITE);
        containerForm.add(panelForm);

        panel.add(containerForm, BorderLayout.CENTER);

        // Eventos
        btnAsignar.addActionListener(e -> {
            if (cmbPredios.getSelectedIndex() <= 0 || cmbProductores.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione productor y predio.", 
                    "Datos incompletos", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String predioSel = (String) cmbPredios.getSelectedItem();
            String productorSel = (String) cmbProductores.getSelectedItem();

            int idProductor = Integer.parseInt(productorSel.split(" - ")[0]);
            String numeroPredial = predioSel.split(" - ")[0];

            if (PredioDatabase.asignarProductorAPredio(idProductor, numeroPredial)) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Productor asignado correctamente al predio", 
                    "Asignación exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                cargarTodosLosDatos.run();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Error en la asignación. Verifique que el productor no esté ya asignado a este predio.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDesasignar.addActionListener(e -> {
            if (cmbPredios.getSelectedIndex() <= 0 || cmbProductores.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione productor y predio para desasignar.", 
                    "Datos incompletos", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String predioSel = (String) cmbPredios.getSelectedItem();
            String productorSel = (String) cmbProductores.getSelectedItem();

            int idProductor = Integer.parseInt(productorSel.split(" - ")[0]);
            String numeroPredial = predioSel.split(" - ")[0];

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de desasignar este productor del predio seleccionado?",
                "Confirmar desasignación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (PredioDatabase.desasignarProductorDePredio(idProductor, numeroPredial)) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Productor desasignado correctamente", 
                        "Desasignación exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarTodosLosDatos.run();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ No se pudo desasignar el productor. Verifique la asignación.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnActualizar.addActionListener(e -> {
            btnActualizar.setEnabled(false);
            btnActualizar.setText("🔄 Actualizando...");
            
            cargarTodosLosDatos.run();
            
            Timer timer = new Timer(2000, ev -> {
                btnActualizar.setEnabled(true);
                btnActualizar.setText("🔄 Actualizar");
            });
            timer.setRepeats(false);
            timer.start();
            
            JOptionPane.showMessageDialog(this, 
                "✅ Datos actualizados correctamente", 
                "Actualización", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    // Métodos de estilo
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Poppins", Font.PLAIN, 13));
        lbl.setForeground(new Color(70, 70, 80));
        return lbl;
    }

    private JTextField crearCampoTexto() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Poppins", Font.PLAIN, 13));
        txt.setBackground(Color.WHITE);
        txt.setForeground(new Color(30, 30, 30));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return txt;
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Poppins", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(30, 30, 30));
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Poppins", Font.PLAIN, 12));
        tabla.setRowHeight(45);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(240, 240, 245));
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(new Color(30, 30, 30));
        tabla.setSelectionBackground(new Color(245, 247, 250));
        tabla.setSelectionForeground(new Color(25, 30, 40));

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Poppins", Font.BOLD, 12));
        header.setBackground(new Color(250, 250, 252));
        header.setForeground(new Color(70, 70, 80));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(240, 240, 245)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton crearBotonPrimario(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorFondo);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(10, 20, 10, 20));

        boton.addMouseListener(new MouseAdapter() {
            Color original = colorFondo;
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(original.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(original);
            }
        });

        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 13));
        boton.setForeground(new Color(70, 130, 255));
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(9, 19, 9, 19)
        ));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(Color.WHITE);
            }
        });

        return boton;
    }

    // Métodos lógicos
    private void agregarPredio() {
        String numeroPredial = txtNumeroPredial.getText().trim();
        String nombre = txtNombre.getText().trim();
        String departamento = (String) cbDepartamento.getSelectedItem();
        String municipio = (String) cbMunicipio.getSelectedItem();
        String vereda = txtVereda.getText().trim();
        String latitudStr = txtLatitud.getText().trim();
        String longitudStr = txtLongitud.getText().trim();

        if (numeroPredial.isEmpty() || nombre.isEmpty() || 
            departamento == null || municipio == null ||
            departamento.equals("Seleccione...") || municipio.equals("Seleccione...") ||
            latitudStr.isEmpty() || longitudStr.isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Complete todos los campos obligatorios.",
                "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double latitud = Double.parseDouble(latitudStr);
            double longitud = Double.parseDouble(longitudStr);

            if (latitud < -4.5 || latitud > 13 || longitud < -79 || longitud > -66) {
                JOptionPane.showMessageDialog(this,
                    "Las coordenadas no son válidas para Colombia",
                    "Coordenadas inválidas", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idPropietario = Integer.parseInt(propietario.getId());
            Predio nuevo = new Predio(numeroPredial, nombre, departamento, municipio, vereda, latitud, longitud, idPropietario);

            if (PredioDatabase.agregarPredio(nuevo)) {
                propietario.agregarPredio(nuevo);
                cargarPredios();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, 
                    "Predio agregado correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar el predio",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Las coordenadas deben ser números válidos",
                "Error en coordenadas", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPredio() {
        int fila = tablaPredios.getSelectedRow();
        if (fila >= 0) {
            String numeroPredial = (String) modeloTablaPredios.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este predio?", "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (PredioDatabase.eliminarPredio(numeroPredial)) {
                    modeloTablaPredios.removeRow(fila);
                    JOptionPane.showMessageDialog(this, "Predio eliminado correctamente");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un predio en la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNumeroPredial.setText("");
        txtNombre.setText("");
        cbDepartamento.setSelectedIndex(0);
        cbMunicipio.removeAllItems();
        txtVereda.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
    }

    private void cargarPredios() {
        if (propietario == null) return;

        List<Predio> prediosDB = PredioDatabase.obtenerPrediosPorPropietario(
            Integer.parseInt(propietario.getId())
        );
        
        propietario.getPredios().clear();
        propietario.getPredios().addAll(prediosDB);

        modeloTablaPredios.setRowCount(0);
        // Usar copia para evitar ConcurrentModificationException
        List<Predio> copiaPredios = new ArrayList<>(prediosDB);
        for (Predio p : copiaPredios) {
            modeloTablaPredios.addRow(new Object[]{
                p.getNumeroPredial(),
                p.getNombre(),
                p.getUbicacionCompleta(),
                p.getCoordenadas()
            });
        }
        
        modeloTablaPredios.fireTableDataChanged();
        if (tablaPredios != null) {
            tablaPredios.repaint();
        }
    }

    private void cargarHistorialInspecciones() {
        if (propietario == null || propietario.getPredios() == null) return;
        
        modeloTablaInspecciones.setRowCount(0);
        
        // SOLUCIÓN: Crear copia de la lista para evitar ConcurrentModificationException
        List<Predio> copiaPredios = new ArrayList<>(propietario.getPredios());
        
        for (Predio p : copiaPredios) {
            List<Inspeccion> inspecciones = InspeccionDatabase.obtenerInspeccionesPorPredio(p.getNumeroPredial());
            for (Inspeccion i : inspecciones) {
                modeloTablaInspecciones.addRow(new Object[]{
                    i.getPredio(), i.getFecha(), i.getTecnicoId(), i.getCultivo(),
                    i.getPlaga(), i.getObservacionesGenerales(), i.getObservacionesEspecificas()
                });
            }
        }
    }

    public void recargarPredios() { 
        cargarPredios(); 
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 10, 10));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(0, 102, 204), 2, true),
            "Reportes Disponibles", 0, 0, new Font("Segoe UI", Font.BOLD, 13)));

        JTextArea areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaResultados.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollResultados = new JScrollPane(areaResultados);

        // 📊 Reporte 1: Historial de inspecciones por predio
        JButton btnHistorialInspecciones = crearBoton("Historial de Inspecciones", new Color(0, 102, 204));
        btnHistorialInspecciones.addActionListener(e -> {
            // Usar copia para evitar ConcurrentModificationException
            List<Predio> copiaPredios = new ArrayList<>(propietario.getPredios());
            if (copiaPredios.isEmpty()) {
                areaResultados.setText("⚠️ No tiene predios registrados");
                return;
            }

            String[] opcionesPredios = copiaPredios.stream() 
                .map(p -> p.getNumeroPredial() + " - " + p.getNombre())
                .toArray(String[]::new);

            String seleccion = (String) JOptionPane.showInputDialog(
                this, "Seleccione un predio:", "Historial de Inspecciones",
                JOptionPane.QUESTION_MESSAGE, null, opcionesPredios, opcionesPredios[0]);

            if (seleccion != null) {
                String numeroPredial = seleccion.split(" - ")[0];
                List<Map<String, Object>> historial = ReportesVistas.obtenerHistorialInspeccionesPredio(numeroPredial);
                
                if (historial.isEmpty()) {
                    areaResultados.setText("No hay inspecciones registradas para este predio");
                } else {
                    areaResultados.setText(ReportesVistas.formatearReporte(historial));
                }
            }
        });

    // 📊 Reporte 2: Plagas más frecuentes
    JButton btnPlagasFrecuentes = crearBoton("Plagas Más Frecuentes", new Color(204, 102, 0));
    btnPlagasFrecuentes.addActionListener(e -> {
        List<Map<String, Object>> plagas = ReportesVistas.obtenerPlagasMasFrecuentes(10);
        
        if (plagas.isEmpty()) {
            areaResultados.setText("No hay datos de plagas registradas");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════\n");
            sb.append("   TOP 10 PLAGAS MÁS FRECUENTES\n");
            sb.append("═══════════════════════════════════════\n\n");
            
            int pos = 1;
            for (Map<String, Object> plaga : plagas) {
                sb.append(String.format("%d. %s\n", pos++, plaga.get("plaga")));
                sb.append(String.format("   Apariciones: %d\n", plaga.get("total_apariciones")));
                sb.append(String.format("   Predios afectados: %d\n", plaga.get("predios_afectados")));
                sb.append(String.format("   Porcentaje: %.2f%%\n\n", plaga.get("porcentaje")));
            }
            
            areaResultados.setText(sb.toString());
        }
    });

    // 📊 Reporte 3: Productores asignados
    JButton btnProductoresAsignados = crearBoton("Mis Productores", new Color(0, 153, 76));
    btnProductoresAsignados.addActionListener(e -> {
        int idPropietario = Integer.parseInt(propietario.getId());
        List<Map<String, Object>> productores = ReportesVistas.obtenerProductoresPorPropietario(idPropietario);
        
        if (productores.isEmpty()) {
            areaResultados.setText("No tiene productores asignados a sus predios");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════\n");
            sb.append("   PRODUCTORES ASIGNADOS\n");
            sb.append("═══════════════════════════════════════\n\n");
            
            for (Map<String, Object> prod : productores) {
                sb.append(String.format("Productor: %s (ID: %d)\n", 
                    prod.get("nombre_productor"), prod.get("id_productor")));
                sb.append(String.format("Predio: %s\n", prod.get("predio")));
                sb.append(String.format("Total predios asignados: %d\n\n", 
                    prod.get("total_predios_asignados")));
            }
            
            areaResultados.setText(sb.toString());
        }
    });

    // 📊 Reporte 4: Cultivos por predio
    JButton btnCultivosPredio = crearBoton("Cultivos por Predio", new Color(76, 153, 0));
    btnCultivosPredio.addActionListener(e -> {
        if (propietario.getPredios().isEmpty()) {
            areaResultados.setText("No tiene predios registrados");
            return;
        }

        String[] opcionesPredios = propietario.getPredios().stream()
            .map(p -> p.getNumeroPredial() + " - " + p.getNombre())
            .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(
            this, "Seleccione un predio:", "Cultivos por Predio",
            JOptionPane.QUESTION_MESSAGE, null, opcionesPredios, opcionesPredios[0]);

        if (seleccion != null) {
            String numeroPredial = seleccion.split(" - ")[0];
            List<Map<String, Object>> cultivos = ReportesVistas.obtenerCultivosPorPredio(numeroPredial);
            
            if (cultivos.isEmpty()) {
                areaResultados.setText("No hay cultivos registrados en este predio");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("═══════════════════════════════════════\n");
                sb.append("   CULTIVOS DEL PREDIO\n");
                sb.append("═══════════════════════════════════════\n\n");
                
                for (Map<String, Object> cultivo : cultivos) {
                    sb.append(String.format("Especie: %s\n", cultivo.get("especie_cientifica")));
                    sb.append(String.format("Nombre común: %s\n", cultivo.get("nombre_comun")));
                    sb.append(String.format("Variedad: %s\n", cultivo.get("variedad")));
                    sb.append(String.format("Ciclo: %s\n\n", cultivo.get("ciclo")));
                }
                
                areaResultados.setText(sb.toString());
            }
        }
    });

    // 📊 Reporte 5: Resumen general
    JButton btnResumenGeneral = crearBoton("Resumen General", new Color(102, 0, 204));
    btnResumenGeneral.addActionListener(e -> {
        int idPropietario = Integer.parseInt(propietario.getId());
        
        int totalPredios = propietario.getPredios().size();
        int totalProductores = ReportesVistas.obtenerProductoresPorPropietario(idPropietario).size();
        
        // Contar total de cultivos e inspecciones
        int totalCultivos = 0;
        int totalInspecciones = 0;
        
        for (Predio p : propietario.getPredios()) {
            totalCultivos += ReportesVistas.obtenerCultivosPorPredio(p.getNumeroPredial()).size();
            totalInspecciones += ReportesVistas.obtenerHistorialInspeccionesPredio(p.getNumeroPredial()).size();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("   RESUMEN GENERAL DE ACTIVIDAD\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append(String.format("Propietario: %s\n\n", propietario.getNombre()));
        sb.append(String.format("Total de Predios: %d\n", totalPredios));
        sb.append(String.format("Total de Productores: %d\n", totalProductores));
        sb.append(String.format("Total de Cultivos: %d\n", totalCultivos));
        sb.append(String.format("Total de Inspecciones: %d\n\n", totalInspecciones));
        
        if (totalPredios > 0) {
            sb.append(String.format("Promedio cultivos/predio: %.1f\n", (double)totalCultivos/totalPredios));
            sb.append(String.format("Promedio inspecciones/predio: %.1f\n", (double)totalInspecciones/totalPredios));
        }
        
        areaResultados.setText(sb.toString());
    });

    // 📊 Botón para limpiar
    JButton btnLimpiar = crearBoton("Limpiar", new Color(128, 128, 128));
    btnLimpiar.addActionListener(e -> areaResultados.setText("Seleccione un reporte para visualizar"));

    // Agregar botones al panel
    panelBotones.add(btnHistorialInspecciones);
    panelBotones.add(btnPlagasFrecuentes);
    panelBotones.add(btnProductoresAsignados);
    panelBotones.add(btnCultivosPredio);
    panelBotones.add(btnResumenGeneral);
    panelBotones.add(btnLimpiar);

    panel.add(panelBotones, BorderLayout.NORTH);
    panel.add(scrollResultados, BorderLayout.CENTER);

    areaResultados.setText("Seleccione un reporte para visualizar");

    return panel;
}
}