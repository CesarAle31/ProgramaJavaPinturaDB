package com.ipesapinturas.models;

import java.math.BigDecimal;

public class Producto {
    private int idPintura;
    private int claveClasificacion;
    private int idProveedor;
    private String nombre;
    private String color;
    private String capacidad;
    private int stock;
    private BigDecimal costo;
    private String presentacion;

    private String proveedorNombre;

    public Producto() {
    }

    public Producto(int idPintura, int claveClasificacion, int idProveedor, String nombre,
                    String color, String capacidad, int stock, BigDecimal costo,
                    String presentacion) {
        this.idPintura = idPintura;
        this.claveClasificacion = claveClasificacion;
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.color = color;
        this.capacidad = capacidad;
        this.stock = stock;
        this.costo = costo;
        this.presentacion = presentacion;
    }

    public Producto(int idPintura, int claveClasificacion, int idProveedor, String nombre,
                    String color, String capacidad, int stock, double costo,
                    String presentacion) {
        this(idPintura, claveClasificacion, idProveedor, nombre, color, capacidad, stock,
                BigDecimal.valueOf(costo), presentacion);
    }

    public int getIdPintura() {
        return idPintura;
    }

    public void setIdPintura(int idPintura) {
        this.idPintura = idPintura;
    }

    public int getClaveClasificacion() {
        return claveClasificacion;
    }

    public void setClaveClasificacion(int claveClasificacion) {
        this.claveClasificacion = claveClasificacion;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
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

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = String.valueOf(capacidad);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getCostoDecimal() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public double getCosto() {
        return costo != null ? costo.doubleValue() : 0.0;
    }

    public void setCosto(double costo) {
        this.costo = BigDecimal.valueOf(costo);
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public int getId() {
        return idPintura;
    }

    public void setId(int id) {
        this.idPintura = id;
    }

    public String getLinea() {
        return String.valueOf(claveClasificacion);
    }

    public void setLinea(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            this.claveClasificacion = 0;
            return;
        }

        try {
            this.claveClasificacion = Integer.parseInt(linea.trim());
        } catch (NumberFormatException ex) {
            this.claveClasificacion = 0;
        }
    }

    public int getProveedorId() {
        return idProveedor;
    }

    public void setProveedorId(int proveedorId) {
        this.idProveedor = proveedorId;
    }

    public double getPrecioVenta() {
        return getCosto();
    }

    public void setPrecioVenta(double precioVenta) {
        // La tabla pintura no tiene precioVenta; se conserva por compatibilidad con la UI existente.
    }

    public double getGanancia() {
        return 0.0;
    }

    @Override
    public String toString() {
        return nombre + " (" + color + ")";
    }
}
