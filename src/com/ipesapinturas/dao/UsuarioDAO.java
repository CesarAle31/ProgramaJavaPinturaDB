package com.ipesapinturas.dao;

import com.ipesapinturas.models.Usuario;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Obtiene un usuario por usuario y contraseña (login)
     */
    public Usuario autenticar(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, contrasena);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapearResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_completo";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Obtiene un usuario por ID
     */
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Guarda un usuario nuevo
     */
    public boolean guardar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_completo, usuario, contrasena, rol, telefono, acceso) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreCompleto());
            pstmt.setString(2, usuario.getUsuario());
            pstmt.setString(3, usuario.getContrasena());
            pstmt.setString(4, usuario.getRol());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setString(6, usuario.getAcceso());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza un usuario existente
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre_completo = ?, usuario = ?, contrasena = ?, " +
                "rol = ?, telefono = ?, acceso = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreCompleto());
            pstmt.setString(2, usuario.getUsuario());
            pstmt.setString(3, usuario.getContrasena());
            pstmt.setString(4, usuario.getRol());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setString(6, usuario.getAcceso());
            pstmt.setInt(7, usuario.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un usuario
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Usuario
     */
    private Usuario mapearResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setRol(rs.getString("rol"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setAcceso(rs.getString("acceso"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            usuario.setFechaCreacion(ts.toLocalDateTime());
        }

        return usuario;
    }
}