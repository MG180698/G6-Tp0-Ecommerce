package com.uade.TPO_Ecommerce_Grupo6.model.dto.carrito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private Long usuarioId;

    private Integer cantidadItemsComprados;

    private BigDecimal montoTotal;

    private LocalDateTime fechaCheckout;

    private String mensaje;
}
