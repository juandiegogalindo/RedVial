// auth.js

// Función que puedes llamar en cada página que requiera sesión
function requireLogin() {
  const token = localStorage.getItem("token");

  if (!token) {
    const path = window.location.pathname;
    // si no estamos ya en login o registro, redirigimos
    if (!path.endsWith("login.html") && !path.endsWith("registro.html")) {
      window.location.href = "login.html";
    }
  }
}

// Cerrar sesión
function logout() {
  localStorage.removeItem("token");
  alert("Sesión cerrada.");
  window.location.href = "login.html";
}