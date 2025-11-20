// Ofertas con autenticación por JWT

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  if (!token) {
    // Por si alguien llega aquí sin estar logueado
    window.location.href = "login.html";
    return {};
  }
  return {
    "Content-Type": "application/json",
    "Authorization": "Bearer " + token
  };
}

async function cargarOfertas() {
  try {
    const resp = await fetch("/api/ofertas", {
      method: "GET",
      headers: getAuthHeaders()
    });

    if (!resp.ok) {
      console.error("Error al obtener ofertas. Status:", resp.status);
      // Si es 401/403, probablemente el token expiró
      if (resp.status === 401 || resp.status === 403) {
        alert("Tu sesión ha expirado. Inicia sesión de nuevo.");
        localStorage.removeItem("token");
        window.location.href = "login.html";
      }
      return;
    }

    const data = await resp.json();

    const tbody = document.getElementById("tabla-ofertas-body");
    tbody.innerHTML = "";

    if (data.length === 0) {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td colspan="6" class="text-center text-muted">
          No hay ofertas registradas.
        </td>
      `;
      tbody.appendChild(tr);
      return;
    }

    data.forEach(o => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${o.id}</td>
        <td>${o.titulo}</td>
        <td>${o.origen}</td>
        <td>${o.destino}</td>
        <td>${o.salario}</td>
        <td>
          <button class="btn btn-sm btn-danger" onclick="eliminarOferta(${o.id})">
            Eliminar
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } catch (e) {
    console.error("Error en cargarOfertas():", e);
  }
}

async function crearOferta() {
  const titulo  = document.getElementById("of_titulo").value.trim();
  const origen  = document.getElementById("of_origen").value.trim();
  const destino = document.getElementById("of_destino").value.trim();
  const salario = document.getElementById("of_salario").value.trim();

  if (!titulo || !origen || !destino || !salario) {
    alert("Completa todos los campos.");
    return;
  }

  try {
    const resp = await fetch("/api/ofertas", {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify({ titulo, origen, destino, salario })
    });

    if (!resp.ok) {
      console.error("Error al crear oferta. Status:", resp.status);
      alert("Error al crear oferta.");
      return;
    }

    alert("Oferta creada correctamente.");

    document.getElementById("of_titulo").value = "";
    document.getElementById("of_origen").value = "";
    document.getElementById("of_destino").value = "";
    document.getElementById("of_salario").value = "";

    cargarOfertas();
  } catch (e) {
    console.error("Error en crearOferta():", e);
    alert("No se pudo crear la oferta. Intenta nuevamente.");
  }
}

async function eliminarOferta(id) {
  if (!confirm("¿Seguro que quieres eliminar esta oferta?")) return;

  try {
    const resp = await fetch(`/api/ofertas/${id}`, {
      method: "DELETE",
      headers: getAuthHeaders()
    });

    if (!resp.ok) {
      console.error("Error al eliminar la oferta. Status:", resp.status);
      alert("Error al eliminar la oferta.");
      return;
    }

    cargarOfertas();
  } catch (e) {
    console.error("Error en eliminarOferta():", e);
    alert("No se pudo eliminar la oferta. Intenta nuevamente.");
  }
}