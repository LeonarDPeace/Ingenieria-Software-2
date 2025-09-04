# ServiCiudad Conectada - Ingeniería de Software 2

## 📋 Descripción del Proyecto

Proyecto de transformación digital para ServiCiudad Cali, implementando una arquitectura de microservicios para unificar los servicios de energía, acueducto y telecomunicaciones en una plataforma digital moderna.

## 🎯 Objetivo

Diseñar e implementar una solución arquitectónica que permita:
- Experiencia ciudadana 360° unificada
- Integración con sistemas legacy (Mainframe IBM Z, Oracle Solaris)
- Cumplimiento de la política "Cali Conectada"
- Mejora del CSAT del 45% actual a 80%+

## 🏗️ Arquitectura

La solución implementa una arquitectura de microservicios con patrones de resiliencia y integración, incluyendo:

### Microservicios Principales:
- **MS-Clientes**: Gestión de usuarios y autenticación
- **MS-Pagos**: Procesamiento de pagos unificados con Saga Pattern
- **MS-Facturación**: Consolidación de facturas
- **MS-Notificaciones**: Comunicaciones multicanal
- **MS-Incidencias**: Gestión de reportes y tickets
- **MS-Administración**: Seguridad y auditoría

### Patrones Implementados:
- **Saga Pattern**: Transacciones distribuidas
- **Circuit Breaker**: Resiliencia ante fallos
- **Adapter Pattern**: Integración con sistemas legacy
- **Message Translator**: Transformación EBCDIC → JSON
- **API Gateway**: Punto de entrada unificado

## 📊 Diagramas del Proyecto

### Para Todo el Proyecto:
1. **Diagrama C4** (`3. DiagramaC4.xml`) - Vista de contexto del sistema
2. **Diagrama de Arquitectura** (`1. DiagramaMejorado_ServiCiudadConectada_Arquitectura.drawio.xml`) - Arquitectura general
3. **Diagrama de Clases General** (`11. DiagramaClasesGeneral_Proyecto.xml`) - Clases principales del sistema

### Para Microservicio de Pagos:
1. **Diagrama Hexagonal** (`2. DiagramaArquitecturaHexagonal_MicroservicioPagos.xml`) - Arquitectura del MS-Pagos
2. **Diagrama de Secuencia** (`9. DiagramaSecuencia_MicroservicioPagos.xml`) - Flujo de pago unificado
3. **Diagrama de Clases MS-Pagos** (`8. DiagramaClases_MicroservicioPagos.xml`) - Clases específicas
4. **Code Diagram** (`10. CodeDiagram_MicroservicioPagos.xml`) - Implementación detallada

## 🔧 Stack Tecnológico

- **Backend**: Java 17, Spring Boot, Spring Cloud
- **Gateway**: Spring Cloud Gateway
- **Resiliencia**: Resilience4j (Circuit Breaker, Retry, TimeLimiter)
- **Mensajería**: Apache Kafka
- **Bases de Datos**: PostgreSQL, MongoDB, Redis
- **Orquestación**: Kubernetes
- **Monitoreo**: Prometheus, Grafana
- **Seguridad**: OAuth2, JWT, ISO 27001

## 📁 Estructura del Proyecto

```
Ingenieria-Software-2/
├── Documentos/           # Documentación técnica
│   ├── 1. Caso Problema Proyecto Final.txt
│   ├── 1. ServiCiudadConectada_RequerimientosFuncionales_y_Arquitectura.txt
│   ├── 2. Escenarios_ServiCiudad.txt
│   └── 3. Politicas_Seguridad.txt
├── Graficos/            # Diagramas del proyecto
│   ├── 1. DiagramaMejorado_ServiCiudadConectada_Arquitectura.drawio.xml
│   ├── 2. DiagramaArquitecturaHexagonal_MicroservicioPagos.xml
│   ├── 3. DiagramaC4.xml
│   ├── 8. DiagramaClases_MicroservicioPagos.xml
│   ├── 9. DiagramaSecuencia_MicroservicioPagos.xml
│   ├── 10. CodeDiagram_MicroservicioPagos.xml
│   └── 11. DiagramaClasesGeneral_Proyecto.xml
└── README.md
```

## 🚀 Características Principales

### Funcionalidades Core:
- ✅ Consulta de saldos unificada
- ✅ Pago en línea multi-servicio
- ✅ Gestión de incidencias
- ✅ Notificaciones personalizadas
- ✅ Factura unificada
- ✅ Panel de administración
- ✅ Auditoría completa

### Calidad del Software:
- **Performance**: < 2s consultas, < 5s pagos
- **Disponibilidad**: 99.9% uptime
- **Seguridad**: Cifrado end-to-end, MFA
- **Usabilidad**: Interfaz responsive, accesible

## 📋 Requerimientos Funcionales

El sistema cumple con 15 requerimientos funcionales principales, organizados en épicas:

1. **Consulta y Gestión de Saldos y Pagos** (Must Have)
2. **Experiencia Digital Integral** (Should Have)
3. **Soporte y Trámites en Línea** (Should Have)
4. **Integración con Sistemas Legacy** (Must Have)
5. **Administración y Seguridad** (Must Have)

## 🔒 Seguridad

Implementación completa de políticas de seguridad ISO 27001:
- Clasificación de información por niveles
- Controles de acceso basados en roles (RBAC)
- Cifrado AES-256 y TLS 1.3
- Auditoría y trazabilidad completa
- Gestión de incidentes estructurada

## 👥 Equipo

- **Project Manager**: Eduard Criollo Yule (ID: 2220335)
- **Arquitecto de Software**: Responsable de diseño de microservicios
- **Desarrollador Full Stack**: Implementación de frontend y backend
- **DevOps Engineer**: Infraestructura y despliegue
- **QA Engineer**: Pruebas y validación

## 📞 Contacto

Para consultas sobre el proyecto:
- **Repositorio**: [GitHub - ServiCiudad Conectada]
- **Documentación**: Ver carpeta `/Documentos/`
- **Diagramas**: Ver carpeta `/Graficos/`

---

*Proyecto desarrollado para el curso de Ingeniería de Software 2*
*Universidad Autónoma de Occidente - 2024*

