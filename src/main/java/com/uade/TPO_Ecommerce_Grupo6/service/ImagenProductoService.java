package com.uade.TPO_Ecommerce_Grupo6.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.TPO_Ecommerce_Grupo6.exception.ProductoNoEncontradoException;
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

    public ImagenProductoService(
            ImagenProductoRepository imagenProductoRepository,
            ProductoRepository productoRepository) {

        this.imagenProductoRepository = imagenProductoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public ImagenProductoResponse agregarImagen(
            Long productoId,
            ImagenProductoRequest request) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() ->
                        new ProductoNoEncontradoException(productoId));

        ImagenProducto imagen = new ImagenProducto();
        imagen.setUrl(request.getUrl());
        imagen.setOrden(request.getOrden());
        imagen.setProducto(producto);

        ImagenProducto imagenGuardada =
                imagenProductoRepository.save(imagen);

        return convertirAResponse(imagenGuardada);
    }

    private ImagenProductoResponse convertirAResponse(
            ImagenProducto imagen) {

        return new ImagenProductoResponse(
                imagen.getId(),
                imagen.getUrl(),
                imagen.getOrden(),
                imagen.getProducto().getId());
    }
}