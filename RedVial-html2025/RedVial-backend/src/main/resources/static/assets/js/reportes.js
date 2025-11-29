// reportes.js

async function cargarReportes() {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      window.location.href = "login.html";
      return;
    }

    const resp = await fetch("/api/reportes", {
      method: "GET",
      headers: {
        "Authorization": "Bearer " + token
      }
    });

    if (!resp.ok) {
      console.error("Error al obtener reportes. Código:", resp.status);
      return;
    }

    const data = await resp.json();
    const cont = document.getElementById("lista-reportes");
    cont.innerHTML = "";

    if (!data || data.length === 0) {
      cont.innerHTML = "<p>No hay reportes todavía.</p>";
      return;
    }

    data.forEach(r => {
      const div = document.createElement("div");
      div.className = "border rounded p-3 mb-3";
      div.innerHTML = `
        <h5>${r.titulo}</h5>
        <p class="mb-1"><strong>Ubicación:</strong> ${r.ubicacion}</p>
        <p>${r.descripcion}</p>
      `;
      cont.appendChild(div);
    });

  } catch (e) {
    console.error("Error en cargarReportes():", e);
  }
}

async function crearReporte() {
  const titulo      = document.getElementById("rep_titulo").value.trim();
  const ubicacion   = document.getElementById("rep_ubicacion").value.trim();
  const descripcion = document.getElementById("rep_descripcion").value.trim();

  if (!titulo || !ubicacion || !descripcion) {
    alert("Completa todos los campos");
    return;
  }

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Tu sesión ha expirado. Inicia sesión otra vez.");
    window.location.href = "login.html";
    return;
  }

  const resp = await fetch("/api/reportes", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token
    },
    body: JSON.stringify({ titulo, ubicacion, descripcion })
  });

  if (!resp.ok) {
    alert("Error al crear reporte");
    return;
  }

  alert("Reporte enviado");

  // limpiar campos
  document.getElementById("rep_titulo").value = "";
  document.getElementById("rep_ubicacion").value = "";
  document.getElementById("rep_descripcion").value = "";

  // recargar todos
  cargarReportes();
}
