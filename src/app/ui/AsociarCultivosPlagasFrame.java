package app.ui;

import app.model.Tecnico;
import app.db.CultivoDatabase;
import app.db.PlagaDatabase;
import app.db.DetalleInspeccionDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AsociarCultivosPlagasFrame extends JFrame {

    private Tecnico tecnico;
    private JComboBox<String> comboCultivo;
    private JComboBox<String> comboPlaga;
    private DefaultListModel<String> modeloAsociaciones;
    private JList<String> listaAsociaciones;

    public AsociarCultivosPlagasFrame(Tecnico tecnico) {
        this.tecnico = tecnico;

        setTitle("Asociar Cultivos y Plagas");
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        cargarAsociacionesExistentes();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(Color.WHITE);
        panelTitulo.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel lblTitulo = new JLabel("Asociar Cultivos y Plagas");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblSubtitulo = new JLabel("Gestiona las relaciones entre cultivos y plagas del sistema");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(120, 120, 130));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblSubtitulo);

        // Panel formulario
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

        // Cultivo
        gbc.gridy = 0;
        panelForm.add(crearLabel("Seleccionar Cultivo:"), gbc);

        comboCultivo = new JComboBox<>();
        comboCultivo.addItem("-- Seleccione un cultivo --");
        List<String> cultivos = CultivoDatabase.obtenerNombresCultivos();
        for (String cultivo : cultivos) {
            comboCultivo.addItem(cultivo);
        }
        estilizarCombo(comboCultivo);

        gbc.gridy = 1;
        panelForm.add(comboCultivo, gbc);

        // Plaga
        gbc.gridy = 2;
        panelForm.add(crearLabel("Seleccionar Plaga:"), gbc);

        comboPlaga = new JComboBox<>();
        comboPlaga.addItem("-- Seleccione una plaga --");
        List<String> plagas = PlagaDatabase.obtenerNombresPlagas();
        for (String plaga : plagas) {
            comboPlaga.addItem(plaga);
        }
        estilizarCombo(comboPlaga);

        gbc.gridy = 3;
        panelForm.add(comboPlaga, gbc);

        // Botón nueva plaga
        JButton btnNuevaPlaga = crearBotonSecundario("+ Nueva Plaga");
        btnNuevaPlaga.addActionListener(e -> agregarNuevaPlaga());

        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panelForm.add(btnNuevaPlaga, gbc);

        // Botón asociar
        JButton btnAsociar = crearBotonPrimario("✓ Asociar", new Color(90, 140, 255));
        btnAsociar.addActionListener(e -> asociarCultivoPlaga());

        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 0, 0);
        panelForm.add(btnAsociar, gbc);

        // Panel lista de asociaciones
        JPanel panelLista = new JPanel(new BorderLayout(0, 10));
        panelLista.setBackground(Color.WHITE);
        panelLista.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel lblListaTitulo = new JLabel("📋 Asociaciones Registradas");
        lblListaTitulo.setFont(new Font("Poppins", Font.BOLD, 15));
        lblListaTitulo.setForeground(new Color(25, 30, 40));

        modeloAsociaciones = new DefaultListModel<>();
        listaAsociaciones = new JList<>(modeloAsociaciones);
        listaAsociaciones.setFont(new Font("Poppins", Font.PLAIN, 13));
        listaAsociaciones.setBackground(Color.WHITE);
        listaAsociaciones.setForeground(new Color(30, 30, 30));
        listaAsociaciones.setSelectionBackground(new Color(245, 247, 250));
        listaAsociaciones.setSelectionForeground(new Color(25, 30, 40));
        listaAsociaciones.setBorder(new EmptyBorder(8, 12, 8, 12));

        JScrollPane scroll = new JScrollPane(listaAsociaciones);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 240), 1));
        scroll.setPreferredSize(new Dimension(0, 200));

        panelLista.add(lblListaTitulo, BorderLayout.NORTH);
        panelLista.add(scroll, BorderLayout.CENTER);

        mainPanel.add(panelTitulo, BorderLayout.NORTH);
        mainPanel.add(panelForm, BorderLayout.CENTER);
        mainPanel.add(panelLista, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void cargarAsociacionesExistentes() {
        List<String> asociaciones = DetalleInspeccionDatabase.obtenerAsociacionesCultivoPlaga();
        modeloAsociaciones.clear();
        for (String asoc : asociaciones) {
            modeloAsociaciones.addElement(asoc);
        }
    }

    private void agregarNuevaPlaga() {
        String nuevaPlaga = JOptionPane.showInputDialog(this,
                "Ingrese el nombre de la nueva plaga:",
                "Nueva Plaga",
                JOptionPane.QUESTION_MESSAGE);

        if (nuevaPlaga != null && !nuevaPlaga.trim().isEmpty()) {
            int idPlaga = PlagaDatabase.buscarOCrearPlaga(nuevaPlaga.trim());
            
            if (idPlaga != -1) {
                comboPlaga.addItem(nuevaPlaga.trim());
                comboPlaga.setSelectedItem(nuevaPlaga.trim());
                JOptionPane.showMessageDialog(this,
                        "✅ Plaga agregada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Error al agregar la plaga",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void asociarCultivoPlaga() {
        String cultivo = (String) comboCultivo.getSelectedItem();
        String plaga = (String) comboPlaga.getSelectedItem();

        if (cultivo == null || cultivo.equals("-- Seleccione un cultivo --")) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un cultivo",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (plaga == null || plaga.equals("-- Seleccione una plaga --")) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una plaga",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String asociacion = cultivo + " → " + plaga;

        // Verificar si ya existe
        for (int i = 0; i < modeloAsociaciones.size(); i++) {
            if (modeloAsociaciones.get(i).equals(asociacion)) {
                JOptionPane.showMessageDialog(this,
                        "Esta asociación ya existe",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        modeloAsociaciones.addElement(asociacion);
        JOptionPane.showMessageDialog(this,
                "✅ Asociación registrada:\n" + asociacion +
                "\n\nSe guardará en la base de datos al registrar una inspección.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Métodos de estilo
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Poppins", Font.PLAIN, 13));
        lbl.setForeground(new Color(70, 70, 80));
        return lbl;
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
        boton.setFont(new Font("Poppins", Font.BOLD, 12));
        boton.setForeground(new Color(70, 130, 255));
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(8, 16, 8, 16)
        ));
        boton.setPreferredSize(new Dimension(140, 36));

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