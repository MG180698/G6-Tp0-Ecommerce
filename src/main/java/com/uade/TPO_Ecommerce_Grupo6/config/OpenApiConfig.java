package com.uade.TPO_Ecommerce_Grupo6.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST E-Commerce - Grupo 6")
                        .version("1.0.0")
                        .description("Documentación de la API REST para el sistema de E-Commerce de UADE (Aplicaciones Interactivas). " +
                                "Incluye catálogo, gestión de productos, imágenes, carrito y checkout.")
                        .contact(new Contact()
                                .name("Grupo 6 - Aplicaciones Interactivas")
                                .email("ecommerce-grupo6@uade.edu.ar"))
                        .license(new License()
                                .name("UADE Academic License")
                                .url("https://www.uade.edu.ar")));
    }
}
