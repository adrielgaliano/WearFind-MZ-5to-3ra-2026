package wearfind.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wearfind.entidades.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
