package com.uade.TPO_Ecommerce_Grupo6.exception;

/**
 * El login fallo: el email no existe o la password no coincide.
 *
 * A proposito no distingue entre los dos casos y devuelve siempre el mismo
 * mensaje. Si respondiera "ese email no existe", cualquiera podria averiguar
 * que direcciones estan registradas en el sitio probando de a una.
 *
 * Se mapea a 401 UNAUTHORIZED.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Email o contraseña incorrectos");
    }
}
