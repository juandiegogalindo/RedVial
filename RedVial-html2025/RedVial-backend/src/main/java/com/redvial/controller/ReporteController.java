package com.redvial.controller;

import com.redvial.model.Reporte;
import com.redvial.model.Usuario;
import com.redvial.model.Role;
import com.redvial.repository.ReporteRepository;
import com.redvial.repository.UsuarioRepository;
import com.redvial.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final JwtUtil jwtUtil;

    public ReporteController(ReporteRepository repo, UsuarioRepository usuarioRepo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.jwtUtil = jwtUtil;
    }

    // Listar reportes
    @GetMapping
    public List<Reporte> listar() {
        return repo.findAll();
    }

    // Crear reporte
    @PostMapping
    public Reporte crear(@RequestBody Reporte r) {
        if (r.getFecha() == null) {
            r.setFecha(java.time.LocalDateTime.now());
        }
        return repo.save(r);
    }

    // Eliminar reporte SOLO ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id,
                                    @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String correo = jwtUtil.extractUsername(token);

        Usuario u = usuarioRepo.findByCorreo(correo).orElse(null);

        if (u == null) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        if (u.getRol() != Role.ROLE_ADMIN) {
            return ResponseEntity.status(403).body("No tienes permisos para eliminar reportes");
        }

        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("El reporte no existe");
        }

        repo.deleteById(id);

        return ResponseEntity.ok("Reporte eliminado");
    }
}
