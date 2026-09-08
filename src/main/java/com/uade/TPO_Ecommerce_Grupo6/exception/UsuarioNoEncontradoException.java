package com.uade.TPO_Ecommerce_Grupo6.exception;
import jakarta.validation.constraints.NotNull;

public class UsuarioNoEncontradoException extends RuntimeException {
    
    public UsuarioNoEncontradoException(@NotNull(message = "El usuario es obligatorio") Long usuarioId) {
        super("No existe usuario con ID: " + usuarioId);
    }

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
