package app.ui;

import app.db.ReportesVistas;
import app.db.UserDatabase;
import app.model.Tecnico;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;  // ✅ AGREGAR ESTA LÍNEA

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class TecnicoFrame extends JFrame {

    private Tecnico tecnico;

    public TecnicoFrame(Tecnico tecnico) {
        this.tecnico = tecnico;

        // Guardar el técnico logueado
        UserDatabase.setCurrentUser(tecnico);
        

        setTitle("Panel Técnico - Sistema Fitosanitario");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();


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

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new EmptyBorder(30, 40, 20, 40));

        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Panel del Técnico");
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblSubtitulo = new JLabel("Bienvenido, " + tecnico.getNombreCompleto());
        lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(120, 120, 130));

        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createVerticalStrut(5));
        panelTitulos.add(lblSubtitulo);

        JButton btnCerrarSesion = crearBotonSecundario("← Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> {
            UserDatabase.setCurrentUser(null);
            new LoginFrame().setVisible(true);
            dispose();
        });

        
        panelSuperior.add(panelTitulos, BorderLayout.WEST);
        panelSuperior.add(btnCerrarSesion, BorderLayout.EAST);

        // Panel central con opciones
        JPanel panelCentral = new JPanel(new GridBagLayout());
        
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(new EmptyBorder(0, 50, 0, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Card Registrar Inspección
        JPanel cardRegistrar = crearCard(
            "📝 Registrar Inspección",
            "Crea el registro de una nueva inspección",
            new Color(80, 200, 120)
        );
        gbc.gridy = 0;
        panelCentral.add(cardRegistrar, gbc);
        cardRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tecnico tecnicoActual = (Tecnico) UserDatabase.getCurrentUser();
                if (tecnicoActual == null) {
                    JOptionPane.showMessageDialog(TecnicoFrame.this, 
                        "⚠ No hay técnico logueado. Inicia sesión nuevamente.");
                    new LoginFrame().setVisible(true);
                    dispose();
                    return;
                }
                new RegistrarInspeccionFrame(tecnicoActual).setVisible(true);
            }
        });

        
        
        
        // Card Asociar Cultivos y Plagas
        JPanel cardAsociar = crearCard(
            "🌿 Asociar Cultivos y Plagas",
            "Gestiona las relaciones entre cultivos y plagas",
            new Color(90, 140, 255)
        );
        gbc.gridy = 1;
        panelCentral.add(cardAsociar, gbc);
        cardAsociar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new AsociarCultivosPlagasFrame(tecnico).setVisible(true);
            }
        });

        // Card Ingresar Observaciones
        JPanel cardObservaciones = crearCard(
            "📋 Ingresar Observaciones",
            "Añade notas y observaciones detalladas",
            new Color(255, 160, 100)
        );
        gbc.gridy = 2;
        panelCentral.add(cardObservaciones, gbc);
        cardObservaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ObservacionFrame(tecnico).setVisible(true);
            }
        });

        // Card Consultar Inspecciones
        JPanel cardConsultar = crearCard(
            "📊 Consultar Inspecciones",
            "Revisa el historial de inspecciones realizadas",
            new Color(153, 102, 255)
        );
        gbc.gridy = 3;
        panelCentral.add(cardConsultar, gbc);
        cardConsultar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ConsultarInspeccionesFrame(tecnico).setVisible(true);
            }
        });
        
        
        // Card Ver Reportes arreglar sergio
        JPanel cardReportes = crearCard(
            "📈 Ver Reportes",
            "Consulta los reportes generados por el técnico",
            new Color(0, 102, 204)
        );
        gbc.gridy = 6;
        panelCentral.add(cardReportes, gbc);

        cardReportes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JFrame ventanaReportes = new JFrame("Reportes del Técnico");
                ventanaReportes.setSize(700, 700);
                ventanaReportes.setLocationRelativeTo(null);
                ventanaReportes.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventanaReportes.add(crearPanelReportes());
                ventanaReportes.setVisible(true);
            }
        });


        // Footer
        JPanel panelFooter = new JPanel();
        panelFooter.setBackground(Color.WHITE);
        panelFooter.setBorder(new EmptyBorder(20, 0, 30, 0));
        JLabel lblFooter = new JLabel("© 2025 Sistema Fitosanitario");
        lblFooter.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(160, 160, 170));
        panelFooter.add(lblFooter);

        mainPanel.add(panelSuperior, BorderLayout.NORTH);
        mainPanel.add(panelCentral, BorderLayout.CENTER);
        mainPanel.add(panelFooter, BorderLayout.SOUTH);

        add(mainPanel);

    }

    private JPanel crearCard(String titulo, String descripcion, Color colorAccento) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 10));
        card.setBackground(new Color(250, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(25, 30, 25, 30)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(800, 110));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // Barra de color lateral
        JPanel barraColor = new JPanel();
        barraColor.setBackground(colorAccento);
        barraColor.setPreferredSize(new Dimension(5, 60));

        // Contenido del card
        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Poppins", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(25, 30, 40));

        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(120, 120, 130));

        panelTexto.add(lblTitulo);
        panelTexto.add(Box.createVerticalStrut(5));
        panelTexto.add(lblDesc);

        // Icono de flecha
        JLabel lblFlecha = new JLabel("→");
        lblFlecha.setFont(new Font("Poppins", Font.BOLD, 28));
        lblFlecha.setForeground(colorAccento);

        card.add(barraColor, BorderLayout.WEST);
        card.add(panelTexto, BorderLayout.CENTER);
        card.add(lblFlecha, BorderLayout.EAST);

        // Efecto hover
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(245, 247, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorAccento, 2, true),
                    new EmptyBorder(24, 29, 24, 29)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(250, 250, 252));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                    new EmptyBorder(25, 30, 25, 30)
                ));
            }
        });

        return card;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Poppins", Font.BOLD, 14));
        boton.setForeground(new Color(70, 130, 255));
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(10, 20, 10, 20)
        ));
        boton.setPreferredSize(new Dimension(160, 42));

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
private JPanel crearPanelReportes() {
    JPanel panel = new JPanel(new BorderLayout(20, 20));
    panel.setBackground(Color.WHITE);
    panel.setBorder(new EmptyBorder(25, 30, 25, 30));

    // Panel superior con título
    JPanel panelSuperior = new JPanel(new BorderLayout());
    panelSuperior.setBackground(Color.WHITE);
    panelSuperior.setBorder(new EmptyBorder(0, 0, 20, 0));

    JLabel lblTitulo = new JLabel("📊 Reportes del Técnico");
    lblTitulo.setFont(new Font("Poppins", Font.BOLD, 28));
    lblTitulo.setForeground(new Color(25, 30, 40));
    
    JLabel lblSubtitulo = new JLabel("Visualiza y analiza los datos de inspecciones");
    lblSubtitulo.setFont(new Font("Poppins", Font.PLAIN, 14));
    lblSubtitulo.setForeground(new Color(120, 120, 130));

    JPanel panelTitulos = new JPanel();
    panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
    panelTitulos.setBackground(Color.WHITE);
    panelTitulos.add(lblTitulo);
    panelTitulos.add(Box.createVerticalStrut(5));
    panelTitulos.add(lblSubtitulo);

    panelSuperior.add(panelTitulos, BorderLayout.WEST);

    // Panel de botones con cards - FIX AQUÍ
    JPanel panelBotones = new JPanel(new GridLayout(2, 2, 15, 15)); // Reducido el gap
    panelBotones.setBackground(Color.WHITE);
    panelBotones.setBorder(new EmptyBorder(10, 0, 20, 0)); // Reducido el margen inferior
    panelBotones.setPreferredSize(new Dimension(650, 260)); // AGREGADO: Tamaño fijo

    // Crear cards para los reportes
    JPanel cardPlagas = crearCardReporte(
        "🌿 Plantas Afectadas",
        "Visualiza el porcentaje de infestación",
        new Color(204, 102, 0),
        "Analizar"
    );
    
    JPanel cardDetectadas = crearCardReporte(
        "🐛 Plagas Detectadas",
        "Consulta las plagas más frecuentes",
        new Color(255, 140, 0),
        "Consultar"
    );
    
    JPanel cardEstadisticas = crearCardReporte(
        "📈 Mis Estadísticas",
        "Revisa tu desempeño como técnico",
        new Color(0, 153, 76),
        "Ver Stats"
    );
    
    JPanel cardLimpiar = crearCardReporte(
        "🗑️ Limpiar",
        "Limpia el área de resultados",
        new Color(128, 128, 128),
        "Limpiar"
    );

    panelBotones.add(cardPlagas);
    panelBotones.add(cardDetectadas);
    panelBotones.add(cardEstadisticas);
    panelBotones.add(cardLimpiar);

    // Área de resultados mejorada
    JPanel panelResultados = new JPanel(new BorderLayout());
    panelResultados.setBackground(Color.WHITE);
    panelResultados.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
        new EmptyBorder(15, 15, 15, 15)
    ));
    panelResultados.setPreferredSize(new Dimension(650, 180)); // AGREGADO: Tamaño fijo

    JLabel lblResultados = new JLabel("Resultados del Reporte");
    lblResultados.setFont(new Font("Poppins", Font.BOLD, 16));
    lblResultados.setForeground(new Color(25, 30, 40));
    lblResultados.setBorder(new EmptyBorder(0, 0, 10, 0));

    JTextArea areaResultados = new JTextArea();
    areaResultados.setEditable(false);
    areaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    areaResultados.setForeground(new Color(60, 60, 70));
    areaResultados.setBackground(new Color(250, 250, 252));
    areaResultados.setMargin(new Insets(15, 15, 15, 15));
    areaResultados.setLineWrap(true);
    areaResultados.setWrapStyleWord(true);

    JScrollPane scrollResultados = new JScrollPane(areaResultados);
    scrollResultados.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 245), 1, true));
    scrollResultados.getVerticalScrollBar().setUnitIncrement(16);

    panelResultados.add(lblResultados, BorderLayout.NORTH);
    panelResultados.add(scrollResultados, BorderLayout.CENTER);

    // Mensaje inicial
    areaResultados.setText("💡 Selecciona un reporte de las opciones superiores para visualizar los datos.\n\n" +
        "Cada reporte te proporcionará información valiosa sobre:\n" +
        "• Niveles de infestación por cultivo\n" +
        "• Plagas más frecuentemente detectadas\n" +
        "• Tu actividad y estadísticas como técnico");

    // Agregar componentes al panel principal
    panel.add(panelSuperior, BorderLayout.NORTH);
    panel.add(panelBotones, BorderLayout.CENTER);
    panel.add(panelResultados, BorderLayout.SOUTH);

    // Asignar funcionalidades a las cards - AQUÍ ESTÁN TODAS LAS FUNCIONES ORIGINALES
    cardPlagas.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            try {
                // Obtener datos de infestación real (TU CÓDIGO ORIGINAL COMPLETO)
                List<Map<String, Object>> datosInfestacion = ReportesVistas.obtenerPorcentajeInfestacion();
                
                if (datosInfestacion.isEmpty()) {
                    areaResultados.setText("No hay datos de infestación registrados.");
                    return;
                }

                // Agrupar por cultivo
                Map<String, List<Double>> infestacionPorCultivo = new HashMap<>();
                for (Map<String, Object> fila : datosInfestacion) {
                    String cultivo = (String) fila.get("cultivo");
                    Double porcentaje = (Double) fila.get("porcentaje_infestacion");
                    
                    if (!infestacionPorCultivo.containsKey(cultivo)) {
                        infestacionPorCultivo.put(cultivo, new ArrayList<>());
                    }
                    infestacionPorCultivo.get(cultivo).add(porcentaje);
                }

                // Crear lista de opciones (SIN "Todos los cultivos")
                List<String> cultivos = new ArrayList<>();
                for (String cultivo : infestacionPorCultivo.keySet()) {
                    cultivos.add(cultivo);
                }

                if (cultivos.isEmpty()) {
                    areaResultados.setText("No hay cultivos con datos de infestación.");
                    return;
                }

                // Selección inteligente
                String seleccion;
                if (cultivos.size() == 1) {
                    // Solo hay un cultivo, seleccionarlo automáticamente
                    seleccion = cultivos.get(0);
                } else {
                    // Varios cultivos, mostrar selector
                    seleccion = (String) JOptionPane.showInputDialog(
                        null,
                        "Selecciona el cultivo que deseas analizar:",
                        "Selector de Cultivo",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        cultivos.toArray(),
                        cultivos.get(0)
                    );

                    if (seleccion == null) {
                        areaResultados.setText("Operación cancelada");
                        return;
                    }
                }

                // Variable final para usar en clase anónima
                final String cultivoFinal = seleccion;
                final List<Double> porcentajesCultivo = infestacionPorCultivo.get(seleccion);

                // Crear panel personalizado con plantas
                JPanel panelPlantas = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        
                        // Fondo con degradado
                        GradientPaint fondoGradiente = new GradientPaint(
                            0, 0, new Color(240, 248, 255),
                            0, getHeight(), new Color(230, 240, 250)
                        );
                        g2d.setPaint(fondoGradiente);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        
                        // Calcular tasa de infección
                        double tasaInfeccionPromedio = 0;
                        if (porcentajesCultivo != null && !porcentajesCultivo.isEmpty()) {
                            double suma = 0;
                            for (Double p : porcentajesCultivo) {
                                suma += p;
                            }
                            tasaInfeccionPromedio = suma / porcentajesCultivo.size();
                        }
                        
                        // Panel superior con información
                        dibujarPanelSuperior(g2d, cultivoFinal, tasaInfeccionPromedio, datosInfestacion);
                        
                        // Leyenda mejorada
                        dibujarLeyenda(g2d);
                        
                        // Calcular plantas a mostrar (100 plantas = 100%)
                        int totalPlantas = 100;
                        int plantasInfectadas = (int) Math.round(tasaInfeccionPromedio);
                        
                        // Dibujar plantas en cuadrícula
                        int plantasPorFila = 10;
                        int espacioX = 50;
                        int espacioY = 60;
                        int inicioX = 100;
                        int inicioY = 280;
                        
                        for (int i = 0; i < totalPlantas; i++) {
                            int x = inicioX + (i % plantasPorFila) * espacioX;
                            int y = inicioY + (i / plantasPorFila) * espacioY;
                            
                            boolean infectada = i < plantasInfectadas;
                            dibujarPlantaMejorada(g2d, x, y, infectada);
                        }
                        
                        // Panel inferior con estadísticas
                        dibujarPanelInferior(g2d, totalPlantas, plantasInfectadas, tasaInfeccionPromedio);
                    }
                    
                    private void dibujarPanelSuperior(Graphics2D g2d, String cultivo, double tasa, List<Map<String, Object>> datos) {
                        int panelX = 30;
                        int panelY = 30;
                        int panelAncho = 690;
                        int panelAlto = 150;
                        
                        // Sombra del panel
                        g2d.setColor(new Color(0, 0, 0, 30));
                        g2d.fillRoundRect(panelX + 2, panelY + 2, panelAncho, panelAlto, 20, 20);
                        
                        // Panel principal con degradado
                        GradientPaint panelGradiente = new GradientPaint(
                            panelX, panelY, new Color(255, 255, 255),
                            panelX, panelY + panelAlto, new Color(250, 250, 255)
                        );
                        g2d.setPaint(panelGradiente);
                        g2d.fillRoundRect(panelX, panelY, panelAncho, panelAlto, 20, 20);
                        
                        // Borde
                        g2d.setColor(new Color(200, 210, 230));
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawRoundRect(panelX, panelY, panelAncho, panelAlto, 20, 20);
                        
                        // Icono del cultivo
                        g2d.setColor(new Color(76, 175, 80));
                        g2d.fillOval(panelX + 20, panelY + 20, 50, 50);
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Segoe UI Emoji", Font.BOLD, 30));
                        g2d.drawString("🌱", panelX + 30, panelY + 55);
                        
                        // Título del cultivo
                        g2d.setColor(new Color(40, 40, 60));
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 26));
                        g2d.drawString(cultivo.toUpperCase(), panelX + 90, panelY + 50);
                        
                        // Barra de progreso de infestación
                        int barraX = panelX + 90;
                        int barraY = panelY + 70;
                        int barraAncho = 450;
                        int barraAlto = 35;
                        
                        // Fondo de la barra
                        g2d.setColor(new Color(230, 230, 240));
                        g2d.fillRoundRect(barraX, barraY, barraAncho, barraAlto, 18, 18);
                        
                        // Barra de progreso con color según nivel
                        Color colorBarra;
                        if (tasa < 10) colorBarra = new Color(76, 175, 80);
                        else if (tasa < 30) colorBarra = new Color(255, 193, 7);
                        else if (tasa < 50) colorBarra = new Color(255, 152, 0);
                        else colorBarra = new Color(244, 67, 54);
                        
                        int anchoProgreso = (int) (barraAncho * tasa / 100);
                        if (anchoProgreso < 40) anchoProgreso = 40; // Mínimo para mostrar texto
                        
                        GradientPaint barraGradiente = new GradientPaint(
                            barraX, barraY, colorBarra,
                            barraX + anchoProgreso, barraY, colorBarra.darker()
                        );
                        g2d.setPaint(barraGradiente);
                        g2d.fillRoundRect(barraX, barraY, anchoProgreso, barraAlto, 18, 18);
                        
                        // Texto del porcentaje sobre la barra
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                        String porcentajeText = String.format("%.1f%%", tasa);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(porcentajeText);
                        g2d.drawString(porcentajeText, barraX + anchoProgreso - textWidth - 15, barraY + 23);
                        
                        // Etiqueta "TASA DE INFESTACIÓN"
                        g2d.setColor(new Color(100, 100, 120));
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        g2d.drawString("TASA DE INFESTACIÓN", barraX, barraY - 5);
                        
                        // Contar inspecciones únicas
                        java.util.Set<String> inspeccionesUnicas = new java.util.HashSet<>();
                        for (Map<String, Object> fila : datos) {
                            if (cultivo.equals(fila.get("cultivo"))) {
                                String key = fila.get("predio") + "-" + fila.get("cultivo") + "-" + fila.get("fecha");
                                inspeccionesUnicas.add(key);
                            }
                        }
                        
                        // Badge de inspecciones
                        int badgeY = barraY + 45;
                        g2d.setColor(new Color(63, 81, 181));
                        g2d.fillRoundRect(barraX, badgeY, 200, 32, 16, 16);
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        g2d.drawString("📊 " + inspeccionesUnicas.size() + " Inspecciones", barraX + 15, badgeY + 21);
                        
                        // Badge de nivel de riesgo
                        String nivelRiesgo;
                        Color colorNivel;
                        if (tasa < 10) {
                            nivelRiesgo = "🟢 BAJO";
                            colorNivel = new Color(76, 175, 80);
                        } else if (tasa < 30) {
                            nivelRiesgo = "🟡 MODERADO";
                            colorNivel = new Color(255, 193, 7);
                        } else if (tasa < 50) {
                            nivelRiesgo = "🟠 ALTO";
                            colorNivel = new Color(255, 152, 0);
                        } else {
                            nivelRiesgo = "🔴 CRÍTICO";
                            colorNivel = new Color(244, 67, 54);
                        }
                        
                        g2d.setColor(colorNivel);
                        g2d.fillRoundRect(barraX + 220, badgeY, 160, 32, 16, 16);
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        g2d.drawString(nivelRiesgo, barraX + 240, badgeY + 21);
                    }
                    
                    private void dibujarLeyenda(Graphics2D g2d) {
                        int leyendaX = 80;
                        int leyendaY = 210;
                        
                        // Fondo de la leyenda
                        g2d.setColor(new Color(255, 255, 255, 200));
                        g2d.fillRoundRect(leyendaX - 10, leyendaY - 10, 420, 45, 15, 15);
                        
                        g2d.setColor(new Color(200, 210, 230));
                        g2d.setStroke(new BasicStroke(1));
                        g2d.drawRoundRect(leyendaX - 10, leyendaY - 10, 420, 45, 15, 15);
                        
                        // Plantas sanas
                        dibujarPlantaMiniatura(g2d, leyendaX, leyendaY, false);
                        g2d.setColor(new Color(60, 60, 80));
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        g2d.drawString("Plantas Sanas", leyendaX + 35, leyendaY + 18);
                        
                        // Plantas infectadas
                        dibujarPlantaMiniatura(g2d, leyendaX + 220, leyendaY, true);
                        g2d.setColor(new Color(60, 60, 80));
                        g2d.drawString("Plantas Infectadas", leyendaX + 255, leyendaY + 18);
                    }
                    
                    private void dibujarPlantaMiniatura(Graphics2D g2d, int x, int y, boolean infectada) {
                        Color colorPlanta = infectada ? new Color(244, 67, 54) : new Color(76, 175, 80);
                        
                        g2d.setColor(colorPlanta);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawLine(x + 12, y + 10, x + 12, y + 25);
                        
                        int[] xHoja = {x + 12, x + 5, x + 12};
                        int[] yHoja = {y + 8, y + 3, y + 6};
                        g2d.fillPolygon(xHoja, yHoja, 3);
                        
                        int[] xHoja2 = {x + 12, x + 19, x + 12};
                        g2d.fillPolygon(xHoja2, yHoja, 3);
                        
                        int[] xHoja3 = {x + 12, x + 5, x + 19};
                        int[] yHoja3 = {y + 18, y + 22, y + 22};
                        g2d.fillPolygon(xHoja3, yHoja3, 3);
                    }
                    
                    private void dibujarPlantaMejorada(Graphics2D g2d, int x, int y, boolean infectada) {
                        Color colorPlanta = infectada ? new Color(244, 67, 54) : new Color(76, 175, 80);
                        Color colorHoja = infectada ? new Color(211, 47, 47) : new Color(104, 195, 108);
                        Color colorSombra = new Color(0, 0, 0, 30);
                        
                        // Sombra
                        g2d.setColor(colorSombra);
                        g2d.fillOval(x + 5, y + 38, 20, 6);
                        
                        // Tallo con degradado
                        GradientPaint talloGradiente = new GradientPaint(
                            x + 15, y + 20, colorPlanta.brighter(),
                            x + 15, y + 40, colorPlanta
                        );
                        g2d.setPaint(talloGradiente);
                        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(x + 15, y + 20, x + 15, y + 40);
                        
                        // Hojas con sombra
                        g2d.setColor(new Color(0, 0, 0, 20));
                        dibujarHojasConSombra(g2d, x + 1, y + 1, colorSombra);
                        
                        // Hojas normales
                        g2d.setColor(colorHoja);
                        dibujarHojas(g2d, x, y, colorHoja);
                        
                        // Manchas en plantas infectadas
                        if (infectada) {
                            g2d.setColor(new Color(183, 28, 28));
                            g2d.fillOval(x + 10, y + 8, 4, 4);
                            g2d.fillOval(x + 18, y + 12, 3, 3);
                            g2d.fillOval(x + 8, y + 22, 4, 4);
                            g2d.fillOval(x + 20, y + 24, 3, 3);
                        } else {
                            // Brillo en plantas sanas
                            g2d.setColor(new Color(255, 255, 255, 100));
                            g2d.fillOval(x + 12, y + 10, 3, 3);
                        }
                    }
                    
                    private void dibujarHojas(Graphics2D g2d, int x, int y, Color color) {
                        g2d.setColor(color);
                        
                        // Hoja superior izquierda
                        int[] xHoja1 = {x + 15, x + 5, x + 15};
                        int[] yHoja1 = {y + 15, y + 5, y + 10};
                        g2d.fillPolygon(xHoja1, yHoja1, 3);
                        
                        // Hoja superior derecha
                        int[] xHoja2 = {x + 15, x + 25, x + 15};
                        int[] yHoja2 = {y + 15, y + 5, y + 10};
                        g2d.fillPolygon(xHoja2, yHoja2, 3);
                        
                        // Hojas medias
                        int[] xHoja3 = {x + 15, x + 3, x + 15};
                        int[] yHoja3 = {y + 25, y + 18, y + 22};
                        g2d.fillPolygon(xHoja3, yHoja3, 3);
                        
                        int[] xHoja4 = {x + 15, x + 27, x + 15};
                        int[] yHoja4 = {y + 25, y + 18, y + 22};
                        g2d.fillPolygon(xHoja4, yHoja4, 3);
                        
                        // Hoja inferior
                        int[] xHoja5 = {x + 15, x + 8, x + 22};
                        int[] yHoja5 = {y + 32, y + 35, y + 35};
                        g2d.fillPolygon(xHoja5, yHoja5, 3);
                    }
                    
                    private void dibujarHojasConSombra(Graphics2D g2d, int x, int y, Color sombra) {
                        dibujarHojas(g2d, x, y, sombra);
                    }
                    
                    private void dibujarPanelInferior(Graphics2D g2d, int total, int infectadas, double tasa) {
                        int yBase = getHeight() - 120;
                        int panelX = 30;
                        int panelAncho = 690;
                        int panelAlto = 90;
                        
                        // Sombra del panel
                        g2d.setColor(new Color(0, 0, 0, 30));
                        g2d.fillRoundRect(panelX + 2, yBase + 2, panelAncho, panelAlto, 20, 20);
                        
                        // Panel con degradado
                        GradientPaint panelGradiente = new GradientPaint(
                            panelX, yBase, new Color(255, 255, 255),
                            panelX, yBase + panelAlto, new Color(250, 250, 255)
                        );
                        g2d.setPaint(panelGradiente);
                        g2d.fillRoundRect(panelX, yBase, panelAncho, panelAlto, 20, 20);
                        
                        // Borde
                        g2d.setColor(new Color(200, 210, 230));
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawRoundRect(panelX, yBase, panelAncho, panelAlto, 20, 20);
                        
                        // Estadísticas
                        int col1 = panelX + 80;
                        int col2 = panelX + 400;
                        
                        // Plantas sanas
                        g2d.setColor(new Color(76, 175, 80));
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
                        g2d.drawString(String.valueOf(total - infectadas), col1, yBase + 48);
                        g2d.setColor(new Color(100, 100, 120));
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        g2d.drawString(String.format("SANAS (%.0f%%)", 100 - tasa), col1, yBase + 70);
                        
                        // Plantas infectadas
                        g2d.setColor(new Color(244, 67, 54));
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
                        g2d.drawString(String.valueOf(infectadas), col2, yBase + 48);
                        g2d.setColor(new Color(100, 100, 120));
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        g2d.drawString(String.format("INFECTADAS (%.1f%%)", tasa), col2, yBase + 70);
                    }
                };
                
                panelPlantas.setPreferredSize(new Dimension(750, 950));
                panelPlantas.setBackground(new Color(245, 245, 240));
                
                // Envolver el panel en un JScrollPane para plantas que no caben
                JScrollPane scrollPlantas = new JScrollPane(panelPlantas);
                scrollPlantas.setPreferredSize(new Dimension(770, 750));
                scrollPlantas.getVerticalScrollBar().setUnitIncrement(16);
                
                // Mostrar información detallada en el área de texto
                StringBuilder sb = new StringBuilder();
                sb.append("═══════════════════════════════════════\n");
                sb.append(String.format("   ANÁLISIS: %s\n", cultivoFinal));
                sb.append("═══════════════════════════════════════\n\n");
                
                if (porcentajesCultivo != null) {
                    double suma = 0;
                    double max = 0;
                    double min = 100;
                    for (Double p : porcentajesCultivo) {
                        suma += p;
                        if (p > max) max = p;
                        if (p < min) min = p;
                    }
                    double promedio = suma / porcentajesCultivo.size();
                    
                    sb.append(String.format("📊 Número de inspecciones: %d\n", porcentajesCultivo.size()));
                    sb.append(String.format("🪲 Infestación promedio: %.2f%%\n", promedio));
                    sb.append(String.format("📈 Infestación máxima: %.2f%%\n", max));
                    sb.append(String.format("📉 Infestación mínima: %.2f%%\n\n", min));
                    
                    // Clasificar nivel de riesgo
                    String nivelRiesgo;
                    if (promedio < 10) nivelRiesgo = "🟢 BAJO";
                    else if (promedio < 30) nivelRiesgo = "🟡 MODERADO";
                    else if (promedio < 50) nivelRiesgo = "🟠 ALTO";
                    else nivelRiesgo = "🔴 CRÍTICO";
                    
                    sb.append(String.format("⚠️ Nivel de riesgo: %s\n", nivelRiesgo));
                }
                
                areaResultados.setText(sb.toString());
                
                // Mostrar ventana con plantas
                JOptionPane.showMessageDialog(
                    null, 
                    scrollPlantas,
                    "Visualización de Infestación - " + cultivoFinal, 
                    JOptionPane.PLAIN_MESSAGE
                );

            } catch (Exception ex) {
                ex.printStackTrace();
                areaResultados.setText("❌ Error al generar el reporte:\n" + ex.getMessage());
            }
        }
    });

    cardDetectadas.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            try {
                java.util.List<Map<String, Object>> datos = ReportesVistas.obtenerPlagasMasFrecuentes();
                
                if (datos.isEmpty()) {
                    areaResultados.setText("🌱 No se han detectado plagas en las inspecciones.");
                    return;
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("--------------------------------------------------------------------------------\n");
                sb.append("          🐛 PLAGAS MÁS FRECUENTES\n");
                sb.append("--------------------------------------------------------------------------------\n\n");
                sb.append(String.format("📊 Total de plagas diferentes: %d\n\n", datos.size()));
                
                for (Map<String, Object> fila : datos) {
                    String nombreComun = (String) fila.get("nombre_comun");
                    String nombreCientifico = (String) fila.get("nombre_cientifico");
                    int vecesDetectada = ((Number) fila.get("veces_detectada")).intValue();
                    int cultivosAfectados = ((Number) fila.get("cultivos_afectados")).intValue();
                    double promedioInfestacion = ((Number) fila.get("promedio_infestacion")).doubleValue();
                    
                    sb.append(String.format("🔸 %s\n", nombreComun));
                    sb.append(String.format("   📝 Científico: %s\n", nombreCientifico));
                    sb.append(String.format("   🔢 Detecciones: %d veces\n", vecesDetectada));
                    sb.append(String.format("   🌿 Cultivos afectados: %d\n", cultivosAfectados));
                    sb.append(String.format("   📈 Infestación promedio: %.2f%%\n\n", promedioInfestacion));
                }
                
                areaResultados.setText(sb.toString());
                
            } catch (Exception ex) {
                areaResultados.setText("❌ Error al generar el reporte de plagas.");
            }
        }
    });

    cardEstadisticas.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            try {
                int idTecnico = Integer.parseInt(tecnico.getId());
                java.util.List<Map<String, Object>> datos = ReportesVistas.obtenerInspeccionesPorTecnico(idTecnico);
                
                if (datos.isEmpty()) {
                    areaResultados.setText("📊 No hay estadísticas disponibles para mostrar.");
                    return;
                }

                Map<String, Object> info = datos.get(0);

                StringBuilder sb = new StringBuilder();
                sb.append("--------------------------------------------------------------------------------\n");
                sb.append("          📈 MIS ESTADÍSTICAS\n");
                sb.append("--------------------------------------------------------------------------------\n\n");
                
                Object tecnicoObj = info.get("tecnico");
                Object documentoObj = info.get("documento");
                Object totalInspObj = info.get("total_inspecciones");
                Object prediosObj = info.get("predios_inspeccionados");
                Object plagasObj = info.get("total_plagas_detectadas");
                Object primeraObj = info.get("primera_inspeccion");
                Object ultimaObj = info.get("ultima_inspeccion");
                
                sb.append(String.format("👤 Técnico: %s\n", tecnicoObj != null ? tecnicoObj : "N/A"));
                sb.append(String.format("📄 Documento: %s\n\n", documentoObj != null ? documentoObj : "N/A"));
                sb.append(String.format("📋 Total inspecciones: %s\n", totalInspObj != null ? totalInspObj : "0"));
                sb.append(String.format("📍 Predios inspeccionados: %s\n", prediosObj != null ? prediosObj : "0"));
                sb.append(String.format("🐛 Total plagas detectadas: %s\n\n", plagasObj != null ? plagasObj : "0"));
                sb.append(String.format("📅 Primera inspección: %s\n", primeraObj != null ? primeraObj : "N/A"));
                sb.append(String.format("📅 Última inspección: %s\n", ultimaObj != null ? ultimaObj : "N/A"));

                areaResultados.setText(sb.toString());
                
            } catch (Exception ex) {
                areaResultados.setText("❌ Error al cargar las estadísticas:\n" + ex.getMessage());
            }
        }
    });

    cardLimpiar.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            areaResultados.setText("💡 Selecciona un reporte de las opciones superiores para visualizar los datos.\n\n" +
                "Cada reporte te proporcionará información valiosa sobre:\n" +
                "• Niveles de infestación por cultivo\n" +
                "• Plagas más frecuentemente detectadas\n" +
                "• Tu actividad y estadísticas como técnico");
        }
    });

    return panel;
}

private JPanel crearCardReporte(String titulo, String descripcion, Color color, String accion) {
    JPanel card = new JPanel();
    card.setLayout(new BorderLayout(10, 6));
    card.setBackground(new Color(250, 250, 252));
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
        new EmptyBorder(15, 15, 15, 15) // Padding reducido
    ));
    card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    // REMOVIDO: setPreferredSize y setMaximumSize para que se ajuste al GridLayout

    // Barra de color lateral
    JPanel barraColor = new JPanel();
    barraColor.setBackground(color);
    barraColor.setPreferredSize(new Dimension(5, 50));

    // Contenido del card
    JPanel panelTexto = new JPanel();
    panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
    panelTexto.setOpaque(false);
    panelTexto.setBorder(new EmptyBorder(2, 8, 2, 8));

    JLabel lblTitulo = new JLabel(titulo);
    lblTitulo.setFont(new Font("Poppins", Font.BOLD, 16)); // Tamaño ajustado
    lblTitulo.setForeground(new Color(25, 30, 40));
    lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel lblDesc = new JLabel("<html>" + descripcion + "</html>"); // HTML para wrap
    lblDesc.setFont(new Font("Poppins", Font.PLAIN, 12));
    lblDesc.setForeground(new Color(80, 80, 90));
    lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

    panelTexto.add(lblTitulo);
    panelTexto.add(Box.createVerticalStrut(6));
    panelTexto.add(lblDesc);

    // Badge de acción
    JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panelAccion.setOpaque(false);
    
    JLabel lblAccion = new JLabel(accion);
    lblAccion.setFont(new Font("Poppins", Font.BOLD, 11));
    lblAccion.setForeground(color);
    lblAccion.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color, 2, true),
        new EmptyBorder(5, 10, 5, 10)
    ));

    panelAccion.add(lblAccion);

    card.add(barraColor, BorderLayout.WEST);
    card.add(panelTexto, BorderLayout.CENTER);
    card.add(panelAccion, BorderLayout.SOUTH);

    // Efecto hover
    card.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            card.setBackground(new Color(245, 247, 250));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                new EmptyBorder(14, 14, 14, 14)
            ));
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            card.setBackground(new Color(250, 250, 252));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(15, 15, 15, 15)
            ));
        }
        
        @Override
        public void mousePressed(java.awt.event.MouseEvent evt) {
            card.setBackground(new Color(240, 242, 245));
        }
    });

    return card;
}
}