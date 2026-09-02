package com.uade.TPO_Ecommerce_Grupo6.model.dto.producto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Producto tal como aparece en el listado del home (GET /api/productos).
 *
 * A propósito NO trae la descripción completa ni la lista entera de imágenes:
 * esas dos cosas solo hacen falta en el detalle (ProductoDetalleResponse). Si
 * el listado tuviera 50 productos, mandar la descripción larga y todas las
 * fotos de cada uno en cada respuesta sería payload de sobra — alcanza con
 * una imagen (la principal) para la miniatura.
 */

@Getter
@AllArgsConstructor
public class ProductoResumenResponse {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private String categoriaNombre;

    // Null si el producto todavía no tiene ninguna foto cargada.
    private String imagenPrincipalUrl;
}
