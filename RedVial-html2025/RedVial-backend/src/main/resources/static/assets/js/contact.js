async function enviarFormularioContacto(event) {
  event.preventDefault();

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Tu sesión ha expirado. Debes iniciar sesión antes de enviar mensajes.");
    window.location.href = "index.html";
    return false;
  }

  const form = document.getElementById("contactForm");

  // SOLO enviamos asunto y mensaje
  const data = {
    asunto: form.subject.value.trim(),
    mensaje: form.message.value.trim()
  };

  if (!data.asunto || !data.mensaje) {
    alert("Completa todos los campos.");
    return false;
  }

  try {
    const resp = await fetch("/api/contacto", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
      },
      body: JSON.stringify(data)
    });

    if (!resp.ok) {
      console.error("Error al enviar:", resp.status);
      alert("Error enviando mensaje.");
      return false;
    }

    alert("Mensaje enviado correctamente.");
    form.reset();
    return true;

  } catch (e) {
    console.error("Error en petición:", e);
    alert("Error enviando mensaje.");
    return false;
  }
}
