package com.ipesapinturas.ui;

import com.ipesapinturas.dao.ClienteDAO;
import com.ipesapinturas.dao.ProductoDAO;
import com.ipesapinturas.models.Cliente;
import com.ipesapinturas.models.Producto;
import com.ipesapinturas.models.ProductoSeleccionado;
import com.ipesapinturas.models.Usuario;
import com.ipesapinturas.models.Venta;
import com.ipesapinturas.services.VentaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NuevaVentaFrame extends JFrame {
    private final Usuario usuarioActual;
    private final ClienteDAO clienteDAO;
    private final ProductoDAO productoDAO;
    private final VentaService ventaService;
    private final List<ProductoSeleccionado> carrito;

    private JLabel folioLabel;
    private JLabel fechaLabel;
    private JLabel vendedorLabel;
    private JComboBox<Cliente> clienteCombo;
    private JComboBox<Producto> productoCombo;
    private JSpinner cantidadSpinner;
    private DefaultTableModel tableModel;
    private JTable carritoTable;
    private JLabel subtotalLabel;
    private JLabel descuentoLabel;
    private JLabel totalLabel;

    public NuevaVentaFrame(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
        this.ventaService = new VentaService();
        this.carrito = new ArrayList<>();

        setTitle("Nueva Venta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        mainPanel.add(crearPanelDatos(), BorderLayout.NORTH);
        mainPanel.add(crearPanelCarrito(), BorderLayout.CENTER);
        mainPanel.add(crearPanelTotales(), BorderLayout.SOUTH);

        add(mainPanel);

        cargarDatos();
        actualizarTotales();
    }

    private JPanel crearPanelDatos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Informacion de venta"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        folioLabel = new JLabel(ventaService.obtenerProximoFolio());
        fechaLabel = new JLabel(LocalDate.now().toString());
        vendedorLabel = new JLabel(usuarioActual != null ? usuarioActual.getNombreCompleto() : "N/A");
        clienteCombo = new JComboBox<>();
        productoCombo = new JComboBox<>();
        cantidadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

        JButton agregarButton = crearBotonPrimario("Agregar al carrito");
        agregarButton.addActionListener(e -> agregarProducto());

        agregarCampo(panel, gbc, 0, 0, "Folio:", folioLabel);
        agregarCampo(panel, gbc, 1, 0, "Fecha:", fechaLabel);
        agregarCampo(panel, gbc, 0, 1, "Vendedor:", vendedorLabel);
        agregarCampo(panel, gbc, 1, 1, "Cliente:", clienteCombo);
        agregarCampo(panel, gbc, 0, 2, "Producto:", productoCombo);
        agregarCampo(panel, gbc, 1, 2, "Cantidad:", cantidadSpinner);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(agregarButton, gbc);

        return panel;
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Carrito de compras"));

        String[] columnas = {"Producto", "Color", "Precio", "Cantidad", "Subtotal"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        carritoTable = new JTable(tableModel);
        carritoTable.getTableHeader().setBackground(new Color(220, 53, 69));
        carritoTable.getTableHeader().setForeground(Color.WHITE);
        panel.add(new JScrollPane(carritoTable), BorderLayout.CENTER);

        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        accionesPanel.setOpaque(false);

        JButton quitarButton = new JButton("Quitar producto");
        quitarButton.addActionListener(e -> quitarProductoSeleccionado());

        JButton limpiarButton = new JButton("Limpiar carrito");
        limpiarButton.addActionListener(e -> limpiarCarrito());

        accionesPanel.add(quitarButton);
        accionesPanel.add(limpiarButton);
        panel.add(accionesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelTotales() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel labelsPanel = new JPanel(new GridBagLayout());
        labelsPanel.setOpaque(false);

        subtotalLabel = new JLabel("$0.00");
        descuentoLabel = new JLabel("$0.00");
        totalLabel = new JLabel("$0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalLabel.setForeground(new Color(220, 53, 69));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.anchor = GridBagConstraints.EAST;

        agregarTotal(labelsPanel, gbc, 0, "Subtotal:", subtotalLabel);
        agregarTotal(labelsPanel, gbc, 1, "Descuento:", descuentoLabel);
        agregarTotal(labelsPanel, gbc, 2, "TOTAL:", totalLabel);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botonesPanel.setOpaque(false);

        JButton guardarButton = crearBotonPrimario("Guardar venta");
        guardarButton.addActionListener(e -> guardarVenta());

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dispose());

        botonesPanel.add(guardarButton);
        botonesPanel.add(cancelarButton);

        panel.add(labelsPanel, BorderLayout.CENTER);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarDatos() {
        clienteCombo.removeAllItems();
        for (Cliente cliente : clienteDAO.obtenerActivos()) {
            clienteCombo.addItem(cliente);
        }

        productoCombo.removeAllItems();
        for (Producto producto : productoDAO.obtenerTodos()) {
            if (producto.getStock() > 0) {
                productoCombo.addItem(producto);
            }
        }
    }

    private void agregarProducto() {
        Producto producto = (Producto) productoCombo.getSelectedItem();
        if (producto == null) {
            mostrarError("Debe seleccionar un producto");
            return;
        }

        int cantidad = (Integer) cantidadSpinner.getValue();
        ProductoSeleccionado existente = buscarEnCarrito(producto.getId());
        int cantidadActual = existente != null ? existente.getCantidad() : 0;

        if (cantidadActual + cantidad > producto.getStock()) {
            mostrarError("Stock insuficiente. Disponible: " + producto.getStock());
            return;
        }

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + cantidad);
        } else {
            carrito.add(new ProductoSeleccionado(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getColor(),
                    producto.getPrecioVenta(),
                    cantidad
            ));
        }

        cantidadSpinner.setValue(1);
        actualizarTablaCarrito();
        actualizarTotales();
    }

    private void quitarProductoSeleccionado() {
        int row = carritoTable.getSelectedRow();
        if (row < 0) {
            mostrarError("Seleccione un producto del carrito");
            return;
        }

        carrito.remove(row);
        actualizarTablaCarrito();
        actualizarTotales();
    }

    private void limpiarCarrito() {
        carrito.clear();
        actualizarTablaCarrito();
        actualizarTotales();
    }

    private void guardarVenta() {
        Cliente cliente = (Cliente) clienteCombo.getSelectedItem();
        if (cliente == null) {
            mostrarError("Debe seleccionar un cliente");
            return;
        }
        if (carrito.isEmpty()) {
            mostrarError("El carrito esta vacio");
            return;
        }

        Venta venta = new Venta();
        venta.setFolio(folioLabel.getText());
        venta.setFecha(LocalDate.now());
        venta.setClienteId(cliente.getId());
        venta.setClienteNombre(cliente.getNombreCompleto());
        venta.setUsuarioId(usuarioActual.getId());
        venta.setUsuarioNombre(usuarioActual.getNombreCompleto());
        venta.setEstado("Completada");

        try {
            ventaService.crearVentaCompleta(venta, new ArrayList<>(carrito));
            JOptionPane.showMessageDialog(this,
                    "Venta guardada correctamente.\nFolio: " + venta.getFolio(),
                    "Venta completada",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void actualizarTablaCarrito() {
        tableModel.setRowCount(0);
        for (ProductoSeleccionado producto : carrito) {
            tableModel.addRow(new Object[]{
                    producto.getNombre(),
                    producto.getColor(),
                    String.format("$%.2f", producto.getPrecioUnitario()),
                    producto.getCantidad(),
                    String.format("$%.2f", producto.getSubtotal())
            });
        }
    }

    private void actualizarTotales() {
        double subtotal = ventaService.calcularSubtotal(carrito);
        int cantidad = ventaService.calcularCantidadTotal(carrito);
        double descuento = ventaService.calcularDescuento(cantidad, subtotal);
        double total = subtotal - descuento;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        descuentoLabel.setText(String.format("$%.2f", descuento));
        totalLabel.setText(String.format("$%.2f", total));
    }

    private ProductoSeleccionado buscarEnCarrito(int productoId) {
        for (ProductoSeleccionado producto : carrito) {
            if (producto.getProductoId() == productoId) {
                return producto;
            }
        }
        return null;
    }

    private JButton crearBotonPrimario(String texto) {
        JButton button = new JButton(texto);
        button.setBackground(new Color(220, 53, 69));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int col, int row,
                              String etiqueta, java.awt.Component campo) {
        gbc.gridwidth = 1;
        gbc.gridx = col * 2;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = col * 2 + 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private void agregarTotal(JPanel panel, GridBagConstraints gbc, int row,
                              String etiqueta, JLabel valor) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        panel.add(valor, gbc);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
