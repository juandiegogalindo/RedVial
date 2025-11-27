package com.redvial.service;

import com.redvial.model.Usuario;
import com.redvial.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Registro
    public String registrar(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByCorreo(usuario.getCorreo());
        if (existente.isPresent()) {
            return "El correo ya está registrado";
        }
        usuarioRepository.save(usuario);
        return "Registro exitoso";
    }

    // Login
    public String login(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByCorreo(usuario.getCorreo());
        if (existente.isPresent() && existente.get().getPassword().equals(usuario.getPassword())) {
            return "Login exitoso";
        } else {
            return "Credenciales incorrectas";
        }
    }
}