/* ============================================================
   WearFind · App logic
   Estado global, render de vistas, CRUD, filtros, modales.
   ============================================================ */

const App = (function () {

  /* ---------- Estado ---------- */
  const state = {
    view: "dashboard",
    prendas: [],
    usuarios: [],
    currentUserId: null, // null = ver todos
    search: "",
    seasonFilter: "all",
    favOnly: false,
    confirmCallback: null,
  };

  /* ---------- Utilidades ---------- */
  const $  = (sel, ctx = document) => ctx.querySelector(sel);
  const $$ = (sel, ctx = document) => Array.from(ctx.querySelectorAll(sel));

  // Mapa de colores -> hex para los puntos/swatches
  const COLOR_MAP = {
    "negro": "#1f1b16", "blanco": "#f8f5ef", "gris": "#9ca3af",
    "beige": "#e8d8c4", "cámel": "#c19a6b", "camel": "#c19a6b",
    "azul": "#3b82f6", "verde": "#10b981", "rojo": "#ef4444",
    "bordó": "#7f1d1d", "bordo": "#7f1d1d", "rosa": "#ec4899",
    "marrón": "#92400e", "marron": "#92400e", "amarillo": "#f59e0b",
    "naranja": "#f97316", "violeta": "#8b5cf6", "celeste": "#38bdf8",
    "mostaza": "#d97706", "jean": "#60a5fa",
  };

  function colorToHex(name) {
    if (!name) return "#9ca3af";
    const key = String(name).toLowerCase().trim();
    return COLOR_MAP[key] || "#9ca3af";
  }

  function seasonClass(s) {
    if (!s) return "season-Todo";
    const key = s.toLowerCase();
    if (key.includes("todo")) return "season-Todo";
    if (key.includes("invierno")) return "season-Invierno";
    if (key.includes("verano")) return "season-Verano";
    if (key.includes("oto")) return "season-Otoño";
    if (key.includes("primavera")) return "season-Primavera";
    return "season-Todo";
  }
  function barClass(s) {
    if (!s) return "bar-Todo";
    const key = s.toLowerCase();
    if (key.includes("todo")) return "bar-Todo";
    if (key.includes("invierno")) return "bar-Invierno";
    if (key.includes("verano")) return "bar-Verano";
    if (key.includes("oto")) return "bar-Otoño";
    if (key.includes("primavera")) return "bar-Primavera";
    return "bar-Todo";
  }

  // Escape HTML para evitar inyecciones en texto del DOM
  function esc(str) {
    if (str == null) return "";
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  // Imagen con fallback a placeholder SVG
  function imgStyleOrFallback(url, nombre) {
    if (url) {
      return `style="background-image:url('${esc(url)}')"`;
    }
    const letter = (nombre || "?").trim().charAt(0).toUpperCase();
    return `class="no-img" data-letter="${esc(letter)}"`;
  }

  // Toast
  let toastTimer;
  function toast(msg, type = "") {
    const el = $("#toast");
    el.textContent = msg;
    el.className = "toast show " + type;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => {
      el.classList.remove("show");
    }, 2800);
  }

  /* ---------- Carga de datos ---------- */
  async function loadAll() {
    try {
      const [prendas, usuarios] = await Promise.all([
        API.Prendas.list(),
        API.Usuarios.list(),
      ]);
      state.prendas = Array.isArray(prendas) ? prendas : [];
      state.usuarios = Array.isArray(usuarios) ? usuarios : [];
    } catch (err) {
      console.error(err);
      toast(err.message || "Error al cargar datos", "error");
      state.prendas = [];
      state.usuarios = [];
    }
  }

  // Mapa id -> usuario para lookup rápido
  function usuarioById(id) {
    return state.usuarios.find((u) => String(u.id) === String(id));
  }

  // Prendas filtradas según el usuario seleccionado
  function prendasForCurrent() {
    if (state.currentUserId == null) return state.prendas;
    return state.prendas.filter((p) => {
      const owner = p.usuario;
      if (!owner) return false;
      return String(owner.id) === String(state.currentUserId);
    });
  }

  /* ---------- Routing de vistas ---------- */
  function setView(view) {
    state.view = view;
    $$(".view").forEach((v) => v.classList.add("hidden"));
    const target = $("#view-" + view);
    if (target) target.classList.remove("hidden");

    $$(".nav-item").forEach((n) => n.classList.toggle("active", n.dataset.view === view));

    // Scroll al top
    $(".content").scrollTo({ top: 0, behavior: "smooth" });

    // Cerrar sidebar en mobile
    closeSidebar();
  }

  /* ---------- Render: Dashboard ---------- */
  function renderDashboard() {
    const prendas = state.prendas; // stats globales
    const total = prendas.length;
    const favs = prendas.filter((p) => p.favorito).length;
    const totalUsers = state.usuarios.length;

    // Distribución por estación
    const seasonCount = {};
    prendas.forEach((p) => {
      const s = p.estacion || "Todo el año";
      seasonCount[s] = (seasonCount[s] || 0) + 1;
    });
    const seasonEntries = Object.entries(seasonCount).sort((a, b) => b[1] - a[1]);
    const maxSeason = Math.max(1, ...seasonEntries.map((e) => e[1]));

    // Top 5 colores
    const colorCount = {};
    prendas.forEach((p) => {
      if (!p.color) return;
      const c = p.color.trim();
      if (!c) return;
      colorCount[c] = (colorCount[c] || 0) + 1;
    });
    const colorEntries = Object.entries(colorCount)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5);

    // Stats cards
    const statIcons = {
      total: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.38 3.46 16 2a4 4 0 0 1-8 0L3.62 3.46a2 2 0 0 0-1.34 2.23l.58 3.47a1 1 0 0 0 .99.84H6v10c0 1.1.9 2 2 2h8a2 2 0 0 0 2-2V10h2.15a1 1 0 0 0 .99-.84l.58-3.47a2 2 0 0 0-1.34-2.23z"/></svg>`,
      fav: `<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" stroke="none"><path d="M12 17.27 18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/></svg>`,
      users: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`,
      season: `<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>`,
    };
    const stats = [
      { label: "Prendas totales", value: total, icon: statIcons.total, color: "linear-gradient(135deg,#6d28d9,#a855f7)" },
      { label: "Favoritos", value: favs, icon: statIcons.fav, color: "linear-gradient(135deg,#e11d48,#fb7185)" },
      { label: "Usuarios", value: totalUsers, icon: statIcons.users, color: "linear-gradient(135deg,#0891b2,#22d3ee)" },
      { label: "Estaciones distintas", value: seasonEntries.length, icon: statIcons.season, color: "linear-gradient(135deg,#ca8a04,#facc15)" },
    ];

    $("#statGrid").innerHTML = stats.map((s) => `
      <div class="stat">
        <div class="stat-icon" style="background:${s.color}">${s.icon}</div>
        <div class="stat-value">${s.value}</div>
        <div class="stat-label">${s.label}</div>
      </div>
    `).join("");

    $("#totalSeasons").textContent = `${seasonEntries.length} estaciones`;

    // Bars por estación
    if (seasonEntries.length === 0) {
      $("#seasonBars").innerHTML = `<p class="muted">Sin datos para mostrar.</p>`;
    } else {
      $("#seasonBars").innerHTML = seasonEntries.map(([name, count]) => `
        <div class="bar-row">
          <span class="bar-label">${esc(name)}</span>
          <div class="bar-track">
            <div class="bar-fill ${barClass(name)}" style="width:${(count / maxSeason) * 100}%"></div>
          </div>
          <span class="bar-value">${count}</span>
        </div>
      `).join("");
    }

    // Color list
    if (colorEntries.length === 0) {
      $("#colorList").innerHTML = `<p class="muted">Sin colores registrados.</p>`;
    } else {
      const maxColor = Math.max(1, ...colorEntries.map((e) => e[1]));
      $("#colorList").innerHTML = colorEntries.map(([name, count]) => `
        <div class="color-item">
          <span class="color-dot" style="background:${colorToHex(name)}"></span>
          <span class="color-name">${esc(name)}</span>
          <div class="bar-track" style="max-width:90px">
            <div class="bar-fill" style="background:${colorToHex(name)};width:${(count / maxColor) * 100}%"></div>
          </div>
          <span class="color-count">${count}</span>
        </div>
      `).join("");
    }

    // Favoritos grid
    const favPrendas = prendas.filter((p) => p.favorito).slice(0, 8);
    if (favPrendas.length === 0) {
      $("#favGrid").innerHTML = `<p class="muted" style="padding:12px">Aún no hay prendas marcadas como favoritas.</p>`;
    } else {
      $("#favGrid").innerHTML = favPrendas.map((p) => {
        const img = p.url_imagen
          ? `style="background-image:url('${esc(p.url_imagen)}')"`
          : `class="no-img"`;
        const letter = (p.nombre || "?").charAt(0).toUpperCase();
        return `
          <div class="mini-card">
            <div class="mini-img" ${img}>
              ${p.url_imagen ? "" : `<div class="placeholder">${esc(letter)}</div>`}
            </div>
            <div class="mini-info">
              <strong>${esc(p.nombre || "Sin nombre")}</strong>
              <span>${esc(p.color || "—")} · ${esc(p.estacion || "—")}</span>
            </div>
          </div>
        `;
      }).join("");
    }
  }

  /* ---------- Render: Prendas grid ---------- */
  function renderPrendas() {
    let list = prendasForCurrent();

    // Filtro por estación
    if (state.seasonFilter !== "all") {
      list = list.filter((p) => (p.estacion || "") === state.seasonFilter);
    }
    // Filtro por favoritos
    if (state.favOnly) {
      list = list.filter((p) => p.favorito);
    }
    // Filtro por búsqueda
    if (state.search) {
      const q = state.search.toLowerCase();
      list = list.filter((p) => {
        return [p.nombre, p.descripcion, p.color, p.marca, p.estacion, p.talle]
          .filter(Boolean)
          .some((v) => String(v).toLowerCase().includes(q));
      });
    }

    // Subtitle
    const owner = state.currentUserId != null ? usuarioById(state.currentUserId) : null;
    const ownerName = owner ? `de @${esc(owner.username)}` : "de todos los usuarios";
    $("#prendasSubtitle").textContent = `${list.length} ${list.length === 1 ? "prenda" : "prendas"} ${ownerName}`;

    const grid = $("#prendaGrid");
    const empty = $("#prendaEmpty");

    if (list.length === 0) {
      grid.innerHTML = "";
      empty.classList.remove("hidden");
      return;
    }
    empty.classList.add("hidden");

    grid.innerHTML = list.map((p) => {
      const fav = p.favorito;
      const letter = (p.nombre || "?").charAt(0).toUpperCase();
      const owner = p.usuario;
      const ownerName = owner ? `@${esc(owner.username)}` : "Sin asignar";

      const imgBlock = p.url_imagen
        ? `<div class="prenda-img" style="background-image:url('${esc(p.url_imagen)}')"></div>`
        : `<div class="prenda-img"><div class="no-img"><div class="placeholder">${esc(letter)}</div></div></div>`;

      return `
        <article class="card-prenda ${fav ? "fav" : ""}" data-id="${p.id}">
          ${imgBlock}
          <button class="fav-badge ${fav ? "active" : ""}" data-action="fav" data-id="${p.id}" title="${fav ? "Quitar de favoritos" : "Marcar como favorito"}">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="${fav ? "currentColor" : "none"}" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 17.27 18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/></svg>
          </button>
          <span class="season-tag ${seasonClass(p.estacion)}">${esc(p.estacion || "Todo el año")}</span>
          <div class="prenda-body">
            <h3 class="prenda-name">${esc(p.nombre || "Sin nombre")}</h3>
            <div class="prenda-meta">
              ${p.color ? `<span class="color-pill" style="--c:${colorToHex(p.color)}">${esc(p.color)}</span>` : ""}
              ${p.talle ? `<span>Talle ${esc(p.talle)}</span>` : ""}
              ${p.marca ? `<span>· ${esc(p.marca)}</span>` : ""}
            </div>
            ${p.descripcion ? `<p class="prenda-desc">${esc(p.descripcion)}</p>` : ""}
            <div class="prenda-foot">
              <span class="owner">👤 <strong>${ownerName}</strong></span>
              <div class="card-actions">
                <button data-action="edit" data-id="${p.id}" title="Editar">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                </button>
                <button class="del" data-action="delete" data-id="${p.id}" title="Eliminar">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                </button>
              </div>
            </div>
          </div>
        </article>
      `;
    }).join("");
  }

  /* ---------- Render: Usuarios ---------- */
  function renderUsuarios() {
    const grid = $("#userGrid");
    const empty = $("#userEmpty");
    if (state.usuarios.length === 0) {
      grid.innerHTML = "";
      empty.classList.remove("hidden");
      return;
    }
    empty.classList.add("hidden");

    grid.innerHTML = state.usuarios.map((u) => {
      const initial = (u.username || "?").charAt(0).toUpperCase();
      const count = state.prendas.filter((p) => p.usuario && String(p.usuario.id) === String(u.id)).length;
      const favCount = state.prendas.filter(
        (p) => p.usuario && String(p.usuario.id) === String(u.id) && p.favorito
      ).length;
      return `
        <article class="user-card-lg" data-id="${u.id}">
          <div class="user-head">
            <div class="avatar-lg">${esc(initial)}</div>
            <div>
              <div class="user-name">@${esc(u.username)}</div>
              <div class="user-email">${esc(u.email)}</div>
            </div>
          </div>
          <div class="user-stats">
            <div class="user-stat">
              <strong>${count}</strong>
              <span>Prendas</span>
            </div>
            <div class="user-stat">
              <strong>${favCount}</strong>
              <span>Favoritos</span>
            </div>
            <div class="user-stat">
              <strong>#${u.id}</strong>
              <span>ID</span>
            </div>
          </div>
          <div class="user-actions">
            <button class="btn btn-ghost" data-user-action="view" data-id="${u.id}">Ver armario</button>
            <button class="btn btn-ghost" data-user-action="delete" data-id="${u.id}">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
            </button>
          </div>
        </article>
      `;
    }).join("");
  }

  /* ---------- Render: Sidebar user switcher + drawer ---------- */
  function renderUserSwitcher() {
    const owner = state.currentUserId != null ? usuarioById(state.currentUserId) : null;
    if (owner) {
      $("#currentUserName").textContent = "@" + owner.username;
      $("#currentUserAvatar").textContent = (owner.username || "?").charAt(0).toUpperCase();
    } else {
      $("#currentUserName").textContent = "Todos";
      $("#currentUserAvatar").textContent = "∞";
    }

    // Drawer items
    const body = $("#drawerBody");
    const items = state.usuarios.map((u) => {
      const active = String(state.currentUserId) === String(u.id);
      const initial = (u.username || "?").charAt(0).toUpperCase();
      return `
        <button class="drawer-item ${active ? "active" : ""}" data-user-id="${u.id}">
          <div class="avatar sm">${esc(initial)}</div>
          <div>
            <strong>@${esc(u.username)}</strong>
            <span>${esc(u.email)}</span>
          </div>
        </button>
      `;
    }).join("");
    const allActive = state.currentUserId == null;
    body.innerHTML = `
      <button class="drawer-item ${allActive ? "active" : ""}" data-user-id="">
        <div class="avatar sm">∞</div>
        <div>
          <strong>Todos</strong>
          <span>Ver todas las prendas</span>
        </div>
      </button>
      ${items}
    `;
  }

  /* ---------- Render: select de usuarios en form ---------- */
  function populateUsuarioSelect(selectedId) {
    const sel = $("#usuarioSelect");
    sel.innerHTML = `<option value="">— Sin asignar —</option>` +
      state.usuarios.map((u) =>
        `<option value="${u.id}" ${String(selectedId) === String(u.id) ? "selected" : ""}>@${esc(u.username)} (${esc(u.email)})</option>`
      ).join("");
  }

  /* ---------- Render maestro ---------- */
  function renderAll() {
    renderUserSwitcher();
    renderDashboard();
    renderPrendas();
    renderUsuarios();
    populateUsuarioSelect();
  }

  /* ---------- Acciones: Prenda CRUD ---------- */
  async function deletePrenda(id) {
    try {
      await API.Prendas.remove(id);
      state.prendas = state.prendas.filter((p) => String(p.id) !== String(id));
      renderAll();
      toast("Prenda eliminada", "success");
    } catch (err) {
      toast(err.message || "No se pudo eliminar", "error");
    }
  }

  async function toggleFav(prenda) {
    try {
      prenda.favorito = !prenda.favorito;
      const updated = await API.Prendas.update(prenda);
      Object.assign(prenda, updated);
      renderAll();
      toast(prenda.favorito ? "★ Agregado a favoritos" : "Quitado de favoritos", "success");
    } catch (err) {
      // revert
      prenda.favorito = !prenda.favorito;
      toast(err.message || "No se pudo actualizar", "error");
    }
  }

  /* ---------- Acciones: Usuario CRUD ---------- */
  async function deleteUsuario(id) {
    try {
      await API.Usuarios.remove(id);
      state.usuarios = state.usuarios.filter((u) => String(u.id) !== String(id));
      state.prendas = state.prendas.filter((p) => !p.usuario || String(p.usuario.id) !== String(id));
      if (String(state.currentUserId) === String(id)) state.currentUserId = null;
      renderAll();
      toast("Usuario eliminado", "success");
    } catch (err) {
      toast(err.message || "No se pudo eliminar el usuario", "error");
    }
  }

  async function createUsuario(username, email) {
    try {
      const u = await API.Usuarios.create({ username, email });
      state.usuarios.push(u);
      renderAll();
      toast("Usuario creado ✓", "success");
      return u;
    } catch (err) {
      toast(err.message || "No se pudo crear el usuario", "error");
      throw err;
    }
  }

  /* ---------- Form: Prenda create/edit ---------- */
  function openPrendaForm(prenda) {
    const form = $("#prendaForm");
    form.reset();
    if (prenda) {
      form.nombre.value = prenda.nombre || "";
      form.descripcion.value = prenda.descripcion || "";
      form.color.value = prenda.color || "";
      form.estacion.value = prenda.estacion || "Todo el año";
      form.talle.value = prenda.talle || "";
      form.marca.value = prenda.marca || "";
      form.url_imagen.value = prenda.url_imagen || "";
      form.favorito.checked = !!prenda.favorito;
      populateUsuarioSelect(prenda.usuario ? prenda.usuario.id : "");
      form.dataset.editId = prenda.id;
    } else {
      delete form.dataset.editId;
      populateUsuarioSelect(state.currentUserId);
      form.estacion.value = "Todo el año";
    }
    updateImgPreview();
    setView("agregar");
  }

  function updateImgPreview() {
    const url = $("#urlImagenInput").value.trim();
    const preview = $("#imgPreview");
    if (url) {
      preview.style.backgroundImage = `url('${url}')`;
      preview.classList.add("has-img");
    } else {
      preview.style.backgroundImage = "";
      preview.classList.remove("has-img");
    }
  }

  async function submitPrendaForm(e) {
    e.preventDefault();
    const form = e.target;
    const data = {
      nombre: form.nombre.value.trim(),
      descripcion: form.descripcion.value.trim(),
      color: form.color.value.trim(),
      estacion: form.estacion.value,
      talle: form.talle.value,
      marca: form.marca.value.trim(),
      url_imagen: form.url_imagen.value.trim(),
      favorito: form.favorito.checked,
    };
    const usuarioId = form.usuarioId.value;
    if (usuarioId) {
      const u = usuarioById(usuarioId);
      if (u) data.usuario = { id: u.id, username: u.username, email: u.email };
    }

    if (!data.nombre) {
      toast("El nombre es obligatorio", "error");
      return;
    }

    const btn = $("#savePrendaBtn");
    btn.disabled = true;
    btn.querySelector("span").textContent = "Guardando…";

    try {
      if (form.dataset.editId) {
        // Edición: adjuntamos id y enviamos por POST (upsert)
        const id = Number(form.dataset.editId);
        const existing = state.prendas.find((p) => String(p.id) === String(id));
        data.id = id;
        // conservar relaciones si no se asignó usuario
        if (!data.usuario && existing && existing.usuario) {
          data.usuario = existing.usuario;
        }
        const updated = await API.Prendas.update(data);
        const idx = state.prendas.findIndex((p) => String(p.id) === String(id));
        if (idx >= 0) state.prendas[idx] = Object.assign(state.prendas[idx], updated);
        toast("Prenda actualizada ✓", "success");
      } else {
        const created = await API.Prendas.create(data);
        state.prendas.push(created);
        toast("Prenda creada ✓", "success");
      }
      await loadAll(); // refrescar para mantener consistencia
      renderAll();
      setView("prendas");
    } catch (err) {
      toast(err.message || "No se pudo guardar", "error");
    } finally {
      btn.disabled = false;
      btn.querySelector("span").textContent = "Guardar prenda";
    }
  }

  /* ---------- Confirm modal ---------- */
  function showConfirm({ title, message, onAccept }) {
    $("#confirmTitle").textContent = title || "¿Confirmar?";
    $("#confirmMessage").textContent = message || "Esta acción no se puede deshacer.";
    state.confirmCallback = onAccept;
    $("#confirmModal").classList.remove("hidden");
  }
  function hideConfirm() {
    $("#confirmModal").classList.add("hidden");
    state.confirmCallback = null;
  }

  /* ---------- User modal ---------- */
  function showUserModal() {
    $("#userForm").reset();
    $("#userModal").classList.remove("hidden");
  }
  function hideUserModal() {
    $("#userModal").classList.add("hidden");
  }

  /* ---------- Drawer ---------- */
  function openDrawer() { $("#userDrawer").classList.remove("hidden"); }
  function closeDrawer() { $("#userDrawer").classList.add("hidden"); }

  /* ---------- Sidebar mobile ---------- */
  function openSidebar() {
    $("#sidebar").classList.add("open");
    if (!document.querySelector(".scrim")) {
      const scrim = document.createElement("div");
      scrim.className = "scrim";
      document.body.appendChild(scrim);
      scrim.addEventListener("click", closeSidebar);
      setTimeout(() => scrim.classList.add("show"), 10);
    }
  }
  function closeSidebar() {
    $("#sidebar").classList.remove("open");
    const scrim = document.querySelector(".scrim");
    if (scrim) {
      scrim.classList.remove("show");
      setTimeout(() => scrim.remove(), 250);
    }
  }

  /* ---------- Bindings ---------- */
  function bind() {
    // Nav
    $$(".nav-item").forEach((btn) => {
      btn.addEventListener("click", () => setView(btn.dataset.view));
    });
    // data-goto links
    document.addEventListener("click", (e) => {
      const link = e.target.closest("[data-goto]");
      if (link) setView(link.dataset.goto);
    });

    // New prenda button (topbar + empty state)
    $("#newPrendaBtn").addEventListener("click", () => openPrendaForm(null));
    document.addEventListener("click", (e) => {
      const a = e.target.closest('[data-action="new"]');
      if (a) openPrendaForm(null);
    });

    // Refresh
    $("#refreshBtn").addEventListener("click", async () => {
      toast("Actualizando…");
      await loadAll();
      renderAll();
      toast("Datos actualizados ✓", "success");
    });

    // Search
    $("#searchInput").addEventListener("input", (e) => {
      state.search = e.target.value.trim();
      if (state.view !== "prendas") setView("prendas");
      renderPrendas();
    });

    // Season chips
    $("#seasonChips").addEventListener("click", (e) => {
      const chip = e.target.closest(".chip-btn");
      if (!chip) return;
      $$("#seasonChips .chip-btn").forEach((c) => c.classList.remove("active"));
      chip.classList.add("active");
      state.seasonFilter = chip.dataset.season;
      renderPrendas();
    });

    // Fav toggle
    $("#favOnlyToggle").addEventListener("change", (e) => {
      state.favOnly = e.target.checked;
      renderPrendas();
    });

    // Prenda grid actions (delegado)
    $("#prendaGrid").addEventListener("click", (e) => {
      const btn = e.target.closest("[data-action]");
      if (!btn) return;
      const id = Number(btn.dataset.id);
      const prenda = state.prendas.find((p) => String(p.id) === String(id));
      if (!prenda) return;
      const action = btn.dataset.action;
      if (action === "fav") {
        toggleFav(prenda);
      } else if (action === "delete") {
        showConfirm({
          title: "¿Eliminar prenda?",
          message: `"${prenda.nombre}" se eliminará permanentemente.`,
          onAccept: () => deletePrenda(id),
        });
      } else if (action === "edit") {
        openPrendaForm(prenda);
      }
    });

    // Form: imagen preview
    $("#urlImagenInput").addEventListener("input", updateImgPreview);

    // Form submit
    $("#prendaForm").addEventListener("submit", submitPrendaForm);

    // Form cancel
    document.addEventListener("click", (e) => {
      const c = e.target.closest('[data-action="cancel-prenda"]');
      if (c) {
        setView("prendas");
      }
    });

    // New user
    $("#newUserBtn").addEventListener("click", showUserModal);
    $("#userCancel").addEventListener("click", hideUserModal);
    $("#userForm").addEventListener("submit", async (e) => {
      e.preventDefault();
      const f = e.target;
      const username = f.username.value.trim();
      const email = f.email.value.trim();
      if (!username || !email) {
        toast("Completá username y email", "error");
        return;
      }
      const u = await createUsuario(username, email);
      if (u) hideUserModal();
    });

    // Usuario grid actions
    $("#userGrid").addEventListener("click", (e) => {
      const btn = e.target.closest("[data-user-action]");
      if (!btn) return;
      const id = Number(btn.dataset.id);
      const action = btn.dataset.userAction;
      if (action === "view") {
        state.currentUserId = id;
        state.seasonFilter = "all";
        state.favOnly = false;
        $$("#seasonChips .chip-btn").forEach((c) =>
          c.classList.toggle("active", c.dataset.season === "all")
        );
        $("#favOnlyToggle").checked = false;
        renderAll();
        setView("prendas");
      } else if (action === "delete") {
        const u = usuarioById(id);
        showConfirm({
          title: "¿Eliminar usuario?",
          message: `Se eliminará @${u.username} y sus prendas asociadas.`,
          onAccept: () => deleteUsuario(id),
        });
      }
    });

    // Confirm modal
    $("#confirmCancel").addEventListener("click", hideConfirm);
    $("#confirmAccept").addEventListener("click", () => {
      if (state.confirmCallback) state.confirmCallback();
      hideConfirm();
    });
    $$(".modal-backdrop").forEach((b) => b.addEventListener("click", hideConfirm));

    // Drawer
    $("#userSwitcher").addEventListener("click", openDrawer);
    $("#drawerClose").addEventListener("click", closeDrawer);
    $(".drawer-backdrop").addEventListener("click", closeDrawer);
    $("#drawerBody").addEventListener("click", (e) => {
      const item = e.target.closest("[data-user-id]");
      if (!item) return;
      const id = item.dataset.userId;
      state.currentUserId = id ? Number(id) : null;
      state.seasonFilter = "all";
      state.favOnly = false;
      $$("#seasonChips .chip-btn").forEach((c) =>
        c.classList.toggle("active", c.dataset.season === "all")
      );
      $("#favOnlyToggle").checked = false;
      renderAll();
      closeDrawer();
      setView("prendas");
    });

    // Mobile menu
    $("#menuToggle").addEventListener("click", openSidebar);

    // Tecla ESC cierra modales/drawer
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") {
        hideConfirm();
        hideUserModal();
        closeDrawer();
        closeSidebar();
      }
    });
  }

  /* ---------- Init ---------- */
  async function init() {
    bind();
    toast("Cargando datos…");
    await loadAll();
    renderAll();
    if (state.prendas.length === 0 && state.usuarios.length === 0) {
      toast(
        "No hay datos. ¿Backend corriendo en :8082 y MySQL con datos en wearfind?",
        "error"
      );
    } else {
      toast(`Cargadas ${state.prendas.length} prendas y ${state.usuarios.length} usuarios`, "success");
    }
  }

  return { init };
})();

document.addEventListener("DOMContentLoaded", App.init);
