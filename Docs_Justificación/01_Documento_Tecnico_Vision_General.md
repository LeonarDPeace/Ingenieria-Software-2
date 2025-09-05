# DOCUMENTO TÉCNICO EXHAUSTIVO
## SERVICIUDAD CONECTADA - TRANSFORMACIÓN DIGITAL
### Universidad Autónoma de Occidente - Ingeniería de Software 2

---

**Versión:** 1.0  
**Fecha:** Septiembre 2025  
**Equipo:** Eduard Criollo, Felipe Charria, Jhonathan Chicaiza, Emmanuel Mena  
**Estado:** Documento de Diseño Técnico Final  

---

## TABLA DE CONTENIDOS

1. [VISIÓN GENERAL DEL PROYECTO](#1-visión-general-del-proyecto)
2. [CONTEXTO Y JUSTIFICACIÓN](#2-contexto-y-justificación)
3. [OBJETIVOS Y ALCANCE](#3-objetivos-y-alcance)
4. [ARQUITECTURA DE SOLUCIÓN](#4-arquitectura-de-solución)
5. [STAKEHOLDERS Y ORGANIZACIÓN](#5-stakeholders-y-organización)

---

## 1. VISIÓN GENERAL DEL PROYECTO

### 1.1 Resumen Ejecutivo

**ServiCiudad Conectada** es un proyecto de transformación digital integral para la ciudad de Cali, que busca unificar los servicios públicos de energía, acueducto y telecomunicaciones en una plataforma digital moderna y resiliente.

El proyecto implementa una **arquitectura de microservicios** con patrones de resiliencia avanzados, específicamente diseñada para integrar sistemas legacy complejos (Mainframe IBM Z, Oracle Solaris) mientras proporciona una experiencia ciudadana 360° unificada.

### 1.2 Problema a Resolver

#### **Situación Actual:**
- **CSAT actual: 45%** - Insatisfacción ciudadana con servicios fragmentados
- **Múltiples portales:** Ciudadanos deben acceder a 3+ sistemas diferentes
- **Sistemas legacy aislados:** Mainframe COBOL, Oracle PL/SQL, sistemas modernos
- **Ausencia de experiencia móvil** nativa para ciudadanos
- **Procesos manuales** en gestión de incidencias y soporte
- **Falta de visibilidad** unificada de servicios y pagos

#### **Impacto del Problema:**
```
Métrica                    | Actual    | Meta      | Impacto
---------------------------|-----------|-----------|------------------
CSAT (Satisfacción)        | 45%       | 80%+      | +35% mejora
Tiempo consulta saldos     | 30-60s    | <2s       | 15x más rápido
Canales de acceso          | 3 sep.    | 1 unif.   | Experiencia 360°
Tiempo resolución incid.   | 48-72h    | <24h      | 3x más eficiente
Adopción digital           | 30%       | 75%       | 2.5x incremento
```

### 1.3 Propuesta de Valor

#### **Para Ciudadanos:**
- ✅ **Experiencia Unificada 360°:** Un solo punto de acceso para todos los servicios
- ✅ **Consulta Instantánea:** Saldos y estados en tiempo real (<2s)
- ✅ **Pagos Simplificados:** Transacciones múltiples en una sola operación
- ✅ **App Móvil Nativa:** Acceso desde cualquier lugar, cualquier momento
- ✅ **Factura Unificada:** Consolidación de todos los servicios en un documento

#### **Para ServiCiudad:**
- ✅ **Integración Legacy Segura:** Preserva inversión en sistemas existentes
- ✅ **Escalabilidad Microservicios:** Crecimiento modular y mantenible
- ✅ **Resiliencia Avanzada:** Circuit Breakers y Saga Patterns
- ✅ **Cumplimiento Normativo:** ISO 27001, protección de datos
- ✅ **Reducción Costos Operativos:** Automatización y autoservicio

## 2. CONTEXTO Y JUSTIFICACIÓN

### 2.1 Análisis del Contexto Urbano

#### **Política "Cali Conectada"**
El proyecto se alinea directamente con la política municipal "Cali Conectada", que busca:
- **Modernización Tecnológica:** Adopción de tecnologías emergentes
- **Inclusión Digital:** Acceso equitativo a servicios digitales
- **Eficiencia Operativa:** Optimización de procesos municipales
- **Transparencia:** Trazabilidad y rendición de cuentas

#### **Análisis Demográfico de Cali**
```
Indicador                  | Valor     | Impacto en Diseño
---------------------------|-----------|---------------------------
Población Total            | 2.2M hab. | Escalabilidad masiva
Penetración Smartphone     | 78%       | Mobile-first approach
Acceso Internet            | 65%       | Diseño progresivo offline
Adultos Mayores (+60)      | 18%       | UX accesible y simple
Bancarización              | 72%       | Múltiples métodos pago
```

### 2.2 Sistemas Legacy Existentes

#### **Mainframe IBM Z (Sistema Energía)**
- **Tecnología:** COBOL, CICS, DB2 z/OS
- **Antigüedad:** 15+ años de operación continua
- **Características:**
  - Alto volumen transaccional (1M+ transacciones/día)
  - Ventana de mantenimiento: Domingos 2:00-6:00 AM
  - Latencia promedio: 30-45 segundos
  - Formato de datos: EBCDIC
  - Criticidad: ALTA - Sistema de facturación principal

#### **Oracle Solaris (Sistema Acueducto)**
- **Tecnología:** Oracle Database 12c, PL/SQL
- **Antigüedad:** 8 años, última actualización 2021
- **Características:**
  - Procesamiento batch nocturno
  - Latencia promedio: 20-30 segundos
  - Disponibilidad: 99.5% SLA
  - Integración: Oracle Forms legacy

#### **Sistemas Modernos (Telecomunicaciones)**
- **Tecnología:** APIs REST/SOAP, PostgreSQL
- **Características:**
  - Latencia baja: <1 segundo
  - APIs documentadas con OpenAPI 3.0
  - Autenticación OAuth2

### 2.3 Análisis de Restricciones Técnicas

#### **Restricciones de Integración:**
1. **No modificación de sistemas legacy** - Principio de no intrusión
2. **Ventanas de mantenimiento limitadas** - Máximo 4 horas semanales
3. **Latencia inherente** - Sistemas legacy con respuestas >30s
4. **Formatos de datos heterogéneos** - EBCDIC, ASCII, JSON
5. **Protocolos diversos** - SNA, TCP/IP, HTTP/HTTPS

#### **Restricciones de Seguridad:**
1. **ISO 27001 compliance** - Auditorías trimestrales
2. **Protección datos personales** - Ley 1581/2012 Colombia
3. **Cifrado extremo a extremo** - AES-256, TLS 1.3
4. **Trazabilidad completa** - Auditoría de todas las transacciones
5. **Segregación de entornos** - Dev/QA/Prod estrictamente separados

## 3. OBJETIVOS Y ALCANCE

### 3.1 Objetivos Estratégicos

#### **Objetivos Primarios (Must Have)**
1. **Unificación de Experiencia Ciudadana**
   - Portal web responsive único
   - App móvil nativa (iOS/Android)
   - Consulta de saldos consolidada <2s
   - Pagos unificados múltiples servicios

2. **Integración Legacy Resiliente**
   - Adaptadores para Mainframe IBM Z
   - Conectividad Oracle Solaris
   - Circuit Breaker patterns
   - Message Translator EBCDIC→JSON

3. **Mejora Indicadores de Satisfacción**
   - CSAT: 45% → 80%+
   - Tiempo consulta: 60s → <2s
   - Adopción digital: 30% → 75%
   - Disponibilidad: >99.9%

#### **Objetivos Secundarios (Should Have)**
1. **Optimización Operativa**
   - Automatización gestión incidencias
   - Dashboard analítica en tiempo real
   - Reportería avanzada para administradores

2. **Innovación Tecnológica**
   - Arquitectura microservicios cloud-native
   - Implementación Saga Pattern
   - Event-driven architecture con Kafka

### 3.2 Alcance del Proyecto

#### **Dentro del Alcance:**
✅ **Portal Web Responsive**
- Framework: React.js + Next.js
- Design System: Material-UI
- PWA capabilities para experiencia móvil

✅ **Aplicación Móvil Nativa**
- React Native multiplataforma
- Notificaciones push
- Funcionalidad offline básica

✅ **Microservicios Core:**
- MS-Clientes (autenticación/autorización)
- MS-Pagos (Saga Pattern distribuido)
- MS-Facturación (consolidación servicios)
- MS-Notificaciones (multicanal)
- MS-Incidencias (gestión tickets)
- MS-Administración (auditoría/seguridad)

✅ **Adaptadores Legacy:**
- Mainframe IBM Z Adapter
- Oracle Solaris Adapter
- Telecom Systems Adapter
- Message Translator (EBCDIC→JSON)

✅ **Infraestructura Resiliente:**
- API Gateway (Spring Cloud Gateway)
- Event Bus (Apache Kafka)
- Circuit Breakers (Resilience4j)
- Monitoring (Prometheus/Grafana)

#### **Fuera del Alcance:**
❌ **Modificación Sistemas Legacy**
- No se alterarán sistemas Mainframe/Oracle existentes
- Solo implementación de adaptadores de lectura/escritura

❌ **Migración de Datos Históricos**
- Mantenimiento de datos históricos en sistemas originales
- Solo sincronización de datos activos/recientes

❌ **Integración Sistemas Terceros**
- Bancos adicionales más allá de PSE
- Sistemas municipales fuera de servicios públicos

### 3.3 Criterios de Éxito

#### **Métricas Técnicas:**
```
Métrica                    | Objetivo   | Método Medición
---------------------------|------------|---------------------------
Tiempo Respuesta Consulta  | <2s        | APM tools (New Relic)
Tiempo Respuesta Pago      | <5s        | Logs transaccionales
Disponibilidad Sistema     | 99.9%      | Uptime monitoring
Throughput Pago           | 100 TPS    | Load testing JMeter
Latencia API Gateway      | <50ms      | Spring Boot Actuator
```

#### **Métricas de Negocio:**
```
Métrica                    | Meta       | Fuente Datos
---------------------------|------------|---------------------------
CSAT Score                | >80%       | Encuestas post-transacción
Adopción Digital          | >75%       | Google Analytics
Reducción Llamadas Soporte| -40%       | Sistema call center
Tiempo Resolución Incid.  | <24h       | Sistema tickets
Pagos Digitales/Total     | >60%       | Business Intelligence
```

## 4. ARQUITECTURA DE SOLUCIÓN

### 4.1 Principios Arquitectónicos

#### **Principios Fundamentales:**
1. **Microservicios Desacoplados:** Cada servicio es independiente y desplegable
2. **Domain-Driven Design (DDD):** Organización por dominios de negocio
3. **API-First:** Todas las funcionalidades expuestas vía APIs REST
4. **Event-Driven:** Comunicación asíncrona entre servicios
5. **Cloud-Native:** Diseño para contenedores y orquestación K8s

#### **Patrones de Resiliencia:**
```
Patrón                     | Propósito                 | Implementación
---------------------------|---------------------------|---------------------------
Circuit Breaker           | Fallos sistemas legacy    | Resilience4j
Saga Pattern              | Transacciones distribuidas| Axon Framework
Retry + Backoff          | Reconexión inteligente    | Spring Retry
Bulkhead                  | Aislamiento de fallos     | Thread pools separados
Cache-Aside              | Performance consultas     | Redis + Spring Cache
```

### 4.2 Vista de Contexto (Diagrama C4)

**Propósito del Diagrama:** Este diagrama C4 de contexto proporciona la vista de más alto nivel del sistema ServiCiudad Conectada, mostrando cómo interactúa con usuarios externos y sistemas de terceros.

**Decisiones que Apoya:**
- Identificación de todos los actores y sistemas externos
- Definición de fronteras del sistema
- Justificación de integraciones necesarias
- Alcance del proyecto claramente definido

**Cómo Leerlo en Entrevista:**
1. **Centro:** Sistema ServiCiudad (caja azul) es el núcleo de la solución
2. **Izquierda:** Usuarios (ciudadanos, operadores, técnicos, admins)
3. **Derecha:** Sistemas externos (legacy y modernos)
4. **Flechas:** Flujos de información y casos de uso principales

**Fuente:** Documento de requerimientos funcionales, análisis stakeholders
**Reproducible:** Draw.io ubicado en `Graficos/Drawio/Proyecto_General/2. DiagramaC4.drawio`

### 4.3 Arquitectura de Microservicios

#### **Distribución por Dominios:**

```
Dominio: GESTIÓN CIUDADANOS
├── MS-Clientes
│   ├── Autenticación OAuth2/JWT
│   ├── Gestión perfiles
│   ├── Roles y permisos (RBAC)
│   └── Integración directorio activo

Dominio: SERVICIOS FINANCIEROS  
├── MS-Pagos (Saga Pattern)
│   ├── Orchestrator transacciones distribuidas
│   ├── Integración PSE
│   ├── Compensaciones automáticas
│   └── Auditoría completa
├── MS-Facturación
│   ├── Consolidación servicios
│   ├── Generación PDF
│   └── Envío email automatizado

Dominio: OPERACIONES Y SOPORTE
├── MS-Incidencias
│   ├── Gestión tickets
│   ├── Workflow aprobaciones
│   ├── SLA tracking
│   └── Notificaciones automáticas
├── MS-Notificaciones
│   ├── Email (SMTP)
│   ├── SMS (proveedores múltiples)
│   ├── Push notifications
│   └── WhatsApp Business API

Dominio: ADMINISTRACIÓN Y SEGURIDAD
├── MS-Administración
│   ├── Auditoría ISO 27001
│   ├── Métricas y reportes
│   ├── Gestión configuración
│   └── Monitoring dashboard
```

### 4.4 Patrones de Integración Legacy

#### **Adapter Pattern - Mainframe IBM Z**
```
Componente: LegacyMainframeAdapter
├── Circuit Breaker (60s timeout, 60% failure threshold)
├── Message Translator (EBCDIC → JSON)
├── Connection Pool (max 10 connections)
├── Retry Logic (3 intentos, exponential backoff)
└── Compensation Handler (Saga Pattern)

Protocolo: SNA/LU6.2 → TCP/IP
Latencia Esperada: 30-45s (sistemas legacy)
Disponibilidad: 99.5% (excluyendo ventana mantenimiento)
```

#### **Message Translator Pattern**
```java
@Component
public class EBCDICMessageTranslator {
    
    @CircuitBreaker(name = "mainframe-translator")
    @Retry(name = "mainframe-translator")
    public ConsultaSaldoResponse traducirRespuestaMainframe(
        byte[] ebcdicResponse) {
        
        // Conversión EBCDIC → ASCII
        String asciiData = EBCDICConverter.convert(ebcdicResponse);
        
        // Parsing campos fijos COBOL
        CobolRecord record = CobolParser.parse(asciiData);
        
        // Mapeo a objeto Java/JSON
        return ConsultaSaldoResponse.builder()
            .numeroServicio(record.getField("NUM_SERVICIO"))
            .saldoActual(record.getDecimalField("SALDO_ACTUAL"))
            .fechaUltimoPago(record.getDateField("FEC_ULT_PAGO"))
            .build();
    }
}
```

## 5. STAKEHOLDERS Y ORGANIZACIÓN

### 5.1 Estructura Organizacional del Proyecto

**Propósito del Organigrama:** Documenta la estructura de roles, responsabilidades y niveles de acceso del equipo de desarrollo para garantizar segregación de funciones y cumplimiento de seguridad.

**Decisiones que Apoya:**
- Asignación de roles por especialización técnica
- Definición de niveles de acceso a sistemas productivos
- Estructura de escalación y toma de decisiones
- Distribución de responsabilidades por dominio

**Cómo Leerlo en Entrevista:**
1. **Nivel Superior:** Project Manager con responsabilidad general
2. **Nivel Técnico:** Tres especialistas con expertise específico
3. **Accesos:** Claramente diferenciados por rol y responsabilidad

#### **Roles y Responsabilidades Detalladas:**

##### **Eduard Criollo Yule - Project Manager (ID: 2220335)**
```
Responsabilidades Estratégicas:
├── Liderazgo proyecto ServiCiudad Conectada
├── Coordinación integración sistemas legacy
├── Gestión stakeholders y timeline
├── Garantía experiencia 360° ciudadanos
└── Supervisión arquitectura microservicios

Responsabilidades Técnicas:
├── Validación cumplimiento "Cali Conectada"
├── Administración documentación proyecto
├── Revisión arquitectura general
└── Coordinación entre equipos técnicos

Accesos Sistemas:
├── Read-only sistemas productivos
├── Admin herramientas proyecto (Jira, Confluence)
├── Dashboard monitoreo general
└── Reportes ejecutivos business intelligence
```

##### **Felipe Charria Caicedo - Integration Specialist (ID: 2216033)**
```
Especialización Legacy:
├── Experto COBOL/PL-SQL
├── Message Translator patterns
├── Administración capa anticorrupción
├── Circuit Breaker design patterns
└── Gestionar conectividad Mainframe/Oracle

Responsabilidades Técnicas:
├── Diseñar adaptadores legacy (IBM Z, Oracle Solaris)
├── Implementar Circuit Breaker patterns
├── Garantizar resiliencia sistémica
├── Coordinar transformación datos EBCDIC→JSON
└── Validar integridad datos legacy

Accesos Sistemas:
├── Solo lectura sistemas legacy
├── Admin entornos desarrollo adaptadores
├── Monitoreo específico sistemas legacy
└── Logs y trazas integración
```

##### **Jhonathan Chicaiza Herrera - Backend Developer (ID: 2215286)**
```
Especialización Microservicios:
├── Spring Boot + Spring Cloud
├── Resilience4j Circuit Breaker
├── Apache Kafka event streaming
├── Saga Pattern implementation
└── Container orchestration (K8s)

Responsabilidades Técnicas:
├── Implementar MS-Pagos con Saga Pattern
├── Desarrollar adaptadores resilientes
├── Configurar Kafka event streaming
├── Implementar monitoreo y métricas
└── Pipeline CI/CD microservicios

Accesos Sistemas:
├── Admin entornos desarrollo/QA
├── Solo lectura producción
├── Full access repositorios código
└── Métricas y logs microservicios
```

##### **Emmanuel Mena - Full Stack Developer (ID: 2230574)**
```
Especialización Frontend:
├── React.js + Next.js
├── React Native (móvil)
├── Material-UI design system
├── Progressive Web Apps (PWA)
└── API integration y testing

Responsabilidades Técnicas:
├── Diseñar experiencia unificada 360°
├── Desarrollar portal web responsive
├── Crear app móvil ciudadanos
├── Integrar con API Gateway
└── UX/UI testing y optimización

Accesos Sistemas:
├── Acceso APIs desarrollo/QA
├── Testing environments frontend
├── Analytics y métricas UX
└── CDN y assets management
```

### 5.2 Matriz de Comunicación

```
Stakeholder           | Interés              | Influencia | Estrategia Comunicación
----------------------|---------------------|------------|-------------------------
Alcaldía Cali         | Alta                | Alta       | Reportes ejecutivos semanales
Ciudadanos            | Alta                | Media      | Demos, beta testing, feedback
Operadores Internos   | Alta                | Media      | Training, workshops técnicos
Equipo Desarrollo     | Alta                | Alta       | Dailies, retrospectivas, docs
Proveedores Legacy    | Media               | Alta       | Coordinación técnica directa
Auditoría ISO 27001   | Media               | Alta       | Documentación compliance
```

---

**Fuente de Datos:** Organigrama proyecto (`Graficos/Drawio/0. Organigrama.drawio`)  
**Reproducible:** Información extraída de documentos oficiales del proyecto  
**Actualización:** Revisar trimestralmente o cuando cambien roles/responsabilidades

---

## CONCLUSIONES DEL DOCUMENTO DE VISIÓN GENERAL

Este documento establece las bases técnicas y organizacionales para el proyecto ServiCiudad Conectada, proporcionando:

1. **Contexto Claro:** Justificación técnica y de negocio del proyecto
2. **Arquitectura Sólida:** Principios y patrones para implementación robusta
3. **Organización Definida:** Roles claros y niveles de acceso apropiados
4. **Métricas Objetivas:** Criterios medibles de éxito del proyecto

**Próximos Documentos:**
- `02_Requerimientos_Funcionales_No_Funcionales.md`
- `03_Arquitectura_Tecnica_Componentes.md`
- `04_Especificacion_APIs_Modelo_Datos.md`
- `05_Estrategia_Pruebas_CI_CD.md`
- `06_Seguridad_Cumplimiento_Costos.md`

---
*Documento técnico preparado por el equipo ServiCiudad Conectada*  
*Universidad Autónoma de Occidente - Septiembre 2025*
