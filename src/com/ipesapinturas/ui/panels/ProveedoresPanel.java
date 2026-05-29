package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.ProveedorDAO;
import com.ipesapinturas.models.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProveedoresPanel extends JPanel {
    private JTable proveedoresTable;
    private DefaultTableModel tableModel;
    private ProveedorDAO proveedorDAO;

    public ProveedoresPanel() {
        proveedorDAO = new ProveedorDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 240));

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        actualizarTabla();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestión de Proveedores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton nuevoButton = new JButton("+ Nuevo Proveedor");
        nuevoButton.setBackground(new Color(220, 53, 69));
        nuevoButton.setForeground(Color.WHITE);
        nuevoButton.addActionListener(e -> abrirDialogoNuevo());

        actionPanel.add(nuevoButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        return topPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        String[] columnNames = {"ID", "Razón Social", "Teléfono", "Dirección", "Municipio"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        proveedoresTable = new JTable(tableModel);
        proveedoresTable.getTableHeader().setBackground(new Color(220, 53, 69));
        proveedoresTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(proveedoresTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void actualizarTabla() {
        tableModel.setRowCount(0);
        List<Proveedor> proveedores = proveedorDAO.obtenerTodos();

        for (Proveedor p : proveedores) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getRazonSocial(),
                    p.getTelefono() != null ? p.getTelefono() : "—",
                    p.getDireccion(),
                    p.getMunicipio()
            });
        }
    }

    private void abrirDialogoNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Proveedor", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField razonField = new JTextField();
        JTextField telefonoField = new JTextField();
        JTextField direccionField = new JTextField();
        JTextField municipioField = new JTextField();

        panel.add(new JLabel("Razón Social:"));
        panel.add(razonField);
        panel.add(new JLabel("Teléfono:"));
        panel.add(telefonoField);
        panel.add(new JLabel("Dirección:"));
        panel.add(direccionField);
        panel.add(new JLabel("Municipio:"));
        panel.add(municipioField);

        JPanel buttonPanel = new JPanel();
        JButton guardarButton = new JButton("Guardar");
        JButton cancelarButton = new JButton("Cancelar");

        guardarButton.addActionListener(e -> {
            Proveedor proveedor = new Proveedor();
            proveedor.setRazonSocial(razonField.getText());
            proveedor.setTelefono(telefonoField.getText());
            proveedor.setDireccion(direccionField.getText());
            proveedor.setMunicipio(municipioField.getText());

            if (proveedorDAO.guardar(proveedor)) {
                JOptionPane.showMessageDialog(dialog, "Proveedor guardado correctamente");
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
}