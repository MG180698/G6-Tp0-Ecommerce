package com.uade.TPO_Ecommerce_Grupo6.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Home: "productos ordenados alfabéticamente" con @EntityGraph para evitar N+1.
    // Carga imagenes y categoria en una sola query en lugar de hacerlo bajo demanda.
    @EntityGraph(attributePaths = {"imagenes", "categoria"})
    @Query("SELECT p FROM Producto p ORDER BY p.nombre ASC")
    List<Producto> findAllByOrderByNombreAsc();

    // Filtro por categoría (?categoriaId=) manteniendo el mismo orden alfabético.
    @EntityGraph(attributePaths = {"imagenes", "categoria"})
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = ?1 ORDER BY p.nombre ASC")
    List<Producto> findByCategoriaIdOrderByNombreAsc(Long categoriaId);
}