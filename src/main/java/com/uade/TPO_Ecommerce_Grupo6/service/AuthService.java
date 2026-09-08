package com.uade.TPO_Ecommerce_Grupo6.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.TPO_Ecommerce_Grupo6.exception.UsuarioNoEncontradoException;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.auth.LoginRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario.UsuarioResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Usuario;
import com.uade.TPO_Ecommerce_Grupo6.repository.UsuarioRepository;

/**
 * Módulo 2 — Autenticación.
 *
 * Valida las credenciales del usuario (email + password).
 * Reutiliza UsuarioRepository del Módulo 1 y el PasswordEncoder de SecurityConfig.
 */
@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Login: valida email + password y devuelve los datos del usuario.
     *
     * @param request LoginRequest con email y password
     * @return UsuarioResponse (sin password)
     * @throws UsuarioNoEncontradoException si el email no existe o la password es inválida
     */
    @Transactional(readOnly = true)
    public UsuarioResponse login(LoginRequest request) {

        // Buscar usuario por email.
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe usuario registrado con email: " + request.getEmail()
                ));

        // Validar password con BCrypt. Nunca se compara en texto plano.
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new UsuarioNoEncontradoException(
                    "Credenciales inválidas para el email: " + request.getEmail()
            );
        }

        // Convertir a DTO (SIN password).
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getFechaRegistro()
        );
    }
}
