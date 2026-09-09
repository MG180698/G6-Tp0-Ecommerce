package com.uade.TPO_Ecommerce_Grupo6.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Usuario que se registra y opera en el sitio.
 *
 * El enunciado no distingue roles de comprador y vendedor: el mismo usuario que
 * compra tambien puede publicar productos. Por eso hay una unica entidad Usuario
 * y no una tabla de roles.
 *
 *
 */

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // EnumType.STRING guarda "CLIENTE" o "VENDEDOR" en la columna, no el
    // numero de posicion del enum: si mañana se agrega un rol en el medio, los
    // datos existentes no cambian de significado.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;
}
