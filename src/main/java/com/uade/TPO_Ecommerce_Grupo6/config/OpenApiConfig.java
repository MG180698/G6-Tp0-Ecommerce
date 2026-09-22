package com.uade.TPO_Ecommerce_Grupo6.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_SEGURIDAD = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(ESQUEMA_SEGURIDAD))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        ESQUEMA_SEGURIDAD,
                                        new SecurityScheme()
                                                .name(ESQUEMA_SEGURIDAD)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        "Ingresar el JWT obtenido en POST /api/auth/login")))
                .info(new Info()
                        .title("API REST E-Commerce - Grupo 6")
                        .version("1.0.0")
                        .description(
                                "Documentación de la API REST del e-commerce. "
                                + "Incluye autenticación JWT, catálogo, gestión "
                                + "de productos, imágenes, carrito y checkout.")
                        .contact(new Contact()
                                .name("Grupo 6 - Aplicaciones Interactivas")
                                .email("ecommerce-grupo6@uade.edu.ar"))
                        .license(new License()
                                .name("UADE Academic License")
                                .url("https://www.uade.edu.ar")));
    }
}