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
        if (correo == null ||
                !correo.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            return ResponseEntity
                    .badRequest()
                    .body("ERROR PROFE EL CORREO DEBE SER ASI (ej: usuario@gmail.com).");
        }

        // Validar teléfono: solo números y longitud 7-15
        String telefono = r.getTelefono();
        if (telefono == null || !telefono.matches("^[0-9]{10}$")) {
        return ResponseEntity
            .badRequest()
            .body("El teléfono debe contener solo números.");
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

    // =====================================================
    // CONFIRMAR VERIFICACIÓN
    // =====================================================
    @GetMapping("/confirmar")
    public ResponseEntity<String> confirmar(@RequestParam("token") String token) {

        Optional<Usuario> opt = repo.findByTokenVerificacion(token);
        if (opt.isEmpty()) {

            String html = """
                    <html>
                    <head>
                        <meta charset="UTF-8"/>
                        <style>
                            body { background:#f5f5f5; font-family:Arial; }
                            .card {
                                margin:80px auto; width:350px; padding:25px;
                                background:white; border-radius:10px;
                                box-shadow:0 2px 10px rgba(0,0,0,0.1);
                                text-align:center;
                            }
                            .btn {
                                display:block;
                                margin-top:20px;
                                padding:10px;
                                background:#007BFF;
                                color:white;
                                text-decoration:none;
                                border-radius:6px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="card">
                            <h2>Enlace inválido ❌</h2>
                            <p>El enlace ya fue usado o no es válido.</p>
                            <a class="btn" href="http://3.21.236.241:8080/registro.html">Volver al registro</a>
                        </div>
                    </body>
                    </html>
                    """;

            return ResponseEntity.status(400).header("Content-Type", "text/html").body(html);
        }

        Usuario u = opt.get();
        u.setVerificado(true);
        u.setTokenVerificacion(null);
        repo.save(u);

        String html = """
                <html>
                <head>
                    <meta charset="UTF-8"/>
                    <meta http-equiv="refresh" content="4; URL=http://3.21.236.241:8080/login.html"/>
                    <style>
                        body { background:#f5f5f5; font-family:Arial; }
                        .card {
                            margin:80px auto; width:350px; padding:25px;
                            background:white; border-radius:10px;
                            box-shadow:0 2px 10px rgba(0,0,0,0.1);
                            text-align:center;
                        }
                        .btn {
                            display:block;
                            margin-top:20px;
                            padding:10px;
                            background:#28A745;
                            color:white;
                            text-decoration:none;
                            border-radius:6px;
                        }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h2>Cuenta verificada ✅</h2>
                        <p>Ya puedes iniciar sesión.</p>
                        <a class="btn" href="http://3.21.236.241:8080">Ir al login</a>
                    </div>
                </body>
                </html>
                """;

        return ResponseEntity.ok().header("Content-Type", "text/html").body(html);
    }
}