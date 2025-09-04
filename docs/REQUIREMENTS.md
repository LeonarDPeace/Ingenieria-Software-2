# Requerimientos del Sistema ServiCiudad Conectada

## Introducción

Este documento especifica los requerimientos funcionales y no funcionales para la plataforma digital ServiCiudad Conectada, que unificará los servicios de energía, acueducto y telecomunicaciones en Santiago de Cali.

## Actores del Sistema

### 1. Ciudadano
Usuario final que consume servicios públicos de ServiCiudad Cali.
- **Acceso**: Portal web, aplicación móvil
- **Funcionalidades**: Consulta saldos, pagos, reportes, notificaciones

### 2. Operador Interno
Personal de ServiCiudad que gestiona solicitudes y da soporte al cliente.
- **Acceso**: Portal administrativo
- **Funcionalidades**: Gestión de tickets, consultas avanzadas, reportes

### 3. Técnico de Campo
Personal técnico que resuelve incidencias físicas en terreno.
- **Acceso**: Aplicación móvil especializada
- **Funcionalidades**: Gestión de órdenes de trabajo, actualización de estado

### 4. Administrador del Sistema
Personal de TI que gestiona la plataforma y sus configuraciones.
- **Acceso**: Panel de administración
- **Funcionalidades**: Configuración, monitoreo, auditoría, reportes

## Requerimientos Funcionales

### ÉPICA 1: Consulta y Gestión de Saldos y Pagos (Must Have)

#### RF-001: Consulta de Saldos Unificada
**Descripción**: El sistema debe permitir a los ciudadanos consultar el saldo actualizado de todos sus servicios desde una interfaz unificada.

**Criterios de Aceptación**:
- El usuario visualiza en un solo lugar el saldo de energía, acueducto y telecomunicaciones
- La consulta es posible desde portal web y aplicación móvil
- Los datos corresponden a información actual de sistemas legacy
- Tiempo de respuesta menor a 10 segundos en condiciones normales
- Manejo de degradación elegante cuando sistemas legacy no estén disponibles

**Prioridad**: Alta  
**Estimación**: 8 Story Points  
**Dependencias**: Integración con sistemas legacy

#### RF-002: Pago en Línea de Servicios
**Descripción**: El sistema debe permitir realizar pagos de servicios públicos de forma individual o combinada en una sola transacción.

**Criterios de Aceptación**:
- Selección de uno o múltiples servicios para pago conjunto
- Integración segura con pasarela de pagos PSE
- Confirmación inmediata de pago exitoso o fallido
- Actualización de saldo en menos de 15 minutos
- Generación de comprobante de pago digital
- Soporte para diferentes métodos de pago

**Prioridad**: Alta  
**Estimación**: 13 Story Points  
**Dependencias**: RF-001, integración PSE, sistemas legacy

#### RF-003: Historial de Transacciones
**Descripción**: El sistema debe mantener un historial completo de todas las transacciones realizadas por el usuario.

**Criterios de Aceptación**:
- Visualización de historial de pagos con filtros por fecha y servicio
- Descarga de comprobantes de pago anteriores
- Búsqueda de transacciones por número de referencia
- Exportación de historial en formato PDF/Excel
- Retención de datos por mínimo 5 años

**Prioridad**: Media  
**Estimación**: 5 Story Points

### ÉPICA 2: Experiencia Digital Integral (Should Have)

#### RF-004: Factura Unificada Digital
**Descripción**: El sistema debe generar y mostrar una factura unificada que consolide todos los servicios en un solo documento.

**Criterios de Aceptación**:
- Visualización de factura unificada con desglose por servicio
- Descarga en formato PDF con diseño profesional
- Envío automático por correo electrónico
- Comparación con períodos anteriores
- Notificaciones de nueva factura disponible

**Prioridad**: Media-Alta  
**Estimación**: 8 Story Points  
**Dependencias**: RF-001, integración legacy

#### RF-005: Notificaciones Personalizadas
**Descripción**: El sistema debe enviar notificaciones relevantes al usuario a través de múltiples canales.

**Criterios de Aceptación**:
- Notificaciones por SMS, email y push notifications
- Alertas de vencimiento de facturas
- Notificaciones de cortes programados
- Confirmaciones de pago
- Configuración de preferencias de notificación
- Historial de notificaciones enviadas

**Prioridad**: Media  
**Estimación**: 8 Story Points

#### RF-006: Dashboard Personalizado
**Descripción**: El sistema debe mostrar un dashboard con información relevante y personalizada para cada usuario.

**Criterios de Aceptación**:
- Resumen de saldos y consumos
- Gráficos de consumo histórico
- Alertas y notificaciones pendientes
- Accesos rápidos a funciones frecuentes
- Personalización de widgets
- Información meteorológica para gestión de consumo

**Prioridad**: Media  
**Estimación**: 13 Story Points

### ÉPICA 3: Soporte y Trámites en Línea (Should Have)

#### RF-007: Reporte Interactivo de Fallas
**Descripción**: El sistema debe permitir reportar fallas e incidencias de forma interactiva con seguimiento en tiempo real.

**Criterios de Aceptación**:
- Formulario interactivo para reporte de fallas
- Asignación automática de número de ticket
- Seguimiento de estado del reporte en tiempo real
- Notificaciones automáticas de avances
- Enrutamiento automático al área responsable
- Calificación del servicio al cerrar el ticket

**Prioridad**: Media  
**Estimación**: 13 Story Points

#### RF-008: Solicitudes en Línea
**Descripción**: El sistema debe permitir realizar solicitudes de servicios de forma digital.

**Criterios de Aceptación**:
- Solicitudes de nuevos servicios
- Cambios de titularidad
- Solicitudes de certificaciones
- Seguimiento de estado de solicitudes
- Carga de documentos requeridos
- Notificaciones de actualización de estado

**Prioridad**: Baja  
**Estimación**: 21 Story Points

### ÉPICA 4: Integración con Sistemas Legacy (Must Have)

#### RF-009: Capa de Integración Anticorrupción
**Descripción**: El sistema debe implementar una capa que permita integrar con sistemas legacy garantizando integridad y consistencia.

**Criterios de Aceptación**:
- Integración transparente con Mainframe IBM Z (energía)
- Integración con base de datos Oracle Solaris (acueducto)
- Adaptación a sistemas modernos de telecomunicaciones
- Gestión de indisponibilidad programada del mainframe
- Transformación de formatos legacy (EBCDIC, texto fijo) a JSON
- Manejo de latencias altas (hasta 30 segundos)

**Prioridad**: Alta  
**Estimación**: 21 Story Points

#### RF-010: Sincronización de Datos
**Descripción**: El sistema debe mantener sincronizados los datos entre la plataforma moderna y los sistemas legacy.

**Criterios de Aceptación**:
- Sincronización bidireccional de datos críticos
- Detección y resolución de conflictos
- Logs de auditoría de sincronización
- Alertas de fallos en sincronización
- Recuperación automática ante errores

**Prioridad**: Alta  
**Estimación**: 13 Story Points

### ÉPICA 5: Administración y Seguridad (Must Have)

#### RF-011: Gestión de Usuarios y Roles
**Descripción**: El sistema debe permitir gestión completa de usuarios con control de acceso basado en roles.

**Criterios de Aceptación**:
- Registro y autenticación segura de usuarios
- Control de acceso basado en roles (RBAC)
- Autenticación multifactor para perfiles administrativos
- Gestión de sesiones con timeout automático
- Recuperación segura de contraseñas
- Auditoría de accesos y acciones

**Prioridad**: Alta  
**Estimación**: 13 Story Points

#### RF-012: Auditoría y Monitoreo
**Descripción**: El sistema debe registrar todas las operaciones críticas para auditoría y cumplimiento.

**Criterios de Aceptación**:
- Registro inmutable de todas las transacciones
- Trazabilidad completa de pagos y consultas
- Logs de acceso y modificaciones
- Dashboards de monitoreo en tiempo real
- Alertas automáticas de eventos críticos
- Reportes de cumplimiento regulatorio

**Prioridad**: Alta  
**Estimación**: 8 Story Points

#### RF-013: Configuración del Sistema
**Descripción**: El sistema debe permitir configuración dinámica de parámetros operacionales.

**Criterios de Aceptación**:
- Configuración de límites de transacción
- Gestión de tarifas y comisiones
- Configuración de notificaciones
- Gestión de ventanas de mantenimiento
- Configuración de integración con sistemas legacy
- Versionado de configuraciones

**Prioridad**: Media  
**Estimación**: 8 Story Points

## Requerimientos No Funcionales

### Rendimiento (Performance)
- **RNF-001**: Tiempo de respuesta para consultas de saldo < 2 segundos (percentil 95)
- **RNF-002**: Tiempo de procesamiento de pagos < 5 segundos (percentil 95)
- **RNF-003**: Capacidad para 10,000 usuarios concurrentes
- **RNF-004**: Throughput mínimo de 1,000 transacciones por minuto

### Disponibilidad (Availability)
- **RNF-005**: Disponibilidad del sistema 99.9% (8.76 horas de inactividad anual)
- **RNF-006**: Tiempo de recuperación ante fallos < 5 minutos (RTO)
- **RNF-007**: Punto de recuperación ante fallos < 1 hora (RPO)
- **RNF-008**: Degradación elegante ante fallos de sistemas legacy

### Escalabilidad (Scalability)
- **RNF-009**: Escalabilidad horizontal automática basada en carga
- **RNF-010**: Soporte para crecimiento de 50% anual de usuarios
- **RNF-011**: Capacidad de procesamiento escalable para Black Friday/fechas pico
- **RNF-012**: Distribución geográfica para reducir latencia

### Seguridad (Security)
- **RNF-013**: Cifrado AES-256 para datos en reposo
- **RNF-014**: TLS 1.3 para comunicaciones
- **RNF-015**: Cumplimiento ISO 27001
- **RNF-016**: Cumplimiento PCI DSS para procesamiento de pagos
- **RNF-017**: Detección automática de fraude
- **RNF-018**: Protección contra ataques DDoS

### Usabilidad (Usability)
- **RNF-019**: Interfaz responsive para dispositivos móviles y desktop
- **RNF-020**: Cumplimiento WCAG 2.1 AA para accesibilidad
- **RNF-021**: Soporte para múltiples navegadores (Chrome, Firefox, Safari, Edge)
- **RNF-022**: Tiempo de aprendizaje < 30 minutos para funciones básicas

### Mantenibilidad (Maintainability)
- **RNF-023**: Cobertura de pruebas automatizadas > 80%
- **RNF-024**: Documentación técnica completa y actualizada
- **RNF-025**: Arquitectura modular para facilitar mantenimiento
- **RNF-026**: Deployments sin tiempo de inactividad (zero-downtime)

### Interoperabilidad (Interoperability)
- **RNF-027**: APIs REST siguiendo estándares OpenAPI 3.0
- **RNF-028**: Integración con sistemas legacy sin modificaciones
- **RNF-029**: Soporte para formatos estándar (JSON, XML, PDF)
- **RNF-030**: Compatibilidad con estándares bancarios colombianos

## Restricciones

### Técnicas
- **REST-001**: Uso obligatorio de Java 17 y Spring Boot 3.x
- **REST-002**: Base de datos PostgreSQL para datos transaccionales
- **REST-003**: Apache Kafka para mensajería asíncrona
- **REST-004**: Kubernetes para orquestación de contenedores
- **REST-005**: No modificación de sistemas legacy existentes

### Negocio
- **REST-006**: Integración con pasarela PSE obligatoria
- **REST-007**: Cumplimiento de normativas bancarias colombianas
- **REST-008**: Soporte 24/7 para funciones críticas
- **REST-009**: Ventana de mantenimiento máxima 4 horas mensuales
- **REST-010**: Retención de datos por mínimo 7 años

### Regulatorias
- **REST-011**: Cumplimiento Ley 1581 de Protección de Datos (Colombia)
- **REST-012**: Cumplimiento directrices Superintendencia de Servicios Públicos
- **REST-013**: Auditoría anual de seguridad por tercero certificado
- **REST-014**: Notificación de brechas de seguridad en 72 horas

## Criterios de Aceptación Generales

### Calidad de Software
- Cobertura de pruebas unitarias mínima 80%
- Cobertura de pruebas de integración mínima 70%
- Sin vulnerabilidades críticas según OWASP Top 10
- Cumplimiento de estándares de código (SonarQube Quality Gate)

### Performance
- Tiempo de carga inicial < 3 segundos
- Optimización para conexiones lentas (2G/3G)
- Compresión de assets y lazy loading
- CDN para contenido estático

### Experiencia de Usuario
- Diseño mobile-first
- Interfaz intuitiva validada por testing de usuarios
- Mensajes de error claros y accionables
- Feedback visual para todas las acciones

## Métricas de Éxito

### Técnicas
- **Disponibilidad**: > 99.9%
- **Performance**: < 2s respuesta promedio
- **Errores**: < 0.1% tasa de error
- **Adopción**: 80% usuarios migrados en 6 meses

### Negocio
- **CSAT**: Incremento del 45% al 80%
- **Calls Reduction**: 60% reducción consultas call center
- **Cost Savings**: $1.5B COP ahorro anual
- **Digital Adoption**: 90% transacciones digitales

---

Este documento define los requerimientos base para la implementación exitosa de ServiCiudad Conectada, garantizando que la solución cumpla con las expectativas de todos los stakeholders y las necesidades del negocio.
