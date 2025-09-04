# API Reference - ServiCiudad Conectada

## Introducción

Esta documentación describe las APIs REST del sistema ServiCiudad Conectada, diseñadas siguiendo los principios RESTful y estándares OpenAPI 3.0.

**Base URL**: `https://api.serviciudad.gov.co/v1`

## Autenticación

Todas las APIs utilizan autenticación OAuth2 con tokens JWT.

### Obtener Token de Acceso

```http
POST /auth/token
Content-Type: application/json

{
  "username": "usuario@email.com",
  "password": "password123",
  "grant_type": "password"
}
```

**Respuesta Exitosa**:
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "read write"
}
```

### Usar Token en Requests

```http
Authorization: Bearer {access_token}
```

## Microservicio de Clientes

### Autenticación y Gestión de Usuarios

#### POST /auth/login
Autenticar usuario en el sistema.

**Request**:
```json
{
  "email": "usuario@email.com",
  "password": "password123"
}
```

**Response 200**:
```json
{
  "user": {
    "id": "usr_123456",
    "email": "usuario@email.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "role": "CITIZEN",
    "lastLogin": "2024-01-15T10:30:00Z"
  },
  "tokens": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJl...",
    "expiresIn": 3600
  }
}
```

#### POST /auth/register
Registrar nuevo usuario.

**Request**:
```json
{
  "email": "nuevo@email.com",
  "password": "newPassword123",
  "firstName": "María",
  "lastName": "García",
  "phoneNumber": "+573001234567",
  "identificationType": "CC",
  "identificationNumber": "12345678"
}
```

**Response 201**:
```json
{
  "id": "usr_789012",
  "email": "nuevo@email.com",
  "status": "PENDING_VERIFICATION",
  "verificationEmailSent": true
}
```

#### GET /users/{userId}
Obtener perfil de usuario.

**Response 200**:
```json
{
  "id": "usr_123456",
  "email": "usuario@email.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "+573001234567",
  "services": [
    {
      "type": "ENERGIA",
      "serviceNumber": "1234567890",
      "address": "Calle 1 #2-3, Cali"
    },
    {
      "type": "ACUEDUCTO", 
      "serviceNumber": "0987654321",
      "address": "Calle 1 #2-3, Cali"
    }
  ],
  "preferences": {
    "notifications": {
      "email": true,
      "sms": false,
      "push": true
    }
  }
}
```

## Microservicio de Pagos

### Procesamiento de Pagos

#### POST /pagos/unificado
Procesar pago unificado de múltiples servicios.

**Request**:
```json
{
  "clienteId": "usr_123456",
  "servicios": [
    {
      "tipo": "ENERGIA",
      "numeroServicio": "1234567890",
      "monto": 85000
    },
    {
      "tipo": "ACUEDUCTO",
      "numeroServicio": "0987654321", 
      "monto": 65000
    }
  ],
  "metodoPago": "PSE",
  "banco": "BANCOLOMBIA",
  "tipoPersona": "N"
}
```

**Response 200**:
```json
{
  "paymentId": "pay_abc123def456",
  "status": "PROCESSING",
  "montoTotal": 150000,
  "comision": 1500,
  "montoFinal": 151500,
  "fechaCreacion": "2024-01-15T14:30:00Z",
  "pseUrl": "https://pse.redeban.com.co/pay?ref=abc123",
  "serviciosProcesados": [
    {
      "tipo": "ENERGIA",
      "status": "PENDING",
      "monto": 85000
    },
    {
      "tipo": "ACUEDUCTO",
      "status": "PENDING", 
      "monto": 65000
    }
  ]
}
```

#### GET /pagos/{paymentId}/status
Consultar estado de pago.

**Response 200**:
```json
{
  "paymentId": "pay_abc123def456",
  "status": "COMPLETED",
  "montoTotal": 150000,
  "fechaCreacion": "2024-01-15T14:30:00Z",
  "fechaCompletado": "2024-01-15T14:32:15Z",
  "transactionId": "TXN_789012345",
  "servicios": [
    {
      "tipo": "ENERGIA",
      "status": "COMPLETED",
      "monto": 85000,
      "saldoAnterior": 120000,
      "saldoActual": 35000
    },
    {
      "tipo": "ACUEDUCTO",
      "status": "COMPLETED",
      "monto": 65000,
      "saldoAnterior": 98000,
      "saldoActual": 33000
    }
  ],
  "comprobante": {
    "url": "https://api.serviciudad.gov.co/v1/pagos/pay_abc123def456/comprobante",
    "format": "PDF"
  }
}
```

#### POST /pagos/{paymentId}/cancelar
Cancelar pago en progreso.

**Response 200**:
```json
{
  "paymentId": "pay_abc123def456",
  "status": "CANCELLED",
  "fechaCancelacion": "2024-01-15T14:35:00Z",
  "compensaciones": [
    {
      "servicio": "ENERGIA",
      "accion": "REVERT_BALANCE",
      "status": "COMPLETED"
    }
  ]
}
```

### Consulta de Saldos

#### GET /saldos/{clienteId}
Obtener saldos unificados del cliente.

**Response 200**:
```json
{
  "clienteId": "usr_123456",
  "fechaConsulta": "2024-01-15T15:00:00Z",
  "servicios": [
    {
      "tipo": "ENERGIA",
      "numeroServicio": "1234567890",
      "saldoActual": 35000,
      "fechaVencimiento": "2024-01-25",
      "diasVencimiento": 10,
      "estado": "VIGENTE",
      "ultimoPago": {
        "fecha": "2024-01-15T14:32:15Z",
        "monto": 85000
      }
    },
    {
      "tipo": "ACUEDUCTO",
      "numeroServicio": "0987654321",
      "saldoActual": 33000,
      "fechaVencimiento": "2024-01-28",
      "diasVencimiento": 13,
      "estado": "VIGENTE",
      "consumo": {
        "periodoActual": 15.5,
        "unidad": "m3",
        "periodo": "2024-01"
      }
    },
    {
      "tipo": "TELECOMUNICACIONES",
      "numeroServicio": "3001234567",
      "saldoActual": 0,
      "fechaVencimiento": "2024-01-20",
      "diasVencimiento": 5,
      "estado": "VIGENTE",
      "plan": {
        "nombre": "Plan Hogar 100MB",
        "velocidad": "100 Mbps",
        "datosRestantes": "50 GB"
      }
    }
  ],
  "resumen": {
    "totalSaldo": 68000,
    "serviciosVigentes": 3,
    "serviciosVencidos": 0,
    "proximoVencimiento": "2024-01-20"
  }
}
```

## Microservicio de Facturación

### Gestión de Facturas

#### GET /facturas/{clienteId}
Obtener factura unificada del cliente.

**Query Parameters**:
- `periodo`: Período de facturación (YYYY-MM)
- `formato`: Formato de respuesta (json, pdf)

**Response 200**:
```json
{
  "facturaId": "fact_202401_usr123456",
  "clienteId": "usr_123456",
  "periodo": "2024-01",
  "fechaGeneracion": "2024-01-31T23:59:59Z",
  "fechaVencimiento": "2024-02-15",
  "servicios": [
    {
      "tipo": "ENERGIA",
      "consumo": {
        "kwh": 245,
        "valorKwh": 350,
        "subsidio": 15000
      },
      "cargos": {
        "consumo": 85750,
        "alumbradoPublico": 8575,
        "contribucion": 4288
      },
      "total": 83613
    },
    {
      "tipo": "ACUEDUCTO",
      "consumo": {
        "m3": 15.5,
        "valorM3": 3500,
        "cargoFijo": 12000
      },
      "cargos": {
        "consumo": 54250,
        "cargoFijo": 12000,
        "alcantarillado": 27125
      },
      "total": 93375
    }
  ],
  "totales": {
    "subtotal": 176988,
    "descuentos": 15000,
    "total": 161988
  },
  "downloads": {
    "pdf": "https://api.serviciudad.gov.co/v1/facturas/fact_202401_usr123456/pdf",
    "xml": "https://api.serviciudad.gov.co/v1/facturas/fact_202401_usr123456/xml"
  }
}
```

#### GET /facturas/{clienteId}/historial
Obtener historial de facturas.

**Query Parameters**:
- `desde`: Fecha inicio (YYYY-MM-DD)
- `hasta`: Fecha fin (YYYY-MM-DD)
- `limite`: Número máximo de resultados (default: 12)

**Response 200**:
```json
{
  "clienteId": "usr_123456",
  "facturas": [
    {
      "facturaId": "fact_202401_usr123456",
      "periodo": "2024-01",
      "total": 161988,
      "estado": "PAGADA",
      "fechaPago": "2024-01-15T14:32:15Z"
    },
    {
      "facturaId": "fact_202312_usr123456", 
      "periodo": "2023-12",
      "total": 143500,
      "estado": "PAGADA",
      "fechaPago": "2023-12-20T09:15:30Z"
    }
  ],
  "resumen": {
    "totalFacturas": 24,
    "totalPagado": 3847560,
    "promedioMensual": 160315
  }
}
```

## Microservicio de Notificaciones

### Gestión de Notificaciones

#### POST /notificaciones/enviar
Enviar notificación a usuario.

**Request**:
```json
{
  "destinatario": "usr_123456",
  "tipo": "VENCIMIENTO_FACTURA",
  "canales": ["EMAIL", "SMS"],
  "datos": {
    "servicios": ["ENERGIA", "ACUEDUCTO"],
    "montoTotal": 161988,
    "fechaVencimiento": "2024-02-15"
  },
  "prioridad": "ALTA"
}
```

**Response 200**:
```json
{
  "notificacionId": "notif_abc123",
  "estado": "ENVIADA",
  "canalesEnviados": [
    {
      "canal": "EMAIL",
      "estado": "ENTREGADO",
      "fechaEnvio": "2024-01-15T16:00:00Z"
    },
    {
      "canal": "SMS",
      "estado": "ENTREGADO", 
      "fechaEnvio": "2024-01-15T16:00:05Z"
    }
  ]
}
```

#### GET /notificaciones/{userId}
Obtener historial de notificaciones del usuario.

**Query Parameters**:
- `desde`: Fecha inicio
- `hasta`: Fecha fin
- `tipo`: Filtro por tipo de notificación
- `estado`: Filtro por estado

**Response 200**:
```json
{
  "userId": "usr_123456",
  "notificaciones": [
    {
      "id": "notif_abc123",
      "tipo": "VENCIMIENTO_FACTURA",
      "titulo": "Factura próxima a vencer",
      "mensaje": "Su factura de $161,988 vence el 15 de febrero",
      "fechaEnvio": "2024-01-15T16:00:00Z",
      "leida": false,
      "canales": ["EMAIL", "SMS"]
    }
  ],
  "resumen": {
    "total": 45,
    "noLeidas": 3,
    "tiposRecientes": ["VENCIMIENTO_FACTURA", "PAGO_CONFIRMADO"]
  }
}
```

## Microservicio de Incidencias

### Gestión de Tickets

#### POST /incidencias
Crear nueva incidencia.

**Request**:
```json
{
  "clienteId": "usr_123456",
  "tipoServicio": "ENERGIA",
  "categoria": "CORTE_SERVICIO",
  "prioridad": "ALTA",
  "descripcion": "Corte de energía en todo el barrio desde las 14:00",
  "ubicacion": {
    "direccion": "Calle 1 #2-3, Cali",
    "barrio": "El Poblado",
    "coordenadas": {
      "lat": 3.4516,
      "lng": -76.5320
    }
  },
  "adjuntos": [
    {
      "tipo": "IMAGEN",
      "url": "https://storage.serviciudad.gov.co/adjuntos/img_001.jpg",
      "descripcion": "Foto del transformador"
    }
  ]
}
```

**Response 201**:
```json
{
  "ticketId": "INC-2024-001234",
  "estado": "ABIERTO",
  "fechaCreacion": "2024-01-15T16:30:00Z",
  "prioridad": "ALTA",
  "tiempoEstimado": "4 horas",
  "responsable": {
    "area": "MANTENIMIENTO_ENERGIA",
    "tecnico": null
  },
  "seguimiento": {
    "url": "https://app.serviciudad.gov.co/incidencias/INC-2024-001234",
    "codigo": "001234"
  }
}
```

#### GET /incidencias/{ticketId}
Consultar estado de incidencia.

**Response 200**:
```json
{
  "ticketId": "INC-2024-001234",
  "estado": "EN_PROGRESO",
  "fechaCreacion": "2024-01-15T16:30:00Z",
  "fechaActualizacion": "2024-01-15T17:15:00Z",
  "prioridad": "ALTA",
  "categoria": "CORTE_SERVICIO",
  "descripcion": "Corte de energía en todo el barrio desde las 14:00",
  "responsable": {
    "area": "MANTENIMIENTO_ENERGIA",
    "tecnico": {
      "nombre": "Carlos Rodríguez",
      "telefono": "+573001234567"
    }
  },
  "actualizaciones": [
    {
      "fecha": "2024-01-15T17:15:00Z",
      "estado": "EN_PROGRESO",
      "comentario": "Técnico asignado, dirigiéndose al sitio",
      "responsable": "Sistema Automático"
    },
    {
      "fecha": "2024-01-15T16:45:00Z", 
      "estado": "ASIGNADO",
      "comentario": "Incidencia asignada a técnico de zona",
      "responsable": "Dispatcher"
    }
  ],
  "tiempoEstimado": "2 horas restantes",
  "afectados": {
    "usuarios": 1250,
    "area": "Barrio El Poblado"
  }
}
```

## Códigos de Estado HTTP

### Exitosos (2xx)
- **200 OK**: Solicitud procesada correctamente
- **201 Created**: Recurso creado exitosamente
- **202 Accepted**: Solicitud aceptada para procesamiento asíncrono
- **204 No Content**: Solicitud exitosa sin contenido de respuesta

### Errores del Cliente (4xx)
- **400 Bad Request**: Solicitud malformada
- **401 Unauthorized**: Autenticación requerida
- **403 Forbidden**: Sin permisos para acceder al recurso
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Conflicto con el estado actual del recurso
- **422 Unprocessable Entity**: Errores de validación
- **429 Too Many Requests**: Límite de velocidad excedido

### Errores del Servidor (5xx)
- **500 Internal Server Error**: Error interno del servidor
- **502 Bad Gateway**: Error en sistema legacy
- **503 Service Unavailable**: Servicio temporalmente no disponible
- **504 Gateway Timeout**: Timeout en sistema legacy

## Formato de Errores

```json
{
  "error": {
    "code": "PAYMENT_FAILED",
    "message": "El pago no pudo ser procesado",
    "details": [
      {
        "field": "monto",
        "message": "El monto excede el límite diario"
      }
    ],
    "timestamp": "2024-01-15T16:45:00Z",
    "requestId": "req_abc123def456"
  }
}
```

## Rate Limiting

Todas las APIs están sujetas a límites de velocidad:

- **Usuarios autenticados**: 1000 requests/hora
- **Endpoints de pago**: 100 requests/hora
- **APIs públicas**: 100 requests/hora

Headers de respuesta:
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1642259400
```

## Webhooks

### Eventos de Pago
```json
{
  "event": "payment.completed",
  "data": {
    "paymentId": "pay_abc123def456",
    "clienteId": "usr_123456",
    "montoTotal": 150000,
    "status": "COMPLETED"
  },
  "timestamp": "2024-01-15T14:32:15Z"
}
```

### Configuración de Webhook
```http
POST /webhooks
Content-Type: application/json

{
  "url": "https://mi-sistema.com/webhooks/serviciudad",
  "events": ["payment.completed", "payment.failed"],
  "secret": "mi_secreto_webhook"
}
```

---

Esta documentación proporciona una referencia completa para integrar con las APIs de ServiCiudad Conectada, facilitando el desarrollo de aplicaciones cliente y la integración con sistemas externos.
