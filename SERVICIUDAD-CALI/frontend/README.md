# 🏛️ ServiCiudad Cali - Frontend

## 📋 Descripción

Sistema web unificado para consulta de servicios públicos (acueducto y energía) de la ciudad de Cali. Proyecto académico desarrollado para el curso de Ingeniería de Software 2.

---

## 🚀 Inicio Rápido

### 1️⃣ Prerrequisitos
- Docker Desktop instalado y corriendo
- Navegador web moderno (Chrome, Firefox, Edge)

### 2️⃣ Iniciar el Backend
```powershell
cd ../
docker-compose up -d
```

### 3️⃣ Abrir la Aplicación
- Doble click en `login.html`
- **O** abrir desde VS Code con Live Server

### 4️⃣ Credenciales de Acceso
```
Usuario: serviciudad
Contraseña: dev2025
```

---

## 📁 Estructura del Proyecto

```
frontend/
├── 📄 index.html              # Página principal (protegida)
├── 📄 login.html              # Página de inicio de sesión (PUNTO DE ENTRADA)
│
├── 📁 assets/                 # Recursos estáticos
│   └── favicon.svg           # Icono de la aplicación
│
├── 📁 css/                    # Hojas de estilo
│   └── styles.css            # Estilos principales
│
├── 📁 js/                     # Scripts JavaScript
│   ├── app.js                # Lógica principal
│   └── auth.js               # Sistema de autenticación
│
└── 📁 docs/                   # Documentación completa
    ├── ESTRUCTURA.md         # Arquitectura del proyecto
    ├── SEGURIDAD.md          # Sistema de autenticación
    ├── PRUEBA_RAPIDA.md      # Guía de pruebas
    ├── RESUMEN_CAMBIOS.md    # Historial de cambios
    └── INICIO_RAPIDO.md      # Guía de inicio
```

---

## ✨ Características

### 🔐 Seguridad
- ✅ Sistema de autenticación con login
- ✅ Protección de rutas (sesiones)
- ✅ Auto-logout por inactividad (30 min)
- ✅ Validación de credenciales con API

### 💰 Consulta de Deudas
- ✅ Búsqueda por ID de cliente (10 dígitos)
- ✅ Visualización de facturas de acueducto
- ✅ Visualización de consumos de energía
- ✅ Estadísticas consolidadas
- ✅ Cálculo de totales a pagar

### 🎨 Interfaz
- ✅ Diseño moderno y responsive
- ✅ Animaciones suaves
- ✅ Tabla de clientes de prueba
- ✅ Indicadores de estado del servidor
- ✅ Mensajes de error amigables

---

## 🧪 Clientes de Prueba

| ID Cliente | Nombre | Consumo Agua | Consumo Energía | Estado |
|------------|--------|--------------|-----------------|--------|
| 1001234567 | Juan Pérez | 15 m³ | 150 kWh | ✅ Al día |
| 1002345678 | María López | 22 m³ | 125 kWh | ✅ Pagada |
| 1004567890 | Carlos Rodríguez | 30 m³ | 200 kWh | ❌ Mora |
| 1006789012 | Ana Martínez | 25 m³ | 165 kWh | ⚠️ Por vencer |
| 1000123456 | Roberto Gómez | 35 m³ | 143 kWh | ❌ Mora crítica |

---

## 🛠️ Tecnologías

- **HTML5** - Estructura semántica
- **CSS3** - Estilos y animaciones
- **JavaScript (Vanilla)** - Sin frameworks
- **Docker** - Backend containerizado
- **Spring Boot** - API REST (backend)

---

## 🔧 Configuración

### API Endpoint
```javascript
// js/auth.js y js/app.js
const API_CONFIG = {
    baseUrl: 'http://localhost:8080',
    username: 'serviciudad',
    password: 'dev2025',
    timeout: 10000
};
```

### Cambiar Credenciales
Editar en `js/auth.js`:
```javascript
const API_CONFIG = {
    username: 'tu_usuario',
    password: 'tu_password'
};
```
**⚠️ También actualizar en el backend Spring Boot**

---

## 🎯 Flujo de Usuario

```
┌─────────────────────────────────┐
│ 1. Abrir login.html             │
├─────────────────────────────────┤
│ 2. Ingresar credenciales        │
│    - Usuario: serviciudad       │
│    - Contraseña: dev2025        │
├─────────────────────────────────┤
│ 3. Sistema valida con API       │
│    - HTTP Basic Auth            │
├─────────────────────────────────┤
│ 4. Redirigir a index.html       │
│    - Sesión guardada            │
├─────────────────────────────────┤
│ 5. Consultar cliente            │
│    - Ingresar ID (10 dígitos)   │
│    - Ver facturas y consumos    │
├─────────────────────────────────┤
│ 6. Cerrar sesión o auto-logout  │
│    - Volver a login.html        │
└─────────────────────────────────┘
```

---
