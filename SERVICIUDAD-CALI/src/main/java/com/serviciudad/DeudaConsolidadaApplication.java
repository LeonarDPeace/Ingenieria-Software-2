package com.serviciudad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación ServiCiudad Cali
 * 
 * Sistema de Consulta Unificada de Servicios Públicos
 * 
 * Patrones implementados:
 * - Adapter Pattern (integración archivo legacy)
 * - Repository Pattern (Spring Data JPA)
 * - Builder Pattern (construcción de DTOs)
 * - DTO Pattern (separación de capas)
 * - IoC/DI Pattern (Spring Framework)
 * 
 * @author Equipo ServiCiudad
 * @version 1.0.0
 * @since 2025-10-15
 */
@SpringBootApplication
public class DeudaConsolidadaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeudaConsolidadaApplication.class, args);
    }
}
