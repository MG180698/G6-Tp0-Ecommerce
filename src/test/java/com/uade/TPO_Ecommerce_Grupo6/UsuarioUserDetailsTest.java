package com.uade.TPO_Ecommerce_Grupo6;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.RolUsuario;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Usuario;

/**
 * Verifica el contrato que Usuario firma con Spring Security al implementar
 * UserDetails. Son pruebas de objeto puro: no levantan el contexto ni la base.
 *
 * Existen porque hoy todavia no hay nadie que consuma UserDetails (el
 * CustomUserDetailsService y el filtro de JWT son tareas aparte), asi que sin
 * estos tests el contrato quedaria sin verificar hasta que alguien lo enchufe.
 */
class UsuarioUserDetailsTest {

    private Usuario usuarioCon(RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername("ana");
        usuario.setEmail("ana@test.com");
        usuario.setPassword("$2a$10$hashDeEjemplo");
        usuario.setNombre("Ana");
        usuario.setApellido("Perez");
        usuario.setRol(rol);
        return usuario;
    }

    @Test
    void getUsernameDevuelveElEmailYNoElNombreDeUsuario() {
        Usuario usuario = usuarioCon(RolUsuario.CLIENTE);

        // Spring Security identifica al usuario por su email, que es con lo que
        // se loguea. El nombre de usuario del registro se lee por otro metodo.
        assertThat(usuario.getUsername()).isEqualTo("ana@test.com");
        assertThat(usuario.getNombreUsuario()).isEqualTo("ana");
    }

    @Test
    void getPasswordDevuelveElHashGuardado() {
        Usuario usuario = usuarioCon(RolUsuario.CLIENTE);

        assertThat(usuario.getPassword()).isEqualTo("$2a$10$hashDeEjemplo");
    }

    @Test
    void elVendedorTieneLaAutoridadRoleVendedor() {
        Usuario usuario = usuarioCon(RolUsuario.VENDEDOR);

        // El prefijo ROLE_ es el que despues hace funcionar hasRole("VENDEDOR").
        assertThat(usuario.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_VENDEDOR");
    }

    @Test
    void elClienteTieneLaAutoridadRoleCliente() {
        Usuario usuario = usuarioCon(RolUsuario.CLIENTE);

        assertThat(usuario.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_CLIENTE");
    }

    @Test
    void sinRolAsignadoSeAsumeElMasRestrictivo() {
        Usuario usuario = usuarioCon(null);

        assertThat(usuario.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_CLIENTE");
    }

    @Test
    void laCuentaEstaActivaYSinVencimientos() {
        Usuario usuario = usuarioCon(RolUsuario.CLIENTE);

        assertThat(usuario.isEnabled()).isTrue();
        assertThat(usuario.isAccountNonExpired()).isTrue();
        assertThat(usuario.isAccountNonLocked()).isTrue();
        assertThat(usuario.isCredentialsNonExpired()).isTrue();
    }
}
