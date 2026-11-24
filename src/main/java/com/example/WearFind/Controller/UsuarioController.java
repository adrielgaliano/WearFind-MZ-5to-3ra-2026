package com.example.WearFind.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.WearFind.Iservice.UsuarioIservice;
import com.example.WearFind.Service.SesionService;
import com.example.WearFind.entidades.Usuario;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioIservice usuarioService;

    @Autowired
    private SesionService sesionService;

    public record DatosLogin(String email, String password) {
    }

    public record DatosRegistro(String username, String email, String password) {
    }

    public record RespuestaSesion(String token, Usuario usuario) {
    }

    @PostMapping("/registro")
    public RespuestaSesion registrar(@RequestBody DatosRegistro datos) {
        Usuario usuario = new Usuario();
        usuario.setUsername(datos.username());
        usuario.setEmail(datos.email());
        usuario.setPassword(datos.password());

        Usuario nuevo = usuarioService.registrarUsuario(usuario);
        return new RespuestaSesion(sesionService.iniciarSesion(nuevo), nuevo);
    }

    @PostMapping("/login")
    public RespuestaSesion iniciarSesion(@RequestBody DatosLogin datos) {
        Usuario usuario = usuarioService.autenticar(datos.email(), datos.password());
        return new RespuestaSesion(sesionService.iniciarSesion(usuario), usuario);
    }

    @PostMapping("/logout")
    public void cerrarSesion(@RequestHeader(value = "Authorization", required = false) String autorizacion) {
        sesionService.cerrarSesion(autorizacion);
    }

    @GetMapping("/me")
    public Usuario miPerfil(@RequestHeader(value = "Authorization", required = false) String autorizacion) {
        return sesionService.usuarioActual(autorizacion);
    }

    @GetMapping
    public List<Usuario> getAllUsuarios(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {
        sesionService.usuarioActual(autorizacion);
        return usuarioService.findAllUsuarios();
    }

    @GetMapping("/{id}")
    public Usuario getUsuarioById(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                                  @PathVariable("id") Long id) {
        sesionService.usuarioActual(autorizacion);
        return usuarioService.findUsuarioById(id);
    }

    @PostMapping
    public Usuario createUsuario(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                                 @RequestBody Usuario usuario) {
        sesionService.usuarioActual(autorizacion);
        return usuarioService.registrarUsuario(usuario);
    }

    @PutMapping("/{id}")
    public Usuario updateUsuario(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                                 @PathVariable("id") Long id, @RequestBody Usuario usuario) {
        Usuario actual = sesionService.usuarioActual(autorizacion);
        if (actual.getId() != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo podés editar tu propio perfil");
        }
        return usuarioService.updateUsuario(id, usuario);
    }

    @DeleteMapping("/{id}")
    public void deleteUsuario(@RequestHeader(value = "Authorization", required = false) String autorizacion,
                              @PathVariable("id") Long id) {
        Usuario actual = sesionService.usuarioActual(autorizacion);
        if (actual.getId() != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo podés eliminar tu propia cuenta");
        }
        usuarioService.deleteUsuario(id);
    }
}