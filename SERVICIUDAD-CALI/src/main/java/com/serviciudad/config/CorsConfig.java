package com.serviciudad.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion de CORS para permitir peticiones cross-origin.
 * 
 * Esta clase configura las politicas CORS para permitir que aplicaciones
 * frontend en diferentes dominios puedan consumir la API.
 * 
 * Configuracion aplicada:
 * - Origenes permitidos: localhost:3000 (React), localhost:4200 (Angular)
 * - Metodos permitidos: GET, POST, PUT, DELETE, PATCH, OPTIONS
 * - Headers permitidos: Content-Type, Authorization, X-Requested-With
 * - Credenciales: Habilitadas
 * - Max Age: 3600 segundos (1 hora)
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 * @since 2025-01-20
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configura las reglas CORS para todos los endpoints de la API.
     * 
     * @param registry CorsRegistry para registrar configuraciones CORS
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:4200",
                        "http://localhost:5173",
                        "http://localhost:5500",
                        "http://127.0.0.1:3000",
                        "http://127.0.0.1:4200",
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:5500"
                )
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
                .allowedHeaders(
                        "Content-Type",
                        "Authorization",
                        "X-Requested-With",
                        "Accept",
                        "Origin",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers"
                )
                .exposedHeaders(
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Credentials"
                )
                .allowCredentials(true)
                .maxAge(3600);
    }
}
