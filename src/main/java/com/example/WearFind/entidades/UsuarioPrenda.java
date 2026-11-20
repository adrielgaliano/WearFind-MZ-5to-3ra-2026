package com.example.WearFind.entidades;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Tabla intermedia entre Usuario y Prenda (relación muchos a muchos).
 * Una prenda puede estar asignada a varios usuarios y un usuario puede tener
 * muchas prendas. El par (usuario, prenda) es único.
 */
@Entity
@Table(
    name = "usuario_prenda",
    uniqueConstraints = @UniqueConstraint(name = "uk_usuario_prenda", columnNames = {"usuario_id", "prenda_id"})
)
public class UsuarioPrenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_up_usuario"))
    @JsonIgnoreProperties({"asignaciones", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "prenda_id", nullable = false, foreignKey = @ForeignKey(name = "fk_up_prenda"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Prenda prenda;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    public UsuarioPrenda() {
        // Constructor requerido por JPA
    }

    public UsuarioPrenda(Usuario usuario, Prenda prenda) {
        this.usuario = usuario;
        this.prenda = prenda;
    }

    @PrePersist
    void antesDePersistir() {
        if (fechaAsignacion == null) {
            fechaAsignacion = LocalDateTime.now();
        }
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Prenda getPrenda() { return prenda; }
    public void setPrenda(Prenda prenda) { this.prenda = prenda; }

    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
}
