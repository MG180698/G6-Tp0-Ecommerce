package com.uade.TPO_Ecommerce_Grupo6.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Un producto con su cantidad dentro del carrito.
 *
 * Entidad de asociacion explicita: Carrito y Producto no pueden unirse con un
 * ManyToMany directo porque la asociacion tiene un atributo propio, la cantidad.
 * Este es el patron estandar de JPA para ese caso.
 *
 */

@Entity
@Table(name = "items_carrito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Siempre >= 1. La validacion vive en el service.
    @Column(nullable = false)
    private Integer cantidad;
}
