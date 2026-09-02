package com.uade.TPO_Ecommerce_Grupo6.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.ImagenProducto;

public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {

    // Trae todas las imagenes de un producto, ordenadas (la de menor orden es la principal)
    List<ImagenProducto> findByProductoIdOrderByOrdenAsc(Long productoId);
}