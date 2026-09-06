# G6-Tp0-Ecommerce

API REST de un sistema de e-commerce, desarrollada como Trabajo Practico
Obligatorio de Aplicaciones Interactivas (UADE, 2do cuatrimestre 2026).
El backend resuelve el registro y login de usuarios, el catalogo de productos
organizado por categorias, la gestion de publicaciones con sus fotos y su stock,
el carrito de compras y el checkout. El checkout calcula el total, valida el
stock disponible y lo descuenta dentro de una unica transaccion. Esta construido
con Java 17, Spring Boot, Spring Data JPA y Maven.

---

> **Estado: kickoff.** Este repo tiene el esqueleto y las 6 entidades JPA.
> Los controllers, services y repositories los agrega cada modulo. Ver
> [Reparto de modulos](#reparto-de-modulos).

## Como levantar el proyecto

Solo hace falta **JDK 17**. La base por defecto es H2 en memoria, asi que no hay
que instalar ninguna base de datos.

```bash
./mvnw spring-boot:run
```

En Windows, desde PowerShell:

```bash
.\mvnw spring-boot:run
```

La API queda en `http://localhost:8080`. Los endpoints los va agregando cada
modulo; el kickoff solo garantiza que la app arranca y crea las tablas.

### Verificar que las tablas se crearon (consola de H2)

Abrir `http://localhost:8080/h2-console`:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:ecommercedb` |
| User | `sa` |
| Password | *(vacio)* |

> El formulario viene con `jdbc:h2:mem:testdb` precargado. **Hay que cambiarlo**
> por `jdbc:h2:mem:ecommercedb` o Connect falla, y el error aparece chiquito
> debajo del formulario.

Deberian verse 6 tablas: `usuarios`, `categorias`, `productos`,
`imagenes_producto`, `carritos`, `items_carrito`.

### Correr contra MySQL (opcional)

Con un MySQL escuchando en `localhost:3306`:

```bash
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

La forma mas simple de tener ese MySQL es el contenedor de la clase 03. Se
ejecuta **una sola vez**:

```bash
docker run --name mysql-uade -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3306:3306 -d mysql:8.0
```

De ahi en mas, cada dia alcanza con `docker start mysql-uade` (y
`docker stop mysql-uade` al terminar). A diferencia de H2, los datos sobreviven
al reinicio de la aplicacion.

La base se llama **`ecommerce_tpo`** y la crea sola el driver. Usa un nombre
propio a proposito: si el contenedor ya tiene una base de otra practica de la
materia, Hibernate encontraria tablas con un esquema distinto y las mezclaria
con las nuestras en vez de reemplazarlas.

## Estructura

```
com.uade.TPO_Ecommerce_Grupo6
├── controller/     Endpoints REST (@RestController). Solo HTTP.
├── service/        Logica de negocio y transacciones (@Service, @Transactional).
├── repository/     Acceso a datos (@Repository, extienden JpaRepository).
├── model/
│   ├── entity/     Las 6 entidades JPA. YA ESTAN, no crear nuevas.
│   └── dto/        Objetos de entrada y salida de la API.
├── exception/      Excepciones propias + manejador global.
├── config/         Seguridad, CORS, Swagger.
└── TpoEcommerceGrupo6Application.java
```

Regla de la catedra: el Controller nunca accede al Repository ni contiene
reglas de negocio, y no maneja transacciones. Eso vive en el Service. Las
entidades nunca salen de la API: siempre se convierten a DTO (asi la password
no puede filtrarse en ninguna respuesta).

## Modelo de datos

Las 6 entidades ya estan creadas y anotadas. **No hay que crear entidades
nuevas ni renombrar las que estan** — si a alguien le falta un campo, se avisa
al grupo antes de tocarlas, porque las comparten varios modulos.

### Diagrama entidad-relacion

![Diagrama entidad-relacion del e-commerce](docs/der.png)

Hecho en Lucidchart, importando el DDL que genera Hibernate a partir de las
clases `@Entity`. El diagrama sale del esquema real, no de un dibujo hecho a
mano, asi que no puede quedar desincronizado del codigo por descuido.

Para regenerarlo si cambia el modelo: volver a exportar el DDL, reimportarlo en
Lucidchart y exportar el PNG de nuevo.

Las cuatro relaciones JPA que pide la catedra, y donde esta cada una:

| Relacion | Donde |
|---|---|
| `@ManyToOne` | Producto → Categoria, Producto → Usuario, ItemCarrito → Carrito, ItemCarrito → Producto, ImagenProducto → Producto |
| `@OneToMany` | Categoria → productos, Producto → imagenes, Carrito → items |
| `@OneToOne` | Carrito → Usuario |
| `@ManyToMany` | Ninguna directa. La de Carrito ↔ Producto esta **resuelta** con `ItemCarrito`, porque la asociacion necesita guardar la cantidad. |

Decisiones que conviene poder defender en la entrega:

- **`precio` es `BigDecimal`, no `double`**: `double` arrastra errores de
  redondeo binario y con plata eso no se perdona.
- **`ItemCarrito` es una entidad y no un `@ManyToMany`**: la asociacion entre
  carrito y producto tiene un atributo propio, la cantidad. Ese es el patron
  estandar de JPA para ese caso.
- **`ImagenProducto` es una entidad y no una lista de Strings**: la catedra pide
  demostrar relaciones JPA explicitas entre entidades reales.
- **Un solo `Usuario`, sin roles**: el enunciado no distingue comprador de
  vendedor, el mismo usuario compra y publica.

## Reparto de modulos

Cada modulo es una vertical completa: su repository, su service, su controller y
sus DTOs. Las entidades ya estan y son compartidas.

| # | Modulo | Endpoints | Depende de |
|---|---|---|---|
| 1 | Usuarios | `POST /api/usuarios` | kickoff |
| 2 | Autenticacion + errores globales | `POST /api/auth/login` | 1 |
| 3 | Categorias (CRUD) | `GET/POST/PUT/DELETE /api/categorias` | kickoff |
| 4 | Catalogo (listado y detalle) | `GET /api/productos`, `GET /api/productos/{id}` | 1, 3 |
| 5 | Gestion de productos | `POST/PUT/DELETE /api/productos` | 1, 3, 4 |
| 6 | Imagenes de producto | `POST /api/productos/{id}/imagenes` | 5 |
| 7 | Carrito (items) | `GET/POST/DELETE /api/carrito` | 1, 4 |
| 8 | Checkout + documentacion | `POST /api/carrito/checkout` | 7 |

## Como trabajamos

- `main` **siempre tiene que compilar y levantar**. Antes de pushear:
  `.\mvnw test`.
- Cada uno trabaja en su rama: `feature/<numero-modulo>-<nombre-corto>`
  (ej. `feature/5-gestion-producto`).
- Al terminar, Pull Request a `main` y **otro integrante lo revisa** antes de
  mergear. Mergear seguido, no todos los PRs juntos al final.
- Commits chicos y continuos, estilo conventional commits. La catedra evalua
  cantidad, calidad y continuidad de los commits **de cada uno**:

```
feat(producto): agrego el ProductoRepository con busqueda por categoria
fix(carrito): valida stock antes de descontar en el checkout
docs(readme): agrego instrucciones de levantado
```

Tipos: `feat`, `fix`, `refactor`, `test`, `docs`.

## Seguridad

La autenticacion va con **Spring Security + JWT**, que es lo que baja la Clase 05.
Las dependencias ya estan en el `pom.xml` (`spring-boot-starter-security`,
`spring-security-test` y las tres de `jjwt` 0.11.5).

`config/SecurityConfig.java` es **provisional**: hoy abre todos los endpoints
(`permitAll`). Existe porque el solo hecho de agregar
`spring-boot-starter-security` hace que Spring Boot pida login en todo y deje en
401 los endpoints que ya funcionaban. Deja armado lo que hace falta: el bean de
BCrypt, el modo stateless y el permiso de frames para la consola de H2.

**El Modulo 2 reemplaza el `permitAll` por las reglas reales** y engancha el
filtro de JWT. Hasta que eso pase, nadie queda bloqueado.

> Ojo con la version de `jjwt`: la Clase 05 fija la **0.11.5**, y la API cambio
> bastante en la 0.12.x. Un tutorial de 0.12 no compila contra esta.

## Entrega

**Martes 8 de septiembre de 2026, 23:59.** El material de la catedra traia dos
fechas distintas para la misma entrega; esta es la confirmada.

Checklist:

- [ ] `.zip` con el codigo fuente subido a la actividad de BSP
- [ ] Link a este repositorio incluido en la entrega
- [ ] Los 3 puntos de la consigna: la app cumple los requerimientos, tiene capa
      de persistencia, y expone una API REST
- [ ] Arquitectura en capas completa (Controller / Service `@Transactional` /
      Repository / Entity / DTO)

## Decisiones que faltan tomar en equipo

1. **`Pedido` / `ItemPedido`**: el enunciado no pide historial de compras, solo
   que el checkout calcule el total y descuente stock. Queda como candidato a
   "funcionalidad extra" si sobra tiempo. Define el Modulo 8.
