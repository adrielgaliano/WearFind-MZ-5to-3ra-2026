/* ============================================================
   WearFind · API Client
   Capa fina sobre fetch() para hablar con el backend Spring.
   Todos los métodos devuelven Promesas; lanzan Error en caso
   de respuesta no-OK para que la UI pueda mostrar toasts.
   ============================================================ */

const API = (function () {
  // Cambiá esta URL si tu backend corre en otro host/puerto.
  // El backend Spring usa server.port=8082 en application.properties.
  const BASE_URL = "http://localhost:8082/api";

  async function request(path, options = {}) {
    const url = `${BASE_URL}${path}`;
    const opts = {
      headers: { "Content-Type": "application/json" },
      ...options,
    };
    if (opts.body && typeof opts.body !== "string") {
      opts.body = JSON.stringify(opts.body);
    }

    let res;
    try {
      res = await fetch(url, opts);
    } catch (err) {
      // Errores de red / CORS / backend caído
      throw new Error(
        "No se pudo conectar con el backend en " +
        BASE_URL +
        ". ¿Está corriendo Spring en el puerto 8082?"
      );
    }

    // 204 No Content (delete exitoso)
    if (res.status === 204) return null;

    let data = null;
    const text = await res.text();
    if (text) {
      try { data = JSON.parse(text); }
      catch { data = text; }
    }

    if (!res.ok) {
      const msg =
        (data && (data.message || data.error)) ||
        `Error ${res.status} ${res.statusText}`;
      throw new Error(msg);
    }
    return data;
  }

  /* ----- Prendas ----- */
  const Prendas = {
    list: () => request("/prendas"),
    byId: (id) => request(`/prendas/${id}`),
    byUsuario: (usuarioId) => request(`/prendas/usuario/${usuarioId}`),
    create: (prenda) => request("/prendas", { method: "POST", body: prenda }),
    remove: (id) => request(`/prendas/${id}`, { method: "DELETE" }),
    // El backend no expone PUT; para "editar" usamos POST con id (save() upsert).
    update: (prenda) => request("/prendas", { method: "POST", body: prenda }),
  };

  /* ----- Usuarios ----- */
  const Usuarios = {
    list: () => request("/usuarios"),
    byId: (id) => request(`/usuarios/${id}`),
    create: (usuario) => request("/usuarios", { method: "POST", body: usuario }),
    remove: (id) => request(`/usuarios/${id}`, { method: "DELETE" }),
  };

  return { Prendas, Usuarios, BASE_URL };
})();
