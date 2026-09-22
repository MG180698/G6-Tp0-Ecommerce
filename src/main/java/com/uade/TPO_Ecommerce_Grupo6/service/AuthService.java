package com.uade.TPO_Ecommerce_Grupo6.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.TPO_Ecommerce_Grupo6.config.JwtUtil;
import com.uade.TPO_Ecommerce_Grupo6.exception.CredencialesInvalidasException;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.auth.LoginRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.auth.LoginResponse;
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

    private static final String TIPO_TOKEN = "Bearer";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Login: valida email + password y devuelve el JWT junto con los datos
     * publicos del usuario.
     *
     * @param request LoginRequest con email y password
     * @return LoginResponse con el token y el usuario (sin password)
     * @throws CredencialesInvalidasException si el email no existe o la password es inválida
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // Buscar usuario por email.
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(CredencialesInvalidasException::new);

        // Validar password con BCrypt. Nunca se compara en texto plano.
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new CredencialesInvalidasException();
        }

        // Convertir a DTO (SIN password).
        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getId(),
                // getNombreUsuario y no getUsername: desde que la entidad
                // implementa UserDetails, getUsername() devuelve el email.
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getFechaRegistro(),
                usuario.getRol()
        );

        String token = jwtUtil.generarToken(usuario);
        return new LoginResponse(token, TIPO_TOKEN, usuarioResponse);
    }
}
