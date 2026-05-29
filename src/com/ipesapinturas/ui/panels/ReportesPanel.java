package com.ipesapinturas.ui.panels;

import com.ipesapinturas.dao.*;

import javax.swing.*;
import java.awt.*;

public class ReportesPanel extends JPanel {

    public ReportesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("Reportes y Análisis");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel reportesPanel = createReportesPanel();
        add(reportesPanel, BorderLayout.CENTER);
    }

    private JPanel createReportesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);

        ProductoDAO productoDAO = new ProductoDAO();
        VentaDAO ventaDAO = new VentaDAO();

        // Reporte de productos
        JPanel productoPanel = crearPanelReporte("Productos",
                "Total de productos en catálogo: " + productoDAO.obtenerTodos().size() +
                        "\nProductos con stock bajo: " + productoDAO.obtenerStockBajo(5).size(),
                new Color(52, 152, 219));
        panel.add(productoPanel);

        // Reporte de ventas
        JPanel ventasPanel = crearPanelReporte("Ventas del Mes",
                "Total de ventas: " + ventaDAO.obtenerTodas().size() +
                        "\nIngresos: $" + String.format("%.2f", ventaDAO.obtenerTotalVentasMes(
                        java.time.LocalDate.now().getMonthValue(),
                        java.time.LocalDate.now().getYear())),
                new Color(46, 204, 113));
        panel.add(ventasPanel);

        // Panel placeholder para más reportes
        JPanel morePanel = crearPanelReporte("Más Reportes",
                "Reportes adicionales en desarrollo",
                new Color(155, 89, 182));
        panel.add(morePanel);

        JPanel exportPanel = crearPanelReporte("Exportar Datos",
                "Funcionalidad de exportación a PDF/Excel",
                new Color(230, 126, 34));
        panel.add(exportPanel);

        return panel;
    }

    private JPanel crearPanelReporte(String titulo, String contenido, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setForeground(color);

        JLabel contenidoLabel = new JLabel("<html>" + contenido.replace("\n", "<br>") + "</html>");
        contenidoLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(contenidoLabel, BorderLayout.CENTER);

        return panel;
    }
}