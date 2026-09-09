package com.uade.TPO_Ecommerce_Grupo6.exception;

/**
 * El usuario esta identificado pero no tiene permiso para esta operacion:
 * no es el dueño del recurso, o no tiene el rol necesario.
 *
 * Se mapea a 403 FORBIDDEN, que es distinto de 401: 401 es "no se quien sos",
 * 403 es "se quien sos y no podes hacer esto".
 */
public class AccesoNoAutorizadoException extends RuntimeException {

    public AccesoNoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
