# Diapositiva 3: Eager Initialization

## ⚡ Creación Inmediata al Cargar la Clase

---

### 🏗️ Eager Initialization

| **Características** |
|:-------------------:|
| ⚡ Creación al cargar la clase |
| ✅ Thread-safe automático |
| ✅ Implementación simple |
| ❌ No es lazy (siempre se crea) |

---

### 💻 Código Ejemplo

```java
public class SystemConfigManager {
    // Instancia creada al cargar la clase
    private static final SystemConfigManager INSTANCE = 
        new SystemConfigManager();
    
    private Properties config;
    
    private SystemConfigManager() {
        // Constructor privado - CRÍTICO
        loadSystemConfiguration();
    }
    
    public static SystemConfigManager getInstance() {
        return INSTANCE; // Solo retorna referencia
    }
    
    private void loadSystemConfiguration() {
        // Carga configuración del sistema
        config = new Properties();
        config.load(getClass().getResourceAsStream("/config.properties"));
    }
}
```

---

### 🔄 Flujo de Ejecución

```
[JVM carga clase] → [Crea INSTANCE] → [getInstance()] → [Retorna INSTANCE]
     ⚡ Inmediato        💾 Una vez         ⚡ Rápido        ✅ Mismo objeto
```

---

### ✅ Ventajas

- **🔒 THREAD-SAFE**: JVM garantiza inicialización segura
- **⚡ PERFORMANCE**: getInstance() es instantáneo
- **🎯 SIMPLE**: Código muy fácil de entender
- **🛡️ ROBUSTO**: Sin race conditions posibles

---

### ❌ Desventajas

- **💾 MEMORIA**: Se crea aunque no se use
- **⏱️ STARTUP**: Puede impactar tiempo de inicio
- **❌ EXCEPCIONES**: Difícil manejo si constructor falla

---

### 🎯 Cuándo Usar

- ✅ Constructor **simple y rápido**
- ✅ **SIEMPRE** vas a usar la instancia
- ✅ Recursos **abundantes** disponibles
- ✅ Startup time **no crítico**

---

### 📊 Comparación Thread-Safety

| Método | Thread-Safe | Complejidad | Performance |
|:------:|:-----------:|:-----------:|:-----------:|
| **Eager** | ✅ Automático | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| Lazy Simple | ❌ No | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---
