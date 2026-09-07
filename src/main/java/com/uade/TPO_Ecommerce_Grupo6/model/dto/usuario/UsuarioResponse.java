package com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Datos del usuario tal como salen de la API.
 *
 * IMPORTANTE: no tiene el campo password, ni siquiera hasheado. Es la razon
 * principal por la que la consigna pide DTOs en vez de devolver la entidad:
 * asi la contraseña no puede filtrarse por descuido en ninguna respuesta.
 */
@Getter
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private LocalDateTime fechaRegistro;
}
