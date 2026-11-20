package com.example.WearFind.entidades;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<UsuarioPrenda> asignaciones = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public List<UsuarioPrenda> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<UsuarioPrenda> asignaciones) {
        this.asignaciones = asignaciones;
    }
}
