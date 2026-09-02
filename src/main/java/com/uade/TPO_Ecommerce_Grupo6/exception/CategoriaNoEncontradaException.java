package com.uade.TPO_Ecommerce_Grupo6.exception;

public class CategoriaNoEncontradaException extends RuntimeException {

    public CategoriaNoEncontradaException(Long id) {
        super("No se encontró la categoría con ID " + id);
    }
}