package com.ipesapinturas.dao;

import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DetalleVentaDAO {

    public boolean guardar(int ventaId, int productoId, int cantidad,
                           double precioUnitario, double subtotal) {
        String sql = "INSERT INTO detalles_venta " +
                "(venta_id, producto_id, cantidad, precio_unitario, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ventaId);
            pstmt.setInt(2, productoId);
            pstmt.setInt(3, cantidad);
            pstmt.setDouble(4, precioUnitario);
            pstmt.setDouble(5, subtotal);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Map<String, Object>> obtenerDetallesPorVenta(int ventaId) {
        List<Map<String, Object>> detalles = new ArrayList<>();
        String sql = "SELECT dv.*, p.nombre, p.color " +
                "FROM detalles_venta dv " +
                "INNER JOIN productos p ON dv.producto_id = p.id " +
                "WHERE dv.venta_id = ? " +
                "ORDER BY dv.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ventaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> detalle = new LinkedHashMap<>();
                detalle.put("id", rs.getInt("id"));
                detalle.put("venta_id", rs.getInt("venta_id"));
                detalle.put("producto_id", rs.getInt("producto_id"));
                detalle.put("producto_nombre", rs.getString("nombre"));
                detalle.put("producto_color", rs.getString("color"));
                detalle.put("cantidad", rs.getInt("cantidad"));
                detalle.put("precio_unitario", rs.getDouble("precio_unitario"));
                detalle.put("subtotal", rs.getDouble("subtotal"));
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    public List<Map<String, Object>> obtenerProductosMasVendidos(int limite) {
        List<Map<String, Object>> productos = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.color, SUM(dv.cantidad) AS cantidad_vendida, " +
                "SUM(dv.subtotal) AS total_vendido " +
                "FROM detalles_venta dv " +
                "INNER JOIN productos p ON dv.producto_id = p.id " +
                "GROUP BY p.id, p.nombre, p.color " +
                "ORDER BY cantidad_vendida DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> producto = new LinkedHashMap<>();
                producto.put("producto_id", rs.getInt("id"));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("color", rs.getString("color"));
                producto.put("cantidad_vendida", rs.getInt("cantidad_vendida"));
                producto.put("total_vendido", rs.getDouble("total_vendido"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    public boolean eliminar(int detalleId) {
        String sql = "DELETE FROM detalles_venta WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, detalleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarPorVenta(int ventaId) {
        String sql = "DELETE FROM detalles_venta WHERE venta_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ventaId);
            return pstmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int contarPorVenta(int ventaId) {
        String sql = "SELECT COUNT(*) AS total FROM detalles_venta WHERE venta_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ventaId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
