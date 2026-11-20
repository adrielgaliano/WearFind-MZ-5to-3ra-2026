package com.example.WearFind.entidades;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

@Entity
@Table(name = "prenda")
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
    @Column(length = 1000)
    private String url_imagen;
    private boolean favorito;

    /*
     * La relación real vive en la tabla intermedia usuario_prenda (UsuarioPrenda).
     * Estos dos campos NO se persisten: los completa PrendaService al devolver
     * la prenda, para que el JSON siga siendo compatible con el frontend:
     *  - usuario  : primer usuario asignado (el "dueño" principal)
     *  - usuarios : todos los usuarios que tienen asignada la prenda
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties({"asignaciones", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties({"asignaciones", "hibernateLazyInitializer", "handler"})
    private List<Usuario> usuarios;

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

    public Usuario getUsuario() {
        return usuario;
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

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}