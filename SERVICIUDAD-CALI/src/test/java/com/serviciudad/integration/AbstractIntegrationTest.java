package com.serviciudad.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Clase base abstracta para tests de integración con Testcontainers.
 * 
 * Proporciona un contenedor PostgreSQL compartido para todos los tests de integración,
 * mejorando el rendimiento al reutilizar el mismo contenedor de base de datos.
 * 
 * <p>El contenedor se inicia una sola vez antes de todas las pruebas y se cierra
 * automáticamente al finalizar la suite completa de tests.</p>
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
public abstract class AbstractIntegrationTest {

    /**
     * Contenedor PostgreSQL compartido entre todos los tests de integración.
     * 
     * <p>Configurado como static para ser reutilizado por todas las clases hijas.
     * Usa PostgreSQL 15 Alpine (imagen ligera) con credenciales de prueba.</p>
     */
    @SuppressWarnings("resource")
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER = 
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("serviciudad_test")
            .withUsername("test")
            .withPassword("test");

    static {
        // Iniciar el contenedor una sola vez para toda la suite de tests
        POSTGRES_CONTAINER.start();
    }

    /**
     * Configura las propiedades dinámicas de Spring para conectarse al contenedor.
     * 
     * <p>Este método es llamado automáticamente por Spring Test Context para
     * inyectar la URL JDBC, usuario y contraseña del contenedor PostgreSQL
     * en el contexto de la aplicación de prueba.</p>
     * 
     * @param registry Registro de propiedades dinámicas de Spring
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }
}
