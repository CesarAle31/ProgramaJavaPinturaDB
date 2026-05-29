package com.ipesapinturas.dao;

import com.ipesapinturas.models.Proveedor;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {
    private static final String SELECT_BASE =
            "SELECT p.idProveedor AS id, p.razonSocial AS razon_social, p.telefono, " +
                    "CONCAT_WS(', ', " +
                    "NULLIF(TRIM(CONCAT_WS(' ', d.calle, d.numero)), ''), " +
                    "co.colonia, m.nombre, m.estado) AS direccion, " +
                    "m.nombre AS municipio, NULL AS fecha_registro " +
                    "FROM proveedor p " +
                    "LEFT JOIN direccion d ON p.idDireccion = d.idDireccion " +
                    "LEFT JOIN colonia co ON d.idColonia = co.idColonia " +
                    "LEFT JOIN municipio m ON co.claveM = m.claveM ";

    public List<Proveedor> obtenerTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY p.razonSocial";

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
        String sql = SELECT_BASE + "WHERE p.idProveedor = ?";
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
        String sql = SELECT_BASE +
                "WHERE p.razonSocial LIKE ? OR m.nombre LIKE ? ORDER BY p.razonSocial";

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
        String sql = "INSERT INTO proveedor (razonSocial, telefono, idDireccion) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Integer idDireccion = guardarDireccion(conn, proveedor.getDireccion(), proveedor.getMunicipio());

            pstmt.setString(1, proveedor.getRazonSocial());
            pstmt.setString(2, proveedor.getTelefono());
            if (idDireccion == null) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, idDireccion);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedor SET razonSocial = ?, telefono = ?, idDireccion = ? " +
                "WHERE idProveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Integer idDireccion = actualizarDireccionProveedor(
                    conn, proveedor.getId(), proveedor.getDireccion(), proveedor.getMunicipio());

            pstmt.setString(1, proveedor.getRazonSocial());
            pstmt.setString(2, proveedor.getTelefono());
            if (idDireccion == null) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, idDireccion);
            }
            pstmt.setInt(4, proveedor.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedor WHERE idProveedor = ?";
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

    private Integer actualizarDireccionProveedor(
            Connection conn, int proveedorId, String direccion, String municipio)
            throws SQLException {
        Integer idDireccion = obtenerDireccionProveedor(conn, proveedorId);
        if (idDireccion == null) {
            return guardarDireccion(conn, direccion, municipio);
        }
        actualizarDireccion(conn, idDireccion, direccion, municipio);
        return idDireccion;
    }

    private Integer obtenerDireccionProveedor(Connection conn, int proveedorId) throws SQLException {
        String sql = "SELECT idDireccion FROM proveedor WHERE idProveedor = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, proveedorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idDireccion");
                    return rs.wasNull() ? null : id;
                }
            }
        }
        return null;
    }

    private Integer guardarDireccion(Connection conn, String direccion, String municipio)
            throws SQLException {
        if (estaVacio(direccion) && estaVacio(municipio)) {
            return null;
        }

        Integer idColonia = obtenerIdColoniaParaMunicipio(conn, municipio);
        String sql = "INSERT INTO direccion (idColonia, calle, numero) VALUES (?, ?, NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (idColonia == null) {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(1, idColonia);
            }
            pstmt.setString(2, estaVacio(direccion) ? null : direccion.trim());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return null;
    }

    private void actualizarDireccion(Connection conn, int idDireccion, String direccion, String municipio)
            throws SQLException {
        Integer idColonia = obtenerIdColoniaParaMunicipio(conn, municipio);
        String sql = "UPDATE direccion SET idColonia = ?, calle = ?, numero = NULL WHERE idDireccion = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (idColonia == null) {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(1, idColonia);
            }
            pstmt.setString(2, estaVacio(direccion) ? null : direccion.trim());
            pstmt.setInt(3, idDireccion);
            pstmt.executeUpdate();
        }
    }

    private Integer obtenerIdColoniaParaMunicipio(Connection conn, String municipio) throws SQLException {
        if (estaVacio(municipio)) {
            return null;
        }

        int claveMunicipio = obtenerOCrearMunicipio(conn, municipio.trim());
        String buscarSql = "SELECT idColonia FROM colonia WHERE claveM = ? AND colonia IS NULL LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(buscarSql)) {
            pstmt.setInt(1, claveMunicipio);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idColonia");
                }
            }
        }

        String insertarSql = "INSERT INTO colonia (claveM, colonia, cp) VALUES (?, NULL, NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertarSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, claveMunicipio);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return null;
    }

    private int obtenerOCrearMunicipio(Connection conn, String municipio) throws SQLException {
        String buscarSql = "SELECT claveM FROM municipio WHERE nombre = ? LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(buscarSql)) {
            pstmt.setString(1, municipio);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("claveM");
                }
            }
        }

        int siguiente = obtenerSiguienteClaveMunicipio(conn);
        String insertarSql = "INSERT INTO municipio (claveM, nombre, estado) VALUES (?, ?, NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertarSql)) {
            pstmt.setInt(1, siguiente);
            pstmt.setString(2, municipio);
            pstmt.executeUpdate();
        }
        return siguiente;
    }

    private int obtenerSiguienteClaveMunicipio(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(claveM), 0) + 1 AS siguiente FROM municipio";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        }
        return 1;
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
