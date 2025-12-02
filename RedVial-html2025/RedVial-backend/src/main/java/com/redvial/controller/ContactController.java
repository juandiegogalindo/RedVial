package com.redvial.controller;

import com.redvial.dto.ContactRequest;
import com.redvial.model.ContactMessage;
import com.redvial.repository.ContactRepository;
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

    public ContactController(ContactRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

@PostMapping
public ResponseEntity<?> guardarYEnviar(@RequestBody ContactRequest r,
                                        Authentication authentication) {

    if (authentication == null) {
        return ResponseEntity.status(401).body("Debes iniciar sesión para enviar mensajes.");
    }

    // 1. Tomamos correo del usuario autenticado
    String correoUsuario = authentication.getName();

    // 2. Buscamos el usuario en BD para obtener su nombre real
    Usuario usuario = usuarioRepo.findByCorreo(correoUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // 3. Guardar en BD
    ContactMessage msg = new ContactMessage();
    msg.setNombre(usuario.getNombre());        // <-- nombre real
    msg.setCorreo(correoUsuario);              // <-- correo del login
    msg.setAsunto(r.getAsunto());
    msg.setMensaje(r.getMensaje());
    repo.save(msg);

    // 4. Enviar correo
    emailService.enviarCorreoContacto(
            usuario.getNombre(),      // nombre real
            correoUsuario,            // correo del login
            r.getAsunto(),
            r.getMensaje()
    );

    return ResponseEntity.ok("Mensaje enviado correctamente.");
}

}
