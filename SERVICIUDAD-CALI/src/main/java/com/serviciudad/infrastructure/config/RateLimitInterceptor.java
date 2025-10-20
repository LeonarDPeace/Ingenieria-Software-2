package com.serviciudad.infrastructure.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interceptor para implementar Rate Limiting usando Bucket4j.
 * 
 * Protege la API contra ataques de Denegación de Servicio (DoS)
 * limitando el número de requests que un cliente puede hacer
 * en un período de tiempo.
 * 
 * <p><strong>Configuración actual:</strong></p>
 * <ul>
 *   <li>Límite: 100 requests por minuto por IP</li>
 *   <li>Algoritmo: Token Bucket</li>
 *   <li>Respuesta cuando se excede: HTTP 429 (Too Many Requests)</li>
 * </ul>
 * 
 * <p><strong>Funcionamiento del Token Bucket:</strong></p>
 * <ol>
 *   <li>Cada IP tiene un "bucket" con capacidad de 100 tokens</li>
 *   <li>Cada request consume 1 token</li>
 *   <li>Los tokens se recargan a razón de 100 tokens/minuto</li>
 *   <li>Si no hay tokens disponibles, el request es rechazado</li>
 * </ol>
 * 
 * @author ServiCiudad Team
 * @version 1.0
 * @since 2025-10-17
 */
@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    /**
     * Mapa de buckets por dirección IP.
     * 
     * Usa ConcurrentHashMap para soportar acceso concurrente
     * sin bloqueos explícitos.
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Configuración del rate limit.
     * 
     * - Capacidad: 100 tokens
     * - Recarga: 100 tokens cada 1 minuto
     * - Estrategia: Intervally (recarga periódica completa)
     */
    private static final long CAPACITY = 100L;
    private static final long REFILL_TOKENS = 100L;
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);

    /**
     * Crea un nuevo bucket con la configuración de rate limiting.
     * 
     * @return Bucket configurado con límite de 100 requests/minuto
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(CAPACITY)
            .refillIntervally(REFILL_TOKENS, REFILL_DURATION)
            .build();
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Obtiene o crea un bucket para la dirección IP especificada.
     * 
     * @param ip Dirección IP del cliente
     * @return Bucket asociado a la IP
     */
    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> createNewBucket());
    }

    /**
     * Intercepta cada request HTTP antes de llegar al controller.
     * 
     * Verifica si el cliente (identificado por IP) tiene tokens
     * disponibles en su bucket. Si no hay tokens, rechaza el
     * request con HTTP 429.
     * 
     * @param request HttpServletRequest con la petición
     * @param response HttpServletResponse para enviar respuesta
     * @param handler Handler del endpoint
     * @return true si el request puede continuar, false si fue rechazado
     * @throws Exception Si ocurre un error durante la interceptación
     */
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        // Obtener IP del cliente
        String ip = getClientIP(request);
        
        // Obtener bucket asociado a la IP
        Bucket bucket = resolveBucket(ip);
        
        // Intentar consumir 1 token
        if (bucket.tryConsume(1)) {
            // Token consumido exitosamente - permitir request
            long tokensRestantes = bucket.getAvailableTokens();
            
            // Agregar headers informativos (opcional)
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(tokensRestantes));
            response.addHeader("X-Rate-Limit-Limit", String.valueOf(CAPACITY));
            
            log.debug("Request permitido desde IP: {} - Tokens restantes: {}", 
                     ip, tokensRestantes);
            
            return true;
        } else {
            // No hay tokens disponibles - rechazar request
            log.warn("Rate limit excedido para IP: {} - Request rechazado", ip);
            
            // Configurar respuesta HTTP 429
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Cuerpo de respuesta JSON
            String jsonResponse = String.format(
                "{" +
                "\"timestamp\": \"%s\"," +
                "\"status\": 429," +
                "\"error\": \"Too Many Requests\"," +
                "\"message\": \"Has excedido el límite de %d requests por minuto. Por favor intenta más tarde.\"," +
                "\"path\": \"%s\"" +
                "}",
                java.time.LocalDateTime.now(),
                CAPACITY,
                request.getRequestURI()
            );
            
            response.getWriter().write(jsonResponse);
            
            return false;
        }
    }

    /**
     * Extrae la dirección IP real del cliente del request.
     * 
     * Considera headers de proxy (X-Forwarded-For) para obtener
     * la IP original cuando la aplicación está detrás de un
     * load balancer o reverse proxy.
     * 
     * @param request HttpServletRequest
     * @return Dirección IP del cliente
     */
    private String getClientIP(HttpServletRequest request) {
        // Intentar obtener IP desde headers de proxy
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For puede contener múltiples IPs (cadena de proxies)
            // La primera IP es la del cliente original
            return xForwardedFor.split(",")[0].trim();
        }
        
        // Otros headers comunes de proxy
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        // Si no hay headers de proxy, usar la IP directa
        return request.getRemoteAddr();
    }

    /**
     * NOTA: Para producción, considerar:
     * 
     * 1. **Almacenamiento Distribuido:**
     *    - Usar Redis para compartir buckets entre instancias
     *    - Implementar Bucket4j con JCache o Redis
     *    - Ejemplo: bucket4j-redis, bucket4j-hazelcast
     * 
     * 2. **Limpieza de Buckets Inactivos:**
     *    - Implementar EvictionPolicy para liberar memoria
     *    - Ejemplo: Caffeine Cache con expiración
     * 
     * 3. **Rate Limiting Diferenciado:**
     *    - Límites distintos según roles de usuario
     *    - Rate limiting por API key en lugar de IP
     *    - Endpoints públicos vs protegidos
     * 
     * 4. **Configuración Externalizada:**
     *    - Mover CAPACITY, REFILL_TOKENS a application.yml
     *    - Permitir cambio dinámico sin redeploy
     * 
     * 5. **Monitoreo:**
     *    - Métricas de rate limiting (requests bloqueados/permitidos)
     *    - Alertas cuando se detectan patrones de ataque
     *    - Dashboard con Grafana/Prometheus
     */
}
