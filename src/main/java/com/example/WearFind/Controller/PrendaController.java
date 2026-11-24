package com.example.WearFind.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.WearFind.Iservice.PrendaIservice;
import com.example.WearFind.Service.SesionService;
import com.example.WearFind.entidades.Prenda;
import com.example.WearFind.entidades.Usuario;

@RestController
@RequestMapping("/api/prendas")
@CrossOrigin(origins = "*")
public class PrendaController {

    @Autowired
    private PrendaIservice prendaService;

    @Autowired
    private SesionService sesionService;

    @GetMapping
    public List<Prenda> getAllPrendas(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {
        sesionService.usuarioActual(autorizacion);
        return prendaService.findAllPrendas();
    }

    @GetMapping("/{id}")
    public Prenda getPrendaById(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                                @PathVariable("id") Long id) {
        sesionService.usuarioActual(autorizacion);
        return prendaService.findPrendaById(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Prenda> getPrendasByUsuario(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable("usuarioId") Long usuarioId) {
        sesionService.usuarioActual(autorizacion);
        return prendaService.findPrendasByUsuarioId(usuarioId);
    }

    @GetMapping("/sin-usuario")
    public List<Prenda> getPrendasSinUsuario(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {
        sesionService.usuarioActual(autorizacion);
        return prendaService.findPrendasSinUsuario();
    }

    @PostMapping
    public Prenda createPrenda(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                               @RequestBody Prenda prenda) {
        Usuario actual = sesionService.usuarioActual(autorizacion);

        if (prenda.getId() > 0) {
            // Edición de prenda existente: solo si pertenece al usuario logueado
            validarPropiedad(prenda.getId(), actual);
        } else {
            // Creación de prenda nueva: si no trae usuario o trae otro, se asigna al logueado
            if (prenda.getUsuario() == null || prenda.getUsuario().getId() <= 0) {
                prenda.setUsuario(actual);
            } else if (prenda.getUsuario().getId() != actual.getId()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "No podés agregar prendas en el armario de otro usuario");
            }
        }
        return prendaService.savePrenda(prenda);
    }

    @PutMapping("/{id}")
    public Prenda updatePrenda(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                               @PathVariable("id") Long id, @RequestBody Prenda prenda) {
        Usuario actual = sesionService.usuarioActual(autorizacion);
        validarPropiedad(id, actual);
        return prendaService.updatePrenda(id, prenda);
    }

    @DeleteMapping("/{id}")
    public void deletePrenda(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                             @PathVariable("id") Long id) {
        Usuario actual = sesionService.usuarioActual(autorizacion);
        validarPropiedad(id, actual);
        prendaService.deletePrenda(id);
    }

    private void validarPropiedad(Long prendaId, Usuario actual) {
        Prenda existente = prendaService.findPrendaById(prendaId);
        if (existente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La prenda no existe");
        }
        boolean esDueno = existente.getUsuarios() != null && existente.getUsuarios().stream()
                .anyMatch(u -> u.getId() == actual.getId());
        if (!esDueno) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tenés permiso para modificar o eliminar prendas de otro usuario");
        }
    }
}