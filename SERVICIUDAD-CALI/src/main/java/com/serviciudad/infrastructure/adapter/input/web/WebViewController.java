package com.serviciudad.infrastructure.adapter.input.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para servir la interfaz web del frontend.
 * 
 * Patrón implementado: MVC (Model-View-Controller)
 * - Este controlador sirve las vistas HTML estáticas
 * - Separa la lógica de presentación del backend REST
 * 
 * Endpoints:
 * - GET / : Página principal de la aplicación
 * - GET /index : Alias para la página principal
 * 
 * @author ServiCiudad Team
 * @version 1.0.0
 * @since 2025-10-19
 */
@Controller
public class WebViewController {

    /**
     * Sirve la página principal de la aplicación.
     * 
     * Spring MVC buscará el archivo en:
     * 1. classpath:/static/index.html
     * 2. file:./frontend/index.html (configurado en WebConfig)
     * 
     * @return forward a index.html
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
