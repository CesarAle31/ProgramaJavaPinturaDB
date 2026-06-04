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
    private JTextField idSearchField;
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

        JLabel titleLabel = new JLabel("Gestion de Proveedores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JLabel idSearchLabel = new JLabel("ID:");
        idSearchField = new JTextField(8);
        idSearchField.addActionListener(e -> buscarPorId());

        JButton buscarIdButton = new JButton("Buscar ID");
        buscarIdButton.addActionListener(e -> buscarPorId());

        JButton limpiarButton = new JButton("Limpiar");
        limpiarButton.addActionListener(e -> {
            idSearchField.setText("");
            actualizarTabla();
        });

        JButton nuevoButton = new JButton("+ Nuevo Proveedor");
        nuevoButton.setBackground(new Color(220, 53, 69));
        nuevoButton.setForeground(Color.WHITE);
        nuevoButton.addActionListener(e -> abrirDialogoNuevo());

        JButton editarButton = new JButton("Editar");
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

        String[] columnNames = {"ID", "Razon Social", "Telefono", "Direccion", "Municipio"};
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
            agregarProveedorATabla(p);
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
            Proveedor proveedor = proveedorDAO.obtenerPorId(id);
            tableModel.setRowCount(0);

            if (proveedor != null) {
                agregarProveedorATabla(proveedor);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontro ningun proveedor con ese ID",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numerico",
                    "ID invalido", JOptionPane.WARNING_MESSAGE);
            idSearchField.selectAll();
            idSearchField.requestFocus();
        }
    }

    private void agregarProveedorATabla(Proveedor p) {
        tableModel.addRow(new Object[]{
                p.getId(),
                p.getRazonSocial(),
                p.getTelefono() != null ? p.getTelefono() : "N/A",
                p.getDireccion(),
                p.getMunicipio()
        });
    }

    private Proveedor buscarProveedorPorIdParaAccion(String accion) {
        String idTexto = idSearchField.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del proveedor para " + accion,
                    "ID requerido", JOptionPane.INFORMATION_MESSAGE);
            idSearchField.requestFocus();
            return null;
        }

        try {
            int id = Integer.parseInt(idTexto);
            Proveedor proveedor = proveedorDAO.obtenerPorId(id);
            tableModel.setRowCount(0);

            if (proveedor == null) {
                JOptionPane.showMessageDialog(this, "No se encontro ningun proveedor con ese ID",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                idSearchField.selectAll();
                idSearchField.requestFocus();
                return null;
            }

            agregarProveedorATabla(proveedor);
            proveedoresTable.setRowSelectionInterval(0, 0);
            return proveedor;
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Nuevo Proveedor", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField razonField = new JTextField();
        JTextField telefonoField = new JTextField();
        JTextField direccionField = new JTextField();
        JTextField municipioField = new JTextField();

        panel.add(new JLabel("Razon Social:"));
        panel.add(razonField);
        panel.add(new JLabel("Telefono:"));
        panel.add(telefonoField);
        panel.add(new JLabel("Direccion:"));
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

    private void abrirDialogoEditar() {
        Proveedor proveedor = buscarProveedorPorIdParaAccion("editar");
        if (proveedor == null) {
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Editar Proveedor", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField razonField = new JTextField(textoSeguro(proveedor.getRazonSocial()));
        JTextField telefonoField = new JTextField(textoSeguro(proveedor.getTelefono()));
        JTextField direccionField = new JTextField(textoSeguro(proveedor.getDireccion()));
        JTextField municipioField = new JTextField(textoSeguro(proveedor.getMunicipio()));

        panel.add(new JLabel("Razon Social:"));
        panel.add(razonField);
        panel.add(new JLabel("Telefono:"));
        panel.add(telefonoField);
        panel.add(new JLabel("Direccion:"));
        panel.add(direccionField);
        panel.add(new JLabel("Municipio:"));
        panel.add(municipioField);

        JPanel buttonPanel = new JPanel();
        JButton actualizarButton = new JButton("Actualizar");
        JButton cancelarButton = new JButton("Cancelar");

        actualizarButton.addActionListener(e -> {
            if (razonField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "La razon social es obligatoria",
                        "Error", JOptionPane.ERROR_MESSAGE);
                razonField.requestFocus();
                return;
            }

            Proveedor proveedorActualizado = new Proveedor();
            proveedorActualizado.setId(proveedor.getId());
            proveedorActualizado.setRazonSocial(razonField.getText().trim());
            proveedorActualizado.setTelefono(telefonoField.getText().trim());
            proveedorActualizado.setDireccion(direccionField.getText().trim());
            proveedorActualizado.setMunicipio(municipioField.getText().trim());

            if (proveedorDAO.actualizar(proveedorActualizado)) {
                JOptionPane.showMessageDialog(dialog, "Proveedor actualizado correctamente");
                idSearchField.setText(String.valueOf(proveedor.getId()));
                buscarPorId();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar proveedor",
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
        Proveedor proveedor = buscarProveedorPorIdParaAccion("eliminar");
        if (proveedor == null) {
            return;
        }

        String mensaje = "Esta seguro de eliminar el proveedor ID " + proveedor.getId()
                + " - " + textoSeguro(proveedor.getRazonSocial()) + "?";
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            if (proveedorDAO.eliminar(proveedor.getId())) {
                JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente");
                idSearchField.setText("");
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar proveedor",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
