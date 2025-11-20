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
  const titulo  = document.getElementById("of_titulo").value;
  const origen  = document.getElementById("of_origen").value;
  const destino = document.getElementById("of_destino").value;
  const salario = document.getElementById("of_salario").value;

  if (!titulo || !origen || !destino || !salario) {
    alert("Completa todos los campos");
    return;
  }

  const resp = await fetch("/api/ofertas", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ titulo, origen, destino, salario })
  });

  if (!resp.ok) {
    alert("Error al crear oferta");
    return;
  }

  alert("Oferta creada");
  document.getElementById("of_titulo").value = "";
  document.getElementById("of_origen").value = "";
  document.getElementById("of_destino").value = "";
  document.getElementById("of_salario").value = "";

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