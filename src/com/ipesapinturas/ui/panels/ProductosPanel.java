package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.ProductoDAO;
import com.ipesapinturas.dao.ProveedorDAO;
import com.ipesapinturas.models.Producto;
import com.ipesapinturas.models.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductosPanel extends JPanel {
    private JTable productosTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ProductoDAO productoDAO;
    private ProveedorDAO proveedorDAO;

    public ProductosPanel() {
        productoDAO = new ProductoDAO();
        proveedorDAO = new ProveedorDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 240));

        // Panel superior
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel de tabla
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Cargar datos
        actualizarTabla();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestión de Productos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Panel de búsqueda y botones
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Buscar:");
        searchField = new JTextField(20);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscar();
            }
        });

        JButton nuevoButton = new JButton("+ Nuevo Producto");
        nuevoButton.setBackground(new Color(220, 53, 69));
        nuevoButton.setForeground(Color.WHITE);
        nuevoButton.addActionListener(e -> abrirDialogoNuevo());

        JButton editarButton = new JButton("✏️ Editar");
        editarButton.addActionListener(e -> abrirDialogoEditar());

        JButton eliminarButton = new JButton("🗑️ Eliminar");
        eliminarButton.setBackground(new Color(220, 53, 69));
        eliminarButton.setForeground(Color.WHITE);
        eliminarButton.addActionListener(e -> eliminar());

        actionPanel.add(searchLabel);
        actionPanel.add(searchField);
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

        // Crear tabla
        String[] columnNames = {"ID", "Nombre", "Color", "Línea", "Capacidad", "Precio", "Stock", "Proveedor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productosTable = new JTable(tableModel);
        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosTable.getTableHeader().setBackground(new Color(220, 53, 69));
        productosTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(productosTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void actualizarTabla() {
        tableModel.setRowCount(0);
        List<Producto> productos = productoDAO.obtenerTodos();

        for (Producto p : productos) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getColor(),
                    p.getLinea(),
                    p.getCapacidad() + " " + p.getPresentacion(),
                    String.format("$%.2f", p.getPrecioVenta()),
                    p.getStock(),
                    p.getProveedorNombre()
            });
        }
    }

    private void buscar() {
        String termino = searchField.getText().trim();
        tableModel.setRowCount(0);

        List<Producto> productos;
        if (termino.isEmpty()) {
            productos = productoDAO.obtenerTodos();
        } else {
            productos = productoDAO.buscar(termino);
        }

        for (Producto p : productos) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getColor(),
                    p.getLinea(),
                    p.getCapacidad() + " " + p.getPresentacion(),
                    String.format("$%.2f", p.getPrecioVenta()),
                    p.getStock(),
                    p.getProveedorNombre()
            });
        }
    }

    private void abrirDialogoNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Producto", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField nombreField = new JTextField();
        JTextField colorField = new JTextField();
        JTextField lineaField = new JTextField();
        JTextField capacidadField = new JTextField();
        JTextField presentacionField = new JTextField();
        JTextField costoField = new JTextField();
        JTextField precioField = new JTextField();
        JTextField stockField = new JTextField();

        JComboBox<Proveedor> proveedorCombo = new JComboBox<>();
        for (Proveedor p : proveedorDAO.obtenerTodos()) {
            proveedorCombo.addItem(p);
        }

        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Color:"));
        panel.add(colorField);
        panel.add(new JLabel("Línea:"));
        panel.add(lineaField);
        panel.add(new JLabel("Capacidad:"));
        panel.add(capacidadField);
        panel.add(new JLabel("Presentación:"));
        panel.add(presentacionField);
        panel.add(new JLabel("Costo:"));
        panel.add(costoField);
        panel.add(new JLabel("Precio de Venta:"));
        panel.add(precioField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);
        panel.add(new JLabel("Proveedor:"));
        panel.add(proveedorCombo);

        JPanel buttonPanel = new JPanel();
        JButton guardarButton = new JButton("Guardar");
        JButton cancelarButton = new JButton("Cancelar");

        guardarButton.addActionListener(e -> {
            try {
                Producto producto = new Producto();
                producto.setNombre(nombreField.getText());
                producto.setColor(colorField.getText());
                producto.setLinea(lineaField.getText());
                producto.setCapacidad(Integer.parseInt(capacidadField.getText()));
                producto.setPresentacion(presentacionField.getText());
                producto.setCosto(Double.parseDouble(costoField.getText()));
                producto.setPrecioVenta(Double.parseDouble(precioField.getText()));
                producto.setStock(Integer.parseInt(stockField.getText()));
                producto.setProveedorId(((Proveedor) proveedorCombo.getSelectedItem()).getId());

                if (productoDAO.guardar(producto)) {
                    JOptionPane.showMessageDialog(dialog, "Producto guardado correctamente");
                    actualizarTabla();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al guardar producto", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Error en los datos ingresados", "Error", JOptionPane.ERROR_MESSAGE);
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
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int productoId = (int) tableModel.getValueAt(selectedRow, 0);
        Producto producto = productoDAO.obtenerPorId(productoId);

        // Similar al diálogo nuevo pero con los datos del producto
        // Por brevedad, se omite la implementación detallada
        JOptionPane.showMessageDialog(this, "Funcionalidad de edición en desarrollo");
    }

    private void eliminar() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int productoId = (int) tableModel.getValueAt(selectedRow, 0);
        int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            if (productoDAO.eliminar(productoId)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente");
                actualizarTabla();
            }
        }
    }
}