package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.VentaDAO;
import com.ipesapinturas.models.Usuario;
import com.ipesapinturas.models.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentasPanel extends JPanel {
    private JTable ventasTable;
    private DefaultTableModel tableModel;
    private VentaDAO ventaDAO;
    private Usuario usuarioActual;

    public VentasPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        ventaDAO = new VentaDAO();

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

        JLabel titleLabel = new JLabel("Historial de Ventas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton nuevaVentaButton = new JButton("+ Nueva Venta");
        nuevaVentaButton.setBackground(new Color(220, 53, 69));
        nuevaVentaButton.setForeground(Color.WHITE);
        nuevaVentaButton.addActionListener(e -> abrirNuevaVenta());

        JButton detallesButton = new JButton("📋 Ver Detalles");
        detallesButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo"));

        actionPanel.add(nuevaVentaButton);
        actionPanel.add(detallesButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        return topPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        String[] columnNames = {"Folio", "Fecha", "Cliente", "Empleado", "Total", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ventasTable = new JTable(tableModel);
        ventasTable.getTableHeader().setBackground(new Color(220, 53, 69));
        ventasTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(ventasTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void actualizarTabla() {
        tableModel.setRowCount(0);
        List<Venta> ventas = ventaDAO.obtenerTodas();

        for (Venta v : ventas) {
            tableModel.addRow(new Object[]{
                    v.getFolio(),
                    v.getFecha(),
                    v.getClienteNombre(),
                    v.getUsuarioNombre(),
                    String.format("$%.2f", v.getTotal()),
                    v.getEstado()
            });
        }
    }

    private void abrirNuevaVenta() {
        com.ipesapinturas.ui.NuevaVentaFrame nuevaVentaFrame =
                new com.ipesapinturas.ui.NuevaVentaFrame(usuarioActual);
        nuevaVentaFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                actualizarTabla();
            }
        });
        nuevaVentaFrame.setVisible(true);
    }
}
