package wearfind.entidades;

import jakarta.persistence.*;
@Entity
public class Prenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String descripcion;
    private String color;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    private String talle;
    private String marca;

    @Enumerated(EnumType.STRING)
    private Temporada temporada;

    private String url_imagen;
    private boolean favorito;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Prenda() {
        // Constructor requerido por JPA
    }

    public Prenda(long id, String nombre, String descripcion, String color, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.categoria = categoria;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public String getTalle() {
        return talle;
    }

    public String getMarca() {
        return marca;
    }

    public Temporada getTemporada() {
        return temporada;
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

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setTalle(String talle) {
        this.talle = talle;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setTemporada(Temporada temporada) {
        this.temporada = temporada;
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
