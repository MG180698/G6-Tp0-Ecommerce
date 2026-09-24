package com.uade.TPO_Ecommerce_Grupo6.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uade.TPO_Ecommerce_Grupo6.model.entity.RolUsuario;
import com.uade.TPO_Ecommerce_Grupo6.model.entity.Usuario;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.WeakKeyException;

/**
 * Pruebas de JwtUtil. Son de objeto puro: se instancia con "new" y no se levanta
 * el contexto de Spring ni la base, asi que corren en milisegundos.
 *
 * Los tests de token adulterado son los que justifican usar JWT: prueban que un
 * cliente no puede fabricarse un token de VENDEDOR.
 */
class JwtUtilTest {

    // Clave de prueba (32 bytes en Base64). Distinta de la de application.properties.
    private static final String SECRETO = "hAPQXs29oDte5QrZlHGJvRd0AXwEFSC00wI3XCXwMx4=";

    // Otra clave valida, para simular un token firmado por alguien que no es el servidor.
    private static final String SECRETO_AJENO = "WFq6LJQ+OIutJp/Xe2CvnrqQ+hbxNV6Bwqz9DsA+vBU=";

    private static final long UNA_HORA_MS = 3_600_000L;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRETO, UNA_HORA_MS);
    }

    private Usuario usuarioCon(String email, RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername("ana");
        usuario.setEmail(email);
        usuario.setPassword("$2a$10$hashDeEjemplo");
        usuario.setNombre("Ana");
        usuario.setApellido("Perez");
        usuario.setRol(rol);
        return usuario;
    }

    // ---------------------------------------------------------------
    // Generar y leer
    // ---------------------------------------------------------------

    @Test
    void elTokenGeneradoGuardaElEmailYNoElNombreDeUsuario() {
        String token = jwtUtil.generarToken(usuarioCon("ana@test.com", RolUsuario.CLIENTE));

        // El subject es el email, igual que UserDetails.getUsername().
        assertThat(jwtUtil.extraerEmail(token)).isEqualTo("ana@test.com");
    }

    @Test
    void elTokenGuardaElRolSinElPrefijoRole() {
        String tokenVendedor = jwtUtil.generarToken(usuarioCon("v@test.com", RolUsuario.VENDEDOR));
        String tokenCliente = jwtUtil.generarToken(usuarioCon("c@test.com", RolUsuario.CLIENTE));

        assertThat(jwtUtil.extraerRol(tokenVendedor)).isEqualTo("VENDEDOR");
        assertThat(jwtUtil.extraerRol(tokenCliente)).isEqualTo("CLIENTE");
    }

    @Test
    void elTokenNoContieneLaPassword() {
        Usuario usuario = usuarioCon("ana@test.com", RolUsuario.CLIENTE);
        String token = jwtUtil.generarToken(usuario);

        // El payload se puede leer sin la clave: no puede llevar datos sensibles.
        String payload = new String(
                Base64.getUrlDecoder().decode(token.split("\\.")[1]),
                StandardCharsets.UTF_8);

        assertThat(payload).doesNotContain(usuario.getPassword());
    }

    // ---------------------------------------------------------------
    // Validar
    // ---------------------------------------------------------------

    @Test
    void unTokenRecienGeneradoEsValido() {
        Usuario usuario = usuarioCon("ana@test.com", RolUsuario.CLIENTE);
        String token = jwtUtil.generarToken(usuario);

        assertThat(jwtUtil.esTokenValido(token)).isTrue();
        assertThat(jwtUtil.esTokenValido(token, usuario)).isTrue();
    }

    @Test
    void elTokenDeUnUsuarioNoValidaParaOtroUsuario() {
        String tokenDeAna = jwtUtil.generarToken(usuarioCon("ana@test.com", RolUsuario.CLIENTE));
        Usuario otro = usuarioCon("otro@test.com", RolUsuario.CLIENTE);

        // La firma es correcta, pero el token pertenece a otra persona.
        assertThat(jwtUtil.esTokenValido(tokenDeAna)).isTrue();
        assertThat(jwtUtil.esTokenValido(tokenDeAna, otro)).isFalse();
    }

    @Test
    void unTokenVencidoNoEsValido() {
        // Expiracion negativa: el token nace ya vencido, sin necesidad de dormir el test.
        JwtUtil conTokensVencidos = new JwtUtil(SECRETO, -1_000L);
        Usuario usuario = usuarioCon("ana@test.com", RolUsuario.CLIENTE);
        String token = conTokensVencidos.generarToken(usuario);

        assertThat(jwtUtil.esTokenValido(token)).isFalse();
        assertThat(jwtUtil.esTokenValido(token, usuario)).isFalse();
    }

    @Test
    void extraerDeUnTokenVencidoLanzaExpiredJwtException() {
        String token = new JwtUtil(SECRETO, -1_000L)
                .generarToken(usuarioCon("ana@test.com", RolUsuario.CLIENTE));

        // Los metodos extraer* no tragan la excepcion: el filtro (Tarea 5) la
        // captura para responder 401. Por eso conviene llamar antes a esTokenValido.
        assertThatThrownBy(() -> jwtUtil.extraerEmail(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void unTokenFirmadoConOtraClaveNoEsValido() {
        Usuario usuario = usuarioCon("ana@test.com", RolUsuario.VENDEDOR);
        String tokenAjeno = new JwtUtil(SECRETO_AJENO, UNA_HORA_MS).generarToken(usuario);

        assertThat(jwtUtil.esTokenValido(tokenAjeno)).isFalse();
    }

    @Test
    void unClienteNoPuedeCambiarSuRolEditandoElPayload() {
        String tokenCliente = jwtUtil.generarToken(usuarioCon("ana@test.com", RolUsuario.CLIENTE));
        String[] partes = tokenCliente.split("\\.");

        // Ataque: se reemplaza el payload por uno que dice VENDEDOR y se conserva
        // la firma original.
        String payloadFalso = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"ana@test.com\",\"rol\":\"VENDEDOR\",\"exp\":9999999999}"
                        .getBytes(StandardCharsets.UTF_8));
        String adulterado = partes[0] + "." + payloadFalso + "." + partes[2];

        // La firma se calculo sobre el payload original, asi que ya no coincide.
        assertThat(jwtUtil.esTokenValido(adulterado)).isFalse();
    }

    @Test
    void unTokenSinFirmaSeRechaza() {
        // Ataque clasico: token con alg=none, o sea sin firma. jjwt lo rechaza
        // porque el parser fue configurado con una clave.
        String sinFirma = Jwts.builder()
                .setSubject("ana@test.com")
                .claim("rol", "VENDEDOR")
                .compact();

        assertThat(jwtUtil.esTokenValido(sinFirma)).isFalse();
    }

    @Test
    void textoQueNoEsUnTokenNoEsValidoYNoLanzaExcepcion() {
        assertThat(jwtUtil.esTokenValido("esto-no-es-un-jwt")).isFalse();
        assertThat(jwtUtil.esTokenValido("")).isFalse();
        assertThat(jwtUtil.esTokenValido(null)).isFalse();
    }

    // ---------------------------------------------------------------
    // Configuracion
    // ---------------------------------------------------------------

    @Test
    void unaClaveMenorA256BitsHaceFallarLaCreacion() {
        String claveCorta = Base64.getEncoder()
                .encodeToString("corta".getBytes(StandardCharsets.UTF_8));

        // Se prefiere que la app no arranque a que firme con una clave adivinable.
        assertThatThrownBy(() -> new JwtUtil(claveCorta, UNA_HORA_MS))
                .isInstanceOf(WeakKeyException.class);
    }
}