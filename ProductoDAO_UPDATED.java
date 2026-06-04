package com.ipesapinturas.dao;

import com.ipesapinturas.models.Producto;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la tabla 'pintura'.
 * Implementa operaciones CRUD utilizando Procedimientos Almacenados (Stored Procedures)
 * invocados mediante CallableStatement.
 *
 * Procedimientos invocados:
 * - sp_agregar_pintura: Inserta un nuevo registro
 * - sp_buscar_pintura: Busca un registro por ID
 * - sp_actualizar_pintura: Actualiza un registro existente
 * - sp_eliminar_pintura: Elimina un registro por ID
 */
public class ProductoDAO {
    // Procedimientos almacenados (Stored Procedures)
    private static final String CALL_AGREGAR = "{call sp_agregar_pintura(?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String CALL_BUSCAR = "{call sp_buscar_pintura(?)}";
    private static final String CALL_ACTUALIZAR = "{call sp_actualizar_pintura(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String CALL_ELIMINAR = "{call sp_eliminar_pintura(?)}";

    // Queries directas (para métodos auxiliares)
    private static final String SELECT_ALL_SQL = "SELECT * FROM pintura;";

    /**
     * Agrega un nuevo registro de pintura en la base de datos.
     * Invoca el procedimiento sp_agregar_pintura.
     *
     * @param p Objeto Producto con los datos a insertar (excepto idPintura)
     * @throws SQLException Si ocurre un error en la base de datos o validación en triggers
     */
    public void agregar(Producto p) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos.");
            }

            try (CallableStatement cstmt = conn.prepareCall(CALL_AGREGAR)) {
                cstmt.setInt(1, p.getClaveClasificacion());
                cstmt.setInt(2, p.getIdProveedor());
                cstmt.setString(3, p.getNombre().trim());
                cstmt.setString(4, p.getColor().trim());
                cstmt.setString(5, p.getCapacidad());
                cstmt.setInt(6, p.getStock());
                cstmt.setBigDecimal(7, p.getCostoDecimal());
                cstmt.setString(8, p.getPresentacion());

                cstmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Se relanza para que la capa UI (JOptionPane) capture errores de triggers
            throw e;
        }
    }

    /**
     * Busca un registro de pintura por su ID.
     * Invoca el procedimiento sp_buscar_pintura.
     *
     * @param id ID del producto a buscar
     * @return Objeto Producto si existe, null si no se encuentra
     */
    public Producto buscarPorId(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return null;
            }

            try (CallableStatement cstmt = conn.prepareCall(CALL_BUSCAR)) {
                cstmt.setInt(1, id);
                try (ResultSet rs = cstmt.executeQuery()) {
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

    /**
     * Lista todos los registros de la tabla pintura.
     *
     * @return Lista de Productos
     */
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

    /**
     * Elimina un registro de pintura por su ID.
     * Invoca el procedimiento sp_eliminar_pintura.
     * Respeta las restricciones definidas en triggers BEFORE DELETE.
     *
     * @param id ID del producto a eliminar
     * @throws SQLException Si ocurre un error o hay restricciones de integridad
     */
    public void eliminar(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos.");
            }

            try (CallableStatement cstmt = conn.prepareCall(CALL_ELIMINAR)) {
                cstmt.setInt(1, id);
                cstmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Se relanza para conservar mensajes SIGNAL SQLSTATE '45000' de triggers
            throw e;
        }
    }

    /**
     * Alias de listarTodo().
     */
    public List<Producto> obtenerTodos() {
        return listarTodo();
    }

    /**
     * Alias de buscarPorId().
     */
    public Producto obtenerPorId(int id) {
        return buscarPorId(id);
    }

    /**
     * Obtiene productos con stock por debajo del límite especificado.
     *
     * @param limite Nivel máximo de stock
     * @return Lista de Productos con stock <= limite
     */
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

    /**
     * Alias de agregar().
     */
    public void guardar(Producto producto) throws SQLException {
        agregar(producto);
    }

    /**
     * Actualiza un registro existente de pintura.
     * Invoca el procedimiento sp_actualizar_pintura.
     *
     * @param producto Objeto Producto con los datos a actualizar (incluido idPintura)
     * @throws SQLException Si ocurre un error en la base de datos o validación en triggers
     */
    public void actualizar(Producto producto) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("No se pudo obtener conexion a la base de datos.");
            }

            try (CallableStatement cstmt = conn.prepareCall(CALL_ACTUALIZAR)) {
                cstmt.setInt(1, producto.getIdPintura());
                cstmt.setInt(2, producto.getClaveClasificacion());
                cstmt.setInt(3, producto.getIdProveedor());
                cstmt.setString(4, producto.getNombre().trim());
                cstmt.setString(5, producto.getColor().trim());
                cstmt.setString(6, producto.getCapacidad());
                cstmt.setInt(7, producto.getStock());
                cstmt.setBigDecimal(8, producto.getCostoDecimal());
                cstmt.setString(9, producto.getPresentacion());

                cstmt.executeUpdate();
            }
        } catch (SQLException e) {
            // Se relanza para que la capa UI maneje errores de triggers
            throw e;
        }
    }

    /**
     * Actualiza solo el stock de un producto.
     *
     * @param productoId ID del producto
     * @param nuevoStock Nuevo valor de stock
     * @return true si la actualización fue exitosa, false en caso contrario
     */
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

    /**
     * Busca productos por término en nombre, color, capacidad o presentación.
     *
     * @param termino Término de búsqueda
     * @return Lista de Productos coincidentes
     */
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

    /**
     * Mapea un ResultSet a un objeto Producto.
     *
     * @param rs ResultSet con los datos del producto
     * @return Objeto Producto con los datos del ResultSet
     * @throws SQLException Si ocurre un error al acceder al ResultSet
     */
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
}
