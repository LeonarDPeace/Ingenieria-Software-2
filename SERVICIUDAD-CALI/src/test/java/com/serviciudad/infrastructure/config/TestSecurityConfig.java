package com.serviciudad.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para entorno de testing.
 * 
 * Deshabilita completamente la autenticación y autorización
 * para facilitar las pruebas unitarias e integración de los
 * REST Controllers sin necesidad de proveer credenciales.
 * 
 * <p>Esta configuración se activa automáticamente cuando se
 * usa @SpringBootTest o @WebMvcTest en los tests.</p>
 * 
 * @author ServiCiudad Team
 * @version 1.0
 * @since 2025-10-17
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
     * Configura una cadena de filtros de seguridad permisiva para tests.
     * 
     * Permite acceso a todos los endpoints sin autenticación,
     * deshabilitando todas las restricciones de seguridad.
     * 
     * @param http Objeto HttpSecurity para configurar seguridad web
     * @return SecurityFilterChain configurado para tests
     * @throws Exception Si ocurre un error en la configuración
     */
    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // Permitir todos los requests
            )
            .csrf(AbstractHttpConfigurer::disable)  // Deshabilitar CSRF
            .httpBasic(AbstractHttpConfigurer::disable)  // Deshabilitar Basic Auth
            .formLogin(AbstractHttpConfigurer::disable);  // Deshabilitar Form Login
        
        return http.build();
    }
}
