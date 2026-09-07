package wearfind.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wearfind.Iservice.UsuarioIservice;
import wearfind.entidades.Usuario;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin (origins="http://localhost:8080")
public class UsuarioController {

    @Autowired
    private UsuarioIservice usuarioService;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.findAllUsuarios();
    }

    @GetMapping("/{id}")
    public Usuario getUsuarioById(@PathVariable Long id) {
        return usuarioService.findUsuarioById(id);
    }

    @PostMapping
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioService.saveUsuario(usuario);
    }

    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
    }
}
