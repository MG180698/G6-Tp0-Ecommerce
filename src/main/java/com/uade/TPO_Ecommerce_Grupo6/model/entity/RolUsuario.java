package com.uade.TPO_Ecommerce_Grupo6.model.entity;

/**
 * Rol del usuario dentro del sitio.
 *
 * El profesor aclaro que un e-commerce tiene un vendedor fijo (el dueño del
 * sitio, como la pagina de una marca), a diferencia de un marketplace donde
 * cualquiera publica. Por eso solo el rol VENDEDOR puede dar de alta, modificar
 * o eliminar productos; el CLIENTE compra.
 *
 * Se corresponde con los roles USER y ADMIN que menciona el material de la
 * Clase 05, con nombres del dominio del TPO.
 */
public enum RolUsuario {

    /** Compra: navega el catalogo, arma el carrito y hace el checkout. */
    CLIENTE,

    /** Ademas de comprar, publica y administra los productos del sitio. */
    VENDEDOR
}
