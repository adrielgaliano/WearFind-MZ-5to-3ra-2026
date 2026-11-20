package com.example.WearFind.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.WearFind.entidades.Prenda;

@Repository
public interface PrendaRepository extends JpaRepository<Prenda, Long> {

    // Prendas asignadas a un usuario, a través de la tabla intermedia usuario_prenda
    @Query("select up.prenda from UsuarioPrenda up where up.usuario.id = :usuarioId order by up.id")
    List<Prenda> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Prendas que no están asignadas a ningún usuario
    @Query("select p from Prenda p where p.id not in (select up.prenda.id from UsuarioPrenda up)")
    List<Prenda> findSinUsuario();
}
