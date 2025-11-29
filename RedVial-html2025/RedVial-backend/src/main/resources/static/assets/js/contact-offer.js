// ===============================
// JWT TOKEN
// ===============================
function getAuthHeaders() {
  const token = localStorage.getItem("token");
  return {
    "Content-Type": "application/json",
    "Authorization": "Bearer " + token
  };
}

// ===============================
// OBTENER ID DE LA URL
// ===============================
function getOfertaId() {
  return new URLSearchParams(window.location.search).get("id");
}

// ===============================
// CARGAR OFERTA SELECCIONADA
// ===============================
async function cargarOfertaSeleccionada() {
  const id = getOfertaId();
  const box = document.getElementById("oferta-detalle");

  if (!id) {
    box.innerHTML = `<span class="text-danger">ID inválido.</span>`;
    return;
  }

  try {
    const resp = await fetch(`/api/ofertas/${id}`, {
      method: "GET",
      headers: getAuthHeaders()
    });

    if (!resp.ok) {
      box.innerHTML = `<span class="text-danger">No se pudo cargar la oferta.</span>`;
      return;
    }

    const o = await resp.json();

    // Mostrar datos
    box.innerHTML = `
      <b>${o.titulo}</b><br>
      Origen: ${o.origen} <br>
      Destino: ${o.destino} <br>
      Salario: ${o.salario} <br>
      Teléfono: ${o.telefonoContacto} <br><br>

      <b>Estado:</b> ${
        o.aceptada
          ? "<span class='badge bg-danger'>Servicio Tomado</span>"
          : "<span class='badge bg-success'>Disponible</span>"
      }
    `;

    // Si YA está aceptada → bloquear formulario
    if (o.aceptada) {
      document.getElementById("formContacto").innerHTML = `
        <div class="alert alert-warning text-center">
          <b>Este servicio ya fue tomado por otro usuario.</b>
        </div>
      `;
      return;
    }

    // Si NO está aceptada → aceptarla ahora
    await fetch(`/api/ofertas/${id}/aceptar`, {
      method: "POST",
      headers: getAuthHeaders()
    });

  } catch (e) {
    console.error(e);
    box.innerHTML = `<span class="text-danger">Error cargando oferta.</span>`;
  }
}

// ===============================
// ENVIAR MENSAJE
// ===============================
async function enviarMensaje(event) {
  event.preventDefault();

  const nombre = document.getElementById("c_nombre").value.trim();
  const asunto = document.getElementById("c_asunto").value.trim();
  const mensaje = document.getElementById("c_mensaje").value.trim();

  const resp = await fetch("/api/contacto", {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify({ nombre, asunto, mensaje })
  });

  if (!resp.ok) {
    alert("No se pudo enviar el mensaje.");
    return;
  }

  alert("Mensaje enviado correctamente.");
  document.getElementById("formContacto").reset();
}

// Ejecutar
document.addEventListener("DOMContentLoaded", cargarOfertaSeleccionada);
