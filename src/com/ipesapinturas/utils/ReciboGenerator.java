package com.ipesapinturas.utils;

import com.ipesapinturas.models.ProductoSeleccionado;
import com.ipesapinturas.models.Venta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReciboGenerator {
    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"));
    private static final DateTimeFormatter FILE_DATE =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static String generarRecibo(Venta venta, List<ProductoSeleccionado> detalles) {
        StringBuilder recibo = new StringBuilder();
        int totalItems = 0;
        double subtotal = 0.0;

        for (ProductoSeleccionado detalle : detalles) {
            totalItems += detalle.getCantidad();
            subtotal += detalle.getSubtotal();
        }

        double descuento = Math.max(0.0, subtotal - venta.getTotal());
        double porcentajeDescuento = subtotal > 0 ? (descuento / subtotal) * 100 : 0.0;

        recibo.append("=====================================\n");
        recibo.append("           IPESA Pinturas\n");
        recibo.append("           PUNTO DE VENTA\n");
        recibo.append("=====================================\n\n");
        recibo.append("FOLIO: ").append(valor(venta.getFolio())).append('\n');
        recibo.append("FECHA: ").append(venta.getFecha()).append('\n');
        recibo.append("VENDEDOR: ").append(valor(venta.getUsuarioNombre())).append("\n\n");
        recibo.append("CLIENTE:\n");
        recibo.append(valor(venta.getClienteNombre())).append("\n\n");
        recibo.append("=====================================\n");
        recibo.append("DETALLE DE COMPRA\n");
        recibo.append("=====================================\n");

        for (ProductoSeleccionado detalle : detalles) {
            recibo.append(detalle.getNombre())
                    .append(" (")
                    .append(valor(detalle.getColor()))
                    .append(")\n");
            recibo.append("  x")
                    .append(detalle.getCantidad())
                    .append(" @ ")
                    .append(CURRENCY.format(detalle.getPrecioUnitario()))
                    .append(" = ")
                    .append(CURRENCY.format(detalle.getSubtotal()))
                    .append('\n');
        }

        recibo.append("=====================================\n");
        recibo.append("Subtotal (").append(totalItems).append(" items): ")
                .append(CURRENCY.format(subtotal)).append('\n');

        if (descuento > 0) {
            recibo.append("Descuento (")
                    .append(String.format("%.0f", porcentajeDescuento))
                    .append("%): -")
                    .append(CURRENCY.format(descuento))
                    .append('\n');
        } else {
            recibo.append("Descuento: ").append(CURRENCY.format(0)).append('\n');
        }

        recibo.append("=====================================\n");
        recibo.append("TOTAL: ").append(CURRENCY.format(venta.getTotal())).append('\n');
        recibo.append("=====================================\n\n");
        recibo.append("    Gracias por su compra!\n");
        recibo.append("           Vuelva pronto\n");

        return recibo.toString();
    }

    public static File guardarRecibo(Venta venta, List<ProductoSeleccionado> detalles)
            throws IOException {
        File carpeta = new File("recibos");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta de recibos");
        }

        String folio = valor(venta.getFolio()).replaceAll("[^A-Za-z0-9]", "");
        if (folio.isEmpty()) {
            folio = "sin_folio";
        }

        File archivo = new File(carpeta,
                "recibo_" + folio + "_" + LocalDateTime.now().format(FILE_DATE) + ".txt");

        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write(generarRecibo(venta, detalles));
        }

        return archivo;
    }

    public static void imprimirRecibo(Venta venta, List<ProductoSeleccionado> detalles) {
        System.out.println(generarRecibo(venta, detalles));
    }

    private static String valor(String texto) {
        return texto == null || texto.trim().isEmpty() ? "N/A" : texto.trim();
    }
}
