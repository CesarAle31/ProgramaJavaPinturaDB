package com.ipesapinturas.dao;

import com.ipesapinturas.models.Proveedor;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public List<Proveedor> obtenerTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores ORDER BY razon_social";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                proveedores.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }

    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT * FROM proveedores WHERE id = ?";
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

    public List<Proveedor> buscar(String termino) {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores WHERE razon_social LIKE ? OR municipio LIKE ? " +
                "ORDER BY razon_social";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String patron = "%" + termino + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                proveedores.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }

    public boolean guardar(Proveedor proveedor) {
        String sql = "INSERT INTO proveedores (razon_social, telefono, direccion, municipio) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proveedor.getRazonSocial());
            pstmt.setString(2, proveedor.getTelefono());
            pstmt.setString(3, proveedor.getDireccion());
            pstmt.setString(4, proveedor.getMunicipio());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedores SET razon_social = ?, telefono = ?, " +
                "direccion = ?, municipio = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proveedor.getRazonSocial());
            pstmt.setString(2, proveedor.getTelefono());
            pstmt.setString(3, proveedor.getDireccion());
            pstmt.setString(4, proveedor.getMunicipio());
            pstmt.setInt(5, proveedor.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Proveedor mapearResultSet(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(rs.getInt("id"));
        proveedor.setRazonSocial(rs.getString("razon_social"));
        proveedor.setTelefono(rs.getString("telefono"));
        proveedor.setDireccion(rs.getString("direccion"));
        proveedor.setMunicipio(rs.getString("municipio"));

        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            proveedor.setFechaRegistro(ts.toLocalDateTime());
        }

        return proveedor;
    }
}