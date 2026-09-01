package com.uade.TPO_Ecommerce_Grupo6.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Publicacion de un producto en venta.
 *
 * Duenios: Modulo 4 (catalogo, lectura) y Modulo 5 (gestion, escritura).
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    // BigDecimal y no double: evita errores de redondeo al operar con dinero.
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    // Nunca negativo. La validacion vive en el service, no en la entidad.
    @Column(nullable = false)
    private Integer stock;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    // ManyToOne: muchos productos pertenecen a una unica categoria. El enunciado
    // habla de "la categoria a la cual pertenece", en singular.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Duenio de la publicacion. Esta FK es la que permite validar que solo su
    // creador pueda manejar el stock o eliminar el producto.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // OneToMany: el enunciado pide "una o mas fotos" por publicacion.
    // orphanRemoval borra la imagen de la base si se la saca de esta lista.
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenProducto> imagenes = new ArrayList<>();
}
