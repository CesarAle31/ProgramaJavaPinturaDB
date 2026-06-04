package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.ClienteDAO;
import com.ipesapinturas.models.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientesPanel extends JPanel {
    private JTable clientesTable;
    private DefaultTableModel tableModel;
    private JTextField idSearchField;
    private ClienteDAO clienteDAO;

    public ClientesPanel() {
        clienteDAO = new ClienteDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 240));

        // Panel superior
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel de tabla
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        actualizarTabla();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("👤 Gestión de Clientes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JLabel idSearchLabel = new JLabel("ID:");
        idSearchField = new JTextField(8);
        idSearchField.addActionListener(e -> buscarPorId());

        JButton buscarIdButton = new JButton("Buscar ID");
        buscarIdButton.addActionListener(e -> buscarPorId());

        JButton limpiarButton = new JButton("🔄 Refrescar");
        limpiarButton.addActionListener(e -> {
            idSearchField.setText("");
            actualizarTabla();
        });

        JButton nuevoButton = new JButton("➕ Nuevo Cliente");
        nuevoButton.setBackground(new Color(220, 53, 69));
        nuevoButton.setForeground(Color.WHITE);
        nuevoButton.addActionListener(e -> abrirDialogoNuevo());

        JButton editarButton = new JButton("✏️ Editar");
        editarButton.addActionListener(e -> abrirDialogoEditar());

        JButton eliminarButton = new JButton("Eliminar");
        eliminarButton.setBackground(new Color(220, 53, 69));
        eliminarButton.setForeground(Color.WHITE);
        eliminarButton.addActionListener(e -> eliminar());

        actionPanel.add(idSearchLabel);
        actionPanel.add(idSearchField);
        actionPanel.add(buscarIdButton);
        actionPanel.add(limpiarButton);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(nuevoButton);
        actionPanel.add(editarButton);
        actionPanel.add(eliminarButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        return topPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        String[] columnNames = {"ID", "Nombre", "Email", "Teléfono", "Dirección", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientesTable = new JTable(tableModel);
        clientesTable.getTableHeader().setBackground(new Color(220, 53, 69));
        clientesTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(clientesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void actualizarTabla() {
        tableModel.setRowCount(0);
        List<Cliente> clientes = clienteDAO.obtenerTodos();

        for (Cliente c : clientes) {
            agregarClienteATabla(c);
        }
    }

    private void buscarPorId() {
        String idTexto = idSearchField.getText().trim();
        if (idTexto.isEmpty()) {
            actualizarTabla();
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            Cliente cliente = clienteDAO.obtenerPorId(id);
            tableModel.setRowCount(0);

            if (cliente != null) {
                agregarClienteATabla(cliente);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontro ningun cliente con ese ID",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numerico",
                    "ID invalido", JOptionPane.WARNING_MESSAGE);
            idSearchField.selectAll();
            idSearchField.requestFocus();
        }
    }

    private void agregarClienteATabla(Cliente c) {
        tableModel.addRow(new Object[]{
                c.getId(),
                c.getNombreCompleto(),
                c.getEmail(),
                c.getTelefono(),
                c.getDireccion(),
                c.getEstado()
        });
    }

    private Cliente buscarClientePorIdParaAccion(String accion) {
        String idTexto = idSearchField.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del cliente para " + accion,
                    "ID requerido", JOptionPane.INFORMATION_MESSAGE);
            idSearchField.requestFocus();
            return null;
        }

        try {
            int id = Integer.parseInt(idTexto);
            Cliente cliente = clienteDAO.obtenerPorId(id);
            tableModel.setRowCount(0);

            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "No se encontro ningun cliente con ese ID",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                idSearchField.selectAll();
                idSearchField.requestFocus();
                return null;
            }

            agregarClienteATabla(cliente);
            clientesTable.setRowSelectionInterval(0, 0);
            return cliente;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numerico",
                    "ID invalido", JOptionPane.WARNING_MESSAGE);
            idSearchField.selectAll();
            idSearchField.requestFocus();
            return null;
        }
    }

    private String textoSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private void abrirDialogoNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Cliente", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField nombreField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telefonoField = new JTextField();
        JTextField direccionField = new JTextField();

        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Teléfono:"));
        panel.add(telefonoField);
        panel.add(new JLabel("Dirección:"));
        panel.add(direccionField);
        panel.add(new JLabel("Estado:"));
        JComboBox<String> estadoCombo = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        panel.add(estadoCombo);

        JPanel buttonPanel = new JPanel();
        JButton guardarButton = new JButton("💾 Guardar");
        JButton cancelarButton = new JButton("❌ Cancelar");

        guardarButton.addActionListener(e -> {
            Cliente cliente = new Cliente();
            cliente.setNombreCompleto(nombreField.getText());
            cliente.setEmail(emailField.getText());
            cliente.setTelefono(telefonoField.getText());
            cliente.setDireccion(direccionField.getText());
            cliente.setEstado((String) estadoCombo.getSelectedItem());

            if (clienteDAO.guardar(cliente)) {
                JOptionPane.showMessageDialog(dialog, "Cliente guardado correctamente");
                actualizarTabla();
                dialog.dispose();
            }
        });

        cancelarButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(guardarButton);
        buttonPanel.add(cancelarButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void abrirDialogoEditar() {
        Cliente cliente = buscarClientePorIdParaAccion("editar");
        if (cliente == null) {
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Cliente", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField nombreField = new JTextField(textoSeguro(cliente.getNombreCompleto()));
        JTextField emailField = new JTextField(textoSeguro(cliente.getEmail()));
        JTextField telefonoField = new JTextField(textoSeguro(cliente.getTelefono()));
        JTextField direccionField = new JTextField(textoSeguro(cliente.getDireccion()));
        JComboBox<String> estadoCombo = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        estadoCombo.setSelectedItem(cliente.getEstado());

        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Telefono:"));
        panel.add(telefonoField);
        panel.add(new JLabel("Direccion:"));
        panel.add(direccionField);
        panel.add(new JLabel("Estado:"));
        panel.add(estadoCombo);

        JPanel buttonPanel = new JPanel();
        JButton actualizarButton = new JButton("💾 Actualizar");
        JButton cancelarButton = new JButton("❌ Cancelar");

        actualizarButton.addActionListener(e -> {
            if (nombreField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre del cliente es obligatorio",
                        "Error", JOptionPane.ERROR_MESSAGE);
                nombreField.requestFocus();
                return;
            }

            Cliente clienteActualizado = new Cliente();
            clienteActualizado.setId(cliente.getId());
            clienteActualizado.setNombreCompleto(nombreField.getText().trim());
            clienteActualizado.setEmail(emailField.getText().trim());
            clienteActualizado.setTelefono(telefonoField.getText().trim());
            clienteActualizado.setDireccion(direccionField.getText().trim());
            clienteActualizado.setEstado((String) estadoCombo.getSelectedItem());

            if (clienteDAO.actualizar(clienteActualizado)) {
                JOptionPane.showMessageDialog(dialog, "Cliente actualizado correctamente");
                idSearchField.setText(String.valueOf(cliente.getId()));
                buscarPorId();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar cliente",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelarButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(actualizarButton);
        buttonPanel.add(cancelarButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void eliminar() {
        Cliente cliente = buscarClientePorIdParaAccion("eliminar");
        if (cliente == null) {
            return;
        }

        String mensaje = "Esta seguro de eliminar el cliente ID " + cliente.getId()
                + " - " + textoSeguro(cliente.getNombreCompleto()) + "?";
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(cliente.getId())) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente");
                buscarPorId();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

