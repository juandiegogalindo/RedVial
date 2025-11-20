package com.redvial.controller;

import com.redvial.dto.ContactRequest;
import com.redvial.model.ContactMessage;
import com.redvial.repository.ContactRepository;
import com.redvial.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacto")   // <- coincide con contact.js
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactRepository repo;
    private final EmailService emailService;

    public ContactController(ContactRepository repo,
                             EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> guardarYEnviar(@RequestBody ContactRequest r) {

        // 1) Guardar en BD
        ContactMessage msg = new ContactMessage();
        msg.setNombre(r.getNombre());
        msg.setCorreo(r.getCorreo());
        msg.setAsunto(r.getAsunto());
        msg.setMensaje(r.getMensaje());
        // fechaEnvio se setea en el constructor
        repo.save(msg);

        // 2) Enviar correo
        emailService.enviarCorreoContacto(
                msg.getNombre(),
                msg.getCorreo(),
                msg.getAsunto(),
                msg.getMensaje()
        );

        return ResponseEntity.ok("Mensaje recibido");
    }
}