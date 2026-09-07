package com.uade.TPO_Ecommerce_Grupo6.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data deriva la query del nombre del metodo, sin escribir SQL:
    // existsByEmail -> SELECT count(*) FROM usuarios WHERE email = ?
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // Lo necesita el Modulo 2 para el login: busca al usuario por su email
    // y despues compara la password con BCrypt.
    Optional<Usuario> findByEmail(String email);
}
