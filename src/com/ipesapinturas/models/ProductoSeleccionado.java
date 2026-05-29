package com.ipesapinturas.models;

public class ProductoSeleccionado {
    private int productoId;
    private String nombre;
    private String color;
    private double precioUnitario;
    private int cantidad;
    private double subtotal;

    public ProductoSeleccionado() {
    }

    public ProductoSeleccionado(int productoId, String nombre, String color,
                                double precioUnitario, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.color = color;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        actualizarSubtotal();
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        actualizarSubtotal();
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        actualizarSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void actualizarSubtotal() {
        this.subtotal = this.precioUnitario * this.cantidad;
    }

    @Override
    public String toString() {
        return nombre + " (" + color + ") x" + cantidad;
    }
}
