package com.ipesapinturas.ui;

import com.ipesapinturas.models.Usuario;
import com.ipesapinturas.ui.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final ImageIcon iconoPlus = new ImageIcon("src/com/ipesapinturas/ui/icons/plus.png");
    private static final ImageIcon iconoEdit = new ImageIcon("src/com/ipesapinturas/ui/icons/edit.png");
    private static final ImageIcon iconoBin = new ImageIcon("src/com/ipesapinturas/ui/icons/bin.png");
    private static final ImageIcon iconoSearch = new ImageIcon("src/com/ipesapinturas/ui/icons/search.png");
    private static final ImageIcon iconoCheck = new ImageIcon("src/com/ipesapinturas/ui/icons/check.png");
    private static final ImageIcon iconoCrisis = new ImageIcon("src/com/ipesapinturas/ui/icons/crisis.png");
    private static final ImageIcon iconoClose      = new ImageIcon("src/com/ipesapinturas/ui/icons/close.png");
    private static final ImageIcon iconoHome       = new ImageIcon("src/com/ipesapinturas/ui/icons/home-button.png");
    private static final ImageIcon iconoBox        = new ImageIcon("src/com/ipesapinturas/ui/icons/box.png");
    private static final ImageIcon iconoHuman      = new ImageIcon("src/com/ipesapinturas/ui/icons/human.png");
    private static final ImageIcon iconoTruck      = new ImageIcon("src/com/ipesapinturas/ui/icons/truck.png");
    private static final ImageIcon iconoCart       = new ImageIcon("src/com/ipesapinturas/ui/icons/cart.png");
    private static final ImageIcon iconoStatistics = new ImageIcon("src/com/ipesapinturas/ui/icons/statistics.png");
    private static final ImageIcon iconoWarning    = new ImageIcon("src/com/ipesapinturas/ui/icons/warning.png");

    public static final ImageIcon ICONO_PLUS = iconoPlus;
    public static final ImageIcon ICONO_EDIT = iconoEdit;
    public static final ImageIcon ICONO_BIN = iconoBin;
    public static final ImageIcon ICONO_SEARCH = iconoSearch;
    public static final ImageIcon ICONO_CHECK = iconoCheck;
    public static final ImageIcon ICONO_CRISIS = iconoCrisis;
    public static final ImageIcon ICONO_CLOSE      = iconoClose;
    public static final ImageIcon ICONO_HOME       = iconoHome;
    public static final ImageIcon ICONO_BOX        = iconoBox;
    public static final ImageIcon ICONO_HUMAN      = iconoHuman;
    public static final ImageIcon ICONO_TRUCK      = iconoTruck;
    public static final ImageIcon ICONO_CART       = iconoCart;
    public static final ImageIcon ICONO_STATISTICS = iconoStatistics;
    public static final ImageIcon ICONO_WARNING    = iconoWarning;

    private Usuario usuarioActual;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel usuarioLabel;

    private JButton btnMenuHome;
    private JButton btnMenuProductos;
    private JButton btnMenuClientes;
    private JButton btnMenuProveedores;
    private JButton btnMenuVentas;
    private JButton btnMenuReportes;

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
        contentPanel.add(new ProductoDemoPanel(), "demoProductos");
        contentPanel.add(new ClientesPanel(), "clientes");
        contentPanel.add(new ProveedoresPanel(), "proveedores");
        contentPanel.add(new VentasPanel(usuarioActual), "ventas");
        contentPanel.add(new ReportesPanel(), "reportes");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        initComponentIcons();
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

        JButton salirButton = new JButton("🚪 Salir");
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
        btnMenuHome        = agregarBotonMenu(menuPanel, "Dashboard",      "dashboard");
        btnMenuProductos   = agregarBotonMenu(menuPanel, "Productos",       "productos");
                             agregarBotonMenu(menuPanel, "Demo Productos",  "demoProductos");
        btnMenuClientes    = agregarBotonMenu(menuPanel, "Clientes",        "clientes");
        btnMenuProveedores = agregarBotonMenu(menuPanel, "Proveedores",     "proveedores");
        btnMenuVentas      = agregarBotonMenu(menuPanel, "Ventas",          "ventas");
        btnMenuReportes    = agregarBotonMenu(menuPanel, "Reportes",        "reportes");

        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    private JButton agregarBotonMenu(JPanel menuPanel, String texto, String panelName) {
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
        return button;
    }

    private void initComponentIcons() {
        btnMenuHome.setIcon(iconoHome);
        btnMenuProductos.setIcon(iconoBox);
        btnMenuClientes.setIcon(iconoHuman);
        btnMenuProveedores.setIcon(iconoTruck);
        btnMenuVentas.setIcon(iconoCart);
        btnMenuReportes.setIcon(iconoStatistics);
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                ICONO_WARNING);

        if (opcion == JOptionPane.YES_OPTION) {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }
}

