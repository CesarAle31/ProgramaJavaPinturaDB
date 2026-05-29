package com.ipesapinturas.services;

import com.ipesapinturas.dao.VentaDAO;
import com.ipesapinturas.models.ProductoSeleccionado;
import com.ipesapinturas.models.Venta;
import com.ipesapinturas.utils.DatabaseConnection;
import com.ipesapinturas.utils.ReciboGenerator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class VentaService {
    private final VentaDAO ventaDAO;

    public VentaService() {
        this.ventaDAO = new VentaDAO();
    }

    public boolean crearVentaCompleta(Venta venta, List<ProductoSeleccionado> productos) {
        validarVenta(venta, productos);

        venta.setTotal(calcularTotal(productos));
        if (venta.getFolio() == null || venta.getFolio().trim().isEmpty()) {
            venta.setFolio(obtenerProximoFolio());
        }
        if (venta.getFecha() == null) {
            venta.setFecha(LocalDate.now());
        }
        if (venta.getEstado() == null || venta.getEstado().trim().isEmpty()) {
            venta.setEstado("Completada");
        }

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new IllegalStateException("No hay conexion a la base de datos");
        }

        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            int folio = insertarVenta(conn, venta);
            venta.setId(folio);
            venta.setFolio(String.valueOf(folio));

            for (ProductoSeleccionado producto : productos) {
                validarStock(conn, producto);
                insertarDetalle(conn, folio, producto);
                actualizarStock(conn, producto);
            }

            conn.commit();

            try {
                ReciboGenerator.guardarRecibo(venta, productos);
            } catch (IOException e) {
                System.err.println("La venta se guardo, pero no se pudo guardar el recibo: " +
                        e.getMessage());
            }

            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new IllegalStateException("No se pudo guardar la venta: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public double calcularDescuento(int cantidad, double subtotal) {
        if (cantidad >= 21) {
            return subtotal * 0.15;
        }
        if (cantidad >= 11) {
            return subtotal * 0.10;
        }
        if (cantidad >= 5) {
            return subtotal * 0.05;
        }
        return 0.0;
    }

    public String obtenerProximoFolio() {
        return ventaDAO.obtenerProximoFolio();
    }

    public double calcularSubtotal(List<ProductoSeleccionado> productos) {
        double subtotal = 0.0;
        if (productos == null) {
            return subtotal;
        }

        for (ProductoSeleccionado producto : productos) {
            subtotal += producto.getSubtotal();
        }
        return subtotal;
    }

    public int calcularCantidadTotal(List<ProductoSeleccionado> productos) {
        int cantidad = 0;
        if (productos == null) {
            return cantidad;
        }

        for (ProductoSeleccionado producto : productos) {
            cantidad += producto.getCantidad();
        }
        return cantidad;
    }

    public double calcularTotal(List<ProductoSeleccionado> productos) {
        double subtotal = calcularSubtotal(productos);
        int cantidad = calcularCantidadTotal(productos);
        return subtotal - calcularDescuento(cantidad, subtotal);
    }

    public void validarVenta(Venta venta, List<ProductoSeleccionado> productos) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula");
        }
        if (venta.getClienteId() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un cliente");
        }
        if (venta.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("No hay usuario vendedor valido");
        }
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio");
        }

        for (ProductoSeleccionado producto : productos) {
            if (producto.getProductoId() <= 0) {
                throw new IllegalArgumentException("Hay un producto invalido en el carrito");
            }
            if (producto.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            if (producto.getPrecioUnitario() <= 0) {
                throw new IllegalArgumentException("El precio del producto debe ser mayor a cero");
            }
        }
    }

    private int insertarVenta(Connection conn, Venta venta) throws SQLException {
        int folio = folioNumerico(venta.getFolio());
        String sql = "INSERT INTO venta (folio, fecha, idCliente, idEmpleado, montoTotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, folio);
            pstmt.setDate(2, Date.valueOf(venta.getFecha()));
            pstmt.setInt(3, venta.getClienteId());
            pstmt.setInt(4, venta.getUsuarioId());
            pstmt.setDouble(5, venta.getTotal());

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No se inserto la venta");
            }
        }

        return folio;
    }

    private void insertarDetalle(Connection conn, int folio, ProductoSeleccionado producto)
            throws SQLException {
        String sql = "INSERT INTO ticket (folio, idPintura, cantidad, precio, importe) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, folio);
            pstmt.setInt(2, producto.getProductoId());
            pstmt.setInt(3, producto.getCantidad());
            pstmt.setDouble(4, producto.getPrecioUnitario());
            pstmt.setDouble(5, producto.getSubtotal());

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No se inserto el detalle de venta");
            }
        }
    }

    private void validarStock(Connection conn, ProductoSeleccionado producto) throws SQLException {
        String sql = "SELECT stock FROM pintura WHERE idPintura = ? FOR UPDATE";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, producto.getProductoId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Producto no encontrado: " + producto.getNombre());
                }

                int stock = rs.getInt("stock");
                if (stock < producto.getCantidad()) {
                    throw new SQLException("Stock insuficiente para " + producto.getNombre() +
                            ". Disponible: " + stock);
                }
            }
        }
    }

    private void actualizarStock(Connection conn, ProductoSeleccionado producto) throws SQLException {
        String sql = "UPDATE pintura SET stock = stock - ? WHERE idPintura = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, producto.getCantidad());
            pstmt.setInt(2, producto.getProductoId());

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No se actualizo el stock de " + producto.getNombre());
            }
        }
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
