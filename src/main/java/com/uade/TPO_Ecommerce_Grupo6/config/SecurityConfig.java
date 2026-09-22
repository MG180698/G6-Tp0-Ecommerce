package com.uade.TPO_Ecommerce_Grupo6.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF se deshabilita en APIs REST stateless: el cliente manda el token en cada request.
            .csrf(csrf -> csrf.disable())

            // Sin sesiones en el servidor. Cada request trae toda su informacion.
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Permite el frame para la consola de H2.
            .headers(h -> h.frameOptions(f -> f.disable()))

            // Reglas de autorización (Tarea 5)
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas de autenticación y documentación
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Gestión de productos: restringido a usuarios con rol VENDEDOR
                .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("VENDEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("VENDEDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("VENDEDOR")

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )

            // Registro del filtro JWT antes del filtro de usuario/contraseña estándar
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt para hashear las contrasenias.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
