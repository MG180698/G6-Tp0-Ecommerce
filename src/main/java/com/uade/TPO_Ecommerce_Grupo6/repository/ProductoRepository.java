package com.uade.TPO_Ecommerce_Grupo6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Home: "productos ordenados alfabéticamente". Se resuelve con Spring Data
    // derivando la query del nombre del método, sin escribir JPQL a mano.
    List<Producto> findAllByOrderByNombreAsc();

    // Filtro por categoría (?categoriaId=) manteniendo el mismo orden alfabético.
    List<Producto> findByCategoriaIdOrderByNombreAsc(Long categoriaId);
}