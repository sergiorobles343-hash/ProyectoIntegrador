package app.ui;

import app.model.Inspeccion;
import app.model.Tecnico;
import app.model.Predio;
import app.model.Cultivo;
import app.model.Plaga;
import app.db.InspeccionDatabase;
import app.db.PredioDatabase;
import app.db.CultivoDatabase;
import app.db.PlagaDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RegistrarInspeccionFrame extends JFrame {

    private Tecnico tecnico;
    private JComboBox<String> cmbPredio, cmbCultivo, cmbPlaga;
    private JTextField txtFecha;
    private JTextArea txtObs;
    private JSpinner spinnerPorcentaje; // ✅ NUEVO: Campo para porcentaje de infestación

    public RegistrarInspeccionFrame(Tecnico tecnico) {
        this.tecnico = tecnico;

        setTitle("Registrar Inspección Fitosanitaria");

        // ✅ Obtener tamaño de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // ✅ Calcular tamaño proporcional (85 % del ancho y alto)
        int width = (int) (screenSize.width * 0.85);
        int height = (int) (screenSize.height * 0.85);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ Inicializar la interfaz
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // ----- Título -----
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(Color.WHITE);
        panelTitulo.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel lblTitulo = new JLabel("Nueva Inspección");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblSubtitulo = new JLabel("Registra los datos de la inspección fitosanitaria");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(120, 120, 130));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblSubtitulo);

        // ----- Formulario -----
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(250, 250, 252));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // 🔹 PREDIO
        gbc.gridy = 0;
        panelForm.add(crearLabel("Predio:"), gbc);

        cmbPredio = new JComboBox<>();
        cmbPredio.addItem("-- Seleccione un predio --");
        List<Predio> predios = PredioDatabase.obtenerPredios();
        for (Predio p : predios) {
            cmbPredio.addItem(p.getNumeroPredial() + " - " + p.getNombre());
        }
        estilizarCombo(cmbPredio);
        gbc.gridy = 1;
        panelForm.add(cmbPredio, gbc);

        // 🔹 FECHA
        gbc.gridy = 2;
        panelForm.add(crearLabel("Fecha:"), gbc);
        txtFecha = crearCampoTexto();
        txtFecha.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtFecha.setEditable(false);
        txtFecha.setBackground(new Color(245, 245, 247));
        gbc.gridy = 3;
        panelForm.add(txtFecha, gbc);

        // 🔹 CULTIVO
        gbc.gridy = 4;
        panelForm.add(crearLabel("Cultivo:"), gbc);
        cmbCultivo = new JComboBox<>();
        cmbCultivo.addItem("-- Seleccione un cultivo --");
        estilizarCombo(cmbCultivo);
        gbc.gridy = 5;
        panelForm.add(cmbCultivo, gbc);

        // 🔹 PLAGA
        gbc.gridy = 6;
        panelForm.add(crearLabel("Plaga:"), gbc);
        cmbPlaga = new JComboBox<>();
        cmbPlaga.addItem("-- Seleccione una plaga --");
        List<Plaga> plagas = PlagaDatabase.obtenerPlagas();
        for (Plaga pl : plagas) {
            cmbPlaga.addItem(pl.getIdPlaga() + " - " + pl.getNombreComun());
        }
        estilizarCombo(cmbPlaga);
        gbc.gridy = 7;
        panelForm.add(cmbPlaga, gbc);

        // 🔹 PORCENTAJE DE INFESTACIÓN
        gbc.gridy = 8;
        JLabel lblPorcentaje = crearLabel("Porcentaje de Infestación (%):");
        lblPorcentaje.setForeground(new Color(200, 50, 50));
        panelForm.add(lblPorcentaje, gbc);

        JPanel panelPorcentaje = new JPanel(new BorderLayout(10, 0));
        panelPorcentaje.setOpaque(false);

        spinnerPorcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5));
        spinnerPorcentaje.setFont(new Font("Poppins", Font.BOLD, 14));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerPorcentaje, "0.0");
        spinnerPorcentaje.setEditor(editor);
        spinnerPorcentaje.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblNivel = new JLabel("Nivel: Bajo");
        lblNivel.setFont(new Font("Poppins", Font.BOLD, 12));
        lblNivel.setForeground(new Color(34, 139, 34));

        spinnerPorcentaje.addChangeListener(e -> {
            double valor = (Double) spinnerPorcentaje.getValue();
            if (valor < 10) {
                lblNivel.setText("🟢 Nivel: BAJO");
                lblNivel.setForeground(new Color(34, 139, 34));
            } else if (valor < 30) {
                lblNivel.setText("🟡 Nivel: MODERADO");
                lblNivel.setForeground(new Color(255, 165, 0));
            } else if (valor < 50) {
                lblNivel.setText("🟠 Nivel: ALTO");
                lblNivel.setForeground(new Color(255, 100, 0));
            } else {
                lblNivel.setText("🔴 Nivel: CRÍTICO");
                lblNivel.setForeground(new Color(220, 20, 60));
            }
        });

        panelPorcentaje.add(spinnerPorcentaje, BorderLayout.CENTER);
        panelPorcentaje.add(lblNivel, BorderLayout.EAST);
        gbc.gridy = 9;
        panelForm.add(panelPorcentaje, gbc);

        // 🔹 OBSERVACIONES
        gbc.gridy = 10;
        panelForm.add(crearLabel("Observaciones:"), gbc);
        txtObs = new JTextArea(5, 20);
        txtObs.setFont(new Font("Poppins", Font.PLAIN, 13));
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        txtObs.setBackground(Color.WHITE);
        txtObs.setForeground(new Color(30, 30, 30));
        txtObs.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JScrollPane scrollObs = new JScrollPane(txtObs);
        scrollObs.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1));
        gbc.gridy = 11;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelForm.add(scrollObs, gbc);

        // 🔹 BOTONES
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBotones.setOpaque(false);
        JButton btnGuardar = crearBotonPrimario("✓ Guardar", new Color(80, 200, 120));
        JButton btnCancelar = crearBotonSecundario("✗ Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        gbc.gridy = 12;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 0, 0);
        panelForm.add(panelBotones, gbc);

        // ✅ Ensamble final
        mainPanel.add(panelTitulo, BorderLayout.NORTH);
        mainPanel.add(panelForm, BorderLayout.CENTER);

        // ✅ Eventos
        configurarEventos(btnGuardar, btnCancelar);

        // ✅ UNA SOLA BARRA DE SCROLL que envuelve TODO el contenido
        JScrollPane scrollPrincipal = new JScrollPane(mainPanel);
        scrollPrincipal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPrincipal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getViewport().setBackground(Color.WHITE);

        // ✅ Establecer el scroll como contenido del frame
        setContentPane(scrollPrincipal);
    }

    private void configurarEventos(JButton btnGuardar, JButton btnCancelar) {
        // Actualizar Cultivos al elegir predio
        cmbPredio.addActionListener(e -> {
            cmbCultivo.removeAllItems();
            cmbCultivo.addItem("-- Seleccione un cultivo --");

            String elegido = (String) cmbPredio.getSelectedItem();
            if (elegido != null && !elegido.equals("-- Seleccione un predio --")) {
                String numPredial = elegido.split(" - ")[0];

                List<Cultivo> cultivos = CultivoDatabase.obtenerCultivosPorPredio(
                    PredioDatabase.obtenerIdPredioPorNumeroPredial(numPredial)
                );

                for (Cultivo c : cultivos) {
                    cmbCultivo.addItem(c.getIdCultivo() + " - " + c.getNombreComun());
                }
            }
        });

        btnGuardar.addActionListener(e -> guardarInspeccion());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarInspeccion() {
        try {
            if (tecnico == null) {
                JOptionPane.showMessageDialog(this, "No hay técnico logueado.");
                return;
            }

            String predioSel = (String) cmbPredio.getSelectedItem();
            String cultivoSel = (String) cmbCultivo.getSelectedItem();
            String plagaSel = (String) cmbPlaga.getSelectedItem();

            if (predioSel.contains("--")) { 
                JOptionPane.showMessageDialog(this, "Seleccione un predio."); 
                return; 
            }
            if (cultivoSel.contains("--")) { 
                JOptionPane.showMessageDialog(this, "Seleccione un cultivo."); 
                return; 
            }
            if (plagaSel.contains("--")) { 
                JOptionPane.showMessageDialog(this, "Seleccione una plaga."); 
                return; 
            }

            String observaciones = txtObs.getText();
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(txtFecha.getText());
            String numPredial = predioSel.split(" - ")[0];

            int idCultivo = Integer.parseInt(cultivoSel.split(" - ")[0]);
            int idPlaga = Integer.parseInt(plagaSel.split(" - ")[0]);
            double porcentajeInfestacion = (Double) spinnerPorcentaje.getValue(); // ✅ NUEVO
            
            Inspeccion nueva = new Inspeccion(
                    numPredial,
                    fecha,
                    tecnico.getId(),
                    String.valueOf(idCultivo),
                    String.valueOf(idPlaga),
                    observaciones,
                    porcentajeInfestacion // ✅ NUEVO: pasar el porcentaje
            );

            if (InspeccionDatabase.agregarInspeccion(nueva)) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Inspección registrada correctamente.\n" +
                    String.format("Porcentaje de infestación: %.1f%%", porcentajeInfestacion),
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Error al registrar inspección.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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

    private JButton crearBotonPrimario(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorFondo);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(10, 20, 10, 20));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = colorFondo;
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(original.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
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

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(Color.WHITE);
            }
        });

        return boton;
    }
}