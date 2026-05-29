package com.ipesapinturas.models;

import java.time.LocalDateTime;

public class Cliente {
    private int id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String direccion;
    private String estado;
    private LocalDateTime fechaRegistro;

    public Cliente() {
    }

    public Cliente(int id, String nombreCompleto, String email, String telefono,
                   String direccion, String estado, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + estado + ")";
    }
}