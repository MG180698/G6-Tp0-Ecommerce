package com.uade.TPO_Ecommerce_Grupo6.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Un producto con su cantidad dentro del carrito.
 * Entidad de asociacion explicita: Carrito y Producto no pueden unirse con un
 * ManyToMany directo porque la asociacion tiene un atributo propio, la cantidad.
 * Este es el patron estandar de JPA para ese caso.
 *
 */

@Entity
@Table(
        name = "items_carrito",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_carrito_producto",
                        columnNames = {"carrito_id", "producto_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Builder
    public ItemCarrito(
            Carrito carrito,
            Producto producto,
            Integer cantidad
    ) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
    }
}
