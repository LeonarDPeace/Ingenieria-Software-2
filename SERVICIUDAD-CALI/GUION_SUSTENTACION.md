# 🎤 GUION DE SUSTENTACIÓN - SERVICIUDAD CALI

## Sistema de Consulta Unificada de Servicios Públicos

**Presentado por:** Equipo ServiCiudad Cali  
**Curso:** Ingeniería de Software 2  
**Universidad:** Universidad Autónoma de Occidente  
**Fecha:** Octubre 2025  

---

## 📋 **ESTRUCTURA DE LA PRESENTACIÓN (15-20 minutos)**

### **1. INTRODUCCIÓN Y CONTEXTO (3 minutos)**
### **2. DEMOSTRACIÓN TÉCNICA (8 minutos)**
### **3. ARQUITECTURA Y PATRONES (5 minutos)**
### **4. RESULTADOS Y CONCLUSIONES (4 minutos)**

---

## 🎯 **1. INTRODUCCIÓN Y CONTEXTO (3 minutos)**

### **Saludo y Presentación del Equipo**

> **"Buenos días, profesor. Somos el equipo ServiCiudad Cali, conformado por:**
> 
> - **Eduard Criollo Yule** - Project Manager & Backend Developer
> - **Felipe Charria Caicedo** - Integration Specialist  
> - **Jhonathan Chicaiza Herrera** - Backend Developer
> - **Emmanuel Mena** - Full Stack Developer
> - **Juan Sebastian Castillo** - Frontend Developer
> 
> **Hoy presentamos ServiCiudad Cali, un sistema que resuelve un problema real de la ciudad."**

### **Problema que Resuelve**

> **"Actualmente, los ciudadanos de Cali deben contactar TRES canales diferentes para consultar sus deudas:**
> 
> - **📞 Llamada telefónica** a Energía (tiempo promedio: 8 minutos)
> - **📞 Llamada telefónica** a Acueducto (tiempo promedio: 6 minutos)  
> - **📞 Llamada telefónica** a Telecomunicaciones (tiempo promedio: 5 minutos)
> 
> **Esto genera:**
> - ⏰ **19 minutos promedio** por consulta completa
> - 📞 **Sobrecarga del contact center** (60% de llamadas son consultas)
> - 😤 **Frustración del ciudadano** por múltiples llamadas
> - 💰 **Costo operativo alto** para la ciudad"

### **Solución Propuesta**

> **"ServiCiudad Cali unifica TODO en un solo punto:**
> 
> - 🌐 **Un solo endpoint** para consultar todas las deudas
> - ⚡ **Respuesta en menos de 300ms**
> - 📱 **Frontend web responsive** para cualquier dispositivo
> - 🔒 **Seguridad con autenticación HTTP Basic**
> - 📊 **Datos consolidados** con estadísticas y alertas"

---

## 🚀 **2. DEMOSTRACIÓN TÉCNICA (8 minutos)**

### **A. Demostración del Frontend (2 minutos)**

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
   - **Mostrar resultado:**
     - ✅ Datos reales de la base de datos
     - ✅ Estadísticas calculadas
     - ✅ Formato de moneda colombiana
     - ✅ Alertas dinámicas

### **B. Demostración de la API REST (3 minutos)**

> **"Ahora vamos a probar la API REST directamente:"**

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

# 4. Consumos de Energía
curl -u serviciudad:dev2025 http://localhost:8080/api/consumos-energia/cliente/1001234567
# Respuesta: Array de consumos
```

**Puntos clave a destacar:**
- ✅ **Todos los endpoints responden 200 OK**
- ✅ **Autenticación HTTP Basic funcionando**
- ✅ **Datos reales de PostgreSQL**
- ✅ **Integración con archivo legacy**
- ✅ **Respuestas JSON bien estructuradas**

### **C. Demostración de Docker (2 minutos)**

> **"El sistema está completamente containerizado:"**

```bash
# Mostrar contenedores corriendo
docker-compose ps

# Mostrar logs de la aplicación
docker-compose logs app | tail -20

# Mostrar logs de PostgreSQL
docker-compose logs postgres | tail -10
```

**Puntos clave:**
- ✅ **Multi-stage Docker build** optimizado
- ✅ **PostgreSQL 15** con datos de prueba
- ✅ **Health checks** configurados
- ✅ **Volúmenes persistentes** para datos

### **D. Demostración de Postman (1 minuto)**

> **"Tenemos una colección completa de Postman:"**

1. **Mostrar colección importada** con 12 endpoints
2. **Ejecutar test run** → 12/12 exitosos
3. **Mostrar environment** configurado
4. **Destacar tests automatizados** que validan respuestas

---

## 🏗️ **3. ARQUITECTURA Y PATRONES (5 minutos)**

### **A. Arquitectura Hexagonal (2 minutos)**

> **"Implementamos Arquitectura Hexagonal para separar la lógica de negocio de la infraestructura:"**

**Mostrar diagrama:**
```
┌─────────────────────────────────────────────────────────────┐
│                   CAPA DE PRESENTACIÓN                      │
│  (REST Controllers + DTOs + Frontend HTML/CSS/JS)          │
│  - DeudaRestController.java (@RestController) ✅           │
│  - FacturaRestController.java (@RestController) ✅         │
│  - ConsumoEnergiaRestController.java (@RestController) ✅  │
│  - frontend/index.html + styles.css + app.js ✅            │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE APLICACIÓN                       │
│  (Use Cases + DTOs + Mappers)                              │
│  - ConsultarDeudaConsolidadaUseCase.java (@Service) ✅     │
│  - ConsultarFacturasPorClienteUseCase.java (@Service) ✅   │
│  - ConsultarConsumosPorClienteUseCase.java (@Service) ✅   │
│  - DeudaConsolidadaDTO (Builder Pattern) ✅                │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     CAPA DE DOMINIO                         │
│  (Entidades de Negocio + Puertos + Lógica)                 │
│  - FacturaAcueducto.java ✅                                 │
│  - ConsumoEnergia.java ✅                                   │
│  - DeudaConsolidada.java ✅                                 │
│  - Ports (Interfaces para Inversión de Dependencias) ✅    │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                 CAPA DE INFRAESTRUCTURA                     │
│  (Adaptadores + Implementaciones de Puertos)               │
│  - ConsumoEnergiaReaderAdapter (@Component) ✅             │
│  - FacturaRepositoryAdapter (@Component) ✅                │
│  - PostgreSQL Database ✅                                   │
│  - Archivo Legacy (consumos_energia.txt) ✅                │
└─────────────────────────────────────────────────────────────┘
```

**Beneficios:**
- ✅ **Independencia de frameworks**
- ✅ **Testabilidad sin infraestructura**
- ✅ **Facilita migración a microservicios**
- ✅ **Lógica de negocio pura en el dominio**

### **B. Los 5 Patrones de Diseño (3 minutos)**

> **"Implementamos exactamente los 5 patrones requeridos:"**

#### **1️⃣ Patrón Adapter**
> **"Para integrar el sistema legacy de energía:"**

**Mostrar código:**
```java
@Component
public class ConsumoEnergiaReaderAdapter implements ConsumoEnergiaReaderPort {
    
    @Override
    public List<ConsumoEnergia> obtenerConsumosPorCliente(String clienteId) {
        // ADAPTACIÓN: Parseo de archivo de ancho fijo a objetos Java
        String clienteIdArchivo = linea.substring(0, 10).trim();
        String periodo = linea.substring(10, 16);
        int consumoKwh = Integer.parseInt(linea.substring(16, 24).trim());
        // ... más lógica de adaptación
    }
}
```

**Beneficios:**
- ✅ **Desacoplamiento** de la lógica de negocio
- ✅ **Testabilidad** con mocks
- ✅ **Mantenibilidad** si cambia el formato

#### **2️⃣ Patrón Builder**
> **"Para construir DTOs complejos de manera legible:"**

**Mostrar código:**
```java
DeudaConsolidadaDTO respuesta = new DeudaConsolidadaDTO.Builder()
    .clienteId(clienteId)
    .nombreCliente("Juan Pérez")
    .fechaConsulta(LocalDateTime.now())
    .facturasAcueducto(facturasDTO)
    .consumosEnergia(consumosDTO)
    .estadisticas(estadisticasDTO)
    .alertas(alertasList)
    .totalAPagar(totalAPagar)
    .build();
```

**Beneficios:**
- ✅ **Legibilidad** del código
- ✅ **Flexibilidad** para campos opcionales
- ✅ **Validación** antes de construir

#### **3️⃣ Patrón DTO**
> **"Para separar entidades de dominio de la API:"**

**Mostrar diferencia:**
```java
// Entidad de Dominio (interna)
public class FacturaAcueducto {
    private Long auditId;               // Campo interno
    private LocalDate fechaEmision;     // Campo interno
    // ... muchos campos internos
}

// DTO para API (pública)
public class FacturaAcueductoDTO {
    private Long id;                    // Solo para referencia
    private String periodo;
    private int consumoMetrosCubicos;
    private double valorPagar;
    // Sin campos internos
}
```

**Beneficios:**
- ✅ **Seguridad** - no exponemos estructura interna
- ✅ **Desacoplamiento** - cambios en BD no afectan API
- ✅ **Optimización** - enviamos solo datos necesarios

#### **4️⃣ Patrón Repository**
> **"Spring Data JPA nos proporciona el patrón Repository:"**

**Mostrar código:**
```java
@Repository
public interface FacturaJpaRepository extends JpaRepository<FacturaJpaEntity, Long> {
    
    // Spring genera automáticamente la implementación
    List<FacturaJpaEntity> findByClienteId(String clienteId);
    
    @Query("SELECT f FROM FacturaJpaEntity f WHERE f.clienteId = :clienteId AND f.estado = 'PENDIENTE'")
    List<FacturaJpaEntity> findPendientesByCliente(@Param("clienteId") String clienteId);
}
```

**Beneficios:**
- ✅ **Eliminación de boilerplate** - no escribimos SQL repetitivo
- ✅ **Type Safety** - errores de compilación en vez de runtime
- ✅ **Abstracción del proveedor** - cambiamos de PostgreSQL a MySQL sin cambiar código

#### **5️⃣ Inversión de Control / Inyección de Dependencias**
> **"Spring maneja toda la inyección de dependencias:"**

**Mostrar código:**
```java
@Service
public class ConsultarDeudaConsolidadaUseCase {
    
    private final FacturaRepositoryPort facturaRepository;
    private final ConsumoEnergiaReaderPort energiaReader;
    
    // Spring inyecta automáticamente las dependencias
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
- ✅ **Bajo acoplamiento** - el controlador no conoce implementaciones concretas
- ✅ **Alta testabilidad** - podemos inyectar mocks en tests
- ✅ **Configuración centralizada** - Spring maneja el ciclo de vida

---

## 📊 **4. RESULTADOS Y CONCLUSIONES (4 minutos)**

### **A. Métricas de Éxito (1 minuto)**

> **"El sistema está 100% operacional con las siguientes métricas:"**

| **Aspecto** | **Resultado** | **Observaciones** |
|-------------|---------------|-------------------|
| **Endpoints** | 12/12 funcionando | 100% de tasa de éxito |
| **Tiempo de respuesta** | < 300ms promedio | Excelente performance |
| **Arquitectura** | Hexagonal completa | Separación de capas perfecta |
| **Patrones** | 5/5 implementados | Adapter, Builder, DTO, Repository, IoC/DI |
| **Docker** | Multi-stage build | Optimizado para producción |
| **Frontend** | Responsive + favicon | Sin errores de consola |
| **Postman** | 12 tests automatizados | Colección completa y actualizada |
| **Documentación** | README + INFORME + Guías | Exhaustiva y actualizada |

### **B. Problemas Superados (1 minuto)**

> **"Durante el desarrollo enfrentamos y resolvimos un problema crítico:"**

**Problema Identificado:**
- ❌ Sistema iniciaba pero no funcionaba
- ❌ 0 endpoints registrados
- ❌ Todos los /api/* retornaban 500 Error

**Causa Raíz:**
- `HexagonalConfig.java` interfería con component scanning de Spring
- Los Use Cases con `@Service` no eran detectados
- Sin Use Cases, los controladores no podían inyectar dependencias

**Solución Implementada:**
- ✅ Eliminamos `HexagonalConfig.java`
- ✅ Usamos component scanning automático de Spring
- ✅ Todos los endpoints se registran correctamente
- ✅ Sistema 100% operacional

### **C. Valor Entregado (1 minuto)**

> **"El sistema aporta valor real a la ciudad de Cali:"**

**Para los Ciudadanos:**
- ⏰ **Reducción de 19 minutos a 30 segundos** por consulta
- 📱 **Acceso desde cualquier dispositivo** con internet
- 📊 **Información consolidada** en una sola vista
- 🔔 **Alertas automáticas** sobre facturas próximas a vencer

**Para la Ciudad:**
- 📞 **Reducción del 60% de llamadas** al contact center
- 💰 **Ahorro en costos operativos** significativo
- 📈 **Mejora en satisfacción** del ciudadano
- 🔧 **Base sólida** para futuras funcionalidades

**Para los Desarrolladores:**
- 🏗️ **Arquitectura escalable** y mantenible
- 🧪 **Fácil testing** con mocks y stubs
- 📚 **Documentación completa** para mantenimiento
- 🐳 **Despliegue simplificado** con Docker

### **D. Evolución Futura (1 minuto)**

> **"El sistema está preparado para crecer:"**

**Migración a Microservicios:**
- La arquitectura hexagonal facilita la descomposición
- Cada adaptador puede convertirse en un microservicio independiente
- Los puertos definen contratos claros para la comunicación

**Mejoras Propuestas:**
- 🔐 **OAuth2/JWT** para autenticación avanzada
- 📊 **Caché con Redis** para mejorar performance
- 📱 **API móvil nativa** para iOS/Android
- 🤖 **Chatbot integrado** para consultas automáticas
- 📈 **Analytics avanzados** para la ciudad

---

## 🎯 **CONCLUSIÓN FINAL**

> **"ServiCiudad Cali es un sistema completamente funcional que demuestra:**
> 
> ✅ **Dominio técnico** en Spring Boot, Docker y arquitecturas modernas
> ✅ **Aplicación práctica** de 5 patrones de diseño fundamentales
> ✅ **Resolución de problemas** reales de la ciudad de Cali
> ✅ **Calidad de código** con separación de responsabilidades
> ✅ **Documentación exhaustiva** para mantenimiento futuro
> 
> **El sistema está listo para producción y puede ser desplegado inmediatamente en la infraestructura de la ciudad.**
> 
> **¿Hay alguna pregunta sobre la implementación técnica o la arquitectura del sistema?"**

---

## 📋 **CHECKLIST DE PREPARACIÓN**

### **Antes de la Presentación:**
- [ ] Verificar que Docker esté corriendo (`docker-compose ps`)
- [ ] Probar todos los endpoints manualmente
- [ ] Tener Postman abierto con la colección cargada
- [ ] Tener el navegador abierto en `http://localhost:8080`
- [ ] Preparar terminal con comandos de demostración
- [ ] Revisar que no haya errores en la consola del navegador

### **Durante la Presentación:**
- [ ] Mantener contacto visual con el profesor
- [ ] Explicar cada paso antes de ejecutarlo
- [ ] Destacar los aspectos técnicos más importantes
- [ ] Mostrar confianza en el conocimiento del sistema
- [ ] Estar preparado para preguntas técnicas específicas

### **Preguntas Frecuentes del Profesor:**
- **"¿Por qué elegieron arquitectura hexagonal?"** → Separación de responsabilidades, testabilidad, preparación para microservicios
- **"¿Cómo garantizan la seguridad?"** → HTTP Basic Auth, validación de entrada, recursos públicos configurados
- **"¿Qué pasa si falla la base de datos?"** → Health checks, manejo de excepciones, logs detallados
- **"¿Cómo escalarían el sistema?"** → Docker Swarm/Kubernetes, load balancers, caché distribuido
- **"¿Qué mejoras implementarían?"** → OAuth2, Redis, WebSockets, CI/CD pipeline

---

## 📚 **RECURSOS ADICIONALES**

### **Documentación Técnica:**
- `README.md` - Guía completa de instalación y uso
- `INFORME.md` - Justificación técnica detallada
- `postman/README_POSTMAN.md` - Guía de Postman
- `GUIA_PRUEBA_FRONTEND.md` - Guía de pruebas del frontend

### **Código Fuente:**
- **GitHub:** https://github.com/LeonarDPeace/Ingenieria-Software-2
- **Estructura:** Arquitectura hexagonal bien organizada
- **Tests:** Suite completa de tests unitarios e integración
- **Docker:** Multi-stage build optimizado

### **Tecnologías Utilizadas:**
- **Backend:** Spring Boot 3.x, Java 17, PostgreSQL 15
- **Frontend:** HTML5, CSS3, JavaScript vanilla
- **Infraestructura:** Docker, Docker Compose
- **Herramientas:** Maven, Postman, Git
- **Patrones:** Adapter, Builder, DTO, Repository, IoC/DI

---

**¡Éxito en la sustentación! 🚀**

*Este guión está diseñado para una presentación de 15-20 minutos. Ajustar el tiempo según las indicaciones del profesor.*
