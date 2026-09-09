package com.uade.TPO_Ecommerce_Grupo6;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Categoria;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Producto;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.RolUsuario;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Usuario;
import com.uade.TPO_Ecommerce_Grupo6.repository.ProductoRepository;

/**
 * @DataJpaTest levanta solo la capa JPA contra H2 (no el service/controller),
 * y hace rollback solo al final de cada test. Alcanza para probar las dos
 * queries derivadas de ProductoRepository sin necesitar el Módulo 1 (Usuario
 * se persiste directo con TestEntityManager, sin pasar por un repository).
 */

@DataJpaTest
class ProductoRepositoryTest {
    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager entityManager;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void listaLosProductosOrdenadosAlfabeticamente() {
        Categoria categoria = persistirCategoria("Indumentaria");
        Usuario vendedor = persistirVendedor();

        persistirProducto("Zapatillas", categoria, vendedor);
        persistirProducto("Buzo", categoria, vendedor);
        persistirProducto("Campera", categoria, vendedor);

        List<Producto> productos = productoRepository.findAllByOrderByNombreAsc();

        assertThat(productos)
                .extracting(Producto::getNombre)
                .containsExactly("Buzo", "Campera", "Zapatillas");
    }

    @Test
    void filtraPorCategoriaSinTraerLasDeOtraCategoria() {
        Categoria indumentaria = persistirCategoria("Indumentaria");
        Categoria tecnologia = persistirCategoria("Tecnología");
        Usuario vendedor = persistirVendedor();

        persistirProducto("Buzo", indumentaria, vendedor);
        persistirProducto("Notebook", tecnologia, vendedor);

        List<Producto> productos =
                productoRepository.findByCategoriaIdOrderByNombreAsc(indumentaria.getId());

        assertThat(productos)
                .extracting(Producto::getNombre)
                .containsExactly("Buzo");
    }

    private Categoria persistirCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return entityManager.persistAndFlush(categoria);
    }

    private Usuario persistirVendedor() {
        Usuario usuario = new Usuario();
        usuario.setUsername("vendedor" + System.nanoTime());
        usuario.setEmail("vendedor" + System.nanoTime() + "@test.com");
        usuario.setPassword("hash");
        usuario.setNombre("Test");
        usuario.setApellido("Vendedor");
        // El rol es obligatorio desde que se separaron CLIENTE y VENDEDOR.
        usuario.setRol(RolUsuario.VENDEDOR);
        return entityManager.persistAndFlush(usuario);
    }

    private void persistirProducto(String nombre, Categoria categoria, Usuario vendedor) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion("Descripción de " + nombre);
        producto.setPrecio(new BigDecimal("100.00"));
        producto.setStock(10);
        producto.setCategoria(categoria);
        producto.setUsuario(vendedor);
        entityManager.persistAndFlush(producto);
    }
}
