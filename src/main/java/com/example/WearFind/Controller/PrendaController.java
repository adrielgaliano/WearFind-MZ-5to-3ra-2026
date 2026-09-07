package wearfind.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wearfind.Iservice.PrendaIservice;
import wearfind.entidades.Prenda;

@RestController
@RequestMapping("/api/prendas")
@CrossOrigin (origins="http://localhost:8080")
public class PrendaController {

    @Autowired
    private PrendaIservice prendaService;

    @GetMapping
    public List<Prenda> getAllPrendas() {
        return prendaService.findAllPrendas();
    }

    @GetMapping("/{id}")
    public Prenda getPrendaById(@PathVariable Long id) {
        return prendaService.findPrendaById(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Prenda> getPrendasByUsuario(@PathVariable Long usuarioId) {
        return prendaService.findPrendasByUsuarioId(usuarioId);
    }

    @PostMapping
    public Prenda createPrenda(@RequestBody Prenda prenda) {
        return prendaService.savePrenda(prenda);
    }

    @DeleteMapping("/{id}")
    public void deletePrenda(@PathVariable Long id) {
        prendaService.deletePrenda(id);
    }
}
