# ServiCiudad Conectada - Proyecto de Transformación Digital

## Descripción del Proyecto

ServiCiudad Conectada es un proyecto de transformación digital para modernizar la experiencia de servicios públicos en Santiago de Cali, Colombia. El proyecto busca unificar los servicios de energía, acueducto y telecomunicaciones en una plataforma digital moderna, integrando sistemas legacy a través de una arquitectura de microservicios.

## Contexto Empresarial

**ServiCiudad Cali** es una empresa mixta de servicios públicos con más de 50 años de experiencia, que enfrenta el desafío de modernizar su infraestructura tecnológica fragmentada:

- **Energía**: Sistema mainframe IBM Z con lógica COBOL
- **Acueducto**: Base de datos Oracle sobre Solaris con PL/SQL
- **Telecomunicaciones**: Sistemas modernos mal integrados

### Problemática Actual

- **CSAT**: 45% (muy por debajo del sector)
- **Consultas diarias**: 5,000 llamadas (60% son consultas de saldo automatizables)
- **Costo anual**: $2,500 millones COP en call center para consultas básicas
- **Tiempo de conciliación**: Hasta 72 horas para reflejar pagos
- **Experiencia fragmentada**: 3 portales, 3 facturas, 3 números de soporte

## Objetivos del Proyecto

### Objetivo Principal
Implementar una plataforma digital unificada que ofrezca una experiencia 360° al ciudadano, cumpliendo con la política municipal "Cali Conectada".

### Objetivos Específicos
1. **Unificar la experiencia del usuario** en un portal web y aplicación móvil
2. **Integrar sistemas legacy** sin reemplazarlos completamente
3. **Automatizar consultas** para reducir carga del call center
4. **Habilitar pagos en tiempo real** con conciliación inmediata
5. **Mejorar CSAT** del 45% actual a 80%+

## Arquitectura de Solución

### Enfoque Tecnológico
- **Arquitectura de Microservicios** con Java/Spring Boot
- **Capa Anti-corrupción** para integración con sistemas legacy
- **API Gateway** como punto de entrada unificado
- **Patrones de Resiliencia** (Circuit Breaker, Retry, Timeout)

### Microservicios Principales
- **MS-Clientes**: Gestión de usuarios y autenticación
- **MS-Pagos**: Procesamiento de pagos unificados
- **MS-Facturación**: Consolidación de facturas
- **MS-Notificaciones**: Comunicaciones multicanal
- **MS-Incidencias**: Gestión de reportes y tickets
- **MS-Administración**: Seguridad y auditoría

## Funcionalidades Principales

### Fase 1 - MVP (Día 1)
- Consulta de saldos unificada (< 10 segundos)
- Pago en línea de servicios (individual o combinado)
- Autenticación segura y gestión de usuarios
- Integración con sistemas legacy

### Fase 2 - Extensiones
- Factura unificada digital
- Reporte interactivo de fallas
- Notificaciones personalizadas
- Panel de administración

### Fase 3 - Optimizaciones
- Analytics y reportes avanzados
- Inteligencia artificial para soporte
- APIs públicas para terceros
- Optimizaciones de performance

## Stack Tecnológico

### Backend
- **Framework**: Java 17, Spring Boot 3.x
- **API Gateway**: Spring Cloud Gateway
- **Resiliencia**: Resilience4j (Circuit Breaker, Retry, TimeLimiter)
- **Mensajería**: Apache Kafka
- **Base de Datos**: PostgreSQL (principal), MongoDB (documentos), Redis (caché)

### Infraestructura
- **Contenedores**: Docker, Kubernetes
- **Monitoreo**: Prometheus, Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **CI/CD**: Jenkins, GitLab CI

### Seguridad
- **Autenticación**: OAuth2, JWT
- **Autorización**: RBAC (Role-Based Access Control)
- **Cifrado**: TLS 1.3, AES-256
- **Cumplimiento**: ISO 27001

## Integración con Sistemas Legacy

### Mainframe IBM Z (Energía)
- **Protocolo**: Terminal 3270 simulado
- **Formato**: EBCDIC → JSON (Message Translator)
- **Patrón**: Adapter + Circuit Breaker
- **Ventana de mantenimiento**: Domingos 4 horas

### Oracle Solaris (Acueducto)
- **Formato**: Texto ancho fijo → JSON
- **Latencia**: Hasta 30 segundos
- **Patrón**: Message Translator + Timeout

### Sistemas Modernos (Telecomunicaciones)
- **Protocolo**: REST/SOAP APIs
- **Patrón**: Facade + Content-Based Router

## Patrones de Diseño Implementados

1. **Adapter Pattern**: Encapsulación de sistemas legacy
2. **Message Translator**: Transformación de formatos de datos
3. **Circuit Breaker**: Resiliencia ante fallos de sistemas legacy
4. **Content-Based Router**: Enrutamiento inteligente de solicitudes
5. **Publish-Subscribe**: Notificaciones desacopladas
6. **Database per Service**: Autonomía de datos por microservicio
7. **Saga Pattern**: Transacciones distribuidas
8. **Builder Pattern**: Construcción de respuestas complejas

## Métricas de Éxito

### Técnicas
- **Disponibilidad**: 99.9% uptime
- **Performance**: < 2s consultas, < 5s pagos
- **Escalabilidad**: 10,000 usuarios concurrentes
- **Resiliencia**: Degradación elegante ante fallos

### Negocio
- **CSAT**: Incremento del 45% al 80%
- **Reducción de llamadas**: 60% de consultas automatizadas
- **Ahorro operativo**: $1,500 millones COP anuales
- **Tiempo de conciliación**: < 15 minutos

## Fases de Implementación

### Fase 1 (Meses 1-4): Fundación
- Implementación del API Gateway
- MS-Clientes y autenticación
- MS-Pagos con integración PSE
- Adaptadores para sistemas legacy

### Fase 2 (Meses 5-8): Expansión
- MS-Facturación unificada
- MS-Notificaciones multicanal
- MS-Incidencias y tickets
- Portal web y app móvil

### Fase 3 (Meses 9-12): Optimización
- MS-Administración y auditoría
- Analytics y reportes
- Optimizaciones de performance
- Capacitación y adopción

## Equipo del Proyecto

- **Project Manager**: Eduard Criollo Yule (ID: 2220335)
- **Arquitecto de Software**: Diseño de microservicios y patrones
- **Desarrollador Backend**: Implementación de APIs y lógica de negocio
- **Desarrollador Frontend**: Portal web y aplicación móvil
- **DevOps Engineer**: Infraestructura y despliegue
- **QA Engineer**: Pruebas y validación de calidad

## Documentación del Proyecto

- **[Arquitectura](./docs/ARCHITECTURE.md)**: Diseño técnico detallado
- **[Requerimientos](./docs/REQUIREMENTS.md)**: Especificaciones funcionales
- **[API Reference](./docs/API.md)**: Documentación de APIs
- **[Deployment](./docs/DEPLOYMENT.md)**: Guías de instalación
- **[Security](./docs/SECURITY.md)**: Políticas de seguridad

## Licencia

Este proyecto es desarrollado para ServiCiudad Cali como parte del curso de Ingeniería de Software 2 en la Universidad Autónoma de Occidente.

---

**Universidad Autónoma de Occidente**  
**Ingeniería de Software 2 - 2024**  
**Santiago de Cali, Colombia**
