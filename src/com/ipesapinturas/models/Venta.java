package com.ipesapinturas.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Venta {
    private int id;
    private String folio;
    private LocalDate fecha;
    private int clienteId;
    private String clienteNombre;
    private int usuarioId;
    private String usuarioNombre;
    private double total;
    private String estado;
    private LocalDateTime fechaCreacion;

    public Venta() {
    }

    public Venta(int id, String folio, LocalDate fecha, int clienteId, String clienteNombre,
                 int usuarioId, String usuarioNombre, double total, String estado,
                 LocalDateTime fechaCreacion) {
        this.id = id;
        this.folio = folio;
        this.fecha = fecha;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.total = total;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return folio + " - " + clienteNombre + " - $" + total;
    }
}