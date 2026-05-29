package com.ipesapinturas.dao;

import com.ipesapinturas.models.Venta;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    private static final String SELECT_BASE =
            "SELECT v.folio AS id, CAST(v.folio AS CHAR) AS folio, v.fecha, " +
                    "v.idCliente AS cliente_id, " +
                    "TRIM(CONCAT_WS(' ', c.nombre, c.apellidoP, c.apellidoM)) AS cliente_nombre, " +
                    "v.idEmpleado AS usuario_id, " +
                    "TRIM(CONCAT_WS(' ', e.nombre, e.apellidoP, e.apellidoM)) AS usuario_nombre, " +
                    "v.montoTotal AS total, 'Completada' AS estado, NULL AS fecha_creacion " +
                    "FROM venta v " +
                    "LEFT JOIN cliente c ON v.idCliente = c.idCliente " +
                    "LEFT JOIN empleado e ON v.idEmpleado = e.idEmpleado ";

    public List<Venta> obtenerTodas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY v.fecha DESC, v.folio DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ventas.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventas;
    }

    public Venta obtenerPorId(int id) {
        String sql = SELECT_BASE + "WHERE v.folio = ?";

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

    public List<Venta> obtenerPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Venta> ventas = new ArrayList<>();
        String sql = SELECT_BASE +
                "WHERE v.fecha BETWEEN ? AND ? ORDER BY v.fecha DESC, v.folio DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(fechaInicio));
            pstmt.setDate(2, java.sql.Date.valueOf(fechaFin));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ventas.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventas;
    }

    public List<Venta> obtenerPorCliente(int clienteId) {
        List<Venta> ventas = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE v.idCliente = ? ORDER BY v.fecha DESC, v.folio DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ventas.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventas;
    }

    public String obtenerProximoFolio() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT COALESCE(MAX(folio), 2024000000) + 1 AS siguiente FROM venta"
            );

            if (rs.next()) {
                return String.valueOf(rs.getLong("siguiente"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "2024000001";
    }

    public boolean guardar(Venta venta) {
        String sql = "INSERT INTO venta (folio, fecha, idCliente, idEmpleado, montoTotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, folioNumerico(venta.getFolio()));
            pstmt.setDate(2, java.sql.Date.valueOf(venta.getFecha()));
            pstmt.setInt(3, venta.getClienteId());
            pstmt.setInt(4, venta.getUsuarioId());
            pstmt.setDouble(5, venta.getTotal());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Venta venta) {
        String sql = "UPDATE venta SET fecha = ?, idCliente = ?, idEmpleado = ?, montoTotal = ? " +
                "WHERE folio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(venta.getFecha()));
            pstmt.setInt(2, venta.getClienteId());
            pstmt.setInt(3, venta.getUsuarioId());
            pstmt.setDouble(4, venta.getTotal());
            pstmt.setInt(5, folioNumerico(venta.getFolio()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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

            try (PreparedStatement detalle = conn.prepareStatement("DELETE FROM ticket WHERE folio = ?")) {
                detalle.setInt(1, id);
                detalle.executeUpdate();
            }

            int eliminadas;
            try (PreparedStatement venta = conn.prepareStatement("DELETE FROM venta WHERE folio = ?")) {
                venta.setInt(1, id);
                eliminadas = venta.executeUpdate();
            }

            conn.commit();
            return eliminadas > 0;
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

    public double obtenerTotalVentasMes(int mes, int anio) {
        String sql = "SELECT COALESCE(SUM(montoTotal), 0) AS total FROM venta " +
                "WHERE MONTH(fecha) = ? AND YEAR(fecha) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mes);
            pstmt.setInt(2, anio);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Venta mapearResultSet(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getInt("id"));
        venta.setFolio(rs.getString("folio"));
        java.sql.Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            venta.setFecha(fecha.toLocalDate());
        }
        venta.setClienteId(rs.getInt("cliente_id"));
        venta.setClienteNombre(rs.getString("cliente_nombre"));
        venta.setUsuarioId(rs.getInt("usuario_id"));
        venta.setUsuarioNombre(rs.getString("usuario_nombre"));
        venta.setTotal(rs.getDouble("total"));
        venta.setEstado(rs.getString("estado"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            venta.setFechaCreacion(ts.toLocalDateTime());
        }

        return venta;
    }

    private int folioNumerico(String folio) {
        if (folio == null) {
            return Integer.parseInt(obtenerProximoFolio());
        }
        String limpio = folio.replaceAll("\\D", "");
        if (limpio.isEmpty()) {
            return Integer.parseInt(obtenerProximoFolio());
        }
        return Integer.parseInt(limpio);
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
