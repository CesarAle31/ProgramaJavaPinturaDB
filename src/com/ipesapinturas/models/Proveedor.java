package com.ipesapinturas.models;

import java.time.LocalDateTime;

public class Proveedor {
    private int id;
    private String razonSocial;
    private String telefono;
    private String direccion;
    private String municipio;
    private LocalDateTime fechaRegistro;

    public Proveedor() {
    }

    public Proveedor(int id, String razonSocial, String telefono, String direccion,
                     String municipio, LocalDateTime fechaRegistro) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.direccion = direccion;
        this.municipio = municipio;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
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

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return razonSocial;
    }
}