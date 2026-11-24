/* ============================================================
   WearFind · API Client
   Gestión de sesión en localStorage y llamadas HTTP al backend.
   ============================================================ */

class Sesion {
  static token()  { return localStorage.getItem("token"); }
  static id()     { return localStorage.getItem("usuarioId"); }
  static nombre() { return localStorage.getItem("username"); }
  static email()  { return localStorage.getItem("email"); }

  static guardar(token, usuario) {
    localStorage.setItem("token", token);
    localStorage.setItem("usuarioId", usuario.id);
    localStorage.setItem("username", usuario.username);
    localStorage.setItem("email", usuario.email);
  }

  static cerrar() {
    localStorage.clear();
  }

  static exigir() {
    if (!Sesion.token()) {
      window.location.href = "login.html";
      return false;
    }
    return true;
  }
}

const API = (function () {
  const BASE_URL = "http://localhost:8082/api";

  async function request(path, options = {}) {
    const url = `${BASE_URL}${path}`;
    const opts = {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...options.headers,
      },
    };

    if (Sesion.token()) {
      opts.headers.Authorization = "Bearer " + Sesion.token();
    }

    if (opts.body && typeof opts.body !== "string") {
      opts.body = JSON.stringify(opts.body);
    }

    let res;
    try {
      res = await fetch(url, opts);
    } catch (err) {
      throw new Error(`No se pudo conectar con el servidor en ${BASE_URL}`);
    }

    if (res.status === 204) return null;

    let data = null;
    const text = await res.text();
    if (text) {
      try { data = JSON.parse(text); }
      catch { data = text; }
    }

    if (res.status === 401 && path !== "/usuarios/login" && path !== "/usuarios/registro") {
      Sesion.cerrar();
      window.location.href = "login.html";
      throw new Error("Debes iniciar sesión");
    }

    if (!res.ok) {
      const msg = (data && (data.message || data.error)) || `Error ${res.status} ${res.statusText}`;
      throw new Error(msg);
    }

    return data;
  }

  const Prendas = {
    list: () => request("/prendas"),
    byId: (id) => request(`/prendas/${id}`),
    byUsuario: (usuarioId) => request(`/prendas/usuario/${usuarioId}`),
    create: (prenda) => request("/prendas", { method: "POST", body: prenda }),
    update: (prenda) => request(`/prendas/${prenda.id}`, { method: "PUT", body: prenda }),
    remove: (id) => request(`/prendas/${id}`, { method: "DELETE" }),
  };

  const Usuarios = {
    list: () => request("/usuarios"),
    byId: (id) => request(`/usuarios/${id}`),
    remove: (id) => request(`/usuarios/${id}`, { method: "DELETE" }),
  };

  const Auth = {
    login: (email, password) =>
      request("/usuarios/login", { method: "POST", body: { email, password } }),
    registro: (username, email, password) =>
      request("/usuarios/registro", { method: "POST", body: { username, email, password } }),
    logout: () => request("/usuarios/logout", { method: "POST" }),
  };

  return { Prendas, Usuarios, Auth, BASE_URL };
})();