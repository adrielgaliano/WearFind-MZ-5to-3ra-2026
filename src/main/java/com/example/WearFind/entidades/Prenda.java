package com.example.WearFind.entidades;

import jakarta.persistence.*;

@Entity
public class Prenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String descripcion;
    private String color;
    private String estacion;
    private String talle;
    private String marca;
    private String url_imagen;
    private boolean favorito;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Prenda() {
        // Constructor requerido por JPA
    }

    public Prenda(long id, String nombre, String descripcion, String color, String estacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.estacion = estacion;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getColor() {
        return color;
    }

    public String getTalle() {
        return talle;
    }

    public String getMarca() {
        return marca;
    }

    public String getEstacion() {
        return estacion;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setTalle(String talle) {
        this.talle = talle;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setEstacion(String estacion) {
        this.estacion = estacion;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}