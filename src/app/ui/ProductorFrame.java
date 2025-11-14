package app.ui;

import app.db.*;
import app.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProductorFrame extends JFrame {

    private final Productor productor;
    private JComboBox<String> cmbPredio, cmbCiclo;
    private JTextField txtEspecie, txtNombreComun, txtVariedad;
    private JTable tablaCultivos, tablaInspecciones;
    private DefaultTableModel modeloCultivos, modeloInspecciones;

    public ProductorFrame(Productor productor) {
        this.productor = productor;

        setTitle("Panel del Productor - " + productor.getNombre());
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
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

        JLabel lblTitulo = new JLabel("Panel del Productor");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblUsuario = new JLabel("Bienvenido, " + productor.getNombre());
        lblUsuario.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblUsuario.setForeground(new Color(120, 120, 130));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblUsuario);

        JButton btnCerrarSesion = crearBotonSecundario("← Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "¿Desea cerrar sesión?",
                    "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        barraSuperior.add(panelTitulo, BorderLayout.WEST);
        barraSuperior.add(btnCerrarSesion, BorderLayout.EAST);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Poppins", Font.BOLD, 13));
        tabs.setBackground(Color.WHITE);
        tabs.setBorder(new EmptyBorder(0, 25, 25, 25));

        tabs.addTab("🌱 Mis Cultivos", crearPanelCultivos());
        tabs.addTab("📊 Inspecciones", crearPanelInspecciones());

        mainPanel.add(barraSuperior, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel crearPanelCultivos() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Formulario
        JPanel panelFormContainer = new JPanel(new BorderLayout());
        panelFormContainer.setBackground(Color.WHITE);
        panelFormContainer.setPreferredSize(new Dimension(380, 0));

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

        JLabel lblFormTitulo = new JLabel("Registrar Cultivo");
        lblFormTitulo.setFont(new Font("Poppins", Font.BOLD, 16));
        lblFormTitulo.setForeground(new Color(25, 30, 40));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        panelForm.add(lblFormTitulo, gbc);

        gbc.insets = new Insets(8, 0, 8, 0);

        cmbPredio = new JComboBox<>();
        txtEspecie = crearCampoTexto();
        txtNombreComun = crearCampoTexto();
        txtVariedad = crearCampoTexto();
        cmbCiclo = new JComboBox<>(new String[]{"Corto", "Medio", "Largo"});
        
        estilizarCombo(cmbPredio);
        estilizarCombo(cmbCiclo);

        gbc.gridy = 1;
        panelForm.add(crearLabel("Predio:"), gbc);
        gbc.gridy = 2;
        panelForm.add(cmbPredio, gbc);

        gbc.gridy = 3;
        panelForm.add(crearLabel("Especie científica:"), gbc);
        gbc.gridy = 4;
        panelForm.add(txtEspecie, gbc);

        gbc.gridy = 5;
        panelForm.add(crearLabel("Nombre común:"), gbc);
        gbc.gridy = 6;
        panelForm.add(txtNombreComun, gbc);

        gbc.gridy = 7;
        panelForm.add(crearLabel("Variedad:"), gbc);
        gbc.gridy = 8;
        panelForm.add(txtVariedad, gbc);

        gbc.gridy = 9;
        panelForm.add(crearLabel("Ciclo:"), gbc);
        gbc.gridy = 10;
        panelForm.add(cmbCiclo, gbc);

        JButton btnRegistrar = crearBotonPrimario("+ Registrar Cultivo", new Color(80, 200, 120));
        btnRegistrar.addActionListener(e -> registrarCultivo());

        gbc.gridy = 11;
        gbc.insets = new Insets(20, 0, 0, 0);
        panelForm.add(btnRegistrar, gbc);

        panelFormContainer.add(panelForm, BorderLayout.NORTH);
        panel.add(panelFormContainer, BorderLayout.WEST);

        // Tabla
        modeloCultivos = new DefaultTableModel(
                new String[]{"Predio", "Especie", "Nombre Común", "Variedad", "Ciclo"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCultivos = new JTable(modeloCultivos);
        estilizarTabla(tablaCultivos);

        JScrollPane scrollTabla = new JScrollPane(tablaCultivos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 240), 1));
        scrollTabla.getViewport().setBackground(Color.WHITE);

        JPanel panelTablaContainer = new JPanel(new BorderLayout());
        panelTablaContainer.setBackground(Color.WHITE);

        JLabel lblTablaTitulo = new JLabel("🌱 Lista de Cultivos");
        lblTablaTitulo.setFont(new Font("Poppins", Font.BOLD, 15));
        lblTablaTitulo.setForeground(new Color(25, 30, 40));
        lblTablaTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));

        panelTablaContainer.add(lblTablaTitulo, BorderLayout.NORTH);
        panelTablaContainer.add(scrollTabla, BorderLayout.CENTER);

        panel.add(panelTablaContainer, BorderLayout.CENTER);

        // Cargar predios después de crear el modelo
        cargarPredios();

        return panel;
    }

    private JPanel crearPanelInspecciones() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitulo = new JLabel("📊 Historial de Inspecciones");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JButton btnActualizar = crearBotonPrimario("↻ Actualizar", new Color(90, 140, 255));
        btnActualizar.addActionListener(e -> cargarInspecciones());

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(btnActualizar, BorderLayout.EAST);

        modeloInspecciones = new DefaultTableModel(
                new String[]{"Predio", "Cultivo", "Fecha", "Técnico", "Plaga", "Obs. Generales", "Obs. Específicas"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInspecciones = new JTable(modeloInspecciones);
        estilizarTabla(tablaInspecciones);

        JScrollPane scroll = new JScrollPane(tablaInspecciones);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 240), 1));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        // Cargar inspecciones al inicio
        cargarInspecciones();

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

    // Métodos lógicos (sin cambios)
    private void cargarPredios() {
        cmbPredio.removeAllItems();
        List<Predio> predios = PredioDatabase.obtenerPrediosPorUsuario(Integer.parseInt(productor.getId()));
        for (Predio p : predios) {
            cmbPredio.addItem(p.getNumeroPredial() + " - " + p.getNombre());
        }
        if (!predios.isEmpty()) cargarCultivosDelPredio(predios.get(0).getIdPredio());

        cmbPredio.addActionListener(e -> {
            String sel = (String) cmbPredio.getSelectedItem();
            if (sel != null) {
                String num = sel.split(" - ")[0];
                Predio p = PredioDatabase.buscarPredioPorNumeroPredial(num);
                if (p != null) cargarCultivosDelPredio(p.getIdPredio());
            }
        });
    }

    private void cargarCultivosDelPredio(int idPredio) {
        if (modeloCultivos == null) return;
        modeloCultivos.setRowCount(0);
        List<Cultivo> cultivos = CultivoDatabase.obtenerCultivosPorPredio(idPredio);
        for (Cultivo c : cultivos) {
            modeloCultivos.addRow(new Object[]{c.getIdPredio(), c.getEspecieCientifica(),
                    c.getNombreComun(), c.getVariedad(), c.getCiclo()});
        }
    }

    private void cargarInspecciones() {
        modeloInspecciones.setRowCount(0);
        List<Predio> predios = PredioDatabase.obtenerPrediosPorUsuario(Integer.parseInt(productor.getId()));
        for (Predio p : predios) {
            List<Inspeccion> inspecciones = InspeccionDatabase.obtenerInspeccionesPorPredio(p.getNumeroPredial());
            for (Inspeccion i : inspecciones) {
                modeloInspecciones.addRow(new Object[]{
                        i.getPredio(),
                        i.getCultivo(),
                        i.getFecha(),
                        i.getTecnicoId(),
                        i.getPlaga(),
                        i.getObservacionesGenerales(),
                        i.getObservacionesEspecificas()
                });
            }
        }
    }

    private void registrarCultivo() {
        String selPredio = (String) cmbPredio.getSelectedItem();
        if (selPredio == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un predio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String numPredial = selPredio.split(" - ")[0];
        Predio predio = PredioDatabase.buscarPredioPorNumeroPredial(numPredial);
        if (predio == null) {
            JOptionPane.showMessageDialog(this, "Predio no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String especie = txtEspecie.getText().trim();
        String nombre = txtNombreComun.getText().trim();
        String variedad = txtVariedad.getText().trim();
        String ciclo = (String) cmbCiclo.getSelectedItem();

        if (especie.isEmpty() || nombre.isEmpty() || variedad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cultivo c = new Cultivo(especie, nombre, variedad, ciclo, String.valueOf(predio.getIdPredio()));
        if (CultivoDatabase.agregarCultivo(c)) {
            JOptionPane.showMessageDialog(this, "Cultivo registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarCultivosDelPredio(predio.getIdPredio());
            txtEspecie.setText("");
            txtNombreComun.setText("");
            txtVariedad.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el cultivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}