package com.example.WearFind.entidades;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String email;

    @OneToMany(mappedBy = "usuario")
    private List<Prenda> listaprendas = new ArrayList<>();

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

    public List<Prenda> getListaprendas() {
        return listaprendas;
    }

    public void setListaprendas(List<Prenda> listaprendas) {
        this.listaprendas = listaprendas;
    }

}
