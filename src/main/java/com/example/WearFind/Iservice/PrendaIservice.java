package wearfind.Iservice;

import java.util.List;
import wearfind.entidades.Prenda;

public interface PrendaIservice {
    public List<Prenda> findAllPrendas();
    public Prenda savePrenda(Prenda prenda);
    public Prenda findPrendaById(Long id);
    public void deletePrenda(Long id);
    public List<Prenda> findPrendasByUsuarioId(Long usuarioId);
}
