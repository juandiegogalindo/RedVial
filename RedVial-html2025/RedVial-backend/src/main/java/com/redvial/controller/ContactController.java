package com.redvial.controller;

import com.redvial.dto.ContactRequest;
import com.redvial.model.ContactMessage;
import com.redvial.model.Usuario;              // <-- IMPORTANTE
import com.redvial.repository.ContactRepository;
import com.redvial.repository.UsuarioRepository;  // <-- IMPORTANTE
import com.redvial.service.EmailService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacto")
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactRepository repo;
    private final EmailService emailService;
    private final UsuarioRepository usuarioRepo;   // <-- IMPORTANTE

    public ContactController(ContactRepository repo,
                             EmailService emailService,
                             UsuarioRepository usuarioRepo) {  // <-- IMPORTANTE
        this.repo = repo;
        this.emailService = emailService;
        this.usuarioRepo = usuarioRepo;            // <-- IMPORTANTE
    }

    @PostMapping
    public ResponseEntity<?> guardarYEnviar(@RequestBody ContactRequest r,
                                            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Debes iniciar sesión para enviar mensajes.");
        }

        // Obtener correo del usuario autenticado
        String correoUsuario = authentication.getName();

        // Buscar usuario en BD
        Usuario usuario = usuarioRepo.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Guardar mensaje en BD
        ContactMessage msg = new ContactMessage();
        msg.setNombre(usuario.getNombre());
        msg.setCorreo(correoUsuario);
        msg.setAsunto(r.getAsunto());
        msg.setMensaje(r.getMensaje());
        repo.save(msg);

        // Enviar correo
        emailService.enviarCorreoContacto(
                usuario.getNombre(),
                correoUsuario,
                r.getAsunto(),
                r.getMensaje()
        );

        return ResponseEntity.ok("Mensaje enviado correctamente.");
    }
}
