package com.uade.TPO_Ecommerce_Grupo6.exception;

/**
 * Se lanza cuando el registro choca contra un dato que tiene que ser unico
 * (email o username ya usados).
 *
 * Modulo 2: mapear a 409 CONFLICT en el GlobalExceptionHandler.
 */
public class DatoDuplicadoException extends RuntimeException {

    public DatoDuplicadoException(String campo, String valor) {
        super("Ya existe un usuario con el " + campo + " '" + valor + "'");
    }
}
