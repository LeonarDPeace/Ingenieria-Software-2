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
- **Saga Pattern**: Transacciones distribuidas (MS-Pagos)
- **Circuit Breaker**: Resiliencia ante fallos sistemas legacy
- **Adapter Pattern**: Integración con Mainframe IBM Z y Oracle Solaris
- **Message Translator**: Transformación EBCDIC → JSON
- **API Gateway**: Punto de entrada unificado
- **Capa Anticorrupción**: Protección integridad datos legacy

## 📊 Diagramas del Proyecto

### Para Todo el Proyecto:
1. **Diagrama de Arquitectura** (`1. DiagramaMejorado_ServiCiudadConectada_Arquitectura.drawio`) - Arquitectura general del sistema
2. **Diagrama C4** (`2. DiagramaC4.drawio`) - Vista de contexto del sistema
3. **Diagrama de Clases General** (`3. DiagramaClasesGeneral_Proyecto.drawio`) - Clases principales del sistema

### Para Microservicio de Pagos:
1. **Diagrama Hexagonal** (`4. DiagramaArquitecturaHexagonal_MicroservicioPagos.drawio`) - Arquitectura del MS-Pagos
2. **Diagrama de Clases MS-Pagos** (`5. DiagramaClases_MicroservicioPagos.drawio`) - Clases específicas
3. **Diagrama de Secuencia** (`6. DiagramaSecuencia_MicroservicioPagos.drawio`) - Flujo de pago unificado
4. **Code Diagram** (`7. CodeDiagram_MicroservicioPagos.drawio`) - Implementación detallada

### Diagrama Organizacional:
1. **Organigrama** (`0. Organigrama.drawio`) - Estructura organizacional del proyecto

## 🔧 Stack Tecnológico

### **Backend & Microservicios:**
- **Java 17**: Lenguaje principal
- **Spring Boot + Spring Cloud**: Framework de microservicios
- **Resilience4j**: Circuit Breaker, Retry, TimeLimiter
- **Apache Kafka**: Event streaming y mensajería
- **Spring Cloud Gateway**: API Gateway

### **Frontend & Experiencia Usuario:**
- **React.js + Next.js**: Portal web responsive
- **React Native**: App móvil ciudadanos
- **Material-UI**: Design system
- **Interfaces unificadas**: Experiencia 360°

### **Integración Legacy:**
- **COBOL/EBCDIC**: Adaptadores Mainframe IBM Z
- **PL/SQL**: Conectividad Oracle Solaris
- **Message Translator**: Transformación de datos
- **Circuit Breaker**: Resiliencia sistemas legacy
- **Capa Anticorrupción**: Protección integridad datos

### **Datos & Persistencia:**
- **PostgreSQL**: Base de datos principal
- **MongoDB**: Datos no estructurados
- **Redis**: Caché y sesiones

### **Infraestructura & Monitoreo:**
- **Kubernetes**: Orquestación de contenedores
- **Prometheus + Grafana**: Monitoreo y métricas
- **OAuth2 + JWT**: Seguridad y autenticación
- **ISO 27001**: Cumplimiento seguridad

## 📁 Estructura del Proyecto

```
Ingenieria-Software-2/
├── Documentos/           # Documentación técnica
│   ├── 1. Caso Problema Proyecto Final.txt
│   ├── 1. ServiCiudadConectada_RequerimientosFuncionales_y_Arquitectura.txt
│   ├── 2. Escenarios_ServiCiudad.txt
│   ├── 3. Politicas_Seguridad.txt
│   ├── pdf/             # Documentos en formato PDF
│   └── Word/            # Documentos en formato Word
├── Graficos/            # Diagramas del proyecto
│   ├── Drawio/          # Archivos editables de Draw.io
│   │   ├── 0. Organigrama.drawio
│   │   ├── Proyecto_General/
│   │   │   ├── 1. DiagramaMejorado_ServiCiudadConectada_Arquitectura.drawio
│   │   │   ├── 2. DiagramaC4.drawio
│   │   │   └── 3. DiagramaClasesGeneral_Proyecto.drawio
│   │   └── Microrservicio_Pagos/
│   │       ├── 4. DiagramaArquitecturaHexagonal_MicroservicioPagos.drawio
│   │       ├── 5. DiagramaClases_MicroservicioPagos.drawio
│   │       ├── 6. DiagramaSecuencia_MicroservicioPagos.drawio
│   │       └── 7. CodeDiagram_MicroservicioPagos.drawio
│   ├── Pdf/             # Diagramas exportados en PDF
│   └── Xml/             # Diagramas en formato XML
├── docs/                # Documentación adicional
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

## 👥 Equipo de Desarrollo

### **Líder de Proyecto**
- **Eduard Criollo Yule** (ID: 2220335)
  - *Project Manager*
  - Liderar proyecto ServiCiudad Conectada
  - Coordinar integración sistemas legacy
  - Gestionar stakeholders y timeline
  - Garantizar experiencia 360° ciudadanos
  - Supervisar arquitectura microservicios

### **Arquitecto Legacy**
- **Felipe Charria Caicedo** (ID: 2216033)
  - *Integration Specialist*
  - Diseñar adaptadores legacy (Mainframe IBM Z, Oracle Solaris)
  - Implementar Circuit Breaker patterns
  - Gestionar conectividad COBOL/PL-SQL
  - Message Translator patterns
  - Administrar capa anticorrupción

### **Desarrollador Microservicios**
- **Jhonathan Chicaiza Herrera** (ID: 2215286)
  - *Backend Developer*
  - Implementar MS-Pagos con Saga Pattern
  - Desarrollar adaptadores resilientes
  - Configurar Kafka event streaming
  - Spring Boot + Spring Cloud
  - Resilience4j Circuit Breaker

### **Arquitecto Frontend**
- **Emmanuel Mena** (ID: 2230574)
  - *Full Stack Developer*
  - Diseñar experiencia unificada 360°
  - Desarrollar portal web responsive
  - Crear app móvil ciudadanos
  - React.js + Next.js, React Native
  - Integrar con API Gateway

### **Desarrollador Frontend**
- **Juan Sebastian Castillo** (ID: 2231921)
  - *Frontend Developer*
  - Implementar interfaz app/portal unificado
  - Garantizar experiencia de usuario intuitiva
  - Consumir APIs de microservicios eficientemente
  - Optimizar rendimiento frontend para dispositivos móviles
  - Responsabilidades Técnicas:
  - Desarrollo con React.js/Angular
  - Construcción de componentes modulares
  - Implementación de patrones de diseño frontend
  - Integración con API Gateway
  - Validación de usabilidad y accesibilidad
  - Coordinación con equipo UX/Backend

### **Niveles de Acceso y Responsabilidades:**

#### **Accesos Productivos:**
- **Eduard Criollo** (PM): Acceso read-only sistemas productivos
- **Felipe Charria** (Legacy): Solo lectura sistemas legacy
- **Jhonathan Chicaiza** (Backend): Admin entornos desarrollo/QA, solo lectura producción
- **Emmanuel Mena** (Frontend): Acceso APIs desarrollo/QA
- **Juan Sebastian Castillo** (Frontend): Acceso APIs desarrollo/QA

#### **Especialización Técnica:**
- **Integración Legacy**: Experto en COBOL/PL-SQL, Message Translator patterns
- **Microservicios**: Saga Pattern, Circuit Breaker, Kafka streaming
- **Frontend**: Experiencia 360°, responsive design, mobile-first
- **Gestión**: Coordinación stakeholders, timeline, arquitectura general

## 📞 Contacto

Para consultas sobre el proyecto:
- **Repositorio**: [GitHub - ServiCiudad Conectada](https://github.com/LeonarDPeace/Ingenieria-Software-2)
- **Documentación**: Ver carpeta `/Documentos/`
- **Diagramas**: Ver carpeta `/Graficos/`
- **Organigrama**: `0. Organigrama.drawio` - Estructura del equipo de trabajo

---

*Proyecto desarrollado para el curso de Ingeniería de Software 2*  
*Universidad Autónoma de Occidente - 2025*  
*Equipo: Eduard Criollo, Felipe Charria, Jhonathan Chicaiza, Emmanuel Mena, Juan Sebastian Castillo*

## 📋 Índice de Diagramas

### Formato Draw.io (Editables):
- `Graficos/Drawio/0. Organigrama.drawio`
- `Graficos/Drawio/Proyecto_General/1. DiagramaMejorado_ServiCiudadConectada_Arquitectura.drawio`
- `Graficos/Drawio/Proyecto_General/2. DiagramaC4.drawio`
- `Graficos/Drawio/Proyecto_General/3. DiagramaClasesGeneral_Proyecto.drawio`
- `Graficos/Drawio/Microrservicio_Pagos/4. DiagramaArquitecturaHexagonal_MicroservicioPagos.drawio`
- `Graficos/Drawio/Microrservicio_Pagos/5. DiagramaClases_MicroservicioPagos.drawio`
- `Graficos/Drawio/Microrservicio_Pagos/6. DiagramaSecuencia_MicroservicioPagos.drawio`
- `Graficos/Drawio/Microrservicio_Pagos/7. CodeDiagram_MicroservicioPagos.drawio`

### Formato PDF (Visualización):
- Disponibles en `Graficos/Pdf/` con la misma estructura organizacional

### Formato XML (Intercambio):
- Disponibles en `Graficos/Xml/` con la misma estructura organizacional

