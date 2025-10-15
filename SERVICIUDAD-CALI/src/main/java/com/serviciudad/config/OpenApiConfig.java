package com.serviciudad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuracion de OpenAPI 3.0 para documentacion de la API.
 * 
 * Esta clase configura SpringDoc para generar documentacion automatica
 * de la API REST utilizando el estandar OpenAPI 3.0.
 * La documentacion estara disponible en:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/api-docs
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 * @since 2025-01-20
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Configura la informacion principal de la API para OpenAPI.
     * 
     * @return OpenAPI configurado con metadatos de la API
     */
    @Bean
    public OpenAPI serviciudadOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList());
    }

    /**
     * Define la informacion descriptiva de la API.
     * 
     * @return Info con metadatos de la API
     */
    private Info apiInfo() {
        return new Info()
                .title("ServiCiudad Cali - API Deuda Consolidada")
                .description("""
                        API REST para la gestion de deudas consolidadas de servicios publicos
                        en la ciudad de Cali, Colombia.
                        
                        Esta API permite:
                        - Consultar facturas de acueducto por cliente y periodo
                        - Procesar archivos legacy de energia en formato COBOL
                        - Consolidar informacion de multiples servicios publicos
                        - Generar reportes de deuda por cliente
                        
                        Implementa los patrones de diseno: Adapter, Repository, Builder, DTO, IoC/DI.
                        """)
                .version("1.0.0")
                .contact(apiContact())
                .license(apiLicense());
    }

    /**
     * Define la informacion de contacto del equipo.
     * 
     * @return Contact con datos del equipo de desarrollo
     */
    private Contact apiContact() {
        return new Contact()
                .name("Equipo ServiCiudad Cali")
                .email("serviciudad@uao.edu.co")
                .url("https://github.com/serviciudad/serviciudad-cali");
    }

    /**
     * Define la licencia de la API.
     * 
     * @return License con informacion de licencia
     */
    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Define los servidores disponibles para la API.
     * 
     * @return List de Server con URLs de los ambientes
     */
    private List<Server> serverList() {
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de desarrollo");

        Server testServer = new Server()
                .url("http://localhost:8081")
                .description("Servidor de pruebas");

        Server prodServer = new Server()
                .url("https://api.serviciudad-cali.gov.co")
                .description("Servidor de produccion");

        return List.of(devServer, testServer, prodServer);
    }
}
