package com.uade.TPO_Ecommerce_Grupo6.model.dto.auth;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario.UsuarioResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Respuesta de autenticacion.
 *
 * El token se envia luego en cada request protegido mediante el encabezado
 * {@code Authorization: Bearer <token>}. Los datos del usuario se incluyen
 * para que el cliente no necesite hacer otra consulta despues del login.
 */
@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo;
    private UsuarioResponse usuario;
}
