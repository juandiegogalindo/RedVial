async function enviarFormularioContacto(event) {
  event.preventDefault();

  const form = document.getElementById("contactForm");

  const nombre  = form.querySelector('input[name="name"]').value.trim();
  const asunto  = form.querySelector('input[name="subject"]').value.trim();
  const mensaje = form.querySelector('textarea[name="message"]').value.trim();

  if (!nombre || !asunto || !mensaje) {
    alert("Por favor completa todos los campos.");
    return false;
  }

  try {
    const resp = await fetch("/api/contacto", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nombre, asunto, mensaje })
    });

    if (!resp.ok) {
      const txt = await resp.text();
      console.error("Error en contacto:", txt);
      alert("Ocurrió un error al enviar el mensaje.");
      return false;
    }

    alert("Tu mensaje ha sido enviado correctamente. ¡Gracias!");
    form.reset();
    return false;
  } catch (e) {
    console.error(e);
    alert("No se pudo enviar el mensaje, intenta de nuevo.");
    return false;
  }
}