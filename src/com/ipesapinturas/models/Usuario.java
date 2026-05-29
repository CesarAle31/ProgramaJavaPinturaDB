package com.ipesapinturas.models;

import java.time.LocalDateTime;

/**
 * Modelo que representa un Usuario en el sistema
 */
public class Usuario {
    private int id;
    private String nombreCompleto;
    private String usuario;
    private String contrasena;
    private String rol;
    private String telefono;
    private String acceso;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor completo
    public Usuario(int id, String nombreCompleto, String usuario, String contrasena,
                   String rol, String telefono, String acceso, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.telefono = telefono;
        this.acceso = acceso;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAcceso() {
        return acceso;
    }

    public void setAcceso(String acceso) {
        this.acceso = acceso;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + rol + ")";
    }
}