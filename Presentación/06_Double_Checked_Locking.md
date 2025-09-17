# Double Checked Locking

## Optimización Avanzada de Rendimiento

---

### Double Checked Locking

| **Características** |
|:-------------------:|
| Optimización de rendimiento |
| Thread-safe y eficiente |
| Reduce overhead de sincronización |
| Implementación compleja |
| **Requiere keyword volatile** |

---

### Ejemplo

```java
public class CacheManager {
    private static volatile CacheManager instance;
    
    private CacheManager() {
        // Constructor privado
        initializeCache();
    }
    
    public static CacheManager getInstance() {
        if (instance == null) {                    // Primera verificación
            synchronized (CacheManager.class) {    // Bloqueo
                if (instance == null) {            // Segunda verificación
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }
}
```

---

### Flujo de Ejecución

```
[getInstance()] → [instance == null?]
       ↓                ↓
    ✅ false         ✅ true
       ↓                ↓
   [Retornar]       [synchronized]
                         ↓
                   [instance == null?]
                      ↓        ↓
                  ✅ true   ❌ false
                      ↓        ↓
                   [Crear]  [Retornar]
```

---

### ¿Por Qué Volatile?

```java
Sin volatile: Cambios en memoria no visibles a otros hilos
Con volatile: Garantiza visibilidad entre hilos
```

**volatile previene reordenamiento de instrucciones del compilador**

---

### Optimización Clave

- **PRIMERA VERIFICACIÓN**: Evita sincronización innecesaria
- **SINCRONIZACIÓN**: Solo cuando instance es null
- **SEGUNDA VERIFICACIÓN**: Evita múltiples creaciones

---

### ✅ Ventajas

- **EFICIENCIA**: Mínimo overhead después de creación
- **THREAD-SAFE**: Garantiza una sola instancia
- **LAZY**: Crea solo cuando necesita

### ❌ Desventajas

- **COMPLEJIDAD**: Difícil de implementar correctamente
- **VOLATILE**: Keyword requerido para funcionar
- **ERRORES**: Fácil de implementar mal

---

### Cuándo Usar

- ✅ Alto **RENDIMIENTO** requerido
- ✅ Acceso **FRECUENTE** a la instancia
- ✅ Equipos con experiencia **AVANZADA**
- ⚠️ **Solo expertos** en concurrencia

---

### ⚠️ Sin Volatile = Roto

```java
// Peligro sin volatile:
1. instance = allocate_memory();    // instance != null
2. construct_object();              // objeto aún no construido
3. // Otro thread ve instance != null pero objeto no listo ❌
```

---
