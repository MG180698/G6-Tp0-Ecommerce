package com.uade.TPO_Ecommerce_Grupo6.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Carrito de compras.
 * OneToOne con Usuario: el enunciado habla de "el carrito" en singular por
 * usuario, no de varios carritos simultaneos. Se crea de forma perezosa (la
 * primera vez que el usuario agrega un item) para no llenar la tabla de
 * carritos vacios de gente que nunca compro.
 *
 *
 */
@Entity
@Table(name = "carritos")
@Getter
@Setter
@NoArgsConstructor
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            unique = true
    )
    private Usuario usuario;

    @OneToMany(
            mappedBy = "carrito",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemCarrito> items = new ArrayList<>();

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    @Builder
    public Carrito(Usuario usuario) {
        this.usuario = usuario;
    }

    public void agregarItem(ItemCarrito item) {
        items.add(item);
        item.setCarrito(this);
    }

    public void eliminarItem(ItemCarrito item) {
        items.remove(item);
        item.setCarrito(null);
    }

    public void vaciar() {
        items.clear();
    }
}
