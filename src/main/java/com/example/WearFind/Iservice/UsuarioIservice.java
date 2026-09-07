package wearfind.Iservice;

import java.util.List;
import wearfind.entidades.Usuario;

public interface UsuarioIservice {
    public List<Usuario> findAllUsuarios();
    public Usuario saveUsuario(Usuario usuario);
    public Usuario findUsuarioById(Long id);
    public void deleteUsuario(Long id);
}
