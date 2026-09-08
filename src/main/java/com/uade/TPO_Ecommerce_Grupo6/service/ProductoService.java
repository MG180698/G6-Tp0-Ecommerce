package com.uade.TPO_Ecommerce_Grupo6.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.TPO_Ecommerce_Grupo6.exception.AccesoNoAutorizadoException;
import com.uade.TPO_Ecommerce_Grupo6.exception.CategoriaNoEncontradaException;
import com.uade.TPO_Ecommerce_Grupo6.exception.ProductoNoEncontradoException;
import com.uade.TPO_Ecommerce_Grupo6.exception.UsuarioNoEncontradoException;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ImagenProductoResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoDetalleResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.producto.ProductoResumenResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Categoria;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.ImagenProducto;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.RolUsuario;
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
                        new UsuarioNoEncontradoException(request.getUsuarioId()));

        validarQueSeaVendedor(usuario);

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
                        new UsuarioNoEncontradoException(request.getUsuarioId()));

        validarQueSeaVendedor(usuario);

        if (!producto.getUsuario().getId().equals(usuario.getId())) {
            throw new AccesoNoAutorizadoException(
                    "Solo el vendedor que publico el producto puede modificarlo");
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
                        new UsuarioNoEncontradoException(usuarioId));

        validarQueSeaVendedor(usuario);

        if (!producto.getUsuario().getId().equals(usuario.getId())) {
            throw new AccesoNoAutorizadoException(
                    "Solo el vendedor que publico el producto puede eliminarlo");
        }

        productoRepository.delete(producto);
    }

    /**
     * Descuenta el stock de un producto vendido durante el checkout.
     * Reutilizado por el Módulo 8 (Checkout) dentro de la misma transacción.
     */
    public void descontarStock(Long productoId, Integer cantidad) {
        Producto producto = buscarEntidadPorId(productoId);
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
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

    /**
     * Un e-commerce tiene un vendedor fijo, no es un marketplace: solo el rol
     * VENDEDOR puede publicar y administrar productos. El CLIENTE compra.
     */
    private void validarQueSeaVendedor(Usuario usuario) {
        if (usuario.getRol() != RolUsuario.VENDEDOR) {
            throw new AccesoNoAutorizadoException(
                    "Solo un usuario con rol VENDEDOR puede administrar productos");
        }
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