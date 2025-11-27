async function registro() {
    const user = document.getElementById("user").value.trim();
    const email = document.getElementById("email").value.trim();
    const telefono = document.getElementById("telefono").value.trim();
    const pass = document.getElementById("pass").value.trim();

    if (!user || !email || !telefono || !pass) {
        alert("Por favor completa todos los campos.");
        return;
    }

    const gmailRegex = /^[A-Za-z0-9._%+-]+@gmail\.com$/;

    if (!gmailRegex.test(email)) {
        alert("El correo debe ser un Gmail válido (ej: usuario@gmail.com).");
        return;
    }

    const data = {
        nombre: user,
        correo: email,
        telefono: telefono,
        password: pass
    };

    try {
        const response = await fetch("http://3.21.236.241:8080/api/auth/registro", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errorText = await response.text();
            alert("Error en el registro: " + errorText);
            return;
        }

        alert("Registro exitoso. Ahora puedes iniciar sesión.");
        window.location.href = "login.html";

    } catch (error) {
        console.error("Error en fetch:", error);
        alert("No se pudo conectar con el servidor.");
    }
}