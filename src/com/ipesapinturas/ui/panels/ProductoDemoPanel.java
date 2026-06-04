package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.ProductoDAO;
import com.ipesapinturas.models.Producto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

public class ProductoDemoPanel extends JPanel {
    private static final String MENSAJE_NO_EXISTE = "No existe una pintura con ese ID.";
    private static final String MENSAJE_REGISTROS_ASOCIADOS =
            "No se puede eliminar porque la pintura tiene registros asociados.";
    private static final String MENSAJE_ELIMINADA = "Pintura eliminada correctamente.";

    private final ProductoDAO productoDAO;
    private final JTextField buscarIdField;
    private final JTextField eliminarIdField;
    private final JTextArea salidaArea;
    private final DefaultTableModel tableModel;
    private final JTable productosTable;

    public ProductoDemoPanel() {
        this.productoDAO = new ProductoDAO();
        this.buscarIdField = new JTextField(10);
        this.eliminarIdField = new JTextField(10);
        this.salidaArea = new JTextArea(10, 40);
        this.tableModel = crearModeloTabla();
        this.productosTable = new JTable(tableModel);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 240));

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentro(), BorderLayout.CENTER);

        refrescarTabla();
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel tituloLabel = new JLabel("Demostracion CRUD de Pinturas");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel controlesPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        controlesPanel.setOpaque(false);
        controlesPanel.add(crearFilaBusqueda());
        controlesPanel.add(crearFilaEliminacion());

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(controlesPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearFilaBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        JButton buscarButton = new JButton("Buscar producto por ID");
        buscarButton.addActionListener(e -> buscarProductoPorId());
        buscarIdField.addActionListener(e -> buscarProductoPorId());

        panel.add(new JLabel("ID a buscar:"));
        panel.add(buscarIdField);
        panel.add(buscarButton);
        return panel;
    }

    private JPanel crearFilaEliminacion() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        JButton eliminarButton = new JButton("Eliminar pintura");
        eliminarButton.setBackground(new Color(220, 53, 69));
        eliminarButton.setForeground(Color.WHITE);
        eliminarButton.addActionListener(e -> eliminarPinturaPorId());
        eliminarIdField.addActionListener(e -> eliminarPinturaPorId());

        JButton refrescarButton = new JButton("Refrescar tabla");
        refrescarButton.addActionListener(e -> refrescarTabla());

        panel.add(new JLabel("ID a eliminar:"));
        panel.add(eliminarIdField);
        panel.add(eliminarButton);
        panel.add(refrescarButton);
        return panel;
    }

    private JPanel crearPanelCentro() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);

        salidaArea.setEditable(false);
        salidaArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        salidaArea.setLineWrap(false);

        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosTable.getTableHeader().setBackground(new Color(220, 53, 69));
        productosTable.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(salidaArea));
        panel.add(new JScrollPane(productosTable));
        return panel;
    }

    private DefaultTableModel crearModeloTabla() {
        String[] columnas = {
                "idPintura",
                "claveClasificacion",
                "idProveedor",
                "nombre",
                "color",
                "capacidad",
                "stock",
                "costo",
                "presentacion"
        };

        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void buscarProductoPorId() {
        Integer id = leerId(buscarIdField, "Ingrese un ID valido para buscar.");
        if (id == null) {
            return;
        }

        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            mostrarSalida(MENSAJE_NO_EXISTE);
            return;
        }

        mostrarSalida(formatearProducto(producto));
        seleccionarProductoEnTabla(id);
    }

    private void eliminarPinturaPorId() {
        Integer id = leerId(eliminarIdField, "Ingrese un ID valido para eliminar.");
        if (id == null) {
            return;
        }

        try {
            productoDAO.eliminar(id);
            mostrarSalida(MENSAJE_ELIMINADA);
            refrescarTabla();
        } catch (SQLException ex) {
            if (MENSAJE_REGISTROS_ASOCIADOS.equals(ex.getMessage())) {
                mostrarSalida(MENSAJE_REGISTROS_ASOCIADOS);
            } else {
                mostrarSalida("Error al eliminar pintura: " + ex.getMessage());
            }
        }
    }

    private Integer leerId(JTextField field, String mensajeError) {
        String texto = field.getText().trim();
        try {
            int id = Integer.parseInt(texto);
            if (id <= 0) {
                throw new NumberFormatException("El ID debe ser mayor a cero.");
            }
            return id;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, mensajeError, "ID invalido", JOptionPane.WARNING_MESSAGE);
            field.selectAll();
            field.requestFocus();
            return null;
        }
    }

    private void refrescarTabla() {
        tableModel.setRowCount(0);
        List<Producto> productos = productoDAO.listarTodo();

        for (Producto producto : productos) {
            tableModel.addRow(new Object[]{
                    producto.getIdPintura(),
                    producto.getClaveClasificacion(),
                    producto.getIdProveedor(),
                    producto.getNombre(),
                    producto.getColor(),
                    producto.getCapacidad(),
                    producto.getStock(),
                    producto.getCostoDecimal(),
                    producto.getPresentacion()
            });
        }
    }

    private void seleccionarProductoEnTabla(int idPintura) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Object valor = tableModel.getValueAt(row, 0);
            if (valor instanceof Integer && (Integer) valor == idPintura) {
                productosTable.setRowSelectionInterval(row, row);
                productosTable.scrollRectToVisible(productosTable.getCellRect(row, 0, true));
                return;
            }
        }
    }

    private String formatearProducto(Producto producto) {
        return "Datos de la pintura\n"
                + "idPintura: " + producto.getIdPintura() + "\n"
                + "claveClasificacion: " + producto.getClaveClasificacion() + "\n"
                + "idProveedor: " + producto.getIdProveedor() + "\n"
                + "nombre: " + producto.getNombre() + "\n"
                + "color: " + producto.getColor() + "\n"
                + "capacidad: " + producto.getCapacidad() + "\n"
                + "stock: " + producto.getStock() + "\n"
                + "costo: " + producto.getCostoDecimal() + "\n"
                + "presentacion: " + producto.getPresentacion();
    }

    private void mostrarSalida(String mensaje) {
        salidaArea.setText(mensaje);
    }
}
