package wearfind.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wearfind.entidades.Prenda;

public interface PrendaRepository extends JpaRepository<Prenda, Long> {
    public List<Prenda> findByUsuarioId(Long usuarioId);
}
