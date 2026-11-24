package com.example.WearFind.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.WearFind.Iservice.PrendaIservice;
import com.example.WearFind.Repository.PrendaRepository;
import com.example.WearFind.Repository.UsuarioPrendaRepository;
import com.example.WearFind.Repository.UsuarioRepository;
import com.example.WearFind.entidades.Prenda;
import com.example.WearFind.entidades.Usuario;
import com.example.WearFind.entidades.UsuarioPrenda;

@Service
public class PrendaService implements PrendaIservice {

    @Autowired
    private PrendaRepository prendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioPrendaRepository usuarioPrendaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Prenda> findAllPrendas() {
        return cargarUsuarios(prendaRepository.findAll());
    }

    @Override
    @Transactional
    public Prenda savePrenda(Prenda prenda) {
        boolean esNueva = prenda.getId() <= 0;

        Usuario solicitado = null;
        if (prenda.getUsuario() != null && prenda.getUsuario().getId() > 0) {
            solicitado = usuarioRepository.findById(prenda.getUsuario().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "El usuario indicado no existe"));
        }

        Prenda guardada = prendaRepository.save(prenda);

        // Solo asignamos dueño al crear la prenda (o si quedó huérfana), para no pisar prendas compartidas al editar
        if (solicitado != null && (esNueva || !usuarioPrendaRepository.existsByPrendaId(guardada.getId()))) {
            usuarioPrendaRepository.save(new UsuarioPrenda(solicitado, guardada));
        }

        return cargarUsuarios(List.of(guardada)).get(0);
    }

    @Override
    @Transactional
    public Prenda updatePrenda(Long id, Prenda prenda) {
        prenda.setId(id);
        return savePrenda(prenda);
    }

    @Override
    @Transactional(readOnly = true)
    public Prenda findPrendaById(Long id) {
        Prenda prenda = prendaRepository.findById(id).orElse(null);
        if (prenda == null) {
            return null;
        }
        return cargarUsuarios(List.of(prenda)).get(0);
    }

    @Override
    @Transactional
    public void deletePrenda(Long id) {
        if (prendaRepository.existsById(id)) {
            usuarioPrendaRepository.deleteByPrendaId(id);
            prendaRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prenda> findPrendasByUsuarioId(Long usuarioId) {
        return cargarUsuarios(prendaRepository.findByUsuarioId(usuarioId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prenda> findPrendasSinUsuario() {
        return prendaRepository.findSinUsuario();
    }

    private List<Prenda> cargarUsuarios(Collection<Prenda> prendas) {
        List<Prenda> resultado = new ArrayList<>(prendas);
        if (resultado.isEmpty()) {
            return resultado;
        }
        List<Long> ids = resultado.stream().map(Prenda::getId).distinct().toList();
        Map<Long, List<Usuario>> porPrenda = new HashMap<>();
        for (UsuarioPrenda up : usuarioPrendaRepository.findByPrendaIdIn(ids)) {
            porPrenda.computeIfAbsent(up.getPrenda().getId(), k -> new ArrayList<>()).add(up.getUsuario());
        }
        for (Prenda p : resultado) {
            List<Usuario> duenos = porPrenda.getOrDefault(p.getId(), List.of());
            p.setUsuarios(duenos);
            p.setUsuario(duenos.isEmpty() ? null : duenos.get(0));
        }
        return resultado;
    }
}