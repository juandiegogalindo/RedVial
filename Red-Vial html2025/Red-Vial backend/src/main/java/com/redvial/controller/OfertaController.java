package com.redvial.controller;

import com.redvial.model.Oferta;
import com.redvial.model.Usuario;
import com.redvial.repository.OfertaRepository;
import com.redvial.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "*")
public class OfertaController {

    private final OfertaRepository repo;
    private final UsuarioRepository usuarioRepo;

    public OfertaController(OfertaRepository repo, UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
    }

    // ===============================
    //     LISTAR OFERTAS (PUBLICO)
    // ===============================
    @GetMapping
    public List<Oferta> listar() {
        return repo.findAll();
    }

    // ===============================
    //     CREAR OFERTA
    // ===============================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Oferta o, Authentication authentication) {
        try {
            // Asignar propietario si está logueado
            if (authentication != null) {
                String correo = authentication.getName();
                Optional<Usuario> u = usuarioRepo.findByCorreo(correo);
                u.ifPresent(o::setPropietario);
            }

            // ✓ teléfonoContacto ya viene en el JSON y se guarda automáticamente
            Oferta saved = repo.save(o);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error al crear oferta: " + e.getMessage());
        }
    }

    // ===============================
    //     ELIMINAR OFERTA
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id, Authentication authentication) {

        Optional<Oferta> opt = repo.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        Oferta oferta = opt.get();

        // Si no está logueado, borrar sin restricciones (tu lógica original)
        if (authentication == null) {
            repo.deleteById(id);
            return ResponseEntity.ok().build();
        }

        String correo = authentication.getName();
        Usuario actual = usuarioRepo.findByCorreo(correo).orElseThrow();

        boolean esAdmin = actual.getRol().name().equals("ROLE_ADMIN");
        boolean esPropietario = oferta.getPropietario() != null &&
                oferta.getPropietario().getId().equals(actual.getId());

        if (!esAdmin && !esPropietario) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar esta oferta.");
        }

        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}