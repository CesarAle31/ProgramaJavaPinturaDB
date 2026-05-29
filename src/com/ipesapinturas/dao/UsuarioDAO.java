package com.ipesapinturas.dao;

import com.ipesapinturas.models.Usuario;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private static final String SELECT_BASE =
            "SELECT u.idEmpleado AS id, " +
                    "TRIM(CONCAT_WS(' ', e.nombre, e.apellidoP, e.apellidoM)) AS nombre_completo, " +
                    "u.usuario, u.password_hash AS contrasena, r.nombre AS rol, " +
                    "CAST(te.telefonoEmpleado AS CHAR) AS telefono, " +
                    "CASE WHEN u.activo = 1 AND e.activo = 1 THEN 'Activo' ELSE 'Inactivo' END AS acceso, " +
                    "u.fecha_creacion " +
                    "FROM usuario u " +
                    "INNER JOIN empleado e ON u.idEmpleado = e.idEmpleado " +
                    "INNER JOIN rol r ON u.idRol = r.idRol " +
                    "LEFT JOIN telefonoempleado te ON e.idEmpleado = te.idEmpleado ";

    public Usuario autenticar(String usuario, String contrasena) {
        String sql = SELECT_BASE + "WHERE u.usuario = ? AND u.activo = 1 AND e.activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Usuario encontrado = mapearResultSet(rs);
                if (contrasena != null && contrasena.equals(encontrado.getContrasena())) {
                    return encontrado;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY nombre_completo";

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

    public Usuario obtenerPorId(int id) {
        String sql = SELECT_BASE + "WHERE u.idEmpleado = ?";
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

    public boolean guardar(Usuario usuario) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            int idEmpleado = crearEmpleado(conn, usuario);
            int idRol = obtenerOCrearRol(conn, usuario.getRol());
            crearUsuario(conn, usuario, idEmpleado, idRol);
            guardarTelefono(conn, idEmpleado, usuario.getTelefono());

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean actualizar(Usuario usuario) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            String[] nombre = separarNombre(usuario.getNombreCompleto());
            String empleadoSql = "UPDATE empleado SET nombre = ?, apellidoP = ?, apellidoM = ?, activo = ? " +
                    "WHERE idEmpleado = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(empleadoSql)) {
                pstmt.setString(1, nombre[0]);
                pstmt.setString(2, nombre[1]);
                pstmt.setString(3, nombre[2]);
                pstmt.setInt(4, "Inactivo".equalsIgnoreCase(usuario.getAcceso()) ? 0 : 1);
                pstmt.setInt(5, usuario.getId());
                pstmt.executeUpdate();
            }

            int idRol = obtenerOCrearRol(conn, usuario.getRol());
            String usuarioSql = "UPDATE usuario SET usuario = ?, password_hash = ?, idRol = ?, activo = ? " +
                    "WHERE idEmpleado = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(usuarioSql)) {
                pstmt.setString(1, usuario.getUsuario());
                pstmt.setString(2, usuario.getContrasena());
                pstmt.setInt(3, idRol);
                pstmt.setInt(4, "Inactivo".equalsIgnoreCase(usuario.getAcceso()) ? 0 : 1);
                pstmt.setInt(5, usuario.getId());
                pstmt.executeUpdate();
            }

            guardarTelefono(conn, usuario.getId(), usuario.getTelefono());
            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean eliminar(int id) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE usuario SET activo = 0 WHERE idEmpleado = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE empleado SET activo = 0 WHERE idEmpleado = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

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

    private int crearEmpleado(Connection conn, Usuario usuario) throws SQLException {
        String[] nombre = separarNombre(usuario.getNombreCompleto());
        int idEmpleado = obtenerSiguienteIdEmpleado(conn);
        String sql = "INSERT INTO empleado (idEmpleado, nombre, apellidoP, apellidoM, activo) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEmpleado);
            pstmt.setString(2, nombre[0]);
            pstmt.setString(3, nombre[1]);
            pstmt.setString(4, nombre[2]);
            pstmt.setInt(5, "Inactivo".equalsIgnoreCase(usuario.getAcceso()) ? 0 : 1);
            pstmt.executeUpdate();
        }
        return idEmpleado;
    }

    private void crearUsuario(Connection conn, Usuario usuario, int idEmpleado, int idRol)
            throws SQLException {
        String sql = "INSERT INTO usuario (idEmpleado, idRol, usuario, password_hash, activo) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEmpleado);
            pstmt.setInt(2, idRol);
            pstmt.setString(3, usuario.getUsuario());
            pstmt.setString(4, usuario.getContrasena());
            pstmt.setInt(5, "Inactivo".equalsIgnoreCase(usuario.getAcceso()) ? 0 : 1);
            pstmt.executeUpdate();
        }
    }

    private void guardarTelefono(Connection conn, int idEmpleado, String telefono) throws SQLException {
        try (PreparedStatement delete = conn.prepareStatement(
                "DELETE FROM telefonoempleado WHERE idEmpleado = ?")) {
            delete.setInt(1, idEmpleado);
            delete.executeUpdate();
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            return;
        }

        String soloDigitos = telefono.replaceAll("\\D", "");
        if (soloDigitos.isEmpty()) {
            return;
        }

        try (PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO telefonoempleado (idEmpleado, telefonoEmpleado) VALUES (?, ?)")) {
            insert.setInt(1, idEmpleado);
            insert.setLong(2, Long.parseLong(soloDigitos));
            insert.executeUpdate();
        }
    }

    private int obtenerSiguienteIdEmpleado(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(idEmpleado), 0) + 1 AS siguiente FROM empleado";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        }
        return 1;
    }

    private int obtenerOCrearRol(Connection conn, String rol) throws SQLException {
        String nombreRol = rol == null || rol.trim().isEmpty() ? "Consulta" : rol.trim();
        String buscarSql = "SELECT idRol FROM rol WHERE nombre = ? LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(buscarSql)) {
            pstmt.setString(1, nombreRol);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idRol");
                }
            }
        }

        String insertarSql = "INSERT INTO rol (nombre, descripcion) VALUES (?, NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertarSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nombreRol);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo crear el rol: " + nombreRol);
    }

    private String[] separarNombre(String nombreCompleto) {
        String limpio = nombreCompleto == null ? "" : nombreCompleto.trim().replaceAll("\\s+", " ");
        if (limpio.isEmpty()) {
            return new String[]{"", "", ""};
        }

        String[] partes = limpio.split(" ");
        if (partes.length == 1) {
            return new String[]{partes[0], "", ""};
        }
        if (partes.length == 2) {
            return new String[]{partes[0], partes[1], ""};
        }

        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < partes.length - 2; i++) {
            if (i > 0) {
                nombres.append(' ');
            }
            nombres.append(partes[i]);
        }
        return new String[]{nombres.toString(), partes[partes.length - 2], partes[partes.length - 1]};
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
