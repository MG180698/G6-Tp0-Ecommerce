package com.uade.TPO_Ecommerce_Grupo6.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario.UsuarioRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario.UsuarioResponse;
import com.uade.TPO_Ecommerce_Grupo6.service.UsuarioService;

import jakarta.validation.Valid;

/**
 * Modulo 1 — Usuarios.
 *
 * Sin anotaciones de Swagger a proposito: springdoc lo trae el Modulo 8 y
 * todavia no esta en main. Cuando se mergee, va a documentar estos endpoints
 * igual, porque detecta todos los @RestController solo.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/usuarios -> registro
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse creado = usuarioService.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // GET /api/usuarios/{id} -> datos del usuario, sin la password
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
}
