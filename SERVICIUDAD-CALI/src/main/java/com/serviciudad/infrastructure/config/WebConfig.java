package com.serviciudad.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring Web MVC.
 * 
 * Registra interceptores y configuraciones personalizadas
 * para el procesamiento de requests HTTP.
 * 
 * Configuración adicional:
 * - Servir archivos estáticos del frontend desde /frontend/**
 * - Mapeo de recursos HTML, CSS, JS
 * 
 * @author ServiCiudad Team
 * @version 1.0
 * @since 2025-10-17
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    /**
     * Registra interceptores HTTP.
     * 
     * - RateLimitInterceptor: Aplica a /api/** (endpoints de negocio)
     * - Excluye: /actuator/**, /swagger-ui/**, /v3/api-docs/**, /frontend/**
     * 
     * @param registry Registro de interceptores
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/api/**")  // Aplicar solo a endpoints de la API
            .excludePathPatterns(
                "/actuator/**",          // Excluir actuator (health checks)
                "/swagger-ui/**",        // Excluir Swagger UI
                "/swagger-ui.html",      // Excluir Swagger página principal
                "/v3/api-docs/**",       // Excluir OpenAPI docs
                "/api-docs/**",          // Excluir API documentation
                "/frontend/**",          // Excluir archivos estáticos del frontend
                "/",                     // Excluir página principal
                "/index.html"            // Excluir index
            );
    }

    /**
     * Configura el manejo de recursos estáticos.
     * 
     * Sirve los archivos del frontend (HTML, CSS, JS) desde:
     * - classpath:/static/ (archivos empaquetados en el JAR)
     * - file:./frontend/ (archivos en desarrollo)
     * 
     * Accesibles en: http://localhost:8080/**
     * 
     * IMPORTANTE: Esta configuración NO interfiere con los endpoints REST (/api/**)
     * porque Spring MVC da prioridad a los @RequestMapping sobre los ResourceHandlers.
     * 
     * @param registry Registro de resource handlers
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Servir archivos estáticos desde el classpath y sistema de archivos
        registry.addResourceHandler("/**")
            .addResourceLocations(
                "classpath:/static/",
                "file:./frontend/"
            )
            .setCachePeriod(3600); // Cache de 1 hora
    }
}

