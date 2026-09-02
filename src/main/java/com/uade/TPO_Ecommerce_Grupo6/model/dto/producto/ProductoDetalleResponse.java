package com.uade.TPO_Ecommerce_Grupo6.model.dto.producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Detalle de un producto (GET /api/productos/{id}): "imagen ampliada +
 * descripción", más todas las fotos ordenadas.
 *
 * Expone vendedorNombre (String), no el objeto Usuario completo: así no se
 * filtran datos del vendedor que no hacen falta acá (email, password hasheada,
 * fecha de registro). Mismo criterio que usa CategoriaResponse para no
 * exponer entidades directamente.
 */

@Getter
@AllArgsConstructor
public class ProductoDetalleResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private LocalDateTime fechaPublicacion;
    private String categoriaNombre;
    private String vendedorNombre;

    // Ordenadas por el campo "orden" de ImagenProducto (la principal primero).
    private List<ImagenProductoResponse> imagenes;
}
