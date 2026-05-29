package com.ipesapinturas.dao;

import com.ipesapinturas.models.Cliente;
import com.ipesapinturas.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private static final String SELECT_BASE =
            "SELECT c.idCliente AS id, " +
                    "TRIM(CONCAT_WS(' ', c.nombre, c.apellidoP, c.apellidoM)) AS nombre_completo, " +
                    "c.correo AS email, c.telefono, " +
                    "CONCAT_WS(', ', " +
                    "NULLIF(TRIM(CONCAT_WS(' ', d.calle, d.numero)), ''), " +
                    "co.colonia, m.nombre, m.estado) AS direccion, " +
                    "CASE WHEN c.activo = 1 THEN 'Activo' ELSE 'Inactivo' END AS estado, " +
                    "NULL AS fecha_registro " +
                    "FROM cliente c " +
                    "LEFT JOIN direccion d ON c.idDireccion = d.idDireccion " +
                    "LEFT JOIN colonia co ON d.idColonia = co.idColonia " +
                    "LEFT JOIN municipio m ON co.claveM = m.claveM ";

    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY nombre_completo";

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
        String sql = SELECT_BASE + "WHERE c.activo = 1 ORDER BY nombre_completo";

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
        String sql = SELECT_BASE + "WHERE c.idCliente = ?";
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
        String sql = SELECT_BASE +
                "WHERE TRIM(CONCAT_WS(' ', c.nombre, c.apellidoP, c.apellidoM)) LIKE ? " +
                "OR c.correo LIKE ? OR c.telefono LIKE ? ORDER BY nombre_completo";

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
        String sql = "INSERT INTO cliente " +
                "(nombre, apellidoP, apellidoM, telefono, idDireccion, correo, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] nombre = separarNombre(cliente.getNombreCompleto());
            Integer idDireccion = guardarDireccion(conn, cliente.getDireccion());

            pstmt.setString(1, nombre[0]);
            pstmt.setString(2, nombre[1]);
            pstmt.setString(3, nombre[2]);
            pstmt.setString(4, cliente.getTelefono());
            if (idDireccion == null) {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(5, idDireccion);
            }
            pstmt.setString(6, cliente.getEmail());
            pstmt.setInt(7, "Inactivo".equalsIgnoreCase(cliente.getEstado()) ? 0 : 1);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, apellidoP = ?, apellidoM = ?, " +
                "telefono = ?, correo = ?, activo = ?, idDireccion = ? WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] nombre = separarNombre(cliente.getNombreCompleto());
            Integer idDireccion = actualizarDireccionCliente(conn, cliente.getId(), cliente.getDireccion());

            pstmt.setString(1, nombre[0]);
            pstmt.setString(2, nombre[1]);
            pstmt.setString(3, nombre[2]);
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setInt(6, "Inactivo".equalsIgnoreCase(cliente.getEstado()) ? 0 : 1);
            if (idDireccion == null) {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(7, idDireccion);
            }
            pstmt.setInt(8, cliente.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE cliente SET activo = 0 WHERE idCliente = ?";
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

    private String[] separarNombre(String nombreCompleto) {
        String limpio = nombreCompleto == null ? "" : nombreCompleto.trim().replaceAll("\\s+", " ");
        if (limpio.isEmpty()) {
            return new String[]{"", "", ""};
        }

        String[] partes = limpio.split(" ");
        if (partes.length == 1) {
            return new String[]{partes[0], "", ""};
        }
        if (partes.length == 2) {
            return new String[]{partes[0], partes[1], ""};
        }

        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < partes.length - 2; i++) {
            if (i > 0) {
                nombres.append(' ');
            }
            nombres.append(partes[i]);
        }
        return new String[]{nombres.toString(), partes[partes.length - 2], partes[partes.length - 1]};
    }

    private Integer actualizarDireccionCliente(Connection conn, int clienteId, String direccion)
            throws SQLException {
        Integer idDireccion = obtenerDireccionCliente(conn, clienteId);
        if (idDireccion == null) {
            return guardarDireccion(conn, direccion);
        }
        actualizarDireccion(conn, idDireccion, direccion);
        return idDireccion;
    }

    private Integer obtenerDireccionCliente(Connection conn, int clienteId) throws SQLException {
        String sql = "SELECT idDireccion FROM cliente WHERE idCliente = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clienteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idDireccion");
                    return rs.wasNull() ? null : id;
                }
            }
        }
        return null;
    }

    private Integer guardarDireccion(Connection conn, String direccion) throws SQLException {
        if (direccion == null || direccion.trim().isEmpty()) {
            return null;
        }

        String sql = "INSERT INTO direccion (idColonia, calle, numero) VALUES (NULL, ?, NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, direccion.trim());
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

    private void actualizarDireccion(Connection conn, int idDireccion, String direccion) throws SQLException {
        String sql = "UPDATE direccion SET idColonia = NULL, calle = ?, numero = NULL WHERE idDireccion = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, direccion == null ? null : direccion.trim());
            pstmt.setInt(2, idDireccion);
            pstmt.executeUpdate();
        }
    }
}
