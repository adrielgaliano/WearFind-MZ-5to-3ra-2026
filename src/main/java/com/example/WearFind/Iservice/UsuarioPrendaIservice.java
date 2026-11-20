package com.example.WearFind.Iservice;

import java.util.List;
import com.example.WearFind.entidades.UsuarioPrenda;

public interface UsuarioPrendaIservice {
    public List<UsuarioPrenda> findAllAsignaciones();
    public List<UsuarioPrenda> findAsignacionesByUsuarioId(Long usuarioId);
    public List<UsuarioPrenda> findAsignacionesByPrendaId(Long prendaId);
    public UsuarioPrenda asignar(Long usuarioId, Long prendaId);
    public void desasignar(Long usuarioId, Long prendaId);
    public void deleteAsignacion(Long id);
}
