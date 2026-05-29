package com.ipesapinturas.dao;

import com.ipesapinturas.models.Cliente;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombre_completo";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public List<Cliente> obtenerActivos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE estado = 'Activo' ORDER BY nombre_completo";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
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

    public List<Cliente> buscar(String termino) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE nombre_completo LIKE ? OR email LIKE ? " +
                "OR telefono LIKE ? ORDER BY nombre_completo";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String patron = "%" + termino + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            pstmt.setString(3, patron);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clientes.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public boolean guardar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre_completo, email, telefono, direccion, estado) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombreCompleto());
            pstmt.setString(2, cliente.getEmail());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getEstado());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre_completo = ?, email = ?, telefono = ?, " +
                "direccion = ?, estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombreCompleto());
            pstmt.setString(2, cliente.getEmail());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getEstado());
            pstmt.setInt(6, cliente.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Cliente mapearResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNombreCompleto(rs.getString("nombre_completo"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setEstado(rs.getString("estado"));

        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            cliente.setFechaRegistro(ts.toLocalDateTime());
        }

        return cliente;
    }
}