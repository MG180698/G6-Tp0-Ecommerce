package com.uade.TPO_Ecommerce_Grupo6.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion PROVISIONAL de Spring Security.
 *
 * Al agregar spring-boot-starter-security, Spring Boot autoconfigura seguridad
 * por defecto: pide login en TODOS los endpoints y genera una password random
 * en cada arranque. Eso deja en 401 a los endpoints que ya funcionaban, asi que
 * sin esta clase el proyecto quedaria roto para todo el equipo.
 *
 * Por eso aca se abre todo (permitAll) y se deja lista la infraestructura que
 * los modulos 1 y 2 van a necesitar: el bean de BCrypt y el modo stateless.
 *
 * === MODULO 2: ESTA CLASE ES TUYA ===
 * Reemplaza el permitAll por las reglas reales, algo como:
 *
 *     .authorizeHttpRequests(auth -> auth
 *         .requestMatchers("/api/auth/**").permitAll()
 *         .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
 *         .anyRequest().authenticated())
 *
 * y enganchale el filtro de JWT. Mientras tanto, nadie queda bloqueado.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF se deshabilita en APIs REST stateless: el ataque se apoya en
            // que el navegador manda la cookie de sesion sola, y aca no hay
            // cookie de sesion, el cliente manda el token a mano.
            .csrf(csrf -> csrf.disable())

            // Sin sesiones en el servidor. Cada request trae toda su informacion.
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // La consola de H2 se dibuja dentro de un <frame>, y Spring Security
            // los bloquea por defecto. Sin esto, /h2-console queda en blanco.
            .headers(h -> h.frameOptions(f -> f.disable()))

            // PROVISIONAL: todo abierto hasta que el modulo 2 defina las reglas.
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * BCrypt para hashear las contrasenias. Lo inyectan el registro (modulo 1)
     * y el login (modulo 2). Nunca se guarda una password en texto plano.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
