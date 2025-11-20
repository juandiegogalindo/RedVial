// ===============================
//   MANEJO DE TOKEN JWT
// ===============================
function getAuthHeaders() {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "login.html";
    return {};
  }
  return {
    "Content-Type": "application/json",
    "Authorization": "Bearer " + token
  };
}

// ===============================
//   CARGAR OFERTAS
// ===============================
async function cargarOfertas() {
  try {
    const resp = await fetch("/api/ofertas", {
      method: "GET",
      headers: getAuthHeaders()
    });

    if (!resp.ok) {
      console.error("Error al obtener ofertas:", resp.status);

      if (resp.status === 401 || resp.status === 403) {
        alert("Tu sesión ha expirado. Inicia sesión nuevamente.");
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
        <td colspan="7" class="text-center text-muted">
          No hay ofertas registradas.
        </td>
      `;
      tbody.appendChild(tr);
      return;
    }

    // Construcción dinámica de las filas
    data.forEach(o => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${o.id}</td>
        <td>${o.titulo}</td>
        <td>${o.origen}</td>
        <td>${o.destino}</td>
        <td>${o.salario}</td>
        <td>${o.telefonoContacto ?? "N/A"}</td>
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

// ===============================
//   CREAR OFERTA
// ===============================
async function crearOferta() {
  const titulo  = document.getElementById("of_titulo").value.trim();
  const origen  = document.getElementById("of_origen").value.trim();
  const destino = document.getElementById("of_destino").value.trim();
  const salario = document.getElementById("of_salario").value.trim();
  const telefonoContacto = document.getElementById("of_telefono").value.trim();

  if (!titulo || !origen || !destino || !salario || !telefonoContacto) {
    alert("Completa todos los campos.");
    return;
  }

  try {
    const resp = await fetch("/api/ofertas", {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify({
        titulo,
        origen,
        destino,
        salario,
        telefonoContacto
      })
    });

    if (!resp.ok) {
      console.error("Error al crear oferta:", resp.status);
      alert("Error al crear la oferta.");
      return;
    }

    alert("Oferta creada correctamente.");

    // Limpiar formulario
    document.getElementById("of_titulo").value = "";
    document.getElementById("of_origen").value = "";
    document.getElementById("of_destino").value = "";
    document.getElementById("of_salario").value = "";
    document.getElementById("of_telefono").value = "";

    cargarOfertas();

  } catch (e) {
    console.error("Error en crearOferta():", e);
    alert("No se pudo crear la oferta. Intenta nuevamente.");
  }
}

// ===============================
//   ELIMINAR OFERTA
// ===============================
async function eliminarOferta(id) {
  if (!confirm("¿Seguro que quieres eliminar esta oferta?")) return;

  try {
    const resp = await fetch(`/api/ofertas/${id}`, {
      method: "DELETE",
      headers: getAuthHeaders()
    });

    if (!resp.ok) {
      console.error("Error eliminando oferta:", resp.status);
      alert("Error al eliminar la oferta.");
      return;
    }

    cargarOfertas();

  } catch (e) {
    console.error("Error en eliminarOferta():", e);
    alert("No se pudo eliminar. Intenta nuevamente.");
  }
}