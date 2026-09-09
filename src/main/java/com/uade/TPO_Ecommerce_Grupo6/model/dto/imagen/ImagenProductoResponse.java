package com.uade.TPO_Ecommerce_Grupo6.model.dto.imagen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenProductoResponse {
    private Long id;
    private String url;
    private Integer orden;
    private Long productoId;  // Opcional para algunos contextos, pero presente para flexibilidad
}

