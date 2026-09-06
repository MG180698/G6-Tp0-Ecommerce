package com.uade.TPO_Ecommerce_Grupo6.repository;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    Optional<ItemCarrito> findByCarrito_IdAndProducto_Id(
            Long carritoId,
            Long productoId
    );
}
