package com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ItemCarritoDTO {

    private Long productoId;

    private String nombre;

    private Integer cantidad;

    private BigDecimal precioUnitario;

    private BigDecimal subtotal;
}
