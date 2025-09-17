# Diapositiva 4: Lazy Initialization

## ⏱️ Creación Solo Cuando Se Necesita

---

### 🔄 Lazy Initialization

| **Características** |
|:-------------------:|
| ⏱️ Creación bajo demanda |
| ✅ Ahorra memoria y recursos |
| ✅ Implementación simple |
| ❌ **NO THREAD-SAFE** |
| ⚠️ Solo aplicaciones single-thread |

---

### 💻 Código Ejemplo

```java
public class DatabaseConnectionPool {
    private static DatabaseConnectionPool instance;
    
    private DatabaseConnectionPool() {
        // Constructor costoso
        initializeConnections();
    }
    
    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {  // ⚠️ Race condition aquí
            instance = new DatabaseConnectionPool();
        }
        return instance;
    }
}
```

---

### 🔄 Flujo de Ejecución

```
[getInstance()] → [instance == null?] → [Crear instancia] → [Retornar]
     ⚡ Primera vez      ✅ true            💾 new Object      ✅ Única
     ⚡ Siguientes      ❌ false              -               ✅ Existente
```

---

### ⚠️ Problema en Multi-Thread

```java
Thread 1: instance == null? → true → crea instancia A
Thread 2: instance == null? → true → crea instancia B  ❌ PROBLEMA
```

**Race Condition**: Ambos threads pueden crear instancias separadas!

---

### ✅ Características Positivas

- **⚡ LAZY LOADING**: Crea solo cuando necesita
- **💾 EFICIENCIA**: No desperdicia recursos
- **🎯 SIMPLE**: Código fácil de entender

### ❌ Problemas Críticos

- **⚠️ RACE CONDITION**: Múltiples instancias posibles
- **🔄 IMPREDECIBLE**: Comportamiento depende del timing
- **🐛 BUGS SILENCIOSOS**: Puede funcionar en desarrollo, fallar en producción

---

### 🎯 Cuándo Usar

- ✅ Aplicaciones **SINGLE-THREAD** únicamente
- ✅ Recursos **COSTOSOS** de crear
- ✅ Posibilidad de **NO** usar la instancia
- ❌ **NUNCA** en aplicaciones multi-thread

---

### 📊 Timeline del Problema

```
Tiempo 1: Thread A evalúa (instance == null) → TRUE
Tiempo 2: Thread B evalúa (instance == null) → TRUE  
Tiempo 3: Thread A crea instancia A
Tiempo 4: Thread B crea instancia B ❌
Resultado: DOS INSTANCIAS = Patrón roto
```

---
