package com.uade.TPO_Ecommerce_Grupo6.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uade.TPO_Ecommerce_Grupo6.model.dto.imagen.ImagenProductoRequest;
import com.uade.TPO_Ecommerce_Grupo6.model.dto.imagen.ImagenProductoResponse;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.ImagenProducto;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;
import com.uade.TPO_Ecommerce_Grupo6.repository.ImagenProductoRepository;
import com.uade.TPO_Ecommerce_Grupo6.repository.ProductoRepository;

@Service
public class ImagenProductoService {

    private final ImagenProductoRepository imagenProductoRepository;
    private final ProductoRepository productoRepository;

    // Inyeccion por constructor: Spring pasa los repositories al crear el service
    public ImagenProductoService(ImagenProductoRepository imagenProductoRepository,
                                 ProductoRepository productoRepository) {
        this.imagenProductoRepository = imagenProductoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public ImagenProductoResponse agregarImagen(Long productoId, ImagenProductoRequest request) {
        // 1. Busco el producto. Si no existe, devuelvo 404.
        // Cuando el Modulo 5 tenga su ProductoNoEncontradoException, se puede reemplazar aca.
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe el producto con id " + productoId));

        // 2. Creo la imagen y la asocio al producto
        ImagenProducto imagen = new ImagenProducto();
        imagen.setUrl(request.getUrl());
        imagen.setOrden(request.getOrden());
        imagen.setProducto(producto);

        // 3. La guardo
        ImagenProducto guardada = imagenProductoRepository.save(imagen);

        // 4. La convierto a DTO de respuesta
        return toResponse(guardada);
    }

    // Convierte la entidad a DTO (no exponemos el objeto Producto entero, solo su id)
    private ImagenProductoResponse toResponse(ImagenProducto imagen) {
        return new ImagenProductoResponse(
                imagen.getId(),
                imagen.getUrl(),
                imagen.getOrden(),
                imagen.getProducto().getId());
    }
}