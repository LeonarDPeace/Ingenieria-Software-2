# Diapositiva 1: Introducción al Patrón Singleton

## 🎯 Patrón Singleton - Conceptos Fundamentales

---

### 📋 Garantías del Patrón

| **Garantiza** | **Propósito** |
|:-------------:|:-------------:|
| • Una sola instancia | • Controlar la creación de objetos |
| • Acceso global | • Proveer acceso global controlado |
| • Lazy initialization | • Lazy initialization (creación bajo demanda) |

---

### 🔍 Definición

> **Patrón creacional** que garantiza **UNA SOLA INSTANCIA** de una clase y proporciona **ACCESO GLOBAL** controlado a esa instancia.

---

### ⭐ Características Clave

- **🎯 INSTANCIA ÚNICA** - Solo un objeto de la clase
- **🌐 ACCESO GLOBAL** - Disponible desde cualquier parte del código  
- **⏱️ LAZY INITIALIZATION** - Creación bajo demanda

---

### 📚 Casos de Uso Típicos

| Configuración Global | Pool de Conexiones |
|:---:|:---:|
| 📋 Settings del sistema | 🔗 Base de datos |
| **Logging Centralizado** | **Cache Manager** |
| 📝 Un solo archivo log | 💾 Memoria compartida |

---

### 💻 Ejemplo Básico

```java
// ❌ Sin Singleton - Múltiples configuraciones
ConfigurationManager config1 = new ConfigurationManager();
ConfigurationManager config2 = new ConfigurationManager();

// ✅ Con Singleton - Una sola configuración  
ConfigurationManager config1 = ConfigurationManager.getInstance();
ConfigurationManager config2 = ConfigurationManager.getInstance();
// config1 == config2 (misma instancia)
```

---

### 🎯 Cuándo Usar

- ✅ Necesitas exactamente **UNA** instancia
- ✅ Acceso global **justificado**
- ✅ Control **centralizado** de recursos

---
