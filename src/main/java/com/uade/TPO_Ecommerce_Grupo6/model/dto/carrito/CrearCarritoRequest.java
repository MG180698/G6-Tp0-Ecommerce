package com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearCarritoRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

}
