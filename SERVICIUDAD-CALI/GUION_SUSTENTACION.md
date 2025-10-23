# GUION DE SUSTENTACION - SERVICIUDAD CALI

## Sistema de Consulta Unificada de Servicios Publicos

**Presentado por:** Equipo ServiCiudad Cali  
**Curso:** Ingenieria de Software 2  
**Universidad:** Universidad Autonoma de Occidente  
**Fecha:** Octubre 2025  

---

## ESTRUCTURA DE LA PRESENTACION (15-20 minutos)

### 1. INTRODUCCION Y CONTEXTO (3 minutos)
### 2. DEMOSTRACION TECNICA (8 minutos)
### 3. ARQUITECTURA Y PATRONES (5 minutos)
### 4. RESULTADOS Y CONCLUSIONES (4 minutos)

---

## 1. INTRODUCCION Y CONTEXTO (3 minutos)

### Saludo y Presentacion del Equipo

> "Buenos dias, profesor. Somos el equipo ServiCiudad Cali, conformado por:
> 
> - Eduard Criollo Yule - Project Manager & Backend Developer
> - Felipe Charria Caicedo - Integration Specialist  
> - Jhonathan Chicaiza Herrera - Backend Developer
> - Emmanuel Mena - Full Stack Developer
> - Juan Sebastian Castillo - Frontend Developer
> 
> Hoy presentamos ServiCiudad Cali, un sistema que resuelve un problema real de la ciudad."

### Problema que Resuelve

> "Actualmente, los ciudadanos de Cali deben contactar TRES canales diferentes para consultar sus deudas:
> 
> - Llamada telefonica a Energia (tiempo promedio: 8 minutos)
> - Llamada telefonica a Acueducto (tiempo promedio: 6 minutos)  
> - Llamada telefonica a Telecomunicaciones (tiempo promedio: 5 minutos)
> 
> Esto genera:
> - 19 minutos promedio por consulta completa
> - Sobrecarga del contact center (60% de llamadas son consultas)
> - Frustracion del ciudadano por multiples llamadas
> - Costo operativo alto para la ciudad"

### Solucion Propuesta

> "ServiCiudad Cali unifica TODO en un solo punto:
> 
> - Un solo endpoint para consultar todas las deudas
> - Respuesta en menos de 300ms
> - Frontend web responsive para cualquier dispositivo
> - Seguridad con autenticacion HTTP Basic
> - Datos consolidados con estadisticas y alertas"

---

## 2. DEMOSTRACION TECNICA (8 minutos)

### A. Demostracion del Frontend (2 minutos)

> **"Vamos a ver el sistema funcionando. Primero, el frontend web:"**

**Acciones a realizar:**
1. **Abrir navegador** → `http://localhost:8080`
2. **Mostrar la interfaz:**
   - ✅ Diseño moderno y responsive
   - ✅ Campo de validación (solo 10 dígitos)
   - ✅ Estado de conexión Docker
   - ✅ Favicon personalizado (sin errores 404)

3. **Probar consulta:**
   - Ingresar ID: `1001234567`
   - Click en "Consultar Deuda Consolidada"
   - Mostrar resultado:
     - Datos reales de la base de datos
     - Estadisticas calculadas
     - Formato de moneda colombiana
     - Alertas dinamicas

### B. Demostracion de la API REST (3 minutos)

> "Ahora vamos a probar la API REST directamente:"

**Usar Postman o cURL:**

```bash
# 1. Health Check (sin autenticación)
curl http://localhost:8080/actuator/health
# Respuesta: {"status":"UP","groups":["liveness","readiness"]}

# 2. Deuda Consolidada (con autenticación)
curl -u serviciudad:dev2025 http://localhost:8080/api/deuda/cliente/1001234567
# Respuesta: JSON completo con deuda consolidada

# 3. Facturas por Cliente
curl -u serviciudad:dev2025 http://localhost:8080/api/facturas/cliente/1001234567
# Respuesta: Array de facturas

# 4. Consumos de Energia
curl -u serviciudad:dev2025 http://localhost:8080/api/consumos-energia/cliente/1001234567
# Respuesta: Array de consumos
```

**Puntos clave a destacar:**
- Todos los endpoints responden 200 OK
- Autenticacion HTTP Basic funcionando
- Datos reales de PostgreSQL
- Integracion con archivo legacy
- Respuestas JSON bien estructuradas

### C. Demostracion de Docker (2 minutos)

> "El sistema esta completamente containerizado:"

```bash
# Mostrar contenedores corriendo
docker-compose ps

# Mostrar logs de la aplicación
docker-compose logs app | tail -20

# Mostrar logs de PostgreSQL
docker-compose logs postgres | tail -10
```

**Puntos clave:**
- Multi-stage Docker build optimizado
- PostgreSQL 15 con datos de prueba
- Health checks configurados
- Volumenes persistentes para datos

### D. Demostracion de Postman (1 minuto)

> "Tenemos una coleccion completa de Postman:"

1. Mostrar coleccion importada con 12 endpoints
2. Ejecutar test run → 12/12 exitosos
3. Mostrar environment configurado
4. Destacar tests automatizados que validan respuestas

---

## 3. ARQUITECTURA Y PATRONES (5 minutos)

### A. Arquitectura Hexagonal (2 minutos)

> "Implementamos Arquitectura Hexagonal para separar la logica de negocio de la infraestructura:"

**Mostrar diagrama:**
```
┌─────────────────────────────────────────────────────────────┐
│                   CAPA DE PRESENTACION                      │
│  (REST Controllers + DTOs + Frontend HTML/CSS/JS)          │
│  - DeudaRestController.java (@RestController)              │
│  - FacturaRestController.java (@RestController)            │
│  - ConsumoEnergiaRestController.java (@RestController)     │
│  - frontend/index.html + styles.css + app.js               │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE APLICACION                       │
│  (Use Cases + DTOs + Mappers)                              │
│  - ConsultarDeudaConsolidadaUseCase.java (@Service)        │
│  - ConsultarFacturasPorClienteUseCase.java (@Service)      │
│  - ConsultarConsumosPorClienteUseCase.java (@Service)      │
│  - DeudaConsolidadaDTO (Builder Pattern)                   │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     CAPA DE DOMINIO                         │
│  (Entidades de Negocio + Puertos + Logica)                 │
│  - FacturaAcueducto.java                                    │
│  - ConsumoEnergia.java                                      │
│  - DeudaConsolidada.java                                    │
│  - Ports (Interfaces para Inversion de Dependencias)       │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                 CAPA DE INFRAESTRUCTURA                     │
│  (Adaptadores + Implementaciones de Puertos)               │
│  - ConsumoEnergiaReaderAdapter (@Component)                │
│  - FacturaRepositoryAdapter (@Component)                   │
│  - PostgreSQL Database                                      │
│  - Archivo Legacy (consumos_energia.txt)                   │
└─────────────────────────────────────────────────────────────┘
```

**Beneficios:**
- Independencia de frameworks
- Testabilidad sin infraestructura
- Facilita migracion a microservicios
- Logica de negocio pura en el dominio

### B. Los 5 Patrones de Diseno (3 minutos)

### B. Los 5 Patrones de Diseno (3 minutos)

> "Implementamos exactamente los 5 patrones requeridos:"

#### 1. Patron Adapter
> "Para integrar el sistema legacy de energia:"

**Mostrar codigo:**
```java
@Component
public class ConsumoEnergiaReaderAdapter implements ConsumoEnergiaReaderPort {
    
    @Override
    public List<ConsumoEnergia> obtenerConsumosPorCliente(String clienteId) {
        // ADAPTACION: Parseo de archivo de ancho fijo a objetos Java
        String clienteIdArchivo = linea.substring(0, 10).trim();
        String periodo = linea.substring(10, 16);
        int consumoKwh = Integer.parseInt(linea.substring(16, 24).trim());
        // ... mas logica de adaptacion
    }
}
```

**Beneficios:**
- Desacoplamiento de la logica de negocio
- Testabilidad con mocks
- Mantenibilidad si cambia el formato

#### 2. Patron Builder
> "Para construir DTOs complejos de manera legible:"

**Mostrar codigo:**
```java
DeudaConsolidadaDTO respuesta = new DeudaConsolidadaDTO.Builder()
    .clienteId(clienteId)
    .nombreCliente("Juan Perez")
    .fechaConsulta(LocalDateTime.now())
    .facturasAcueducto(facturasDTO)
    .consumosEnergia(consumosDTO)
    .estadisticas(estadisticasDTO)
    .alertas(alertasList)
    .totalAPagar(totalAPagar)
    .build();
```

**Beneficios:**
- Legibilidad del codigo
- Flexibilidad para campos opcionales
- Validacion antes de construir

#### 3. Patron DTO
> "Para separar entidades de dominio de la API:"

**Mostrar diferencia:**
```java
// Entidad de Dominio (interna)
public class FacturaAcueducto {
    private Long auditId;               // Campo interno
    private LocalDate fechaEmision;     // Campo interno
    // ... muchos campos internos
}

// DTO para API (publica)
public class FacturaAcueductoDTO {
    private Long id;                    // Solo para referencia
    private String periodo;
    private int consumoMetrosCubicos;
    private double valorPagar;
    // Sin campos internos
}
```

**Beneficios:**
- Seguridad - no exponemos estructura interna
- Desacoplamiento - cambios en BD no afectan API
- Optimizacion - enviamos solo datos necesarios

#### 4. Patron Repository
> "Spring Data JPA nos proporciona el patron Repository:"

**Mostrar codigo:**
```java
@Repository
public interface FacturaJpaRepository extends JpaRepository<FacturaJpaEntity, Long> {
    
    // Spring genera automaticamente la implementacion
    List<FacturaJpaEntity> findByClienteId(String clienteId);
    
    @Query("SELECT f FROM FacturaJpaEntity f WHERE f.clienteId = :clienteId AND f.estado = 'PENDIENTE'")
    List<FacturaJpaEntity> findPendientesByCliente(@Param("clienteId") String clienteId);
}
```

**Beneficios:**
- Eliminacion de boilerplate - no escribimos SQL repetitivo
- Type Safety - errores de compilacion en vez de runtime
- Abstraccion del proveedor - cambiamos de PostgreSQL a MySQL sin cambiar codigo

#### 5. Inversion de Control / Inyeccion de Dependencias
> "Spring maneja toda la inyeccion de dependencias:"

**Mostrar codigo:**
```java
@Service
public class ConsultarDeudaConsolidadaUseCase {
    
    private final FacturaRepositoryPort facturaRepository;
    private final ConsumoEnergiaReaderPort energiaReader;
    
    // Spring inyecta automaticamente las dependencias
    @Autowired
    public ConsultarDeudaConsolidadaUseCase(
        FacturaRepositoryPort facturaRepository,
        ConsumoEnergiaReaderPort energiaReader
    ) {
        this.facturaRepository = facturaRepository;
        this.energiaReader = energiaReader;
    }
}
```

**Beneficios:**
- Bajo acoplamiento - el controlador no conoce implementaciones concretas
- Alta testabilidad - podemos inyectar mocks en tests
- Configuracion centralizada - Spring maneja el ciclo de vida

---

## 4. RESULTADOS Y CONCLUSIONES (4 minutos)

### A. Metricas de Exito (1 minuto)

> "El sistema esta 100% operacional con las siguientes metricas:"

| Aspecto | Resultado | Observaciones |
|-------------|---------------|-------------------|
| Endpoints | 12/12 funcionando | 100% de tasa de exito |
| Tiempo de respuesta | < 300ms promedio | Excelente performance |
| Arquitectura | Hexagonal completa | Separacion de capas perfecta |
| Patrones | 5/5 implementados | Adapter, Builder, DTO, Repository, IoC/DI |
| Docker | Multi-stage build | Optimizado para produccion |
| Frontend | Responsive + favicon | Sin errores de consola |
| Postman | 12 tests automatizados | Coleccion completa y actualizada |
| Documentacion | README + INFORME + Guias | Exhaustiva y actualizada |

### B. Valor Entregado (2 minutos)
- ❌ 0 endpoints registrados
- ❌ Todos los /api/* retornaban 500 Error

**Causa Raíz:**
### B. Valor Entregado (2 minutos)

> "El sistema aporta valor real a la ciudad de Cali:"

**Para los Ciudadanos:**
- Reduccion de 19 minutos a 30 segundos por consulta
- Acceso desde cualquier dispositivo con internet
- Informacion consolidada en una sola vista
- Alertas automaticas sobre facturas proximas a vencer

**Para la Ciudad:**
- Reduccion del 60% de llamadas al contact center
- Ahorro en costos operativos significativo
- Mejora en satisfaccion del ciudadano
- Base solida para futuras funcionalidades

**Para los Desarrolladores:**
- Arquitectura escalable y mantenible
- Facil testing con mocks y stubs
- Documentacion completa para mantenimiento
- Despliegue simplificado con Docker

### C. Evolucion Futura (1 minuto)

> "El sistema esta preparado para crecer:"

**Migracion a Microservicios:**
- La arquitectura hexagonal facilita la descomposicion
- Cada adaptador puede convertirse en un microservicio independiente
- Los puertos definen contratos claros para la comunicacion

**Mejoras Propuestas:**
- OAuth2/JWT para autenticacion avanzada
- Cache con Redis para mejorar performance
- API movil nativa para iOS/Android
- Chatbot integrado para consultas automaticas
- Analytics avanzados para la ciudad

### D. Scripts de Automatizacion (1 minuto)

> "El proyecto incluye scripts de PowerShell para facilitar el uso y evaluacion:"

**Scripts Disponibles:**

1. **inicio-rapido.ps1** (Recomendado para evaluadores)
   - Inicializa todo el sistema con un solo comando
   - Verifica Docker, levanta contenedores, valida salud
   - Abre automaticamente el navegador en http://localhost:8080
   - Tiempo de ejecucion: ~30 segundos

2. **run-all-tests.ps1** (Pruebas completas)
   - Ejecuta todos los tests con cobertura JaCoCo
   - Genera reportes en target/site/jacoco/index.html
   - Muestra estadisticas por categoria (Unitarios, Integracion, E2E)
   - Tiempo de ejecucion: ~2 minutos

3. **quick-test.ps1** (Desarrollo rapido)
   - Ejecuta solo tests rapidos sin cobertura
   - Modo watch para desarrollo continuo
   - Validacion rapida de cambios
   - Tiempo de ejecucion: ~15 segundos

4. **rebuild-docker.ps1** (Reconstruccion completa)
   - Limpia contenedores e imagenes antiguas
   - Reconstruye todo desde cero sin cache
   - Reinicia sistema con configuracion limpia
   - Tiempo de ejecucion: ~3 minutos

**Demostracion en vivo:**
```powershell
# Iniciar sistema completo
.\inicio-rapido.ps1

# Ejecutar suite de pruebas
.\run-all-tests.ps1
```

---

## CONCLUSION FINAL

> "ServiCiudad Cali es un sistema completamente funcional que demuestra:
> 
> - Dominio tecnico en Spring Boot, Docker y arquitecturas modernas
> - Aplicacion practica de 5 patrones de diseno fundamentales
> - Resolucion de problemas reales de la ciudad de Cali
> - Calidad de codigo con separacion de responsabilidades
> - Documentacion exhaustiva para mantenimiento futuro
> 
> El sistema esta listo para produccion y puede ser desplegado inmediatamente en la infraestructura de la ciudad.
> 
> Hay alguna pregunta sobre la implementacion tecnica o la arquitectura del sistema?"

---

## CHECKLIST DE PREPARACION

### Antes de la Presentacion:
- [ ] Verificar que Docker este corriendo (`docker-compose ps`)
- [ ] Probar todos los endpoints manualmente
- [ ] Tener Postman abierto con la coleccion cargada
- [ ] Tener el navegador abierto en `http://localhost:8080`
- [ ] Preparar terminal con comandos de demostracion
- [ ] Revisar que no haya errores en la consola del navegador

### Durante la Presentacion:
- [ ] Mantener contacto visual con el profesor
- [ ] Explicar cada paso antes de ejecutarlo
- [ ] Destacar los aspectos tecnicos mas importantes
- [ ] Mostrar confianza en el conocimiento del sistema
- [ ] Estar preparado para preguntas tecnicas especificas

### Preguntas Frecuentes del Profesor:
- "Por que elegieron arquitectura hexagonal?" → Separacion de responsabilidades, testabilidad, preparacion para microservicios
- "Como garantizan la seguridad?" → HTTP Basic Auth, validacion de entrada, recursos publicos configurados
- "Que pasa si falla la base de datos?" → Health checks, manejo de excepciones, logs detallados
- "Como escalarian el sistema?" → Docker Swarm/Kubernetes, load balancers, cache distribuido
- "Que mejoras implementarian?" → OAuth2, Redis, WebSockets, CI/CD pipeline

---

## RECURSOS ADICIONALES

### Documentacion Tecnica:
- `README.md` - Guia completa de instalacion y uso
- `INFORME.md` - Justificacion tecnica detallada
- `postman/README_POSTMAN.md` - Guia de Postman

### Codigo Fuente:
- GitHub: https://github.com/LeonarDPeace/Ingenieria-Software-2
- Estructura: Arquitectura hexagonal bien organizada
- Tests: Suite completa de tests unitarios e integracion
- Docker: Multi-stage build optimizado

### Tecnologias Utilizadas:
- Backend: Spring Boot 3.x, Java 17, PostgreSQL 15
- Frontend: HTML5, CSS3, JavaScript vanilla
- Infraestructura: Docker, Docker Compose
- Herramientas: Maven, Postman, Git
- Patrones: Adapter, Builder, DTO, Repository, IoC/DI

---

Exito en la sustentacion

*Este guion esta disenado para una presentacion de 15-20 minutos. Ajustar el tiempo segun las indicaciones del profesor.*
