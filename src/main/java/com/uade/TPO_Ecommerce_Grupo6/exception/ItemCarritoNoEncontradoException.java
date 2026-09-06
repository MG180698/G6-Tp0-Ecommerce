package com.uade.TPO_Ecommerce_Grupo6.exception;

public class ItemCarritoNoEncontradoException extends RuntimeException {

    public ItemCarritoNoEncontradoException(Long productoId) {
        super(
                "El producto " + productoId +
                        " no se encuentra en el carrito"
        );
    }
}