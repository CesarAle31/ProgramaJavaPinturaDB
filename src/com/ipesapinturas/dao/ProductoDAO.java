package com.ipesapinturas.dao;

import com.ipesapinturas.models.Producto;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    /**
     * Obtiene todos los productos con información del proveedor
     */
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, pr.razon_social FROM productos p " +
                "LEFT JOIN proveedores pr ON p.proveedor_id = pr.id " +
                "ORDER BY p.nombre";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Obtiene productos con stock bajo
     */
    public List<Producto> obtenerStockBajo(int limite) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, pr.razon_social FROM productos p " +
                "LEFT JOIN proveedores pr ON p.proveedor_id = pr.id " +
                "WHERE p.stock <= ? ORDER BY p.stock";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                productos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Obtiene un producto por ID
     */
    public Producto obtenerPorId(int id) {
        String sql = "SELECT p.*, pr.razon_social FROM productos p " +
                "LEFT JOIN proveedores pr ON p.proveedor_id = pr.id " +
                "WHERE p.id = ?";

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
     * Busca productos por nombre, color o línea
     */
    public List<Producto> buscar(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, pr.razon_social FROM productos p " +
                "LEFT JOIN proveedores pr ON p.proveedor_id = pr.id " +
                "WHERE p.nombre LIKE ? OR p.color LIKE ? OR p.linea LIKE ? " +
                "ORDER BY p.nombre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String patron = "%" + termino + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            pstmt.setString(3, patron);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                productos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Guarda un producto nuevo
     */
    public boolean guardar(Producto producto) {
        String sql = "INSERT INTO productos (nombre, color, linea, capacidad, presentacion, " +
                "costo, precio_venta, stock, proveedor_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getColor());
            pstmt.setString(3, producto.getLinea());
            pstmt.setInt(4, producto.getCapacidad());
            pstmt.setString(5, producto.getPresentacion());
            pstmt.setDouble(6, producto.getCosto());
            pstmt.setDouble(7, producto.getPrecioVenta());
            pstmt.setInt(8, producto.getStock());
            pstmt.setInt(9, producto.getProveedorId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza un producto
     */
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, color = ?, linea = ?, capacidad = ?, " +
                "presentacion = ?, costo = ?, precio_venta = ?, stock = ?, proveedor_id = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getColor());
            pstmt.setString(3, producto.getLinea());
            pstmt.setInt(4, producto.getCapacidad());
            pstmt.setString(5, producto.getPresentacion());
            pstmt.setDouble(6, producto.getCosto());
            pstmt.setDouble(7, producto.getPrecioVenta());
            pstmt.setInt(8, producto.getStock());
            pstmt.setInt(9, producto.getProveedorId());
            pstmt.setInt(10, producto.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza solo el stock de un producto
     */
    public boolean actualizarStock(int productoId, int nuevoStock) {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, productoId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un producto
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
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
     * Mapea un ResultSet a un objeto Producto
     */
    private Producto mapearResultSet(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setColor(rs.getString("color"));
        producto.setLinea(rs.getString("linea"));
        producto.setCapacidad(rs.getInt("capacidad"));
        producto.setPresentacion(rs.getString("presentacion"));
        producto.setCosto(rs.getDouble("costo"));
        producto.setPrecioVenta(rs.getDouble("precio_venta"));
        producto.setStock(rs.getInt("stock"));
        producto.setProveedorId(rs.getInt("proveedor_id"));
        producto.setProveedorNombre(rs.getString("razon_social"));

        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            producto.setFechaRegistro(ts.toLocalDateTime());
        }

        return producto;
    }
}