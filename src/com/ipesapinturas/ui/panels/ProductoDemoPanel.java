package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.ProductoDAO;
import com.ipesapinturas.models.Producto;
import com.ipesapinturas.ui.MainFrame;

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
    private static final String MENSAJE_ELIMINADA = "Pintura eliminada correctamente.";
    private static final String TITULO_BUSQUEDA = "Búsqueda de Producto";
    private static final String TITULO_ELIMINACION = "Eliminación de Producto";
    private static final String TITULO_ERROR = "Error";

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

        JLabel tituloLabel = new JLabel("🔍 Demostración CRUD de Pinturas");
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
        buscarButton.setIcon(MainFrame.ICONO_SEARCH);
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
        eliminarButton.setIcon(MainFrame.ICONO_BIN);
        eliminarButton.setBackground(new Color(220, 53, 69));
        eliminarButton.setForeground(Color.WHITE);
        eliminarButton.addActionListener(e -> eliminarPinturaPorId());
        eliminarIdField.addActionListener(e -> eliminarPinturaPorId());

        JButton refrescarButton = new JButton("🔄 Refrescar tabla");
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
        String idTexto = buscarIdField.getText().trim();

        if (idTexto.isEmpty()) {
            mostrarAdvertencia("Ingrese un ID para buscar.");
            buscarIdField.requestFocus();
            return;
        }

        Integer id;
        try {
            id = Integer.parseInt(idTexto);
            if (id <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            mostrarError("El ID debe ser numérico.");
            buscarIdField.selectAll();
            buscarIdField.requestFocus();
            return;
        }

        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            mostrarBusqueda(MENSAJE_NO_EXISTE);
            buscarIdField.selectAll();
            return;
        }

        mostrarExito("Producto encontrado correctamente.\n\n" + formatearProducto(producto));
        seleccionarProductoEnTabla(id);
    }

    private void eliminarPinturaPorId() {
        String idTexto = eliminarIdField.getText().trim();

        if (idTexto.isEmpty()) {
            mostrarAdvertencia("Ingrese un ID para eliminar.");
            eliminarIdField.requestFocus();
            return;
        }

        Integer id;
        try {
            id = Integer.parseInt(idTexto);
            if (id <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            mostrarError("El ID debe ser numérico.");
            eliminarIdField.selectAll();
            eliminarIdField.requestFocus();
            return;
        }

        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            mostrarBusqueda(MENSAJE_NO_EXISTE);
            eliminarIdField.selectAll();
            return;
        }

        int opcion = confirmar("¿Seguro que deseas eliminar esta pintura?\n\n" + formatearProducto(producto));
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            productoDAO.eliminar(id);
            mostrarExito(MENSAJE_ELIMINADA);
            eliminarIdField.setText("");
            refrescarTabla();
        } catch (SQLException ex) {
            mostrarError("Error al eliminar pintura:\n" + ex.getMessage());
        }
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, TITULO_BUSQUEDA,
                JOptionPane.PLAIN_MESSAGE, MainFrame.ICONO_CHECK);
    }

    private void mostrarBusqueda(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, TITULO_BUSQUEDA,
                JOptionPane.PLAIN_MESSAGE, MainFrame.ICONO_SEARCH);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, TITULO_ERROR,
                JOptionPane.PLAIN_MESSAGE, MainFrame.ICONO_CRISIS);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia",
                JOptionPane.PLAIN_MESSAGE, MainFrame.ICONO_SEARCH);
    }

    private int confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, TITULO_ELIMINACION,
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, MainFrame.ICONO_WARNING);
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

        mostrarExito("Tabla actualizada correctamente.\n(" + productos.size() + " registros)");
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

}

