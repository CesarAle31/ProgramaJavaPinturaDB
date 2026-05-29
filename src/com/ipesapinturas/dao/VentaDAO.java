package com.ipesapinturas.dao;

import com.ipesapinturas.models.Venta;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public List<Venta> obtenerTodas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre_completo as cliente_nombre, u.nombre_completo as usuario_nombre " +
                "FROM ventas v " +
                "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                "ORDER BY v.fecha DESC";

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
        String sql = "SELECT v.*, c.nombre_completo as cliente_nombre, u.nombre_completo as usuario_nombre " +
                "FROM ventas v " +
                "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                "WHERE v.id = ?";

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
        String sql = "SELECT v.*, c.nombre_completo as cliente_nombre, u.nombre_completo as usuario_nombre " +
                "FROM ventas v " +
                "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                "WHERE DATE(v.fecha) BETWEEN ? AND ? " +
                "ORDER BY v.fecha DESC";

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
        String sql = "SELECT v.*, c.nombre_completo as cliente_nombre, u.nombre_completo as usuario_nombre " +
                "FROM ventas v " +
                "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                "WHERE v.cliente_id = ? ORDER BY v.fecha DESC";

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

            // Obtiene el último folio y suma 1
            ResultSet rs = stmt.executeQuery(
                    "SELECT COALESCE(MAX(CAST(SUBSTRING(folio, 2) AS UNSIGNED)), 20240000000) + 1 as siguiente FROM ventas"
            );

            if (rs.next()) {
                return "#" + rs.getLong("siguiente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "#20240000001";
    }

    public boolean guardar(Venta venta) {
        String sql = "INSERT INTO ventas (folio, fecha, cliente_id, usuario_id, total, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, venta.getFolio());
            pstmt.setDate(2, java.sql.Date.valueOf(venta.getFecha()));
            pstmt.setInt(3, venta.getClienteId());
            pstmt.setInt(4, venta.getUsuarioId());
            pstmt.setDouble(5, venta.getTotal());
            pstmt.setString(6, venta.getEstado());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Venta venta) {
        String sql = "UPDATE ventas SET fecha = ?, cliente_id = ?, usuario_id = ?, " +
                "total = ?, estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(venta.getFecha()));
            pstmt.setInt(2, venta.getClienteId());
            pstmt.setInt(3, venta.getUsuarioId());
            pstmt.setDouble(4, venta.getTotal());
            pstmt.setString(5, venta.getEstado());
            pstmt.setInt(6, venta.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM ventas WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double obtenerTotalVentasMes(int mes, int año) {
        String sql = "SELECT COALESCE(SUM(total), 0) as total FROM ventas " +
                "WHERE MONTH(fecha) = ? AND YEAR(fecha) = ? AND estado = 'Completada'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mes);
            pstmt.setInt(2, año);

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
        venta.setFecha(rs.getDate("fecha").toLocalDate());
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
}