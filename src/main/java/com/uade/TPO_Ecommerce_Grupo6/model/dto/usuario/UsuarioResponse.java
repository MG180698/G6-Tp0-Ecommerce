package com.uade.TPO_Ecommerce_Grupo6.model.dto.usuario;

import java.time.LocalDateTime;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.RolUsuario;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private LocalDateTime fechaRegistro;
    private RolUsuario rol;
}
