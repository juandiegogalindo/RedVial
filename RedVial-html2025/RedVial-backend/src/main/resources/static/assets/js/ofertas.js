// ===============================
//   MANEJO DE TOKEN JWT
// ===============================
function getAuthHeaders() {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "index.html";
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

    data.forEach(o => {
      const tr = document.createElement("tr");
      let buttonHTML = `
        <button class="btn btn-sm btn-success" onclick="aceptarOferta(${o.id})">
          Aceptar y Contactar
        </button>
      `;

      // Si la oferta ya está aceptada, deshabilitamos el botón
      if (o.aceptada) {
        buttonHTML = `
          <button class="btn btn-sm btn-secondary" disabled>
            Oferta ya aceptada
          </button>
        `;
      }

      tr.innerHTML = `
        <td>${o.id}</td>
        <td>${o.titulo}</td>
        <td>${o.origen}</td>
        <td>${o.destino}</td>
        <td>${o.salario}</td>
        <td>${o.telefonoContacto ?? "N/A"}</td>
        <td>${buttonHTML}</td>
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
//   NUEVO: ACEPTAR OFERTA
// ===============================
function aceptarOferta(id) {
  window.location.href = "contact-offer.html?id=" + id;
}
