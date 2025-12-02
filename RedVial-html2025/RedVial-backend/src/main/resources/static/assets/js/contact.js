async function enviarFormularioContacto(event) {
  event.preventDefault();

  const asunto = document.querySelector("input[name='subject']").value.trim();
  const mensaje = document.querySelector("textarea[name='message']").value.trim();

  if (!asunto || !mensaje) {
    alert("Completa todos los campos.");
    return false;
  }

  const token = localStorage.getItem("token");

  const resp = await fetch("/api/contacto", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token
    },
    body: JSON.stringify({
      asunto,
      mensaje
    })
  });

  if (!resp.ok) {
    alert("Error enviando mensaje.");
    return false;
  }

  alert("Mensaje enviado correctamente.");
  document.getElementById("contactForm").reset();
  return false;
}
