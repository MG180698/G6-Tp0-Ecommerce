package com.uade.TPO_Ecommerce_Grupo6.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Herramienta para trabajar con tokens JWT: generarlos al hacer login, validarlos
 * en cada request y leer de ellos el email y el rol del usuario.
 *
 * Un JWT tiene tres partes separadas por puntos: header.payload.firma.
 * El payload (email, rol, vencimiento) viaja en Base64 y CUALQUIERA puede leerlo,
 * por eso nunca se guarda ahi la password. Lo que lo hace confiable es la firma:
 * se calcula con una clave que solo conoce el servidor, asi que si alguien
 * modifica el payload (por ejemplo, cambia CLIENTE por VENDEDOR) la firma deja de
 * coincidir y el token se rechaza.
 *
 * Usa jjwt 0.11.5 (la version fijada en el pom). Ojo: la API de 0.12.x es otra
 * (parser().verifyWith(...)), asi que los tutoriales nuevos no compilan aca.
 *
 * Esta clase solo sabe de tokens. No consulta la base ni toca el contexto de
 * seguridad: eso es del JwtAuthenticationFilter (Tarea 5).
 */
@Component
public class JwtUtil {

    /** Nombre del claim donde se guarda el rol (CLIENTE / VENDEDOR), sin el prefijo ROLE_. */
    private static final String CLAIM_ROL = "rol";

    /** Prefijo que Usuario.getAuthorities() le agrega al rol para que funcione hasRole(). */
    private static final String PREFIJO_ROL = "ROLE_";

    private final SecretKey clave;
    private final JwtParser parser;
    private final long expiracionMs;

    /**
     * Se recibe por constructor (y no con @Value en los campos) por dos motivos:
     * la clave se arma una sola vez y la app falla al arrancar si esta mal
     * configurada, en vez de fallar en el primer login; y la clase se puede
     * instanciar con "new JwtUtil(...)" en un test, sin levantar Spring.
     *
     * @param secretoBase64 clave secreta codificada en Base64 (minimo 256 bits = 32 bytes)
     * @param expiracionMs  cuanto dura un token, en milisegundos
     */
    public JwtUtil(@Value("${jwt.secret}") String secretoBase64,
                   @Value("${jwt.expiration-ms}") long expiracionMs) {

        // hmacShaKeyFor lanza WeakKeyException si la clave tiene menos de 256 bits:
        // HS256 exige ese minimo (RFC 7518) y una clave corta se puede adivinar.
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
        this.expiracionMs = expiracionMs;

        // El parser es inmutable y thread-safe, asi que se construye una vez y se
        // reutiliza en todos los requests.
        this.parser = Jwts.parserBuilder().setSigningKey(clave).build();
    }

    /**
     * Genera un token firmado para el usuario.
     *
     * Recibe UserDetails y no Usuario para no acoplar esta clase a la entidad;
     * como Usuario implementa UserDetails, en el login se le pasa directamente.
     *
     * Contenido del token: subject = email (getUsername() devuelve el email),
     * claim "rol", fecha de emision y fecha de vencimiento.
     */
    public String generarToken(UserDetails userDetails) {
        Date ahora = new Date();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(CLAIM_ROL, rolDe(userDetails))
                .setIssuedAt(ahora)
                .setExpiration(new Date(ahora.getTime() + expiracionMs))
                // HS256 = firma simetrica con una unica clave. Alcanza porque el
                // mismo backend que firma es el que valida. RS256 (par de claves)
                // haria falta si terceros tuvieran que verificar sin poder firmar.
                .signWith(clave, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Devuelve el email guardado en el token (el subject).
     *
     * @throws JwtException si el token esta vencido, mal formado o la firma no coincide
     */
    public String extraerEmail(String token) {
        return parsear(token).getSubject();
    }

    /**
     * Devuelve el rol guardado en el token, sin el prefijo ROLE_ (ej: "VENDEDOR").
     *
     * @throws JwtException si el token esta vencido, mal formado o la firma no coincide
     */
    public String extraerRol(String token) {
        return parsear(token).get(CLAIM_ROL, String.class);
    }

    /**
     * Dice si el token es autentico y no vencio. Nunca lanza excepcion: cualquier
     * problema (firma incorrecta, vencido, mal formado, nulo o vacio) es "false".
     *
     * Sirve para decidir si dejar pasar un request sin importar de quien es.
     */
    public boolean esTokenValido(String token) {
        try {
            parsear(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Igual que {@link #esTokenValido(String)}, y ademas exige que el token sea de
     * ESTE usuario (mismo email). Es la que va a usar el filtro despues de cargar
     * el UserDetails desde la base.
     */
    public boolean esTokenValido(String token, UserDetails userDetails) {
        try {
            String email = parsear(token).getSubject();
            return userDetails.getUsername().equals(email);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Verifica la firma y el vencimiento, y devuelve el contenido del token.
     * Es el unico lugar donde se parsea: todos los metodos publicos pasan por
     * aca, asi que ninguno lee un token sin haber verificado antes la firma.
     */
    private Claims parsear(String token) {
        return parser.parseClaimsJws(token).getBody();
    }

    /** Saca el rol de las authorities: "ROLE_VENDEDOR" -> "VENDEDOR". */
    private String rolDe(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith(PREFIJO_ROL) ? a.substring(PREFIJO_ROL.length()) : a)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario " + userDetails.getUsername() + " no tiene ningun rol asignado"));
    }
}