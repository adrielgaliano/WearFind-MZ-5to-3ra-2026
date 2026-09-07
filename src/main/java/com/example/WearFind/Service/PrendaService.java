package wearfind.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wearfind.Iservice.PrendaIservice;
import wearfind.Repository.PrendaRepository;
import wearfind.entidades.Prenda;

@Service
public class PrendaService implements PrendaIservice {

    @Autowired
    private PrendaRepository prendaRepository;

    @Override
    public List<Prenda> findAllPrendas() {
        return prendaRepository.findAll();
    }

    @Override
    public Prenda savePrenda(Prenda prenda) {
        return prendaRepository.save(prenda);
    }

    @Override
    public Prenda findPrendaById(Long id) {
        return prendaRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePrenda(Long id) {
        prendaRepository.deleteById(id);
    }

    @Override
    public List<Prenda> findPrendasByUsuarioId(Long usuarioId) {
        return prendaRepository.findByUsuarioId(usuarioId);
    }
}
