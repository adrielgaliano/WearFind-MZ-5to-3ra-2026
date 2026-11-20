package com.example.WearFind.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.WearFind.Iservice.UsuarioPrendaIservice;
import com.example.WearFind.Repository.PrendaRepository;
import com.example.WearFind.Repository.UsuarioPrendaRepository;
import com.example.WearFind.Repository.UsuarioRepository;
import com.example.WearFind.entidades.Prenda;
import com.example.WearFind.entidades.Usuario;
import com.example.WearFind.entidades.UsuarioPrenda;

@Service
public class UsuarioPrendaService implements UsuarioPrendaIservice {

    @Autowired
    private UsuarioPrendaRepository usuarioPrendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrendaRepository prendaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioPrenda> findAllAsignaciones() {
        return usuarioPrendaRepository.findAllConDetalle();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioPrenda> findAsignacionesByUsuarioId(Long usuarioId) {
        return usuarioPrendaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioPrenda> findAsignacionesByPrendaId(Long prendaId) {
        return usuarioPrendaRepository.findByPrendaId(prendaId);
    }

    @Override
    @Transactional
    public UsuarioPrenda asignar(Long usuarioId, Long prendaId) {
        if (usuarioId == null || prendaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId y prendaId son obligatorios");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + usuarioId));
        Prenda prenda = prendaRepository.findById(prendaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenda no encontrada: " + prendaId));
        if (usuarioPrendaRepository.existsByUsuarioIdAndPrendaId(usuarioId, prendaId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La prenda ya está asignada a ese usuario");
        }
        return usuarioPrendaRepository.save(new UsuarioPrenda(usuario, prenda));
    }

    @Override
    @Transactional
    public void desasignar(Long usuarioId, Long prendaId) {
        UsuarioPrenda asignacion = usuarioPrendaRepository.findByUsuarioIdAndPrendaId(usuarioId, prendaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La asignación no existe"));
        usuarioPrendaRepository.delete(asignacion);
    }

    @Override
    @Transactional
    public void deleteAsignacion(Long id) {
        if (usuarioPrendaRepository.existsById(id)) {
            usuarioPrendaRepository.deleteById(id);
        }
    }
}
