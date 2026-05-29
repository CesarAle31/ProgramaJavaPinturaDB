package com.ipesapinturas.dao;

import com.ipesapinturas.models.Producto;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private static final String SELECT_BASE =
            "SELECT p.idPintura AS id, p.nombre, p.color, " +
                    "COALESCE(c.linea, '') AS linea, p.capacidad, p.presentacion, " +
                    "p.costo, p.costo AS precio_venta, p.stock, " +
                    "p.idProveedor AS proveedor_id, pr.razonSocial AS razon_social, " +
                    "NULL AS fecha_registro " +
                    "FROM pintura p " +
                    "LEFT JOIN clasificacion c ON p.claveClasificacion = c.claveClasificacion " +
                    "LEFT JOIN proveedor pr ON p.idProveedor = pr.idProveedor ";

    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY p.nombre";

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

    public List<Producto> obtenerStockBajo(int limite) {
        List<Producto> productos = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE p.stock <= ? ORDER BY p.stock";

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

    public Producto obtenerPorId(int id) {
        String sql = SELECT_BASE + "WHERE p.idPintura = ?";

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

    public List<Producto> buscar(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = SELECT_BASE +
                "WHERE p.nombre LIKE ? OR p.color LIKE ? OR c.linea LIKE ? ORDER BY p.nombre";

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

    public boolean guardar(Producto producto) {
        String sql = "INSERT INTO pintura " +
                "(idPintura, claveClasificacion, idProveedor, nombre, color, capacidad, stock, costo, presentacion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int id = obtenerSiguienteIdPintura(conn);
            Integer claveClasificacion = obtenerOCrearClasificacion(conn, producto.getLinea());

            pstmt.setInt(1, id);
            if (claveClasificacion == null) {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(2, claveClasificacion);
            }
            if (producto.getProveedorId() <= 0) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, producto.getProveedorId());
            }
            pstmt.setString(4, producto.getNombre());
            pstmt.setString(5, producto.getColor());
            pstmt.setString(6, String.valueOf(producto.getCapacidad()));
            pstmt.setInt(7, producto.getStock());
            pstmt.setDouble(8, producto.getCosto());
            pstmt.setString(9, producto.getPresentacion());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE pintura SET claveClasificacion = ?, idProveedor = ?, nombre = ?, " +
                "color = ?, capacidad = ?, stock = ?, costo = ?, presentacion = ? WHERE idPintura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Integer claveClasificacion = obtenerOCrearClasificacion(conn, producto.getLinea());
            if (claveClasificacion == null) {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(1, claveClasificacion);
            }
            if (producto.getProveedorId() <= 0) {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(2, producto.getProveedorId());
            }
            pstmt.setString(3, producto.getNombre());
            pstmt.setString(4, producto.getColor());
            pstmt.setString(5, String.valueOf(producto.getCapacidad()));
            pstmt.setInt(6, producto.getStock());
            pstmt.setDouble(7, producto.getCosto());
            pstmt.setString(8, producto.getPresentacion());
            pstmt.setInt(9, producto.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarStock(int productoId, int nuevoStock) {
        String sql = "UPDATE pintura SET stock = ? WHERE idPintura = ?";
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

    public boolean eliminar(int id) {
        String sql = "DELETE FROM pintura WHERE idPintura = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Producto mapearResultSet(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setColor(rs.getString("color"));
        producto.setLinea(rs.getString("linea"));
        producto.setCapacidad(parseEntero(rs.getString("capacidad")));
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

    private int obtenerSiguienteIdPintura(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(idPintura), 0) + 1 AS siguiente FROM pintura";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        }
        return 1;
    }

    private int parseEntero(String valor) {
        if (valor == null) {
            return 0;
        }
        String digitos = valor.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(digitos);
    }

    private Integer obtenerOCrearClasificacion(Connection conn, String linea) throws SQLException {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }

        String buscarSql = "SELECT claveClasificacion FROM clasificacion WHERE linea = ? LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(buscarSql)) {
            pstmt.setString(1, linea.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("claveClasificacion");
                }
            }
        }

        int siguiente = obtenerSiguienteClaveClasificacion(conn);
        String insertarSql = "INSERT INTO clasificacion (claveClasificacion, tipo, linea, descripcion) " +
                "VALUES (?, NULL, ?, NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertarSql)) {
            pstmt.setInt(1, siguiente);
            pstmt.setString(2, linea.trim());
            pstmt.executeUpdate();
        }
        return siguiente;
    }

    private int obtenerSiguienteClaveClasificacion(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(claveClasificacion), 699) + 1 AS siguiente FROM clasificacion";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        }
        return 700;
    }
}
