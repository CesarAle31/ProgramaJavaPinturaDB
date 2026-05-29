package com.ipesapinturas.ui;

import com.ipesapinturas.models.Usuario;
import com.ipesapinturas.ui.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Usuario usuarioActual;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel usuarioLabel;

    public MainFrame(Usuario usuario) {
        this.usuarioActual = usuario;

        setTitle("IPESA Pinturas - Punto de Venta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel superior (Header)
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel lateral (Menú)
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Panel central (Contenido)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(240, 240, 240));

        // Agregar paneles al CardLayout
        contentPanel.add(new DashboardPanel(usuarioActual), "dashboard");
        contentPanel.add(new ProductosPanel(), "productos");
        contentPanel.add(new ClientesPanel(), "clientes");
        contentPanel.add(new ProveedoresPanel(), "proveedores");
        contentPanel.add(new VentasPanel(usuarioActual), "ventas");
        contentPanel.add(new ReportesPanel(), "reportes");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(220, 53, 69));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setPreferredSize(new Dimension(0, 60));

        JLabel titleLabel = new JLabel("IPESA Pinturas - Sistema de Punto de Venta");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(220, 53, 69));

        usuarioLabel = new JLabel("Usuario: " + usuarioActual.getNombreCompleto());
        usuarioLabel.setForeground(Color.WHITE);
        usuarioLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton salirButton = new JButton("Salir");
        salirButton.setBackground(new Color(180, 40, 55));
        salirButton.setForeground(Color.WHITE);
        salirButton.setFocusPainted(false);
        salirButton.addActionListener(e -> cerrarSesion());

        rightPanel.add(usuarioLabel);
        rightPanel.add(Box.createHorizontalStrut(20));
        rightPanel.add(salirButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(45, 45, 48));
        menuPanel.setPreferredSize(new Dimension(200, 0));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Logo
        JLabel logoLabel = new JLabel("IPESA");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(new Color(220, 53, 69));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(logoLabel);
        menuPanel.add(Box.createVerticalStrut(20));

        // Botones del menú
        agregarBotonMenu(menuPanel, "📊 Dashboard", "dashboard");
        agregarBotonMenu(menuPanel, "🎨 Productos", "productos");
        agregarBotonMenu(menuPanel, "👥 Clientes", "clientes");
        agregarBotonMenu(menuPanel, "🏭 Proveedores", "proveedores");
        agregarBotonMenu(menuPanel, "💳 Ventas", "ventas");
        agregarBotonMenu(menuPanel, "📈 Reportes", "reportes");

        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    private void agregarBotonMenu(JPanel menuPanel, String texto, String panelName) {
        JButton button = new JButton(texto);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 45));
        button.setBackground(new Color(60, 60, 65));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 53, 69));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 65));
            }
        });

        button.addActionListener(e -> {
            cardLayout.show(contentPanel, panelName);
        });

        menuPanel.add(button);
        menuPanel.add(Box.createVerticalStrut(5));
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }
}