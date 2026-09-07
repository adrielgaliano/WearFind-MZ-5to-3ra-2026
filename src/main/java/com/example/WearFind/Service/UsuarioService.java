package wearfind.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wearfind.Iservice.UsuarioIservice;
import wearfind.Repository.UsuarioRepository;
import wearfind.entidades.Usuario;

@Service
public class UsuarioService implements UsuarioIservice {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
