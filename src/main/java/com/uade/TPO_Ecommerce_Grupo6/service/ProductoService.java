package com.uade.TPO_Ecommerce_Grupo6.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uade.TPO_Ecommerce_Grupo6.exception.ProductoNoEncontradoException;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ImagenProductoResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoDetalleResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoResumenResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.ImagenProducto;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;
import com.uade.TPO_Ecommerce_Grupo6.repository.ImagenProductoRepository;
import com.uade.TPO_Ecommerce_Grupo6.repository.ProductoRepository;

/**
 * Módulo 4 — Catálogo (solo lectura): home con listado alfabético + filtro por
 * categoría, y detalle de producto.
 *
 * El Módulo 5 (gestión: alta/baja/modificación) va a agregar sus métodos de
 * escritura en esta misma clase, igual que hace CategoriaService con create/
 * update/delete. Por eso la clase queda marcada @Transactional a secas (lectura
 * y escritura por defecto) y cada método de lectura se anota aparte con
 * readOnly = true, en vez de poner readOnly = true a nivel clase — así el
 * Módulo 5 no tiene que tocar esta línea para que sus métodos de escritura
 * abran una transacción de verdad.
 */

@Service
@Transactional
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final ImagenProductoRepository imagenProductoRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            ImagenProductoRepository imagenProductoRepository) {

        this.productoRepository = productoRepository;
        this.imagenProductoRepository = imagenProductoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResumenResponse> listarTodos(Long categoriaId) {
        List<Producto> productos = (categoriaId != null)
                ? productoRepository.findByCategoriaIdOrderByNombreAsc(categoriaId)
                : productoRepository.findAllByOrderByNombreAsc();

        return productos.stream()
                .map(this::convertirAResumen)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoDetalleResponse buscarPorId(Long id) {
        Producto producto = buscarEntidadPorId(id);

        // Reutiliza el método que dejó el Módulo 6 en vez de escribir uno propio:
        // ya trae las imágenes ordenadas por "orden" resuelto en la base.
        List<ImagenProducto> imagenes =
                imagenProductoRepository.findByProductoIdOrderByOrdenAsc(id);

        return convertirADetalle(producto, imagenes);
    }

    private Producto buscarEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    private ProductoResumenResponse convertirAResumen(Producto producto) {
        // Acá sí se usa la colección LAZY de la entidad (no el repository de
        // imágenes): pedirle al repository la imagen principal producto por
        // producto dentro de este map() generaría una consulta extra por cada
        // fila del listado (N+1). Para un solo producto (el detalle, arriba)
        // esa consulta extra no pesa; para una lista de N productos sí.
        String imagenPrincipalUrl = producto.getImagenes().stream()
                .min(Comparator.comparing(
                        ImagenProducto::getOrden,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(ImagenProducto::getUrl)
                .orElse(null);

        return new ProductoResumenResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria().getNombre(),
                imagenPrincipalUrl
        );
    }

    private ProductoDetalleResponse convertirADetalle(
            Producto producto,
            List<ImagenProducto> imagenes) {

        List<ImagenProductoResponse> imagenesResponse = imagenes.stream()
                .map(imagen -> new ImagenProductoResponse(
                        imagen.getId(),
                        imagen.getUrl(),
                        imagen.getOrden()))
                .toList();

        String vendedorNombre =
                producto.getUsuario().getNombre() + " " + producto.getUsuario().getApellido();

        return new ProductoDetalleResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getFechaPublicacion(),
                producto.getCategoria().getNombre(),
                vendedorNombre,
                imagenesResponse
        );
    }
}
