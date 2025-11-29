// login.js

async function hacerLogin(event) {
  event.preventDefault(); // para que no recargue el form

  const correo = document.getElementById("login_correo").value.trim();
  const password = document.getElementById("login_password").value.trim();

  if (!correo || !password) {
    alert("Ingresa correo y contraseña");
    return;
  }

  try {
    const resp = await fetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ correo, password })
    });

    if (!resp.ok) {
      const txt = await resp.text();
      alert("Error al iniciar sesión: " + txt);
      return;
    }

    const data = await resp.json();
    // MUY IMPORTANTE: guardamos el token en localStorage con la clave "token"
    localStorage.setItem("token", data.token);
    localStorage.setItem("usuarioId", data.id);
    localStorage.setItem("rol", data.rol);

    alert("Bienvenido");
    window.location.href = "index.html";

  } catch (e) {
    console.error(e);
    alert("Error al conectar con el servidor");
  }
}
