package com.serviciudad.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuración de seguridad de Spring Security.
 * 
 * Implementa autenticación básica HTTP con usuario en memoria.
 * En producción, se recomienda usar un UserDetailsService con base de datos
 * y autenticación basada en tokens (JWT).
 * 
 * <p>Características de seguridad implementadas:</p>
 * <ul>
 *   <li>Autenticación HTTP Basic</li>
 *   <li>Sesiones Stateless (sin estado) - apropiado para API REST</li>
 *   <li>CSRF deshabilitado (API REST stateless)</li>
 *   <li>Endpoints públicos: /actuator/health, Swagger UI, API Docs</li>
 *   <li>Todos los demás endpoints requieren autenticación</li>
 * </ul>
 * 
 * <p><strong>⚠️ ADVERTENCIA DE SEGURIDAD:</strong></p>
 * <ul>
 *   <li>Usuario en memoria solo para desarrollo/testing</li>
 *   <li>Contraseña en texto plano (usar {bcrypt} en producción)</li>
 *   <li>Cambiar credenciales antes de desplegar a producción</li>
 * </ul>
 * 
 * @author ServiCiudad Team
 * @version 1.0
 * @since 2025-10-17
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad.
     * 
     * Define las reglas de autorización, mecanismos de autenticación
     * y políticas de sesión para la aplicación.
     * 
     * @param http Objeto HttpSecurity para configurar seguridad web
     * @return SecurityFilterChain configurado
     * @throws Exception Si ocurre un error en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuración de autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin autenticación)
                .requestMatchers(
                    "/",                          // Página principal del frontend
                    "/index.html",                // Frontend HTML
                    "/styles.css",                // Frontend CSS
                    "/app.js",                    // Frontend JavaScript
                    "/favicon.svg",               // Favicon del sitio
                    "/favicon.ico",               // Favicon alternativo
                    "/actuator/health",           // Health check para monitoreo
                    "/actuator/info",             // Información de la aplicación
                    "/swagger-ui/**",             // Swagger UI
                    "/swagger-ui.html",           // Swagger UI página principal
                    "/v3/api-docs/**",            // OpenAPI 3.0 specification
                    "/api-docs/**"                // API documentation
                ).permitAll()
                
                // Todos los demás endpoints requieren autenticación
                .anyRequest().authenticated()
            )
            
            // Autenticación HTTP Basic
            .httpBasic(withDefaults())
            
            // Deshabilitar CSRF (Cross-Site Request Forgery)
            // Seguro para APIs REST stateless que no usan cookies de sesión
            .csrf(AbstractHttpConfigurer::disable)
            
            // Política de sesión STATELESS
            // No se crean sesiones HTTP - cada request debe incluir credenciales
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }

    /**
     * Configura el servicio de detalles de usuario en memoria.
     * 
     * <p><strong>⚠️ SOLO PARA DESARROLLO:</strong></p>
     * <ul>
     *   <li>Usuario: serviciudad</li>
     *   <li>Contraseña: dev2025 (prefijo {noop} = sin encriptación)</li>
     *   <li>Rol: USER</li>
     * </ul>
     * 
     * <p><strong>En producción, implementar:</strong></p>
     * <ul>
     *   <li>UserDetailsService con base de datos</li>
     *   <li>Contraseñas encriptadas con BCrypt: {bcrypt}$2a$...</li>
     *   <li>Roles y permisos granulares</li>
     *   <li>Autenticación basada en JWT</li>
     * </ul>
     * 
     * @return UserDetailsService con usuario en memoria
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("serviciudad")
            .password("{noop}dev2025")  // {noop} = no password encoder (SOLO DESARROLLO)
            .roles("USER")
            .build();
        
        // Para agregar más usuarios en desarrollo:
        // UserDetails admin = User.builder()
        //     .username("admin")
        //     .password("{noop}admin2025")
        //     .roles("USER", "ADMIN")
        //     .build();
        
        return new InMemoryUserDetailsManager(user);
    }
    
    /**
     * NOTA DE MIGRACIÓN A PRODUCCIÓN:
     * 
     * Para producción, reemplazar userDetailsService() con:
     * 
     * @Bean
     * public PasswordEncoder passwordEncoder() {
     *     return new BCryptPasswordEncoder();
     * }
     * 
     * @Bean
     * public UserDetailsService userDetailsService(UserRepository userRepository) {
     *     return username -> userRepository.findByUsername(username)
     *         .map(user -> User.builder()
     *             .username(user.getUsername())
     *             .password(user.getPassword())  // Ya encriptada con BCrypt
     *             .roles(user.getRoles().toArray(new String[0]))
     *             .build())
     *         .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
     * }
     */
}
