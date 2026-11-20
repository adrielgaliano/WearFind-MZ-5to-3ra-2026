package com.example.WearFind.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.WearFind.Iservice.UsuarioPrendaIservice;
import com.example.WearFind.entidades.UsuarioPrenda;

@RestController
@RequestMapping("/api/usuario-prendas")
@CrossOrigin(origins = "*")
public class UsuarioPrendaController {

    public record AsignacionRequest(Long usuarioId, Long prendaId) {
    }

    @Autowired
    private UsuarioPrendaIservice usuarioPrendaService;

    @GetMapping
    public List<UsuarioPrenda> getAllAsignaciones() {
        return usuarioPrendaService.findAllAsignaciones();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<UsuarioPrenda> getByUsuario(@PathVariable("usuarioId") Long usuarioId) {
        return usuarioPrendaService.findAsignacionesByUsuarioId(usuarioId);
    }

    @GetMapping("/prenda/{prendaId}")
    public List<UsuarioPrenda> getByPrenda(@PathVariable("prendaId") Long prendaId) {
        return usuarioPrendaService.findAsignacionesByPrendaId(prendaId);
    }

    // Body: { "usuarioId": 1, "prendaId": 3 }
    @PostMapping
    public UsuarioPrenda asignar(@RequestBody AsignacionRequest request) {
        return usuarioPrendaService.asignar(request.usuarioId(), request.prendaId());
    }

    @DeleteMapping("/usuario/{usuarioId}/prenda/{prendaId}")
    public void desasignar(@PathVariable("usuarioId") Long usuarioId,
                           @PathVariable("prendaId") Long prendaId) {
        usuarioPrendaService.desasignar(usuarioId, prendaId);
    }

    @DeleteMapping("/{id}")
    public void deleteAsignacion(@PathVariable("id") Long id) {
        usuarioPrendaService.deleteAsignacion(id);
    }
}
