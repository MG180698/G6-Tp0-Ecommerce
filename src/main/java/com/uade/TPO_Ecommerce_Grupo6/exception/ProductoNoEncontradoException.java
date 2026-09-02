package com.uade.TPO_Ecommerce_Grupo6.exception;
// Mismo patrón que CategoriaNoEncontradaException (Módulo 3): una excepción
// de dominio por entidad, que el GlobalExceptionHandler del Módulo 2 va a
// mapear a un 404. Hasta que ese handler exista, se propaga como 500 — igual
// que le pasa hoy a CategoriaNoEncontradaException.
public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(Long id) {
        super("No se encontró el producto con ID " + id);
    }
}
