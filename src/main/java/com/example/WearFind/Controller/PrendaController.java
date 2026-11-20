package com.example.WearFind.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.WearFind.Iservice.PrendaIservice;
import com.example.WearFind.entidades.Prenda;

@RestController
@RequestMapping("/api/prendas")
@CrossOrigin(origins = "*")
public class PrendaController {

    @Autowired
    private PrendaIservice prendaService;

    @GetMapping
    public List<Prenda> getAllPrendas() {
        return prendaService.findAllPrendas();
    }

    @GetMapping("/{id}")
    public Prenda getPrendaById(@PathVariable("id") Long id) {
        return prendaService.findPrendaById(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Prenda> getPrendasByUsuario(@PathVariable("usuarioId") Long usuarioId) {
        return prendaService.findPrendasByUsuarioId(usuarioId);
    }

    @GetMapping("/sin-usuario")
    public List<Prenda> getPrendasSinUsuario() {
        return prendaService.findPrendasSinUsuario();
    }

    @PostMapping
    public Prenda createPrenda(@RequestBody Prenda prenda) {
        return prendaService.savePrenda(prenda);
    }

    @PutMapping("/{id}")
    public Prenda updatePrenda(@PathVariable("id") Long id, @RequestBody Prenda prenda) {
        return prendaService.updatePrenda(id, prenda);
    }

    @DeleteMapping("/{id}")
    public void deletePrenda(@PathVariable("id") Long id) {
        prendaService.deletePrenda(id);
    }
}