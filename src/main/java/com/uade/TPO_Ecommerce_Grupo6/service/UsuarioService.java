package com.uade.TPO_Ecommerce_Grupo6.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.TPO_Ecommerce_Grupo6.exception.DatoDuplicadoException;
import com.uade.TPO_Ecommerce_Grupo6.exception.UsuarioNoEncontradoException;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario.UsuarioRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario.UsuarioResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Usuario;
import com.uade.TPO_Ecommerce_Grupo6.repository.UsuarioRepository;

/**
 * Modulo 1 — Registro y consulta de usuarios.
 *
 * El Modulo 2 (autenticacion) agrega el login aparte, en su propio service:
 * aca solo vive el alta y la consulta.
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // El bean de BCrypt lo define SecurityConfig (del kickoff).
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse registrar(UsuarioRequest request) {
        // El email y el username son unicos en la base. Si no se validara aca,
        // la violacion de la constraint saldria como un error crudo de SQL.
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DatoDuplicadoException("email", request.getEmail());
        }
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new DatoDuplicadoException("nombre de usuario", request.getUsername());
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        // Nunca se guarda la password en texto plano.
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        // La fecha la pone el servidor, no el cliente.
        usuario.setFechaRegistro(LocalDateTime.now());

        return convertirAResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        return convertirAResponse(usuario);
    }

    private UsuarioResponse convertirAResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getFechaRegistro());
    }
}
