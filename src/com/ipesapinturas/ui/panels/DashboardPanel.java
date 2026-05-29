package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.*;
import com.ipesapinturas.models.Usuario;
import com.ipesapinturas.models.Venta;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DashboardPanel extends JPanel {
    private Usuario usuarioActual;

    public DashboardPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 240));

        // Panel de título
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(45, 45, 48));
        add(titleLabel, BorderLayout.NORTH);

        // Panel de estadísticas
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 3, 15, 15));
        statsPanel.setOpaque(false);

        // Obtener datos
        ProductoDAO productoDAO = new ProductoDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        ProveedorDAO proveedorDAO = new ProveedorDAO();
        VentaDAO ventaDAO = new VentaDAO();

        int totalProductos = productoDAO.obtenerTodos().size();
        int totalClientes = clienteDAO.obtenerActivos().size();
        int totalProveedores = proveedorDAO.obtenerTodos().size();
        int ventasHoy = ventaDAO.obtenerPorFecha(LocalDate.now(), LocalDate.now()).size();
        double ingresosMes = ventaDAO.obtenerTotalVentasMes(LocalDate.now().getMonthValue(),
                LocalDate.now().getYear());
        int productosStockBajo = productoDAO.obtenerStockBajo(5).size();

        // Cards de estadísticas
        statsPanel.add(crearCard("📦 Productos", String.valueOf(totalProductos), new Color(52, 152, 219)));
        statsPanel.add(crearCard("👥 Clientes Activos", String.valueOf(totalClientes), new Color(46, 204, 113)));
        statsPanel.add(crearCard("🏭 Proveedores", String.valueOf(totalProveedores), new Color(155, 89, 182)));
        statsPanel.add(crearCard("🛒 Ventas Hoy", String.valueOf(ventasHoy), new Color(230, 126, 34)));
        statsPanel.add(crearCard("💰 Ingresos del Mes", formatearMoneda(ingresosMes), new Color(220, 53, 69)));
        statsPanel.add(crearCard("⚠️ Stock Bajo", String.valueOf(productosStockBajo), new Color(241, 196, 15)));

        return statsPanel;
    }

    private JPanel crearCard(String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        tituloLabel.setForeground(new Color(100, 100, 100));

        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valorLabel.setForeground(color);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        centerPanel.setOpaque(false);
        centerPanel.add(tituloLabel);
        centerPanel.add(valorLabel);

        card.add(centerPanel, BorderLayout.CENTER);

        // Agregar sombra simple
        card.setPreferredSize(new Dimension(200, 120));

        return card;
    }

    private String formatearMoneda(double monto) {
        return String.format("$%,.2f", monto);
    }
}