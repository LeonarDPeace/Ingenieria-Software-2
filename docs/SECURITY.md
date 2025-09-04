# Políticas de Seguridad - ServiCiudad Conectada

## Introducción

Este documento establece las políticas y controles de seguridad para la plataforma ServiCiudad Conectada, garantizando la protección de datos personales, la integridad de los sistemas y el cumplimiento de normativas colombianas e internacionales.

## Marco Regulatorio y Cumplimiento

### Normativas Nacionales
- **Ley 1581 de 2012**: Protección de Datos Personales (Colombia)
- **Decreto 1377 de 2013**: Reglamentación Ley de Protección de Datos
- **Ley 1273 de 2009**: Delitos Informáticos
- **Circular Externa 052 de 2007**: Superintendencia Financiera (Seguridad Informática)

### Estándares Internacionales
- **ISO 27001:2013**: Sistema de Gestión de Seguridad de la Información
- **ISO 27002:2013**: Código de Práctica para Controles de Seguridad
- **PCI DSS 4.0**: Estándar de Seguridad de Datos para la Industria de Pagos
- **NIST Cybersecurity Framework**: Marco de Ciberseguridad
- **OWASP Top 10**: Principales riesgos de seguridad en aplicaciones web

## Clasificación de la Información

### Nivel 1 - Público
**Definición**: Información que puede ser divulgada al público sin restricciones.

**Ejemplos**:
- Información general de servicios
- Tarifas públicas
- Políticas de privacidad
- Documentación técnica pública

**Controles**:
- Sin restricciones especiales de acceso
- Versionado y control de cambios
- Publicación a través de canales oficiales

### Nivel 2 - Interno
**Definición**: Información para uso interno de ServiCiudad que no debe ser divulgada externamente.

**Ejemplos**:
- Procedimientos operativos internos
- Métricas de rendimiento del sistema
- Información de configuración no sensible
- Reportes de incidencias internas

**Controles**:
- Acceso restringido a empleados autorizados
- Autenticación obligatoria
- Logging de accesos
- Acuerdos de confidencialidad

### Nivel 3 - Confidencial
**Definición**: Información sensible cuya divulgación podría causar daño significativo.

**Ejemplos**:
- Datos personales de ciudadanos
- Información financiera de pagos
- Credenciales de sistemas
- Contratos y acuerdos comerciales

**Controles**:
- Cifrado en tránsito y reposo (AES-256)
- Control de acceso basado en roles (RBAC)
- Autenticación multifactor obligatoria
- Auditoría completa de accesos
- Clasificación y marcado de documentos

### Nivel 4 - Crítico
**Definición**: Información cuya divulgación o alteración podría causar daño severo a la organización.

**Ejemplos**:
- Claves criptográficas maestras
- Información de vulnerabilidades críticas
- Códigos fuente de componentes de seguridad
- Planes de continuidad del negocio

**Controles**:
- Cifrado con algoritmos certificados
- Segregación de funciones
- Acceso solo por personal autorizado específicamente
- Almacenamiento en sistemas de alta seguridad
- Procedimientos especiales de manejo

## Control de Acceso

### Principio de Menor Privilegio
Todos los usuarios y sistemas tienen únicamente los permisos mínimos necesarios para realizar sus funciones.

### Roles y Responsabilidades

#### 1. Ciudadano (CITIZEN)
**Permisos**:
- Consultar propios saldos y facturas
- Realizar pagos de sus servicios
- Reportar incidencias
- Actualizar perfil personal
- Consultar historial de transacciones

**Restricciones**:
- No acceso a datos de otros usuarios
- No acceso a funciones administrativas
- Límites de transacción configurables

#### 2. Operador de Soporte (SUPPORT_OPERATOR)
**Permisos**:
- Ver información de clientes (solo lectura)
- Gestionar tickets de soporte
- Consultar transacciones para resolución de incidencias
- Actualizar estado de tickets

**Restricciones**:
- No acceso a información financiera sensible
- No modificación de datos de usuario
- Auditoría obligatoria de todas las consultas

#### 3. Técnico de Campo (FIELD_TECHNICIAN)
**Permisos**:
- Actualizar estado de órdenes de trabajo
- Consultar información técnica de servicios
- Reportar resolución de incidencias
- Acceder a mapas de infraestructura

**Restricciones**:
- Acceso solo a información técnica relevante
- Sin acceso a datos financieros
- Geolocalización obligatoria para ciertas operaciones

#### 4. Administrador de Sistema (SYSTEM_ADMIN)
**Permisos**:
- Configuración del sistema
- Gestión de usuarios y roles
- Monitoreo y auditoría
- Mantenimiento de la plataforma

**Restricciones**:
- Autenticación multifactor obligatoria
- Segregación de funciones críticas
- Auditoría detallada de todas las acciones
- Aprobación dual para cambios críticos

#### 5. Auditor (AUDITOR)
**Permisos**:
- Acceso de solo lectura a logs y auditoría
- Generación de reportes de cumplimiento
- Consulta de métricas de seguridad

**Restricciones**:
- Solo acceso a información de auditoría
- Sin permisos de modificación
- Segregación total de funciones operativas

### Autenticación

#### Factores de Autenticación
1. **Algo que conoces**: Contraseña
2. **Algo que tienes**: Token SMS, aplicación authenticator
3. **Algo que eres**: Biometría (futuro)

#### Políticas de Contraseñas
```
Longitud mínima: 12 caracteres
Complejidad: Al menos 1 mayúscula, 1 minúscula, 1 número, 1 símbolo
Caducidad: 90 días para administradores, 180 días para usuarios
Historial: No reutilizar las últimas 12 contraseñas
Intentos fallidos: Bloqueo después de 5 intentos por 15 minutos
```

#### Autenticación Multifactor (MFA)
- **Obligatorio para**: Administradores, operadores, técnicos
- **Opcional para**: Ciudadanos (recomendado para pagos > $500,000)
- **Métodos soportados**: SMS, TOTP (Google Authenticator), Push notifications

### Autorización

#### Control de Acceso Basado en Roles (RBAC)
```json
{
  "roles": {
    "CITIZEN": {
      "permissions": [
        "billing:read:own",
        "payment:create:own",
        "profile:update:own",
        "incident:create",
        "notification:read:own"
      ]
    },
    "SUPPORT_OPERATOR": {
      "permissions": [
        "customer:read:assigned",
        "ticket:manage:assigned",
        "transaction:read:support",
        "incident:update:assigned"
      ]
    },
    "SYSTEM_ADMIN": {
      "permissions": [
        "system:configure",
        "user:manage",
        "audit:read",
        "metric:read",
        "maintenance:execute"
      ],
      "requires_mfa": true,
      "requires_approval": ["user:delete", "system:critical_config"]
    }
  }
}
```

## Seguridad en Comunicaciones

### Cifrado en Tránsito
- **TLS 1.3** obligatorio para todas las comunicaciones externas
- **mTLS** (mutual TLS) para comunicación entre microservicios
- **Perfect Forward Secrecy** habilitado
- Certificados con validez máxima de 90 días (renovación automática)

### Configuración de TLS
```nginx
# NGINX SSL Configuration
ssl_protocols TLSv1.3;
ssl_ciphers ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384;
ssl_prefer_server_ciphers off;
ssl_session_cache shared:SSL:10m;
ssl_session_timeout 1d;
ssl_session_tickets off;

# HSTS
add_header Strict-Transport-Security "max-age=63072000" always;

# Certificate Transparency
ssl_ct on;
ssl_ct_static_scts /path/to/scts;
```

### APIs y Microservicios
- **OAuth2 + JWT** para autenticación de APIs
- **Rate limiting** para prevenir ataques de denegación de servicio
- **API Gateway** como punto único de entrada con validación centralizada
- **Firmas digitales** para requests críticos (pagos)

## Seguridad de Datos

### Cifrado en Reposo
- **AES-256** para bases de datos y almacenamiento de archivos
- **Gestión de claves** con rotación automática cada 90 días
- **Key Management Service (KMS)** para manejo seguro de claves
- **Separación de claves** por ambiente (dev, staging, production)

### Configuración de Base de Datos
```sql
-- PostgreSQL encryption configuration
SET shared_preload_libraries = 'pg_tde';
SET pg_tde.keyring_configuration = '/etc/postgresql/keyring.conf';

-- Transparent Data Encryption
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    amount DECIMAL(15,2) ENCRYPTED,
    card_number VARCHAR(20) ENCRYPTED,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### Protección de Datos Personales

#### Minimización de Datos
- Recolección solo de datos estrictamente necesarios
- Pseudonimización de identificadores personales
- Anonimización para analytics y reportes

#### Técnicas de Protección
```java
// Pseudonimización de datos
public class DataPseudonymizer {
    
    @Service
    public String pseudonymizeUserId(String realUserId) {
        return DigestUtils.sha256Hex(realUserId + SECRET_SALT);
    }
    
    @Service
    public String maskCreditCard(String cardNumber) {
        return cardNumber.substring(0, 4) + "****" + 
               cardNumber.substring(cardNumber.length() - 4);
    }
    
    @Service
    public String encryptPII(String personalData) {
        return AES.encrypt(personalData, getEncryptionKey());
    }
}
```

#### Derechos de los Titulares
- **Derecho de acceso**: Portal para consultar datos personales
- **Derecho de rectificación**: Actualización de información personal
- **Derecho de supresión**: Eliminación de datos (con excepciones legales)
- **Derecho de portabilidad**: Exportación de datos en formato estándar

## Gestión de Vulnerabilidades

### Proceso de Gestión
1. **Identificación**: Escaneo automatizado semanal
2. **Evaluación**: Clasificación según criticidad
3. **Priorización**: SLA basado en severidad
4. **Remediación**: Aplicación de parches
5. **Verificación**: Validación de corrección

### SLA de Remediación
```
Críticas: 24 horas
Altas: 72 horas
Medias: 7 días
Bajas: 30 días
```

### Herramientas de Seguridad
- **SAST**: SonarQube para análisis estático de código
- **DAST**: OWASP ZAP para pruebas dinámicas
- **SCA**: Snyk para análisis de dependencias
- **Container Scanning**: Trivy para vulnerabilidades en contenedores

## Monitoreo y Respuesta a Incidentes

### Centro de Operaciones de Seguridad (SOC)
Monitoreo 24/7 con alertas automáticas para:
- Intentos de acceso no autorizado
- Anomalías en patrones de tráfico
- Fallos de autenticación repetidos
- Transacciones sospechosas
- Modificaciones no autorizadas de configuración

### Detección de Anomalías
```yaml
# Reglas de detección (Elastic Security)
rules:
  - name: "Multiple failed logins"
    condition: "failed_login_count > 5 within 5m"
    action: "block_ip_temporary"
    
  - name: "Unusual payment amount"
    condition: "payment_amount > avg(payment_amount) * 10"
    action: "require_additional_auth"
    
  - name: "Admin access outside hours"
    condition: "admin_login AND (hour < 8 OR hour > 18)"
    action: "alert_security_team"
```

### Plan de Respuesta a Incidentes

#### Clasificación de Incidentes
1. **Nivel 1 - Crítico**: Brecha de seguridad activa, datos comprometidos
2. **Nivel 2 - Alto**: Intento de ataque detectado, vulnerabilidad crítica
3. **Nivel 3 - Medio**: Comportamiento sospechoso, vulnerabilidad media
4. **Nivel 4 - Bajo**: Violación de política menor, vulnerabilidad baja

#### Proceso de Respuesta
1. **Detección** (objetivo: inmediata)
2. **Análisis inicial** (objetivo: 15 minutos)
3. **Contención** (objetivo: 1 hora para críticos)
4. **Erradicación** (objetivo: 4 horas para críticos)
5. **Recuperación** (objetivo: 8 horas para críticos)
6. **Lecciones aprendidas** (objetivo: 7 días)

### Notificación de Brechas
Cumplimiento de la Ley 1581 de 2012:
- **Autoridad competente**: 72 horas
- **Afectados**: Sin demora indebida (máximo 72 horas)
- **Canales**: Email, SMS, portal web, medios oficiales

## Seguridad en el Desarrollo

### DevSecOps

#### Pipeline de Seguridad
```yaml
# Security gates in CI/CD
stages:
  - static_analysis:
      tools: [sonarqube, semgrep]
      gate: "no_critical_vulnerabilities"
      
  - dependency_check:
      tools: [snyk, npm_audit]
      gate: "no_high_severity_dependencies"
      
  - container_scan:
      tools: [trivy, aqua]
      gate: "no_critical_os_vulnerabilities"
      
  - dynamic_testing:
      tools: [owasp_zap, burp]
      gate: "no_exploitable_vulnerabilities"
      
  - compliance_check:
      tools: [chef_inspec, aws_config]
      gate: "compliance_score > 95%"
```

#### Secure Coding Guidelines
1. **Validación de entrada**: Validar y sanitizar todas las entradas
2. **Gestión de errores**: No exponer información sensible en errores
3. **Logging seguro**: No registrar datos sensibles en logs
4. **Gestión de sesiones**: Tokens seguros con expiración adecuada
5. **Configuración segura**: Principios de configuración por defecto segura

### Code Review de Seguridad
- **Revisión obligatoria** por al menos 2 desarrolladores
- **Checklist de seguridad** para todas las PR
- **Herramientas automáticas** integradas en el proceso
- **Revisión de seguridad** para cambios críticos

## Gestión de Terceros

### Evaluación de Proveedores
Todos los proveedores que manejen datos de ServiCiudad deben:
- Completar cuestionario de seguridad
- Proporcionar certificaciones de seguridad (ISO 27001, SOC 2)
- Firmar acuerdos de confidencialidad
- Permitir auditorías de seguridad
- Notificar incidentes de seguridad en 24 horas

### Contratos y SLA
```
Disponibilidad: 99.9% mínimo
Tiempo de respuesta a incidentes: 2 horas
Cifrado: AES-256 mínimo
Backup: Diario con retención de 90 días
Auditoría: Acceso a logs por 1 año
Localización de datos: Colombia únicamente
```

## Formación y Concienciación

### Programa de Formación en Seguridad
- **Inducción**: Formación obligatoria para nuevos empleados
- **Actualización anual**: Renovación de conocimientos
- **Formación especializada**: Para roles críticos
- **Simulacros**: Ejercicios de phishing y respuesta a incidentes

### Métricas de Concienciación
- Tasa de participación en formación: > 95%
- Tasa de éxito en simulacros: > 80%
- Tiempo de reporte de incidentes: < 1 hora
- Cumplimiento de políticas: > 98%

## Auditoría y Cumplimiento

### Auditorías Internas
- **Frecuencia**: Trimestral
- **Alcance**: Controles técnicos y administrativos
- **Responsable**: Equipo de auditoría interna
- **Seguimiento**: Plan de acción para hallazgos

### Auditorías Externas
- **Frecuencia**: Anual
- **Estándar**: ISO 27001, PCI DSS
- **Auditor**: Entidad certificada independiente
- **Certificación**: Mantenimiento de certificaciones vigentes

### Métricas de Seguridad

#### KPIs Técnicos
```
Tiempo promedio de detección: < 1 hora
Tiempo promedio de respuesta: < 4 horas
Disponibilidad del sistema: > 99.9%
Vulnerabilidades críticas: 0
Parches aplicados en SLA: > 95%
```

#### KPIs de Cumplimiento
```
Cumplimiento de políticas: > 98%
Formación completada: > 95%
Incidentes reportados a tiempo: > 99%
Auditorías sin hallazgos críticos: 100%
Certificaciones vigentes: 100%
```

## Plan de Continuidad y Recuperación

### Objetivos de Recuperación
- **RTO (Recovery Time Objective)**: 4 horas para sistemas críticos
- **RPO (Recovery Point Objective)**: 1 hora para datos críticos
- **MTTR (Mean Time To Repair)**: 2 horas para incidentes críticos

### Estrategias de Backup
```yaml
backup_strategy:
  databases:
    frequency: "hourly"
    retention: "90 days"
    encryption: "AES-256"
    location: "multi-region"
    
  application_data:
    frequency: "daily"
    retention: "30 days"
    encryption: "AES-256"
    verification: "weekly_restore_test"
    
  configuration:
    frequency: "on_change"
    retention: "1 year"
    version_control: "git"
```

### Sitio de Recuperación
- **Ubicación**: Región secundaria en Colombia
- **Capacidad**: 100% de la capacidad principal
- **Sincronización**: Tiempo real para datos críticos
- **Activación**: Automática en caso de falla principal

---

Este documento de políticas de seguridad garantiza que ServiCiudad Conectada opere con los más altos estándares de seguridad, protegiendo los datos de los ciudadanos y manteniendo la confianza en los servicios digitales públicos.
