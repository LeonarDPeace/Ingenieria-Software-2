# Diapositiva 5: Synchronized Method

## 🔒 Seguridad en Múltiples Hilos

---

### 🔐 Synchronized Method

| **Características** |
|:-------------------:|
| 🔒 Thread-safe garantizado |
| ✅ Seguro para múltiples hilos |
| ✅ Implementación simple |
| ❌ **IMPACTO EN RENDIMIENTO** |
| ⚠️ Sincronización en CADA llamada |

---

### 💻 Código Ejemplo

```java
public class LogManager {
    private static LogManager instance;
    
    private LogManager() {
        // Constructor privado
        initializeLogger();
    }
    
    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }
}
```

---

### 🔄 Flujo con Sincronización

```
Thread 1: [LOCK] → getInstance() → crear/retornar → [UNLOCK]
Thread 2:  [WAIT] ..................... [LOCK] → getInstance() → [UNLOCK]
Thread 3:         [WAIT] .................................. [LOCK] → [UNLOCK]
```

**Solo UN thread a la vez puede ejecutar getInstance()**

---

### ✅ Ventajas

- **🔒 THREAD-SAFE**: Garantiza una sola instancia
- **🎯 SIMPLE**: Solo agregar `synchronized`
- **🛡️ CONFIABLE**: Sin race conditions
- **⏱️ LAZY**: Crea solo cuando necesita

---

### ❌ Desventajas

- **⚡ OVERHEAD**: Sincronización costosa
- **🚫 BLOQUEO**: Un hilo a la vez
- **📈 ESCALABILIDAD**: Problema con muchos hilos
- **🔄 INNECESARIO**: Solo primera llamada necesita sync

---

### 📊 Impacto en Rendimiento

```
Primera llamada:    SINCRONIZACIÓN + CREACIÓN     (necesaria)
Siguientes llamadas: SINCRONIZACIÓN sin CREACIÓN  (innecesaria)
```

**Problema**: El 99% de las llamadas tienen overhead innecesario

---

### 🎯 Cuándo Usar

- ✅ Aplicaciones **MULTI-THREAD** simples
- ✅ Frecuencia de acceso **BAJA**
- ✅ Rendimiento **NO** crítico
- ⚠️ **EVITAR** en sistemas de alto tráfico

---

### 📈 Ejemplo de Overhead

```java
// Sistema con 1000 requests/segundo
// Sin sync: getInstance() = 1 microsegundo
// Con sync: getInstance() = 50 microsegundos
// Overhead: 49,000 microsegundos/segundo = 5% CPU desperdiciada
```

---
