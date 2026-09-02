package com.uade.TPO_Ecommerce_Grupo6.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}