package com.example.WearFind.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.WearFind.entidades.Prenda;

@Repository
public interface PrendaRepository extends JpaRepository<Prenda, Long> {
    public List<Prenda> findByUsuarioId(Long usuarioId);
}