package com.uade.TPO_Ecommerce_Grupo6.exception;

import jakarta.validation.constraints.NotNull;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(@NotNull(message = "El usuario es obligatorio") Long usuarioId) {
    }
}
