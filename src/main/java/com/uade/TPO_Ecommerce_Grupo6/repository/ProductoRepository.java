package com.uade.TPO_Ecommerce_Grupo6.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}