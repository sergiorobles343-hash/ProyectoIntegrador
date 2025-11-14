package app.ui;

import app.db.Conexion;
import app.model.Tecnico;
import app.db.InspeccionDatabase;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConsultarInspeccionesFrame extends JFrame {

    private JTable tablaInspecciones;
    private DefaultTableModel modeloTabla;
    private Tecnico tecnico;

    public ConsultarInspeccionesFrame(Tecnico tecnico) {
        this.tecnico = tecnico;

        setTitle("Consultar Inspecciones");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        cargarInspecciones(tecnico.getId());
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Mis Inspecciones");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblSubtitulo = new JLabel("Historial completo de inspecciones registradas");
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(120, 120, 130));

        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createVerticalStrut(5));
        panelTitulos.add(lblSubtitulo);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(Color.WHITE);

        JButton btnActualizar = crearBotonPrimario("↻ Actualizar", new Color(90, 140, 255));
        JButton btnEliminar = crearBotonPrimario("🗑 Eliminar", new Color(255, 100, 100));

        btnActualizar.addActionListener(e -> actualizarInspeccionSeleccionada());
        btnEliminar.addActionListener(e -> eliminarInspeccionSeleccionada());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);

        panelSuperior.add(panelTitulos, BorderLayout.WEST);
        panelSuperior.add(panelBotones, BorderLayout.EAST);

        // Tabla
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Predio", "Fecha", "Cultivo", "Plaga", "Obs. Generales", "Obs. Específicas"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaInspecciones = new JTable(modeloTabla);
        estilizarTabla(tablaInspecciones);

        JScrollPane scrollPane = new JScrollPane(tablaInspecciones);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Nota informativa
        JPanel panelNota = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNota.setBackground(Color.WHITE);
        panelNota.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel lblNota = new JLabel("💡 Haz doble clic en una fila para ver los detalles completos");
        lblNota.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblNota.setForeground(new Color(120, 120, 130));
        panelNota.add(lblNota);

        mainPanel.add(panelSuperior, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(panelNota, BorderLayout.SOUTH);

        add(mainPanel);

        // Evento doble clic
        tablaInspecciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int fila = tablaInspecciones.getSelectedRow();
                    if (fila != -1) {
                        String predio = safeValue(fila, 1);
                        String fecha = safeValue(fila, 2);
                        String cultivo = safeValue(fila, 3);
                        String plaga = safeValue(fila, 4);
                        String obsGenerales = safeValue(fila, 5);
                        String obsEspecificas = safeValue(fila, 6);

                        DetalleInspeccionFrame detalle = new DetalleInspeccionFrame(
                                predio, fecha, cultivo, plaga, obsGenerales, obsEspecificas
                        );
                        detalle.setAlwaysOnTop(true);
                        detalle.setVisible(true);
                    }
                }
            }
        });
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
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

    private String safeValue(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor != null ? valor.toString() : "";
    }

    private void cargarInspecciones(String tecnicoId) {
    modeloTabla.setRowCount(0);

    String sql = "{ ? = call FN_OBTENER_INSPEC_TECNICO(?) }";

    try (Connection conn = Conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {

        cs.setInt(2, Integer.parseInt(tecnicoId));
        cs.registerOutParameter(1, Types.REF_CURSOR);
        cs.execute();

        ResultSet rs = (ResultSet) cs.getObject(1);

        Map<Integer, Object[]> mapaInspecciones = new LinkedHashMap<>();

        while (rs.next()) {
            int id = rs.getInt("ID_INSPECCION");

            if (mapaInspecciones.containsKey(id)) {
                Object[] existente = mapaInspecciones.get(id);
                String obsEspecificas = (String) existente[6];
                String nuevaObs = rs.getString("OBSERVACIONES_ESPECIFICAS");

                if (nuevaObs != null && !nuevaObs.trim().isEmpty()) {
                    existente[6] = obsEspecificas + "\n- " + nuevaObs;
                }

                mapaInspecciones.put(id, existente);
            } else {
                mapaInspecciones.put(id, new Object[]{
                        id,
                        rs.getString("PREDIO"),
                        rs.getDate("FECHA"),
                        rs.getString("CULTIVO"),
                        rs.getString("PLAGA"),
                        rs.getString("OBSERVACIONES_GENERALES"),
                        rs.getString("OBSERVACIONES_ESPECIFICAS") != null ?
                                "- " + rs.getString("OBSERVACIONES_ESPECIFICAS") : ""
                });
            }
        }

        for (Object[] fila : mapaInspecciones.values()) {
            modeloTabla.addRow(fila);
        }

        rs.close();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar inspecciones: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    private void eliminarInspeccionSeleccionada() {
        int fila = tablaInspecciones.getSelectedRow();
        
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una inspección para eliminar",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idInspeccion = (Integer) modeloTabla.getValueAt(fila, 0);
        String predio = safeValue(fila, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la inspección del predio '" + predio + "'?\n" +
                "Esto también eliminará los detalles asociados.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminado = InspeccionDatabase.eliminarInspeccion(idInspeccion);

            if (eliminado) {
                modeloTabla.removeRow(fila);
                JOptionPane.showMessageDialog(this,
                        "✅ Inspección eliminada correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ No se pudo eliminar la inspección",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarInspeccionSeleccionada() {
        int fila = tablaInspecciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una inspección para actualizar.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String predio = safeValue(fila, 1);
        String fecha = safeValue(fila, 2);
        String cultivo = safeValue(fila, 3);
        String plaga = safeValue(fila, 4);
        String obsG = safeValue(fila, 5);
        String obsE = safeValue(fila, 6);

        ActualizarInspeccionFrame frame = new ActualizarInspeccionFrame(
                id, predio, fecha, cultivo, plaga, obsG, obsE
        );

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cargarInspecciones(tecnico.getId());
            }
        });

        frame.setVisible(true);
    }

    private JButton crearBotonPrimario(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorFondo);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(10, 20, 10, 20));
        boton.setPreferredSize(new Dimension(130, 38));

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
}