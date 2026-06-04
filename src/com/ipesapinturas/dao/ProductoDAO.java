package com.ipesapinturas.dao;

import com.ipesapinturas.models.Producto;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private static final String INSERT_SQL =
            "INSERT INTO pintura (idPintura, claveClasificacion, idProveedor, nombre, color, capacidad, stock, costo, presentacion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM pintura WHERE idPintura = ?;";
    private static final String SELECT_ALL_SQL = "SELECT * FROM pintura;";
    private static final String DELETE_SQL = "DELETE FROM pintura WHERE idPintura = ?;";

    public void agregar(Producto p) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos.");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
                int idPintura = p.getIdPintura() > 0 ? p.getIdPintura() : obtenerSiguienteIdPintura(conn);
                pstmt.setInt(1, idPintura);
                pstmt.setInt(2, p.getClaveClasificacion());
                pstmt.setInt(3, p.getIdProveedor());
                pstmt.setString(4, p.getNombre().trim());
                pstmt.setString(5, p.getColor().trim());
                pstmt.setString(6, p.getCapacidad());
                pstmt.setInt(7, p.getStock());
                pstmt.setBigDecimal(8, p.getCostoDecimal());
                pstmt.setString(9, p.getPresentacion());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Se relanza para que Swing muestre el mensaje del trigger MariaDB.
            throw e;
        }
    }

    public Producto buscarPorId(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapearProducto(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Producto> listarTodo() {
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return productos;
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos.");
            }

            try (PreparedStatement eliminarStmt = conn.prepareStatement(DELETE_SQL)) {
                eliminarStmt.setInt(1, id);
                eliminarStmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Se relanza para conservar mensajes SIGNAL SQLSTATE '45000'.
            throw e;
        }
    }

    public List<Producto> obtenerTodos() {
        return listarTodo();
    }

    public Producto obtenerPorId(int id) {
        return buscarPorId(id);
    }

    public List<Producto> obtenerStockBajo(int limite) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM pintura WHERE stock <= ?;";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return productos;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, limite);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        productos.add(mapearProducto(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    public void guardar(Producto producto) throws SQLException {
        agregar(producto);
    }

    public void actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE pintura SET claveClasificacion = ?, idProveedor = ?, nombre = ?, " +
                "color = ?, capacidad = ?, stock = ?, costo = ?, presentacion = ? WHERE idPintura = ?;";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos.");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, producto.getClaveClasificacion());
                pstmt.setInt(2, producto.getIdProveedor());
                pstmt.setString(3, producto.getNombre().trim());
                pstmt.setString(4, producto.getColor().trim());
                pstmt.setString(5, producto.getCapacidad());
                pstmt.setInt(6, producto.getStock());
                pstmt.setBigDecimal(7, producto.getCostoDecimal());
                pstmt.setString(8, producto.getPresentacion());
                pstmt.setInt(9, producto.getIdPintura());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Se relanza para que la capa UI maneje errores de triggers.
            throw e;
        }
    }

    public boolean actualizarStock(int productoId, int nuevoStock) {
        String sql = "UPDATE pintura SET stock = ? WHERE idPintura = ?;";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, nuevoStock);
                pstmt.setInt(2, productoId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Producto> buscar(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM pintura WHERE nombre LIKE ? OR color LIKE ? OR capacidad LIKE ? OR presentacion LIKE ?;";
        String patron = "%" + (termino == null ? "" : termino.trim()) + "%";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return productos;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, patron);
                pstmt.setString(2, patron);
                pstmt.setString(3, patron);
                pstmt.setString(4, patron);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        productos.add(mapearProducto(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdPintura(rs.getInt("idPintura"));
        producto.setClaveClasificacion(rs.getInt("claveClasificacion"));
        producto.setIdProveedor(rs.getInt("idProveedor"));
        producto.setNombre(rs.getString("nombre"));
        producto.setColor(rs.getString("color"));
        producto.setCapacidad(rs.getString("capacidad"));
        producto.setStock(rs.getInt("stock"));
        producto.setCosto(rs.getBigDecimal("costo"));
        producto.setPresentacion(rs.getString("presentacion"));
        return producto;
    }

    private int obtenerSiguienteIdPintura(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(idPintura), 0) + 1 AS siguiente FROM pintura;";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        }
        return 1;
    }
}
