# Coleccion Postman - ServiCiudad Cali API

Esta carpeta contiene la coleccion completa de Postman para probar todos los endpoints de la API ServiCiudad Cali.

## Archivos Incluidos

### Colecciones

- **ServiCiudad_API.postman_collection.json**: Coleccion completa con 26 requests organizados en 3 carpetas:
  - Facturas Acueducto (12 endpoints)
  - Consumos Energia (6 endpoints)
  - Actuator Monitoring (8 endpoints)

### Environments

- **ServiCiudad_Local.postman_environment.json**: Variables para entorno local (localhost:8080)
- **ServiCiudad_Docker.postman_environment.json**: Variables para entorno Docker (localhost:8080)

## Instalacion

### Opcion 1: Importar en Postman Desktop

1. Abrir Postman Desktop
2. Click en "Import"
3. Seleccionar "File"
4. Navegar a la carpeta `postman/` y seleccionar:
   - `ServiCiudad_API.postman_collection.json`
   - `ServiCiudad_Local.postman_environment.json`
5. Click en "Import"

### Opcion 2: Importar en Postman Web

1. Ir a https://web.postman.co/
2. Click en "Import"
3. Drag and drop los archivos JSON
4. Confirmar importacion

## Uso

### 1. Seleccionar Environment

En la esquina superior derecha de Postman, seleccionar el environment apropiado:
- **ServiCiudad Local**: Para desarrollo local
- **ServiCiudad Docker**: Para pruebas con Docker Compose

### 2. Ejecutar Requests

#### Flujo de Prueba Completo

**A. Crear Factura:**
```
POST /api/facturas
```
- El test automatico guarda el `facturaId` en variables de entorno

**B. Consultar Deuda Consolidada:**
```
GET /api/facturas/cliente/{{clienteId}}/deuda-consolidada
```
- Retorna deuda total (acueducto + energia)
- Incluye estadisticas y alertas

**C. Registrar Pago:**
```
POST /api/facturas/{{facturaId}}/pagar
```
- Cambia estado a PAGADA
- Test verifica que `pagada = true`

**D. Verificar Health:**
```
GET /actuator/health
```
- Test verifica que `status = UP`

### 3. Variables Disponibles

Las siguientes variables estan configuradas en los environments:

| Variable | Valor Default | Descripcion |
|----------|--------------|-------------|
| `baseUrl` | `http://localhost:8080` | URL base de la API |
| `clienteId` | `0001234567` | ID del cliente para pruebas |
| `facturaId` | `1` | ID de factura (se actualiza automaticamente) |
| `periodo` | `202510` | Periodo en formato YYYYMM |

### 4. Tests Automatizados

Cada request incluye tests automatizados que verifican:

**Request de Creacion:**
- Status code 201
- Respuesta contiene ID generado
- ID del cliente coincide

**Request de Deuda Consolidada:**
- Status code 200
- Respuesta contiene `totalAPagar`
- Respuesta contiene objeto `estadisticas`

**Request de Health Check:**
- Status code 200
- Application status es `UP`

## Estructura de la Coleccion

```
ServiCiudad Cali API/
├── Facturas Acueducto/
│   ├── 01 - Listar Todas las Facturas
│   ├── 02 - Obtener Factura por ID
│   ├── 03 - Crear Nueva Factura
│   ├── 04 - Actualizar Factura
│   ├── 05 - Eliminar Factura
│   ├── 06 - Obtener Facturas por Cliente
│   ├── 07 - Obtener Deuda Consolidada
│   ├── 08 - Obtener Facturas por Periodo
│   ├── 09 - Obtener Facturas Vencidas
│   ├── 10 - Calcular Deuda Total Cliente
│   ├── 11 - Contar Facturas Pendientes
│   └── 12 - Registrar Pago de Factura
├── Consumos Energia/
│   ├── 01 - Listar Todos los Consumos
│   ├── 02 - Obtener Consumos por Cliente
│   ├── 03 - Obtener Consumos por Periodo
│   ├── 04 - Obtener Consumo Especifico
│   ├── 05 - Validar Archivo
│   └── 06 - Contar Registros
└── Actuator - Monitoring/
    ├── 01 - Health Check
    ├── 02 - Liveness Probe
    ├── 03 - Readiness Probe
    ├── 04 - Application Info
    ├── 05 - Metrics List
    ├── 06 - JVM Memory Used
    ├── 07 - HTTP Server Requests
    └── 08 - Prometheus Metrics
```

## Ejecutar Collection Runner

### Desde Postman Desktop

1. Click derecho en la coleccion "ServiCiudad Cali API"
2. Seleccionar "Run collection"
3. Seleccionar el environment (Local o Docker)
4. Configurar:
   - Iterations: 1
   - Delay: 500ms (opcional)
5. Click "Run ServiCiudad Cali API"

### Desde Postman CLI (newman)

Instalar newman:
```bash
npm install -g newman
```

Ejecutar coleccion:
```bash
newman run ServiCiudad_API.postman_collection.json \
  -e ServiCiudad_Local.postman_environment.json \
  --reporters cli,html \
  --reporter-html-export newman-report.html
```

Con reporte detallado:
```bash
newman run ServiCiudad_API.postman_collection.json \
  -e ServiCiudad_Local.postman_environment.json \
  -r cli,json,html \
  --reporter-json-export results.json \
  --reporter-html-export report.html \
  --delay-request 1000
```

## Ejemplos de Respuestas

### Deuda Consolidada

```json
{
  "clienteId": "0001234567",
  "nombreCliente": "Juan Carlos Perez",
  "fechaConsulta": "2025-10-15T16:30:45",
  "facturasAcueducto": [...],
  "consumosEnergia": [...],
  "estadisticas": {
    "totalFacturasAcueducto": 2,
    "totalConsumoAcueducto": 33,
    "deudaAcumuladaAcueducto": 203000.00,
    "promedioConsumoAcueducto": 16.5,
    "totalConsumoEnergia": 2900,
    "deudaAcumuladaEnergia": 348000.50
  },
  "alertas": [
    "VENCIMIENTO_PROXIMO: Tiene 1 factura(s) proximas a vencer"
  ],
  "totalAPagar": 551000.50
}
```

### Health Check

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 215989518336,
        "threshold": 10485760
      }
    }
  }
}
```

## Troubleshooting

### Error: "Could not send request"

**Causa**: La aplicacion no esta corriendo

**Solucion**:
```bash
# Opcion 1: Maven
mvn spring-boot:run

# Opcion 2: Docker Compose
docker-compose up -d

# Verificar
curl http://localhost:8080/actuator/health
```

### Error: "404 Not Found"

**Causa**: Endpoint incorrecto o API en puerto diferente

**Solucion**:
1. Verificar que `baseUrl` en environment este correcto
2. Verificar que la aplicacion este corriendo en el puerto configurado
3. Verificar logs: `docker-compose logs app`

### Tests Fallan

**Causa**: Datos de prueba no existen o respuesta inesperada

**Solucion**:
1. Ejecutar requests en orden: Crear → Consultar → Actualizar → Eliminar
2. Verificar que el `clienteId` exista en la base de datos
3. Revisar response body en la consola de Postman

## Integracion con CI/CD

### Ejecutar en GitHub Actions

Agregar step al workflow `.github/workflows/ci-cd.yml`:

```yaml
- name: Run Postman tests
  run: |
    npm install -g newman
    newman run postman/ServiCiudad_API.postman_collection.json \
      -e postman/ServiCiudad_Docker.postman_environment.json \
      --reporters cli,junit \
      --reporter-junit-export results.xml
```

### Ejecutar en Jenkins

```groovy
stage('API Tests') {
    steps {
        sh '''
            npm install -g newman
            newman run postman/ServiCiudad_API.postman_collection.json \
              -e postman/ServiCiudad_Local.postman_environment.json
        '''
    }
}
```

## Recursos Adicionales

- [Documentacion Postman](https://learning.postman.com/docs/)
- [Newman CLI](https://github.com/postmanlabs/newman)
- [Swagger UI](http://localhost:8080/swagger-ui.html) - Documentacion interactiva de la API

## Soporte

Para reportar problemas o sugerencias:
- Repositorio: https://github.com/LeonarDPeace/Ingenieria-Software-2
- Equipo: Eduard Criollo, Felipe Charria, Jhonathan Chicaiza, Emmanuel Mena, Juan Sebastian Castillo
