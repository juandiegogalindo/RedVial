package com.redvial.controller;

import com.redvial.dto.AuthRequest;
import com.redvial.dto.AuthResponse;
import com.redvial.dto.RegisterRequest;
import com.redvial.model.Role;
import com.redvial.model.Usuario;
import com.redvial.repository.UsuarioRepository;
import com.redvial.security.JwtUtil;
import com.redvial.service.EmailService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository repo;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    public AuthController(UsuarioRepository repo,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          PasswordEncoder encoder,
                          EmailService emailService) {
        this.repo = repo;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    // =====================================================
    // REGISTRO
    // =====================================================
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegisterRequest r) {

        // Validar campos obligatorios
        if (r.getNombre() == null || r.getNombre().trim().isEmpty() ||
        r.getCorreo() == null || r.getCorreo().trim().isEmpty() ||
        r.getTelefono() == null || r.getTelefono().trim().isEmpty() ||
        r.getPassword() == null || r.getPassword().trim().isEmpty()) {

    return ResponseEntity
            .badRequest()
            .body("Todos los campos son obligatorios.");
}

        if (r.getCorreo() != null) {
            r.setCorreo(r.getCorreo().trim().toLowerCase());
        }

       String correo = r.getCorreo();

        // REGEX OPCIÓN C: aceptar solo correos con dominio real (no localhost)
        String regexCorreoValido = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,24}$";

        if (correo == null || !correo.matches(regexCorreoValido)) {
        return ResponseEntity
            .badRequest()
            .body("El correo ingresado no es válido. Usa un correo real como gmail, outlook, yahoo o tu correo institucional.");
        }


        // Validar teléfono: solo números y longitud 7-15
        String telefono = r.getTelefono();
        if (telefono == null || !telefono.matches("^[0-9]{10}$")) {
        return ResponseEntity
            .badRequest()
            .body("El teléfono debe contener solo números (10 Caracteres).");
        }

        // 🔹 Verificar si ya está registrado
        if (repo.findByCorreo(correo).isPresent()) {
            return ResponseEntity.status(409).body("El correo ya está registrado");
        }

        Usuario u = new Usuario();
        u.setNombre(r.getNombre());
        u.setCorreo(correo);
        u.setTelefono(r.getTelefono());
        u.setPassword(encoder.encode(r.getPassword()));
        u.setRol(Role.ROLE_USER);

        u.setVerificado(false);
        u.setFechaRegistro(LocalDateTime.now());
        u.setTokenVerificacion(UUID.randomUUID().toString());

        repo.save(u);

        emailService.enviarCorreoRegistro(u.getCorreo(), u.getTokenVerificacion());

        return ResponseEntity.ok("Usuario registrado. Revisa tu correo para verificar tu cuenta.");
    }

    // =====================================================
    // LOGIN
    // =====================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest r) {

        Optional<Usuario> opt = repo.findByCorreo(r.getCorreo());
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }

        Usuario usuario = opt.get();

        if (!usuario.isVerificado()) {
            return ResponseEntity.status(403).body("Debes verificar tu correo.");
        }

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            r.getCorreo(),
                            r.getPassword()
                    )
            );

            String token = jwtUtil.generateToken(usuario.getCorreo());

            return ResponseEntity.ok(
                    new AuthResponse(
                            token,
                            usuario.getId(),
                            usuario.getRol().name()
                    )
            );

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/loginAdmin")
    public ResponseEntity<?> loginAdmin(@RequestBody AuthRequest r) {

    Optional<Usuario> opt = repo.findByCorreo(r.getCorreo());
    if (opt.isEmpty()) {
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    Usuario usuario = opt.get();

    // 🔹 Debe estar verificado
    if (!usuario.isVerificado()) {
        return ResponseEntity.status(403).body("Debes verificar tu correo.");
    }

    // 🔹 VALIDAR QUE SEA ADMINISTRADOR
    if (usuario.getRol() != Role.ROLE_ADMIN) {
        return ResponseEntity.status(403).body("No tienes permisos de administrador.");
    }

    try {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        r.getCorreo(),
                        r.getPassword()
                )
        );

        String token = jwtUtil.generateToken(usuario.getCorreo());

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        usuario.getId(),
                        usuario.getRol().name()
                )
        );

    } catch (AuthenticationException e) {
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }
}


    // =====================================================
    // CONFIRMAR VERIFICACIÓN
    // =====================================================
   @GetMapping("/confirmar")
public ResponseEntity<String> confirmar(@RequestParam("token") String token) {

    Optional<Usuario> opt = repo.findByTokenVerificacion(token);

    // =====================================================
    // 1. TOKEN NO EXISTE → LINK INVÁLIDO
    // =====================================================
    if (opt.isEmpty()) {
        String html = """
                <html>
                <head><meta charset="UTF-8"/></head>
                <body style="background:#f5f5f5;font-family:Arial;">
                    <div style="margin:80px auto;width:350px;padding:25px;background:white;
                                border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1);
                                text-align:center;">
                        <h2>Enlace inválido ❌</h2>
                        <p>El enlace ya fue usado o no es válido.</p>
                        <a href="https://redvial.site/registro.html"
                           style="display:block;margin-top:20px;padding:10px;background:#007BFF;
                                  color:white;text-decoration:none;border-radius:6px;">
                           Volver al registro
                        </a>
                    </div>
                </body>
                </html>
                """;

        return ResponseEntity.status(400)
                .header("Content-Type", "text/html")
                .body(html);
    }

    Usuario u = opt.get();

    // =====================================================
    // 2. SI YA ESTABA VERIFICADO → MOSTRAR MENSAJE
    // =====================================================
    if (u.isVerificado()) {

        String html = """
                <html>
                <head><meta charset="UTF-8"/></head>
                <body style="background:#f5f5f5;font-family:Arial;">
                    <div style="margin:80px auto;width:350px;padding:25px;background:white;
                                border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1);
                                text-align:center;">
                        <h2>Esta cuenta ya fue verificada ✔</h2>
                        <p>Puedes iniciar sesión sin problema.</p>
                        <a href="https://redvial.site/"
                           style="display:block;margin-top:20px;padding:10px;background:#28A745;
                                  color:white;text-decoration:none;border-radius:6px;">
                           Ir al login
                        </a>
                    </div>
                </body>
                </html>
                """;

        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(html);
    }

    // =====================================================
    // 3. VALIDAR EXPIRACIÓN DEL TOKEN (1 HORA)
    // =====================================================
    LocalDateTime fecha = u.getFechaRegistro();
    LocalDateTime expiracion = fecha.plusHours(1); // <-- aquí puedes poner plusMinutes(1) para pruebas
    LocalDateTime ahora = LocalDateTime.now();

    if (expiracion.isBefore(ahora)) {

        // ❗ ELIMINAR USUARIO NO VERIFICADO
        repo.delete(u);

        String html = """
                <html>
                <head><meta charset="UTF-8"/></head>
                <body style="background:#f5f5f5;font-family:Arial;">
                    <div style="margin:80px auto;width:350px;padding:25px;background:white;
                                border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1);
                                text-align:center;">
                        <h2>Enlace expirado ⏰</h2>
                        <p>Tu enlace de verificación venció (duración máxima: 1 hora).</p>
                        <p>Regístrate nuevamente para recibir un nuevo enlace.</p>
                        <a href="https://redvial.site/registro.html"
                           style="display:block;margin-top:20px;padding:10px;background:#ffc107;
                                  color:black;text-decoration:none;border-radius:6px;">
                           Volver al registro
                        </a>
                    </div>
                </body>
                </html>
                """;

        return ResponseEntity.status(400)
                .header("Content-Type", "text/html")
                .body(html);
    }

    // =====================================================
    // 4. TOKEN VÁLIDO → VERIFICAR USUARIO
    // =====================================================
    u.setVerificado(true);
    u.setTokenVerificacion(null);
    repo.save(u);

    String html = """
            <html>
            <head><meta charset="UTF-8"/><meta http-equiv="refresh" content="4; URL=https://redvial.site/"/></head>
            <body style="background:#f5f5f5;font-family:Arial;">
                <div style="margin:80px auto;width:350px;padding:25px;background:white;
                            border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1);
                            text-align:center;">
                    <h2>Cuenta verificada ✅</h2>
                    <p>Ya puedes iniciar sesión.</p>
                    <a href="https://redvial.site/"
                       style="display:block;margin-top:20px;padding:10px;background:#28A745;
                              color:white;text-decoration:none;border-radius:6px;">
                       Ir al login
                    </a>
                </div>
            </body>
            </html>
            """;

    return ResponseEntity.ok()
            .header("Content-Type", "text/html")
            .body(html);
}

}
