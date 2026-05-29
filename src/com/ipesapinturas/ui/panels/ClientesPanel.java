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

        JLabel titleLabel = new JLabel("Gestión de Clientes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton nuevoButton = new JButton("+ Nuevo Cliente");
        nuevoButton.setBackground(new Color(220, 53, 69));
        nuevoButton.setForeground(Color.WHITE);
        nuevoButton.addActionListener(e -> abrirDialogoNuevo());

        JButton editarButton = new JButton("✏️ Editar");
        editarButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo"));

        JButton eliminarButton = new JButton("🗑️ Eliminar");
        eliminarButton.setBackground(new Color(220, 53, 69));
        eliminarButton.setForeground(Color.WHITE);
        eliminarButton.addActionListener(e -> eliminar());

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
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getNombreCompleto(),
                    c.getEmail(),
                    c.getTelefono(),
                    c.getDireccion(),
                    c.getEstado()
            });
        }
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
        JButton guardarButton = new JButton("Guardar");
        JButton cancelarButton = new JButton("Cancelar");

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

    private void eliminar() {
        int selectedRow = clientesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int clienteId = (int) tableModel.getValueAt(selectedRow, 0);
        int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(clienteId)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente");
                actualizarTabla();
            }
        }
    }
}