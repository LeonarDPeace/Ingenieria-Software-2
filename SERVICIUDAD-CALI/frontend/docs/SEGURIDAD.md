# 🔐 Sistema de Seguridad - ServiCiudad Cali

## 📋 Descripción General

Se ha implementado un **sistema de autenticación** como capa de seguridad inicial para acceder a la aplicación ServiCiudad Cali. Ahora los usuarios deben iniciar sesión antes de poder consultar información de servicios públicos.

---

## 🚀 Cambios Implementados

### 1. **Página de Login (login.html)**
- ✅ Nueva página de inicio de sesión como página principal
- ✅ Diseño moderno con gradiente y animaciones
- ✅ Validación de credenciales contra el API de Docker
- ✅ Verificación de conexión con el servidor en tiempo real
- ✅ Credenciales de prueba visibles para desarrollo

### 2. **Sistema de Autenticación (js/auth.js)**
- ✅ Gestión de sesiones con `sessionStorage`
- ✅ Verificación automática de autenticación
- ✅ Redirección automática según el estado de sesión
- ✅ Auto-logout después de 30 minutos de inactividad
- ✅ Función de cerrar sesión

### 3. **Página Principal Protegida (index.html)**
- ✅ Verificación de sesión al cargar
- ✅ Botón de "Cerrar Sesión" en la esquina superior derecha
- ✅ Redirección automática a login si no está autenticado

### 4. **Correcciones de Bugs (js/app.js)**
- ✅ Corregidos valores "undefined" en consumos
- ✅ Corregidos valores "NaN" en totales
- ✅ Mejor manejo de propiedades opcionales del API
- ✅ Formateo consistente de números y monedas

---

## 🔑 Credenciales de Acceso

### Producción
```
Usuario: serviciudad
Contraseña: dev2025
```

### Configuración
Las credenciales se validan contra el API de Spring Boot con HTTP Basic Auth:
- **Endpoint**: `http://localhost:8080/actuator/health`
- **Método**: HTTP Basic Authentication

---

## 🔄 Flujo de Autenticación

```
1. Usuario accede a la aplicación
   ↓
2. Se carga login.html
   ↓
3. Verificar si ya está autenticado
   ├─ Sí → Redirigir a index.html
   └─ No → Mostrar formulario de login
   ↓
4. Usuario ingresa credenciales
   ↓
5. Validar contra API (HTTP Basic Auth)
   ├─ Correctas → Guardar sesión → index.html
   └─ Incorrectas → Mostrar error
   ↓
6. Usuario usa la aplicación
   ├─ Timer de inactividad activo (30 min)
   └─ Puede cerrar sesión manualmente
   ↓
7. Fin de sesión
   ├─ Manual: Click en "Cerrar Sesión"
   └─ Automático: 30 min inactividad
   ↓
8. Limpiar sessionStorage → login.html
```

---

## 📁 Archivos del Sistema de Seguridad

```
frontend/
├── login.html           # Página de inicio de sesión (PUNTO DE ENTRADA)
├── index.html           # Página principal (PROTEGIDA)
├── js/
│   ├── auth.js          # Sistema de autenticación
│   └── app.js           # Lógica principal (con correcciones)
└── css/
    └── styles.css       # Estilos (incluye estilos de login)
```

---

## 🛡️ Características de Seguridad

### ✅ Implementadas
1. **Autenticación HTTP Basic**: Credenciales validadas contra el API
2. **Gestión de Sesiones**: Uso de `sessionStorage` para tracking
3. **Auto-logout por Inactividad**: 30 minutos sin actividad
4. **Protección de Rutas**: Redirección automática según estado
5. **Verificación de Servidor**: Comprobación de disponibilidad del API

### 🔮 Mejoras Futuras (Opcional)
- [ ] Implementar JWT tokens
- [ ] Agregar "Recordarme" con localStorage
- [ ] Implementar recuperación de contraseña
- [ ] Agregar registro de usuarios
- [ ] Implementar roles y permisos
- [ ] Agregar autenticación de dos factores (2FA)
- [ ] Log de auditoría de accesos

---

## 🚀 Cómo Usar

### 1. Iniciar el Servidor
```bash
cd ..  # Ir al directorio raíz de SERVICIUDAD-CALI
docker-compose up -d
```

### 2. Acceder a la Aplicación
- **URL**: Abrir `login.html` en el navegador
- **Primera Vista**: Página de login

### 3. Iniciar Sesión
- Usuario: `serviciudad`
- Contraseña: `dev2025`
- Click en "🔐 Iniciar Sesión"

### 4. Usar la Aplicación
- Seleccionar un cliente de prueba o ingresar un ID
- Consultar deuda consolidada
- Ver facturas y consumos

### 5. Cerrar Sesión
- Click en "🔓 Cerrar Sesión" (esquina superior derecha)
- O esperar 30 minutos de inactividad

---

## 📊 Estado de Sesión

### Datos Almacenados en sessionStorage:
```javascript
{
  isAuthenticated: "true",
  username: "serviciudad",
  loginTime: "2025-10-22T21:44:00.000Z"
}
```

### Verificación de Estado:
```javascript
// En cualquier página
const estaAutenticado = sessionStorage.getItem('isAuthenticated');
console.log(estaAutenticado ? '✅ Autenticado' : '❌ No autenticado');
```

---

## 🎯 Testing

### Casos de Prueba:

1. **✅ Login Exitoso**
   - Ingresar credenciales correctas
   - Verificar redirección a index.html
   - Verificar datos en sessionStorage

2. **✅ Login Fallido**
   - Ingresar credenciales incorrectas
   - Verificar mensaje de error
   - Verificar que permanece en login.html

3. **✅ Protección de Rutas**
   - Intentar acceder directamente a index.html sin login
   - Verificar redirección a login.html

4. **✅ Cerrar Sesión**
   - Hacer login
   - Click en "Cerrar Sesión"
   - Verificar redirección a login.html
   - Verificar limpieza de sessionStorage

5. **✅ Auto-logout**
   - Hacer login
   - Esperar 30 minutos sin actividad
   - Verificar auto-logout y redirección

6. **✅ Sesión Persistente**
   - Hacer login
   - Recargar página (F5)
   - Verificar que permanece autenticado

---

## 🔧 Configuración Avanzada

### Cambiar Timeout de Inactividad
En `js/auth.js`, línea ~130:
```javascript
// Cambiar de 30 minutos a 60 minutos
inactivityTimer = setTimeout(() => {
    // ...
}, 60 * 60 * 1000); // 60 minutos
```

### Cambiar Credenciales
En `js/auth.js`, línea 4-9:
```javascript
const API_CONFIG = {
    baseUrl: 'http://localhost:8080',
    username: 'tu_usuario',  // ← Cambiar aquí
    password: 'tu_password', // ← Cambiar aquí
    timeout: 5000
};
```

**⚠️ IMPORTANTE**: También actualizar en el servidor Spring Boot.

---

## 📞 Soporte

Para problemas o preguntas:
- 📧 **Proyecto**: Ingeniería de Software 2
- 🏫 **Universidad**: Autónoma de Occidente
- 📅 **Fecha**: Octubre 2025
- 💻 **GitHub**: [Ver Código](https://github.com/LeonarDPeace/Ingenieria-Software-2)

---

## ✅ Checklist de Verificación

- [x] ✅ Página de login creada y funcional
- [x] ✅ Sistema de autenticación implementado
- [x] ✅ Protección de rutas configurada
- [x] ✅ Auto-logout por inactividad
- [x] ✅ Botón de cerrar sesión visible
- [x] ✅ Valores "undefined" corregidos
- [x] ✅ Valores "NaN" corregidos
- [x] ✅ Verificación de servidor en login
- [x] ✅ Manejo de errores mejorado
- [x] ✅ Documentación completa

---

**¡Sistema de seguridad implementado exitosamente! 🎉**
