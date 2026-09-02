package com.uade.TPO_Ecommerce_Grupo6.model.dto.producto;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Una foto de producto, tal como se expone en la API.
 *
 * Se comparte entre el detalle de producto (Módulo 4) y el alta de
 * imágenes (Módulo 6), por eso vive en su propio archivo y no como clase
 * anidada de ProductoDetalleResponse.
 */

@Getter
@AllArgsConstructor
public class ImagenProductoResponse {
    private Long id;
    private String url;
    private Integer orden;
}
