package com.redvial.controller;

import com.redvial.model.Reporte;
import com.redvial.repository.ReporteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteRepository repo;

    public ReporteController(ReporteRepository repo) {
        this.repo = repo;
    }

    // Obtener TODOS los reportes
    @GetMapping
    public List<Reporte> listar() {
        return repo.findAll();
    }

    // Crear nuevo reporte
    @PostMapping
    public Reporte crear(@RequestBody Reporte r) {
        // Opcional: asegurar fecha por si viene null
        if (r.getFecha() == null) {
            r.setFecha(java.time.LocalDateTime.now());
        }
        return repo.save(r);
    }

    // Borrar (si lo quieres usar luego)
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        repo.deleteById(id);
    }
}