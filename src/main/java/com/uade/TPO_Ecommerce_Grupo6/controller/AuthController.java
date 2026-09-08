package com.uade.TPO_Ecommerce_Grupo6.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.auth.LoginRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario.UsuarioResponse;
import com.uade.TPO_Ecommerce_Grupo6.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Autenticación", description = "Endpoints de login y gestión de sesión")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Autenticar usuario con email y contraseña")
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest request) {
        UsuarioResponse usuario = authService.login(request);
        return ResponseEntity.ok(usuario);
    }
}
