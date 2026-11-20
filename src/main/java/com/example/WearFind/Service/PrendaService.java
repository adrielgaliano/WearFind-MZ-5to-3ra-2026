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
        // Usuario pedido por el frontend (campo "usuario" del JSON), si lo hay
        Usuario solicitado = null;
        if (prenda.getUsuario() != null && prenda.getUsuario().getId() > 0) {
            solicitado = usuarioRepository.findById(prenda.getUsuario().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "El usuario indicado no existe"));
        }

        Prenda guardada = prendaRepository.save(prenda);

        if (solicitado != null) {
            sincronizarPropietario(guardada, solicitado);
        }
        return cargarUsuarios(List.of(guardada)).get(0);
    }

    /**
     * Compatibilidad con el formulario actual (un solo select de usuario):
     *  - si el usuario pedido ya tiene la prenda, no se toca nada;
     *  - si no la tiene, la prenda pasa a pertenecer a ese usuario.
     * Para asignaciones múltiples usar /api/usuario-prendas.
     */
    private void sincronizarPropietario(Prenda prenda, Usuario solicitado) {
        List<UsuarioPrenda> actuales = usuarioPrendaRepository.findByPrendaId(prenda.getId());
        boolean yaAsignada = actuales.stream()
                .anyMatch(up -> up.getUsuario().getId() == solicitado.getId());
        if (yaAsignada) {
            return;
        }
        if (!actuales.isEmpty()) {
            usuarioPrendaRepository.deleteAll(actuales);
            usuarioPrendaRepository.flush();
        }
        usuarioPrendaRepository.save(new UsuarioPrenda(solicitado, prenda));
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

    /**
     * Completa los campos transitorios usuario / usuarios de cada prenda
     * con una sola consulta a la tabla intermedia (evita N+1).
     */
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
