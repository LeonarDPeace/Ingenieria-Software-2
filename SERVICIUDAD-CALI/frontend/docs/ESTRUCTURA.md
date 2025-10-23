# 📁 Estructura del Proyecto Frontend

## 🏗️ Arquitectura de Carpetas

```
frontend/
├── 📄 index.html              # Página principal (protegida)
├── 📄 login.html              # Página de inicio de sesión
│
├── 📁 assets/                 # Recursos estáticos
│   ├── favicon.svg           # Icono de la aplicación
│   └── images/               # Imágenes del proyecto
│
├── 📁 css/                    # Hojas de estilo
│   └── styles.css            # Estilos principales
│
├── 📁 js/                     # Scripts JavaScript
│   ├── app.js                # Lógica principal de la aplicación
│   └── auth.js               # Sistema de autenticación
│
└── 📁 docs/                   # Documentación
    ├── ESTRUCTURA.md         # Este archivo
    ├── SEGURIDAD.md          # Documentación de seguridad
    ├── PRUEBA_RAPIDA.md      # Guía de pruebas
    ├── RESUMEN_CAMBIOS.md    # Historial de cambios
    └── INICIO_RAPIDO.md      # Guía de inicio rápido
```

---

## 📋 Descripción de Archivos

### 📄 Archivos HTML (Raíz)

#### `index.html`
- **Propósito**: Página principal de la aplicación
- **Estado**: Protegida (requiere autenticación)
- **Características**:
  - Consulta de deudas de servicios públicos
  - Visualización de facturas y consumos
  - Botón de cerrar sesión
  - Tabla de clientes de prueba

#### `login.html`
- **Propósito**: Página de inicio de sesión
- **Estado**: Punto de entrada de la aplicación
- **Características**:
  - Formulario de autenticación
  - Validación de credenciales
  - Verificación de estado del servidor
  - Diseño con gradiente moderno

---

### 📁 assets/ - Recursos Estáticos

Carpeta para almacenar todos los recursos estáticos de la aplicación:
- **Imágenes**: Logos, iconos, ilustraciones
- **Fuentes**: Tipografías personalizadas (si aplica)
- **Iconos**: favicon.svg y otros iconos
- **Multimedia**: Videos, audio (si aplica)

**Convención de nombres**:
```
assets/
├── favicon.svg
├── logo-principal.svg
├── logo-secundario.svg
└── images/
    ├── hero-banner.jpg
    ├── cliente-avatar.png
    └── icono-agua.svg
```

---

### 📁 css/ - Hojas de Estilo

#### `styles.css`
- **Propósito**: Estilos globales de la aplicación
- **Organización**:
  ```css
  /* 1. Reset y Variables CSS */
  /* 2. Estilos Globales */
  /* 3. Layout Principal */
  /* 4. Componentes */
  /* 5. Páginas Específicas */
  /* 6. Utilidades */
  /* 7. Responsive */
  ```

**Posibles expansiones**:
```
css/
├── styles.css          # Estilos principales
├── login.css           # Estilos específicos de login
├── components.css      # Componentes reutilizables
├── utilities.css       # Clases utilitarias
└── responsive.css      # Media queries
```

---

### 📁 js/ - Scripts JavaScript

#### `auth.js`
- **Propósito**: Sistema de autenticación
- **Funciones principales**:
  - `verificarSesion()` - Verifica estado de autenticación
  - `handleLogin(event)` - Maneja el proceso de login
  - `cerrarSesion()` - Cierra la sesión del usuario
  - `verificarConexionDockerLogin()` - Verifica conexión con API
  - `resetInactivityTimer()` - Gestiona auto-logout

**Dependencias**: Ninguna (Vanilla JavaScript)

#### `app.js`
- **Propósito**: Lógica principal de la aplicación
- **Funciones principales**:
  - `consultarDeuda()` - Consulta deuda de un cliente
  - `mostrarResultado(data)` - Renderiza los resultados
  - `renderEstadisticas(stats)` - Muestra estadísticas
  - `renderFacturasAcueducto(facturas)` - Muestra facturas de agua
  - `renderConsumosEnergia(consumos)` - Muestra consumos de energía
  - `formatearMoneda(valor)` - Formatea valores monetarios
  - `verificarConexionDocker()` - Verifica estado del servidor

**Dependencias**: 
- `auth.js` (para validación de sesión)

**Posibles expansiones**:
```
js/
├── app.js              # Lógica principal
├── auth.js             # Autenticación
├── config.js           # Configuración global
├── api.js              # Llamadas al API
├── utils.js            # Utilidades generales
└── components/
    ├── facturas.js     # Componente de facturas
    └── estadisticas.js # Componente de estadísticas
```

---

### 📁 docs/ - Documentación

#### `ESTRUCTURA.md`
- **Este archivo**: Documentación de la estructura del proyecto

#### `SEGURIDAD.md`
- Sistema de autenticación
- Credenciales de acceso
- Flujo de seguridad
- Características implementadas

#### `PRUEBA_RAPIDA.md`
- Checklist de verificación
- Casos de prueba
- Solución de problemas
- Clientes de prueba

#### `RESUMEN_CAMBIOS.md`
- Historial de cambios
- Bugs corregidos
- Archivos creados/modificados
- Comparación antes/después

#### `INICIO_RAPIDO.md`
- Guía de inicio
- Pasos para ejecutar la aplicación
- Comandos útiles
- Configuración básica

---

## 🔗 Referencias entre Archivos

### Flujo de Carga

```
1. Usuario abre login.html
   ↓
2. Carga css/styles.css (estilos)
   ↓
3. Carga js/auth.js (autenticación)
   ↓
4. Verifica credenciales y redirige a index.html
   ↓
5. index.html carga:
   - css/styles.css (estilos)
   - js/auth.js (verifica sesión)
   - js/app.js (lógica principal)
```

### Dependencias de CSS

```html
<!-- En login.html e index.html -->
<link rel="stylesheet" href="css/styles.css">
```

### Dependencias de JavaScript

```html
<!-- En login.html -->
<script src="js/auth.js"></script>

<!-- En index.html -->
<script src="js/auth.js"></script>
<script src="js/app.js"></script>
```

---

## 🎨 Estándares de Diseño Web Aplicados

### ✅ Separación de Responsabilidades
- **HTML**: Estructura y contenido
- **CSS**: Presentación y diseño
- **JavaScript**: Comportamiento e interactividad

### ✅ Organización por Tipo de Recurso
- `/assets/` - Recursos estáticos (imágenes, iconos)
- `/css/` - Hojas de estilo
- `/js/` - Scripts
- `/docs/` - Documentación

### ✅ Convenciones de Nombres
- **Archivos HTML**: minúsculas con guiones (`login.html`)
- **Archivos CSS**: minúsculas con guiones (`styles.css`)
- **Archivos JS**: camelCase (`auth.js`, `app.js`)
- **Documentación**: MAYÚSCULAS con guiones (`ESTRUCTURA.md`)

### ✅ Rutas Relativas
- Uso de rutas relativas para facilitar el deployment
- Compatibilidad con diferentes entornos

### ✅ Versionado de Assets
- Cache busting en CSS y JS: `styles.css?v=2.0`

---

## 🚀 Escalabilidad

### Estructura Preparada para Crecer

Si el proyecto crece, la estructura permite fácilmente:

#### 1. Modularizar CSS
```
css/
├── base/
│   ├── reset.css
│   ├── variables.css
│   └── typography.css
├── components/
│   ├── buttons.css
│   ├── forms.css
│   └── cards.css
├── layout/
│   ├── header.css
│   ├── footer.css
│   └── grid.css
└── pages/
    ├── login.css
    └── dashboard.css
```

#### 2. Modularizar JavaScript
```
js/
├── core/
│   ├── config.js
│   ├── api.js
│   └── storage.js
├── modules/
│   ├── auth/
│   │   ├── login.js
│   │   └── session.js
│   └── deuda/
│       ├── consulta.js
│       └── visualizacion.js
└── utils/
    ├── formatters.js
    └── validators.js
```

#### 3. Agregar Componentes
```
js/components/
├── Modal.js
├── Toast.js
├── Loader.js
└── DataTable.js
```

#### 4. Integrar Frameworks (Opcional)
```
js/
├── libs/
│   ├── jquery.min.js
│   ├── bootstrap.bundle.min.js
│   └── chart.js
└── vendor/
    └── external-library.js
```

---

## 📦 Build y Deployment

### Configuración Actual
- ✅ Sin proceso de build (Vanilla JavaScript)
- ✅ Deploy directo de archivos
- ✅ Compatible con cualquier servidor web estático

### Mejoras Futuras (Opcional)
```json
{
  "scripts": {
    "start": "live-server frontend/",
    "build": "npm run minify-css && npm run minify-js",
    "minify-css": "csso css/styles.css -o dist/styles.min.css",
    "minify-js": "uglifyjs js/*.js -o dist/app.min.js"
  }
}
```

---

## 🔧 Mantenimiento

### Agregar Nuevos Archivos

#### CSS:
1. Crear archivo en `/css/`
2. Vincularlo en HTML:
   ```html
   <link rel="stylesheet" href="css/nuevo-estilo.css">
   ```

#### JavaScript:
1. Crear archivo en `/js/`
2. Vincularlo en HTML (antes del `</body>`):
   ```html
   <script src="js/nuevo-script.js"></script>
   ```

#### Imágenes:
1. Guardar en `/assets/images/`
2. Referenciar en HTML:
   ```html
   <img src="assets/images/logo.png" alt="Logo">
   ```

---

## 📊 Métricas del Proyecto

### Estadísticas Actuales
```
Archivos HTML:       2
Archivos CSS:        1
Archivos JavaScript: 2
Líneas de código:    ~500 líneas (total)
Dependencias:        0 (Vanilla JS)
Tamaño total:        ~50 KB
```

### Performance
- ⚡ Carga rápida (sin frameworks pesados)
- 🎯 SEO-friendly (HTML semántico)
- 📱 Responsive (mobile-first)
- ♿ Accesible (estándares WCAG)

---

## 🎯 Mejores Prácticas Implementadas

- ✅ Separación clara de responsabilidades
- ✅ Código modular y reutilizable
- ✅ Comentarios descriptivos en el código
- ✅ Nombres de variables/funciones descriptivos
- ✅ Manejo de errores robusto
- ✅ Validación de datos en cliente y servidor
- ✅ Seguridad (autenticación, protección de rutas)
- ✅ Documentación completa

---

## 📚 Referencias

- [MDN Web Docs](https://developer.mozilla.org/)
- [W3C Standards](https://www.w3.org/standards/)
- [JavaScript Style Guide](https://standardjs.com/)
- [CSS Guidelines](https://cssguidelin.es/)

---

**Última actualización**: 22 de Octubre de 2025  
**Mantenido por**: Proyecto Ingeniería de Software 2 - UAO
