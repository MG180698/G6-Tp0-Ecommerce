package com.uade.TPO_Ecommerce_Grupo6.repository;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    Optional<ItemCarrito> findByCarrito_IdAndProducto_Id(
            Long carritoId,
            Long productoId
    );
}
