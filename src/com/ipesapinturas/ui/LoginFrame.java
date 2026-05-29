package com.ipesapinturas.ui;

import com.ipesapinturas.dao.UsuarioDAO;
import com.ipesapinturas.models.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private static final boolean DESACTIVAR_LOGIN_TEMPORAL = true;

    private JTextField usuarioField;
    private JPasswordField contrasenaField;
    private JButton loginButton;
    private JButton salirButton;
    private Usuario usuarioLogueado;

    public LoginFrame() {
        setTitle("IPESA Pinturas - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 45, 48));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de logo/título
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(45, 45, 48));
        JLabel titleLabel = new JLabel("IPESA Pinturas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        headerPanel.add(titleLabel);

        // Panel de formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(2, 2, 10, 10));
        formPanel.setBackground(new Color(45, 45, 48));

        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setForeground(Color.WHITE);
        usuarioField = new JTextField(15);
        usuarioField.setText("cesar.martinez");

        JLabel contrasenaLabel = new JLabel("Contraseña:");
        contrasenaLabel.setForeground(Color.WHITE);
        contrasenaField = new JPasswordField(15);
        contrasenaField.setText("admin123");

        formPanel.add(usuarioLabel);
        formPanel.add(usuarioField);
        formPanel.add(contrasenaLabel);
        formPanel.add(contrasenaField);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(45, 45, 48));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        loginButton = new JButton("Ingresar");
        loginButton.setBackground(new Color(220, 53, 69));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.addActionListener(this::autenticar);

        salirButton = new JButton("Salir");
        salirButton.setBackground(new Color(108, 117, 125));
        salirButton.setForeground(Color.WHITE);
        salirButton.setFont(new Font("Arial", Font.BOLD, 12));
        salirButton.setPreferredSize(new Dimension(100, 35));
        salirButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(salirButton);

        // Agregar paneles
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void autenticar(ActionEvent e) {
        String usuario = usuarioField.getText().trim();
        String contrasena = new String(contrasenaField.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioLogueado = usuarioDAO.autenticar(usuario, contrasena);

        if (usuarioLogueado != null) {
            JOptionPane.showMessageDialog(this, "¡Bienvenido " + usuarioLogueado.getNombreCompleto() + "!");

            // Abrir ventana principal
            MainFrame mainFrame = new MainFrame(usuarioLogueado);
            mainFrame.setVisible(true);

            // Cerrar login
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos",
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            contrasenaField.setText("");
            usuarioField.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (DESACTIVAR_LOGIN_TEMPORAL) {
                Usuario usuarioTemporal = new Usuario();
                usuarioTemporal.setId(1);
                usuarioTemporal.setNombreCompleto("Usuario Temporal");
                usuarioTemporal.setUsuario("temporal");
                usuarioTemporal.setRol("Administrador");
                usuarioTemporal.setAcceso("Activo");

                MainFrame mainFrame = new MainFrame(usuarioTemporal);
                mainFrame.setVisible(true);
                return;
            }

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
