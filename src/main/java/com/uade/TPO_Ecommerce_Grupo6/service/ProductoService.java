package com.uade.TPO_Ecommerce_Grupo6.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.TPO_Ecommerce_Grupo6.exception.CategoriaNoEncontradaException;
import com.uade.TPO_Ecommerce_Grupo6.exception.ProductoNoEncontradoException;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ImagenProductoResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoDetalleResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoResumenResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Categoria;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.ImagenProducto;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Usuario;
import com.uade.TPO_Ecommerce_Grupo6.repository.CategoriaRepository;
import com.uade.TPO_Ecommerce_Grupo6.repository.ImagenProductoRepository;
import com.uade.TPO_Ecommerce_Grupo6.repository.ProductoRepository;
import com.uade.TPO_Ecommerce_Grupo6.repository.UsuarioRepository;

/**
 * Módulo 4 — Catálogo (solo lectura): home con listado alfabético + filtro por
 * categoría, y detalle de producto.
 *
 * El Módulo 5 (gestión: alta/baja/modificación) agrega sus métodos de
 * escritura en esta misma clase.
 */
@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ImagenProductoRepository imagenProductoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            ImagenProductoRepository imagenProductoRepository,
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository) {

        this.productoRepository = productoRepository;
        this.imagenProductoRepository = imagenProductoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ProductoDetalleResponse crear(ProductoRequest request) {

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() ->
                        new CategoriaNoEncontradaException(request.getCategoriaId()));

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe el usuario con ID " + request.getUsuarioId()));

        Producto producto = new Producto();

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setCategoria(categoria);
        producto.setUsuario(usuario);

        Producto productoGuardado = productoRepository.save(producto);

        return convertirADetalle(productoGuardado, List.of());
    }

    public ProductoDetalleResponse actualizar(Long id, ProductoRequest request) {

        Producto producto = buscarEntidadPorId(id);

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe el usuario con ID " + request.getUsuarioId()));

        if (!producto.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException(
                    "El usuario no puede modificar un producto que no le pertenece");
        }

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() ->
                        new CategoriaNoEncontradaException(request.getCategoriaId()));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);

        Producto productoActualizado = productoRepository.save(producto);

        List<ImagenProducto> imagenes =
                imagenProductoRepository.findByProductoIdOrderByOrdenAsc(id);

        return convertirADetalle(productoActualizado, imagenes);
    }

    public void eliminar(Long id, Long usuarioId) {

        Producto producto = buscarEntidadPorId(id);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe el usuario con ID " + usuarioId));

        if (!producto.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException(
                    "El usuario no puede eliminar un producto que no le pertenece");
        }

        productoRepository.delete(producto);
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

        List<ImagenProducto> imagenes =
                imagenProductoRepository.findByProductoIdOrderByOrdenAsc(id);

        return convertirADetalle(producto, imagenes);
    }

    private Producto buscarEntidadPorId(Long id) {

        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    private ProductoResumenResponse convertirAResumen(Producto producto) {

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