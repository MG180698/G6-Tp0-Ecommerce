package com.uade.TPO_Ecommerce_Grupo6.model.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Usuario que se registra y opera en el sitio.
 *
 * Implementa UserDetails, que es el contrato que Spring Security necesita para
 * autenticar: de donde sacar el identificador, la contrasenia y los permisos.
 * Al implementarlo sobre la propia entidad, el CustomUserDetailsService puede
 * devolver el Usuario directamente, sin tener que copiarlo a otra clase.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario elegido en el registro.
     *
     * OJO: se lee con {@link #getNombreUsuario()}, no con getUsername(). Ese
     * nombre se lo lleva UserDetails, que por contrato devuelve el email.
     */
    @Getter(AccessLevel.NONE)
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

    /** El nombre de usuario del registro, no el identificador de seguridad. */
    public String getNombreUsuario() {
        return username;
    }

    // ============================================================
    // UserDetails
    // ============================================================

    /**
     * Identificador con el que Spring Security reconoce al usuario.
     *
     * Devuelve el <b>email</b>, no el campo username, porque el login del
     * enunciado pide mail y contrasenia. Tiene que coincidir con el dato por el
     * que el CustomUserDetailsService busca al usuario (findByEmail).
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Permisos del usuario.
     *
     * Spring Security espera el prefijo "ROLE_" para que despues funcionen
     * hasRole("VENDEDOR") en la configuracion y @PreAuthorize en los metodos.
     * Si el rol fuera nulo, se asume el mas restrictivo: CLIENTE.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        RolUsuario rolEfectivo = (rol != null) ? rol : RolUsuario.CLIENTE;

        return List.of(new SimpleGrantedAuthority("ROLE_" + rolEfectivo.name()));
    }

    // Los cuatro metodos de estado de la cuenta. En Spring Security 6 ya vienen
    // con implementacion por defecto que devuelve true, pero se dejan escritos
    // para que quede explicito que hoy no manejamos cuentas vencidas ni
    // bloqueadas: si mañana se agrega esa funcionalidad, se cambia aca.

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
