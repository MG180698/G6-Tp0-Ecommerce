package com.uade.TPO_Ecommerce_Grupo6.model.dto.imagen;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImagenProductoRequest {

    @NotBlank(message = "La url de la imagen es obligatoria")
    private String url;

    // Opcional: define cual imagen es la principal (la de menor orden)
    private Integer orden;
}