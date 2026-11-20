package com.example.WearFind.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.WearFind.entidades.UsuarioPrenda;

@Repository
public interface UsuarioPrendaRepository extends JpaRepository<UsuarioPrenda, Long> {

    @Query("select up from UsuarioPrenda up join fetch up.usuario join fetch up.prenda order by up.id")
    List<UsuarioPrenda> findAllConDetalle();

    @Query("select up from UsuarioPrenda up join fetch up.usuario join fetch up.prenda "
         + "where up.usuario.id = :usuarioId order by up.id")
    List<UsuarioPrenda> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("select up from UsuarioPrenda up join fetch up.usuario join fetch up.prenda "
         + "where up.prenda.id = :prendaId order by up.fechaAsignacion, up.id")
    List<UsuarioPrenda> findByPrendaId(@Param("prendaId") Long prendaId);

    @Query("select up from UsuarioPrenda up join fetch up.usuario join fetch up.prenda "
         + "where up.prenda.id in :prendaIds order by up.fechaAsignacion, up.id")
    List<UsuarioPrenda> findByPrendaIdIn(@Param("prendaIds") Collection<Long> prendaIds);

    Optional<UsuarioPrenda> findByUsuarioIdAndPrendaId(Long usuarioId, Long prendaId);

    boolean existsByUsuarioIdAndPrendaId(Long usuarioId, Long prendaId);

    boolean existsByPrendaId(Long prendaId);

    void deleteByPrendaId(Long prendaId);
}
