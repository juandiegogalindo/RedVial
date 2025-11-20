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

    @GetMapping
    public List<Oferta> listar() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Oferta o, Authentication authentication) {

        try {
            // 🔹 Si hay usuario autenticado, se guarda como propietario.
            if (authentication != null) {
                String correo = authentication.getName();
                Optional<Usuario> optUser = usuarioRepo.findByCorreo(correo);
                optUser.ifPresent(o::setPropietario);
            }

            // Aquí ya viene telefonoContacto desde el JSON (si lo mandas desde JS)
            Oferta saved = repo.save(o);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al crear oferta: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id, Authentication authentication) {
        Optional<Oferta> opt = repo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Oferta oferta = opt.get();

        // Si no hay auth, de momento dejamos eliminar igual que antes (puedes endurecer después)
        if (authentication == null) {
            repo.deleteById(id);
            return ResponseEntity.ok().build();
        }

        String correo = authentication.getName();
        Usuario actual = usuarioRepo.findByCorreo(correo).orElseThrow();

        boolean esAdmin = actual.getRol().name().equals("ROLE_ADMIN");
        boolean esPropietario = oferta.getPropietario() != null
                && oferta.getPropietario().getId().equals(actual.getId());

        if (!esAdmin && !esPropietario) {
            return ResponseEntity.status(403).body("No tiene permiso para eliminar esta oferta");
        }

        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}