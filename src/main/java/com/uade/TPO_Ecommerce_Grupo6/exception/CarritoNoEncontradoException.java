package com.uade.TPO_Ecommerce_Grupo6.exception;

public class CarritoNoEncontradoException extends RuntimeException {

    public CarritoNoEncontradoException(Long usuarioId) {
        super("No existe un carrito para el usuario " + usuarioId);
    }
}
