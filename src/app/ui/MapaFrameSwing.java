package app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.Locale;

public class MapaFrameSwing extends JFrame {

    private double lat, lon;
    private int zoom = 12;
    private String nombrePredio;
    private JProgressBar progressBar;
    private JLabel lblEstado;
    private MapaPanel panelMapa;
    
    private String mapboxAccessToken = "pk.eyJ1Ijoic295ZGVzdHJveSIsImEiOiJjbWh0a2V1NjMxeHJyMmxvb2l5aXV4ejg3In0.H47SFmiakn9Rox-GwfcHfw";
    private String estiloActual = "streets-v12";

    public MapaFrameSwing(double lat, double lon, String nombrePredio) {
        this.lat = lat;
        this.lon = lon;
        this.nombrePredio = nombrePredio;
        
        setTitle("Mapa - " + nombrePredio + " | Mapbox");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        cargarMapaMapbox();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 250));
        
        // Panel de progreso con diseño moderno
        JPanel panelProgreso = new JPanel(new BorderLayout(0, 8));
        panelProgreso.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelProgreso.setBackground(Color.WHITE);
        
        lblEstado = new JLabel("🔄 Conectando con Mapbox...", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEstado.setForeground(new Color(60, 60, 67));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 24));
        progressBar.setBackground(new Color(230, 230, 235));
        progressBar.setForeground(new Color(0, 122, 255));
        progressBar.setBorderPainted(false);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        panelProgreso.add(lblEstado, BorderLayout.NORTH);
        panelProgreso.add(progressBar, BorderLayout.CENTER);
        mainPanel.add(panelProgreso, BorderLayout.NORTH);
        
        // Panel del mapa con zoom y arrastre
        panelMapa = new MapaPanel();
        panelMapa.setBackground(new Color(240, 240, 245));
        JScrollPane scrollPane = new JScrollPane(panelMapa);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(240, 240, 245));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de controles con diseño moderno
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelControles.setBackground(Color.WHITE);
        panelControles.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JButton btnZoomIn = crearBotonModerno("🔍+", "Acercar");
        JButton btnZoomOut = crearBotonModerno("🔍-", "Alejar");
        JButton btnRecargar = crearBotonModerno("🔄", "Recargar");
        
        btnZoomIn.addActionListener(e -> cambiarZoom(1));
        btnZoomOut.addActionListener(e -> cambiarZoom(-1));
        btnRecargar.addActionListener(e -> {
            panelMapa.resetearTransformacion();
            cargarMapaMapbox();
        });
        
        panelControles.add(btnZoomOut);
        panelControles.add(btnRecargar);
        panelControles.add(btnZoomIn);
        
        mainPanel.add(panelControles, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }

    private JButton crearBotonModerno(String texto, String tooltip) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(60, 36));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(0, 122, 255));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(10, 132, 255));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0, 122, 255));
            }
        });
        
        return btn;
    }

    private void cambiarZoom(int delta) {
        zoom = Math.max(1, Math.min(20, zoom + delta));
        panelMapa.resetearTransformacion();
        cargarMapaMapbox();
    }

    private void cargarMapaMapbox() {
        new Thread(() -> {
            try {
                actualizarProgreso(20, "🔄 Configurando formato de coordenadas...");
                
                // Aumentar resolución para mejor calidad (máximo permitido por Mapbox)
                int width = 1280;
                int height = 1280;
                
                String marker = String.format(Locale.US, "pin-s+ff0000(%.6f,%.6f)", lon, lat);
                
                String urlStr = String.format(Locale.US,
                    "https://api.mapbox.com/styles/v1/mapbox/%s/static/%s/%.6f,%.6f,%d/%dx%d@2x?access_token=%s",
                    estiloActual, 
                    marker, 
                    lon, lat,
                    zoom, 
                    width, height, 
                    mapboxAccessToken
                );
                
                System.out.println("🔗 URL Mapbox (alta resolución): " + urlStr);
                System.out.println("📍 Coordenadas originales - Lat: " + lat + ", Lon: " + lon);
                
                actualizarProgreso(50, "📡 Descargando mapa en alta resolución...");
                BufferedImage mapa = descargarImagen(urlStr);
                
                if (mapa != null) {
                    actualizarProgreso(100, "✅ Mapa cargado exitosamente");
                    panelMapa.setImagen(mapa);
                    ocultarProgreso();
                }
                
            } catch (Exception e) {
                mostrarError("Mapbox: " + e.getMessage());
            }
        }).start();
    }

    private BufferedImage descargarImagen(String urlStr) throws IOException {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            
            int responseCode = connection.getResponseCode();
            System.out.println("📞 Response Code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedImage imagen = ImageIO.read(connection.getInputStream());
                System.out.println("✅ Imagen descargada: " + imagen.getWidth() + "x" + imagen.getHeight());
                return imagen;
            } else {
                String errorBody = leerError(connection);
                System.err.println("❌ Error Mapbox: " + responseCode + " - " + errorBody);
                throw new IOException("HTTP " + responseCode + " - " + errorBody);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    private String leerError(HttpURLConnection connection) {
        try {
            java.io.InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                java.util.Scanner scanner = new java.util.Scanner(errorStream).useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "Sin mensaje de error";
            }
        } catch (Exception e) {
            System.err.println("Error leyendo respuesta: " + e.getMessage());
        }
        return "No se pudo leer el mensaje de error";
    }

    private void cargarMapaConValidacion() {
        new Thread(() -> {
            try {
                if (!sonCoordenadasValidas(lat, lon)) {
                    mostrarError("Coordenadas inválidas. Latitud debe estar entre -90 y 90, Longitud entre -180 y 180");
                    return;
                }
                
                int width = 1280;
                int height = 1280;
                
                String marker = String.format(Locale.US, "pin-s+ff0000(%.6f,%.6f)", lon, lat);
                String urlStr = String.format(Locale.US,
                    "https://api.mapbox.com/styles/v1/mapbox/%s/static/%s/%.6f,%.6f,%d/%dx%d@2x?access_token=%s",
                    estiloActual, marker, lon, lat, zoom, width, height, mapboxAccessToken
                );
                
                System.out.println("🔗 URL Validada: " + urlStr);
                
                BufferedImage mapa = descargarImagen(urlStr);
                if (mapa != null) {
                    panelMapa.setImagen(mapa);
                    ocultarProgreso();
                }
                
            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
            }
        }).start();
    }

    private boolean sonCoordenadasValidas(double lat, double lon) {
        return lat >= -90.0 && lat <= 90.0 && lon >= -180.0 && lon <= 180.0;
    }

    private class MapaPanel extends JPanel {
        private BufferedImage imagen;
        private double scale = 1.0;
        private Point offset = new Point(0, 0);
        private Point lastDragPoint;
        
        public MapaPanel() {
            setBackground(new Color(240, 240, 245));
            
            // Zoom con rueda del mouse (solo visual)
            addMouseWheelListener(e -> {
                double delta = e.getPreciseWheelRotation();
                double factor = delta < 0 ? 1.1 : 0.9;
                
                Point mousePos = e.getPoint();
                double oldScale = scale;
                scale = Math.max(0.5, Math.min(5.0, scale * factor));
                
                // Ajustar offset para hacer zoom hacia el cursor
                offset.x = (int)(mousePos.x - (mousePos.x - offset.x) * (scale / oldScale));
                offset.y = (int)(mousePos.y - (mousePos.y - offset.y) * (scale / oldScale));
                
                actualizarPreferredSize();
                repaint();
            });
            
            // Arrastre con el mouse
            MouseAdapter mouseAdapter = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    lastDragPoint = e.getPoint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                
                public void mouseReleased(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                    lastDragPoint = null;
                }
                
                public void mouseDragged(MouseEvent e) {
                    if (lastDragPoint != null) {
                        int dx = e.getX() - lastDragPoint.x;
                        int dy = e.getY() - lastDragPoint.y;
                        offset.translate(dx, dy);
                        lastDragPoint = e.getPoint();
                        repaint();
                    }
                }
            };
            
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }
        
        public void resetearTransformacion() {
            scale = 1.0;
            offset = new Point(0, 0);
            actualizarPreferredSize();
            repaint();
        }
        
        public void centrarMapa() {
            if (imagen != null && getParent() != null) {
                int w = (int)(imagen.getWidth() * scale);
                int h = (int)(imagen.getHeight() * scale);
                
                // Centrar la imagen en la ventana visible
                int viewportWidth = getParent().getWidth();
                int viewportHeight = getParent().getHeight();
                
                offset.x = (viewportWidth - w) / 2;
                offset.y = (viewportHeight - h) / 2;
                
                actualizarPreferredSize();
                repaint();
            }
        }
        
        public void setImagen(BufferedImage imagen) {
            this.imagen = imagen;
            scale = 1.0;
            
            // Centrar el mapa inmediatamente al cargarlo
            SwingUtilities.invokeLater(() -> {
                centrarMapa();
            });
        }
        
        private void actualizarPreferredSize() {
            if (imagen != null) {
                int w = Math.max(getParent().getWidth(), (int)(imagen.getWidth() * scale));
                int h = Math.max(getParent().getHeight(), (int)(imagen.getHeight() * scale));
                setPreferredSize(new Dimension(w, h));
                revalidate();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Antialiasing para mejor calidad
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (imagen != null) {
                int w = (int)(imagen.getWidth() * scale);
                int h = (int)(imagen.getHeight() * scale);
                
                // Centrar la imagen si es más pequeña que el panel
                int x = offset.x;
                int y = offset.y;
                
                if (w < getWidth()) {
                    x = (getWidth() - w) / 2;
                }
                if (h < getHeight()) {
                    y = (getHeight() - h) / 2;
                }
                
                g2d.drawImage(imagen, x, y, w, h, this);
                
                // Panel de información con diseño moderno
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(10, 10, 280, 80, 12, 12);
                
                g2d.setColor(new Color(200, 200, 200, 100));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(10, 10, 280, 80, 12, 12);
                
                g2d.setColor(new Color(60, 60, 67));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2d.drawString("📍 Coordenadas", 20, 30);
                
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2d.drawString(String.format("Lat: %.6f", lat), 20, 50);
                g2d.drawString(String.format("Lon: %.6f", lon), 20, 68);
                
                g2d.drawString(String.format("🔍 Zoom: %d | Escala: %.1fx", zoom, scale), 20, 85);
                
            } else {
                g2d.setColor(new Color(150, 150, 160));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                String mensaje = "Cargando mapa de Mapbox...";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(mensaje)) / 2;
                int y = getHeight() / 2;
                g2d.drawString(mensaje, x, y);
            }
        }
    }

    private void actualizarProgreso(int valor, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(valor);
            lblEstado.setText(mensaje);
        });
    }

    private void ocultarProgreso() {
        SwingUtilities.invokeLater(() -> {
            Container parent = progressBar.getParent();
            if (parent != null && parent.getParent() != null) {
                parent.getParent().remove(parent);
                revalidate();
                repaint();
            }
        });
    }

    private void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            int option = JOptionPane.showConfirmDialog(this,
                "❌ " + mensaje + 
                "\n\n¿Intentar con validación de coordenadas?",
                "Error Mapbox", 
                JOptionPane.YES_NO_OPTION);
                
            if (option == JOptionPane.YES_OPTION) {
                cargarMapaConValidacion();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MapaFrameSwing(4.570868, -74.297333, "Bogotá, Colombia");
        });
    }
}