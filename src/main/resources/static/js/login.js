/* ============================================================
   WearFind · Login y Registro
   ============================================================ */

(function () {
  const form          = document.getElementById("authForm");
  const usernameField = document.getElementById("usernameField");
  const usernameInput = document.getElementById("username");
  const passwordInput = document.getElementById("password");
  const errorBox      = document.getElementById("authError");
  const btn           = document.getElementById("authBtn");

  let modoRegistro = false;

  function mostrarError(msg) {
    errorBox.textContent = msg;
    errorBox.hidden = !msg;
  }

  document.getElementById("authToggle").addEventListener("click", (e) => {
    e.preventDefault();
    modoRegistro = !modoRegistro;
    mostrarError("");

    usernameField.hidden = !modoRegistro;
    usernameInput.required = modoRegistro;
    passwordInput.autocomplete = modoRegistro ? "new-password" : "current-password";

    document.getElementById("authTitle").textContent = modoRegistro ? "Crear cuenta" : "Iniciar sesión";
    document.getElementById("authSubtitle").textContent = modoRegistro
      ? "Creá tu cuenta para empezar a armar tu armario."
      : "Ingresá para ver y administrar tu armario.";
    document.getElementById("authAltText").textContent = modoRegistro ? "¿Ya tenés cuenta?" : "¿No tenés cuenta?";
    e.target.textContent = modoRegistro ? "Ingresá" : "Creá una";
    btn.querySelector("span").textContent = modoRegistro ? "Crear cuenta" : "Ingresar";
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    mostrarError("");
    btn.disabled = true;

    const email    = document.getElementById("email").value.trim();
    const password = passwordInput.value;

    try {
      const resultado = modoRegistro
        ? await API.Auth.registro(usernameInput.value.trim(), email, password)
        : await API.Auth.login(email, password);

      Sesion.guardar(resultado.token, resultado.usuario);
      window.location.href = "index.html";
    } catch (err) {
      mostrarError(err.message);
      btn.disabled = false;
    }
  });
})();