package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.ProductoDAO;
import com.ipesapinturas.dao.ProveedorDAO;
import com.ipesapinturas.models.Producto;
import com.ipesapinturas.models.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProductosPanel extends JPanel {
    private JTable productosTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField idSearchField;
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
                idSearchField.setText("");
                buscar();
            }
        });

        JLabel idSearchLabel = new JLabel("ID:");
        idSearchField = new JTextField(8);
        idSearchField.addActionListener(e -> buscarPorId());

        JButton buscarIdButton = new JButton("Buscar ID");
        buscarIdButton.addActionListener(e -> buscarPorId());

        JButton limpiarButton = new JButton("Limpiar");
        limpiarButton.addActionListener(e -> {
            searchField.setText("");
            idSearchField.setText("");
            actualizarTabla();
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
            agregarProductoATabla(p);
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
            agregarProductoATabla(p);
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
            Producto producto = productoDAO.obtenerPorId(id);
            searchField.setText("");
            tableModel.setRowCount(0);

            if (producto != null) {
                agregarProductoATabla(producto);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontro ningun producto con ese ID",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numerico",
                    "ID invalido", JOptionPane.WARNING_MESSAGE);
            idSearchField.selectAll();
            idSearchField.requestFocus();
        }
    }

    private void agregarProductoATabla(Producto p) {
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

    private Producto buscarProductoPorIdParaAccion(String accion) {
        String idTexto = idSearchField.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del producto para " + accion,
                    "ID requerido", JOptionPane.INFORMATION_MESSAGE);
            idSearchField.requestFocus();
            return null;
        }

        try {
            int id = Integer.parseInt(idTexto);
            Producto producto = productoDAO.obtenerPorId(id);
            searchField.setText("");
            tableModel.setRowCount(0);

            if (producto == null) {
                JOptionPane.showMessageDialog(this, "No se encontro ningun producto con ese ID",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                idSearchField.selectAll();
                idSearchField.requestFocus();
                return null;
            }

            agregarProductoATabla(producto);
            productosTable.setRowSelectionInterval(0, 0);
            return producto;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID numerico",
                    "ID invalido", JOptionPane.WARNING_MESSAGE);
            idSearchField.selectAll();
            idSearchField.requestFocus();
            return null;
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
                producto.setCapacidad(capacidadField.getText().trim());
                producto.setPresentacion(presentacionField.getText());
                producto.setCosto(Double.parseDouble(costoField.getText()));
                producto.setPrecioVenta(Double.parseDouble(precioField.getText()));
                producto.setStock(Integer.parseInt(stockField.getText()));
                producto.setProveedorId(((Proveedor) proveedorCombo.getSelectedItem()).getId());

                productoDAO.agregar(producto);
                JOptionPane.showMessageDialog(dialog, "Producto guardado correctamente");
                refrescarTablaProductos();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Error en los datos ingresados", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
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
        Producto producto = buscarProductoPorIdParaAccion("editar");
        if (producto == null) {
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Producto", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField nombreField = new JTextField(producto.getNombre());
        JTextField colorField = new JTextField(producto.getColor());
        JTextField lineaField = new JTextField(producto.getLinea());
        JTextField capacidadField = new JTextField(String.valueOf(producto.getCapacidad()));
        JTextField presentacionField = new JTextField(producto.getPresentacion());
        JTextField costoField = new JTextField(String.valueOf(producto.getCosto()));
        JTextField precioField = new JTextField(String.valueOf(producto.getPrecioVenta()));
        JTextField stockField = new JTextField(String.valueOf(producto.getStock()));

        JComboBox<Proveedor> proveedorCombo = new JComboBox<>();
        for (Proveedor p : proveedorDAO.obtenerTodos()) {
            proveedorCombo.addItem(p);
        }
        seleccionarProveedor(proveedorCombo, producto.getProveedorId());

        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Color:"));
        panel.add(colorField);
        panel.add(new JLabel("Linea:"));
        panel.add(lineaField);
        panel.add(new JLabel("Capacidad:"));
        panel.add(capacidadField);
        panel.add(new JLabel("Presentacion:"));
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
        JButton guardarButton = new JButton("Actualizar");
        JButton cancelarButton = new JButton("Cancelar");

        guardarButton.addActionListener(e -> {
            try {
                if (nombreField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "El nombre del producto es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                    nombreField.requestFocus();
                    return;
                }

                Producto productoActualizado = new Producto();
                productoActualizado.setId(producto.getId());
                productoActualizado.setNombre(nombreField.getText().trim());
                productoActualizado.setColor(colorField.getText().trim());
                productoActualizado.setLinea(lineaField.getText().trim());
                productoActualizado.setCapacidad(capacidadField.getText().trim());
                productoActualizado.setPresentacion(presentacionField.getText().trim());
                productoActualizado.setCosto(Double.parseDouble(costoField.getText().trim()));
                productoActualizado.setPrecioVenta(Double.parseDouble(precioField.getText().trim()));
                productoActualizado.setStock(Integer.parseInt(stockField.getText().trim()));
                productoActualizado.setProveedorId(obtenerProveedorIdSeleccionado(proveedorCombo));

                productoDAO.actualizar(productoActualizado);
                JOptionPane.showMessageDialog(dialog, "Producto actualizado correctamente");
                idSearchField.setText(String.valueOf(producto.getId()));
                buscarPorId();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Costo, precio y stock deben ser valores numericos validos",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
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

    private void seleccionarProveedor(JComboBox<Proveedor> proveedorCombo, int proveedorId) {
        for (int i = 0; i < proveedorCombo.getItemCount(); i++) {
            Proveedor proveedor = proveedorCombo.getItemAt(i);
            if (proveedor != null && proveedor.getId() == proveedorId) {
                proveedorCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private int obtenerProveedorIdSeleccionado(JComboBox<Proveedor> proveedorCombo) {
        Proveedor proveedor = (Proveedor) proveedorCombo.getSelectedItem();
        return proveedor != null ? proveedor.getId() : 0;
    }

    private void eliminar() {
        Producto producto = buscarProductoPorIdParaAccion("eliminar");
        if (producto == null) {
            return;
        }

        String mensaje = "Esta seguro de eliminar el producto ID " + producto.getId()
                + " - " + producto.getNombre() + "?";
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                productoDAO.eliminar(producto.getId());
                JOptionPane.showMessageDialog(this, "Pintura eliminada correctamente");
                idSearchField.setText("");
                refrescarTablaProductos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refrescarTablaProductos() {
        actualizarTabla();
    }
}
