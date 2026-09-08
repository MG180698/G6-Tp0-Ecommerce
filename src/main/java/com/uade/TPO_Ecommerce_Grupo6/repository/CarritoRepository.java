package com.uade.TPO_Ecommerce_Grupo6.repository;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_Id(Long usuarioId);
}
