package com.ipesapinturas.models;

import java.time.LocalDateTime;

public class Producto {
    private int id;
    private String nombre;
    private String color;
    private String linea;
    private int capacidad;
    private String presentacion;
    private double costo;
    private double precioVenta;
    private int stock;
    private int proveedorId;
    private String proveedorNombre;
    private LocalDateTime fechaRegistro;

    public Producto() {
    }

    public Producto(int id, String nombre, String color, String linea, int capacidad,
                    String presentacion, double costo, double precioVenta, int stock,
                    int proveedorId, String proveedorNombre, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.linea = linea;
        this.capacidad = capacidad;
        this.presentacion = presentacion;
        this.costo = costo;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(int proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public double getGanancia() {
        return precioVenta - costo;
    }

    @Override
    public String toString() {
        return nombre + " (" + color + ")";
    }
}