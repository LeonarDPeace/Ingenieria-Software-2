# ServiCiudad Conectada

**Transformación Digital de Servicios Públicos para Santiago de Cali**

[![Estado del Proyecto](https://img.shields.io/badge/estado-en%20desarrollo-yellow)](https://github.com/LeonarDPeace/Ingenieria-Software-2)
[![Versión](https://img.shields.io/badge/versión-1.0.0-blue)](https://github.com/LeonarDPeace/Ingenieria-Software-2/releases)
[![Licencia](https://img.shields.io/badge/licencia-Académica-green)](./LICENSE)
[![Documentación](https://img.shields.io/badge/docs-completa-brightgreen)](./docs/)

## Visión General

ServiCiudad Conectada es un proyecto de transformación digital que moderniza la experiencia de servicios públicos en Santiago de Cali, Colombia. La solución unifica los servicios de energía, acueducto y telecomunicaciones en una plataforma digital moderna, integrando sistemas legacy a través de una arquitectura de microservicios resiliente.

### Problema Empresarial

ServiCiudad Cali enfrenta desafíos críticos:
- **CSAT actual**: 45% (muy por debajo del sector)
- **Experiencia fragmentada**: 3 portales, 3 facturas, 3 números de soporte
- **Costos operativos**: $2,500 millones COP anuales en call center para consultas básicas
- **Tiempo de conciliación**: Hasta 72 horas para reflejar pagos

### Solución Propuesta

Plataforma digital unificada con:
- **Portal web y app móvil** para experiencia 360°
- **Microservicios resilientes** con patrones de integración
- **Capa anticorrupción** para sistemas legacy
- **Saga Pattern** para transacciones distribuidas
- **Circuit Breaker** para resiliencia ante fallos

## Arquitectura Técnica

### Stack Tecnológico
```
Frontend:     React, React Native
Backend:      Java 17, Spring Boot 3.x, Spring Cloud
Gateway:      Spring Cloud Gateway
Messaging:    Apache Kafka
Databases:    PostgreSQL, MongoDB, Redis
Container:    Docker, Kubernetes
Monitoring:   Prometheus, Grafana, ELK Stack
Security:     OAuth2, JWT, TLS 1.3, AES-256
```

### Microservicios
- **MS-Clientes**: Autenticación y gestión de usuarios
- **MS-Pagos**: Procesamiento de pagos distribuidos
- **MS-Facturación**: Consolidación de facturas
- **MS-Notificaciones**: Comunicaciones multicanal
- **MS-Incidencias**: Gestión de tickets y soporte
- **MS-Administración**: Auditoría y configuración

### Integración Legacy
- **Mainframe IBM Z**: Energía (COBOL/EBCDIC → JSON)
- **Oracle Solaris**: Acueducto (PL/SQL, formato fijo → JSON)
- **Sistemas Modernos**: Telecomunicaciones (REST/SOAP)

## Documentación

### Inicio Rápido
- **[Resumen del Proyecto](./OVERVIEW.md)** - Visión completa y objetivos
- **[Guía de Contribución](./CONTRIBUTING.md)** - Cómo participar en el proyecto

### Documentación Técnica
- **[Arquitectura](./docs/ARCHITECTURE.md)** - Diseño técnico detallado
- **[Requerimientos](./docs/REQUIREMENTS.md)** - Especificaciones funcionales
- **[API Reference](./docs/API.md)** - Documentación de APIs REST
- **[Deployment](./docs/DEPLOYMENT.md)** - Guías de despliegue
- **[Security](./docs/SECURITY.md)** - Políticas de seguridad

### Diagramas Técnicos

#### Proyecto General
- [Diagrama C4](./Graficos/3.%20DiagramaC4.xml) - Contexto del sistema
- [Arquitectura General](./Graficos/1.%20DiagramaMejorado_ServiCiudadConectada_Arquitectura.drawio.xml) - Vista arquitectónica
- [Clases Generales](./Graficos/11.%20DiagramaClasesGeneral_Proyecto.xml) - Modelo de dominio

#### Microservicio de Pagos
- [Arquitectura Hexagonal](./Graficos/2.%20DiagramaArquitecturaHexagonal_MicroservicioPagos.xml)
- [Diagrama de Clases](./Graficos/8.%20DiagramaClases_MicroservicioPagos.xml)
- [Diagrama de Secuencia](./Graficos/9.%20DiagramaSecuencia_MicroservicioPagos.xml)
- [Code Diagram](./Graficos/10.%20CodeDiagram_MicroservicioPagos.xml)

## Funcionalidades Principales

### Ciudadanos
- Consulta de saldos unificada (< 10 segundos)
- Pago en línea multi-servicio
- Factura unificada digital
- Reporte de incidencias con seguimiento
- Notificaciones personalizadas

### Operadores
- Panel administrativo centralizado
- Gestión de tickets de soporte
- Consultas avanzadas de clientes
- Reportes operacionales

### Administradores
- Configuración del sistema
- Monitoreo y métricas
- Auditoría y cumplimiento
- Gestión de usuarios y roles

## Métricas de Éxito

### Objetivos de Negocio
- **CSAT**: 45% → 80%+ 
- **Reducción llamadas**: 60% consultas automatizadas
- **Ahorro operativo**: $1,500 millones COP anuales
- **Tiempo conciliación**: 72h → 15 minutos

### Objetivos Técnicos
- **Disponibilidad**: 99.9% uptime
- **Performance**: < 2s consultas, < 5s pagos
- **Escalabilidad**: 10,000 usuarios concurrentes
- **Seguridad**: 0 brechas críticas

## Seguridad y Cumplimiento

- **ISO 27001**: Gestión de seguridad de la información
- **PCI DSS**: Seguridad en procesamiento de pagos
- **Ley 1581/2012**: Protección de datos personales (Colombia)
- **RBAC**: Control de acceso basado en roles
- **Cifrado**: AES-256 en reposo, TLS 1.3 en tránsito

## Estructura del Repositorio

```
Ingenieria-Software-2/
├── docs/                    # Documentación técnica
│   ├── ARCHITECTURE.md     # Arquitectura del sistema
│   ├── REQUIREMENTS.md     # Requerimientos funcionales
│   ├── API.md              # Documentación de APIs
│   ├── DEPLOYMENT.md       # Guías de despliegue
│   └── SECURITY.md         # Políticas de seguridad
├── Documentos/             # Documentos de análisis
├── Graficos/               # Diagramas técnicos
│   ├── Drawio/            # Archivos fuente Draw.io
│   ├── Pdf/               # Versiones PDF
│   └── Xml/               # Diagramas en XML
├── OVERVIEW.md             # Resumen ejecutivo
├── CONTRIBUTING.md         # Guía de contribución
├── CHANGELOG.md            # Historial de cambios
└── README.md               # Este archivo
```

## Roadmap de Desarrollo

### Fase 1 (Meses 1-4): Fundación
- [ ] Implementación del API Gateway
- [ ] MS-Clientes y autenticación
- [ ] MS-Pagos con integración PSE
- [ ] Adaptadores para sistemas legacy

### Fase 2 (Meses 5-8): Expansión
- [ ] MS-Facturación unificada
- [ ] MS-Notificaciones multicanal
- [ ] MS-Incidencias y tickets
- [ ] Portal web y app móvil

### Fase 3 (Meses 9-12): Optimización
- [ ] MS-Administración y auditoría
- [ ] Analytics y reportes
- [ ] Optimizaciones de performance
- [ ] Capacitación y adopción

## Equipo del Proyecto

### Core Team
- **Eduard Criollo Yule** - Project Manager (ID: 2220335)
- **Arquitecto de Software** - Diseño de microservicios
- **Tech Lead** - Liderazgo técnico
- **Full Stack Developer** - Implementación
- **DevOps Engineer** - Infraestructura
- **QA Engineer** - Calidad y testing

### Stakeholders
- **ServiCiudad Cali** - Cliente y sponsor
- **Universidad Autónoma de Occidente** - Institución académica
- **Ciudadanos de Cali** - Usuarios finales

## Contribuir al Proyecto

Agradecemos contribuciones que mejoren la plataforma. Por favor:

1. Lee nuestra [Guía de Contribución](./CONTRIBUTING.md)
2. Revisa los [Issues abiertos](https://github.com/LeonarDPeace/Ingenieria-Software-2/issues)
3. Sigue nuestras convenciones de código y documentación
4. Crea Pull Requests descriptivos

## Licencia

Este proyecto está desarrollado con fines académicos para el curso de Ingeniería de Software 2 en la Universidad Autónoma de Occidente.

## Contacto

- **Repositorio**: https://github.com/LeonarDPeace/Ingenieria-Software-2
- **Issues**: https://github.com/LeonarDPeace/Ingenieria-Software-2/issues
- **Universidad**: Universidad Autónoma de Occidente
- **Curso**: Ingeniería de Software 2
- **Año**: 2024

---

**Universidad Autónoma de Occidente**  
**Facultad de Ingeniería**  
**Programa de Ingeniería de Sistemas**  
**Santiago de Cali, Colombia**

