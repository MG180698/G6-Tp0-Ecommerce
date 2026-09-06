package com.uade.TPO_Ecommerce_Grupo6.exception;

public class CarritoYaExisteException extends RuntimeException {

    public CarritoYaExisteException(Long usuarioId) {
        super("El usuario " + usuarioId + " ya tiene un carrito");
    }
}
