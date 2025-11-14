package app.ui;

import app.db.PredioDatabase;
import app.db.UserDatabase;
import app.model.Predio;
import app.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EditarPredioFrame extends JFrame {

    private Predio predio;
    private PropietarioFrame frameAnterior;

    private JTextField txtNumeroPredial, txtNombre, txtDepartamento;
    private JTextField txtMunicipio, txtVereda, txtLatitud, txtLongitud;
    private JComboBox<String> cmbPropietarios;
    private JButton btnGuardar, btnCancelar;

    public EditarPredioFrame(Predio predio, PropietarioFrame frameAnterior) {
        this.predio = predio;
        this.frameAnterior = frameAnterior;

        setTitle(" Editar Predio - " + predio.getNumeroPredial());
        setSize(500, 600);
        setLocationRelativeTo(frameAnterior);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("Editar Predio", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 102, 204));
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        gbc.gridwidth = 1;

        // Número Predial (fijo)
        gbc.gridy = 1;
        panel.add(new JLabel("Número Predial (Fijo):"), gbc);
        txtNumeroPredial = new JTextField(predio.getNumeroPredial());
        txtNumeroPredial.setEditable(false);
        txtNumeroPredial.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1;
        panel.add(txtNumeroPredial, gbc);

        // Nombre (editable)
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField(predio.getNombre());
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);

        // Departamento (no editable)
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Departamento:"), gbc);
        txtDepartamento = new JTextField(predio.getDepartamento());
        txtDepartamento.setEditable(false);
        txtDepartamento.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1;
        panel.add(txtDepartamento, gbc);

        // Municipio (no editable)
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Municipio:"), gbc);
        txtMunicipio = new JTextField(predio.getMunicipio());
        txtMunicipio.setEditable(false);
        txtMunicipio.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1;
        panel.add(txtMunicipio, gbc);

        // Vereda (no editable)
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Vereda:"), gbc);
        txtVereda = new JTextField(predio.getVereda());
        txtVereda.setEditable(false);
        txtVereda.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1;
        panel.add(txtVereda, gbc);

        // Latitud (no editable)
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Latitud:"), gbc);
        txtLatitud = new JTextField(String.valueOf(predio.getLatitud()));
        txtLatitud.setEditable(false);
        txtLatitud.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1;
        panel.add(txtLatitud, gbc);

        // Longitud (no editable)
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Longitud:"), gbc);
        txtLongitud = new JTextField(String.valueOf(predio.getLongitud()));
        txtLongitud.setEditable(false);
        txtLongitud.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1;
        panel.add(txtLongitud, gbc);

        // Propietario
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Propietario:"), gbc);
        cmbPropietarios = new JComboBox<>();
        cargarPropietarios();
        gbc.gridx = 1;
        panel.add(cmbPropietarios, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(Color.WHITE);

        btnGuardar = new JButton(" Guardar Cambios");
        btnGuardar.setBackground(new Color(0, 153, 76));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(204, 0, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        add(panel);

        // Eventos
        btnGuardar.addActionListener(e -> guardarCambios());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarPropietarios() {
        cmbPropietarios.removeAllItems();
        List<Usuario> propietarios = UserDatabase.obtenerUsuariosPorRol("Propietario");

        for (Usuario prop : propietarios) {
            String item = prop.getId() + " - " + prop.getNombreCompleto() + " (" + prop.getDocumento() + ")";
            cmbPropietarios.addItem(item);

            if (Integer.parseInt(prop.getId()) == predio.getIdUsuario()) {
                cmbPropietarios.setSelectedItem(item);
            }
        }
    }

    private void guardarCambios() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un nombre para el predio",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String seleccion = (String) cmbPropietarios.getSelectedItem();
            int idPropietario = Integer.parseInt(seleccion.split(" - ")[0]);
            predio.setIdUsuario(idPropietario);
            predio.setNombre(nombre);

            if (PredioDatabase.actualizarPredio(predio)) {
                JOptionPane.showMessageDialog(this,
                        "✅ Predio actualizado correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                frameAnterior.recargarPredios();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Error al actualizar el predio",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar los datos del propietario",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
