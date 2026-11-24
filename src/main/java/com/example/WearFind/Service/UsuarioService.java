package com.example.WearFind.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.WearFind.Iservice.UsuarioIservice;
import com.example.WearFind.Repository.PrendaRepository;
import com.example.WearFind.Repository.UsuarioPrendaRepository;
import com.example.WearFind.Repository.UsuarioRepository;
import com.example.WearFind.entidades.Usuario;
import com.example.WearFind.entidades.UsuarioPrenda;

@Service
public class UsuarioService implements UsuarioIservice {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrendaRepository prendaRepository;

    @Autowired
    private UsuarioPrendaRepository usuarioPrendaRepository;

    @Override
    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario saveUsuario(Usuario usuario) {
        if (usuario.getId() > 0) {
            Usuario existente = usuarioRepository.findById(usuario.getId()).orElse(null);
            if (existente != null) {
                existente.setUsername(usuario.getUsername());
                existente.setEmail(usuario.getEmail());
                return usuarioRepository.save(existente);
            }
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario updateUsuario(Long id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }
        existente.setUsername(usuario.getUsername());
        existente.setEmail(usuario.getEmail());
        return usuarioRepository.save(existente);
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (esVacio(usuario.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingresá un username");
        }
        if (esVacio(usuario.getEmail()) || !usuario.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingresá un email válido");
        }
        if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres");
        }

        String email = usuario.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese email");
        }

        usuario.setId(0);
        usuario.setUsername(usuario.getUsername().trim());
        usuario.setEmail(email);
        usuario.setPassword(ClaveUtil.encriptar(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario autenticar(String email, String password) {
        Usuario usuario = null;
        if (email != null) {
            usuario = usuarioRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
        }
        // Mismo mensaje si el email no existe o la contraseña no coincide
        if (usuario == null || !ClaveUtil.verificar(password, usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
        }
        return usuario;
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    @Override
    public Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Elimina el usuario y sus asignaciones. Las prendas que quedan sin ningún
     * usuario por este borrado también se eliminan (igual que antes); las que
     * comparte con otros usuarios se conservan.
     */
    @Override
    @Transactional
    public void deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return;
        }
        List<UsuarioPrenda> asignaciones = usuarioPrendaRepository.findByUsuarioId(id);
        List<Long> prendaIds = asignaciones.stream().map(up -> up.getPrenda().getId()).toList();

        usuarioPrendaRepository.deleteAll(asignaciones);
        usuarioPrendaRepository.flush();

        for (Long prendaId : prendaIds) {
            if (!usuarioPrendaRepository.existsByPrendaId(prendaId)) {
                prendaRepository.deleteById(prendaId);
            }
        }
        usuarioRepository.deleteById(id);
    }
}
