async function cargarOfertas() {
  try {
    const resp = await fetch("/api/ofertas");
    if (!resp.ok) {
      console.error("Error al obtener ofertas");
      return;
    }
    const data = await resp.json();

    const tbody = document.getElementById("tabla-ofertas-body");
    tbody.innerHTML = "";

    data.forEach(o => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${o.id}</td>
        <td>${o.titulo}</td>
        <td>${o.origen}</td>
        <td>${o.destino}</td>
        <td>${o.salario}</td>
        <td>${o.telefonoContacto ?? ""}</td>
        <td>
          <button class="btn btn-sm btn-danger" onclick="eliminarOferta(${o.id})">
            Eliminar
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } catch (e) {
    console.error(e);
  }
}

async function crearOferta() {
  const titulo   = document.getElementById("of_titulo").value.trim();
  const origen   = document.getElementById("of_origen").value.trim();
  const destino  = document.getElementById("of_destino").value.trim();
  const salario  = document.getElementById("of_salario").value.trim();
  const telefono = document.getElementById("of_telefono").value.trim();

  if (!titulo || !origen || !destino || !salario || !telefono) {
    alert("Completa todos los campos (incluido el teléfono de contacto)");
    return;
  }

  const resp = await fetch("/api/ofertas", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      titulo,
      origen,
      destino,
      salario,
      telefonoContacto: telefono     // 👈 nombre igual al del backend
    })
  });

  if (!resp.ok) {
    alert("Error al crear oferta");
    return;
  }

  alert("Oferta creada");

  document.getElementById("of_titulo").value   = "";
  document.getElementById("of_origen").value   = "";
  document.getElementById("of_destino").value  = "";
  document.getElementById("of_salario").value  = "";
  document.getElementById("of_telefono").value = "";

  cargarOfertas();
}

async function eliminarOferta(id) {
  if (!confirm("¿Seguro que quieres eliminar esta oferta?")) return;

  const resp = await fetch(`/api/ofertas/${id}`, {
    method: "DELETE"
  });

  if (!resp.ok) {
    alert("Error al eliminar la oferta");
    return;
  }

  cargarOfertas();
}