package com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.ItemCarrito.ItemCarritoDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CarritoDTO {

    private Long id;

    private Long usuarioId;

    private List<ItemCarritoDTO> items;

    private Integer cantidadProductos;

    private BigDecimal total;
}