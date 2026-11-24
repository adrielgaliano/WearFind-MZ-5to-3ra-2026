package com.example.WearFind.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.WearFind.Repository.UsuarioRepository;
import com.example.WearFind.entidades.Usuario;

@Service
public class SesionService {

    // token -> id del usuario que inició sesión (vive en memoria: si se reinicia el servidor, hay que volver a ingresar)
    private final Map<String, Long> sesiones = new ConcurrentHashMap<>();

    @Autowired
    private UsuarioRepository usuarioRepository;

    public String iniciarSesion(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        sesiones.put(token, usuario.getId());
        return token;
    }

    public void cerrarSesion(String cabecera) {
        String token = extraerToken(cabecera);
        if (token != null) {
            sesiones.remove(token);
        }
    }

    // Devuelve el usuario dueño del token; si no hay sesión válida, responde 401
    public Usuario usuarioActual(String cabecera) {
        String token = extraerToken(cabecera);
        if (token == null || !sesiones.containsKey(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tenés que iniciar sesión");
        }
        Usuario usuario = usuarioRepository.findById(sesiones.get(token)).orElse(null);
        if (usuario == null) {
            sesiones.remove(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión ya no es válida");
        }
        return usuario;
    }

    // La cabecera viene como "Bearer <token>"
    private String extraerToken(String cabecera) {
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            return null;
        }
        return cabecera.substring(7).trim();
    }
}
