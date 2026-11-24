package com.example.WearFind.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.WearFind.Iservice.UsuarioPrendaIservice;
import com.example.WearFind.Service.SesionService;
import com.example.WearFind.entidades.Usuario;
import com.example.WearFind.entidades.UsuarioPrenda;

@RestController
@RequestMapping("/api/usuario-prendas")
@CrossOrigin(origins = "*")
public class UsuarioPrendaController {

    public record AsignacionRequest(Long usuarioId, Long prendaId) {
    }

    @Autowired
    private UsuarioPrendaIservice usuarioPrendaService;

    @Autowired
    private SesionService sesionService;

    @GetMapping
    public List<UsuarioPrenda> getAllAsignaciones(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {
        sesionService.usuarioActual(autorizacion);
        return usuarioPrendaService.findAllAsignaciones();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<UsuarioPrenda> getByUsuario(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable("usuarioId") Long usuarioId) {
        sesionService.usuarioActual(autorizacion);
        return usuarioPrendaService.findAsignacionesByUsuarioId(usuarioId);
    }

    @GetMapping("/prenda/{prendaId}")
    public List<UsuarioPrenda> getByPrenda(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable("prendaId") Long prendaId) {
        sesionService.usuarioActual(autorizacion);
        return usuarioPrendaService.findAsignacionesByPrendaId(prendaId);
    }

    @PostMapping
    public UsuarioPrenda asignar(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                                 @RequestBody AsignacionRequest request) {
        Usuario actual = sesionService.usuarioActual(autorizacion);
        if (request.usuarioId() == null || actual.getId() != request.usuarioId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo podés agregar prendas a tu propio armario");
        }
        return usuarioPrendaService.asignar(request.usuarioId(), request.prendaId());
    }

    @DeleteMapping("/usuario/{usuarioId}/prenda/{prendaId}")
    public void desasignar(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                           @PathVariable("usuarioId") Long usuarioId,
                           @PathVariable("prendaId") Long prendaId) {
        Usuario actual = sesionService.usuarioActual(autorizacion);
        if (actual.getId() != usuarioId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo podés quitar prendas de tu propio armario");
        }
        usuarioPrendaService.desasignar(usuarioId, prendaId);
    }

    @DeleteMapping("/{id}")
    public void deleteAsignacion(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                                 @PathVariable("id") Long id) {
        Usuario actual = sesionService.usuarioActual(autorizacion);
        boolean esMia = usuarioPrendaService.findAsignacionesByUsuarioId(actual.getId()).stream()
                .anyMatch(up -> up.getId() == id);
        if (!esMia) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tenés permiso para eliminar esta asignación");
        }
        usuarioPrendaService.deleteAsignacion(id);
    }
}