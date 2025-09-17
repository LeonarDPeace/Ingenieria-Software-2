# Diapositiva 12: Best Practices y Recomendaciones

## 📋 Mejores Prácticas para Implementar Singleton

---

### 📋 Best Practices y Recomendaciones

| **Guía Completa** |
|:-----------------:|
| 📋 Guía completa de implementación |
| ✅ Qué hacer y qué no hacer |
| 🔧 Técnicas avanzadas |
| 🎯 Casos de uso apropiados |
| ⚠️ Errores comunes a evitar |

---

### ✅ Best Practices - QUÉ HACER

#### **1. USAR ENUM SINGLETON** (primera opción)
```java
public enum ConfigManager {
    INSTANCE;
    public void loadConfig() { ... }
}
```

#### **2. BILL PUGH PATTERN** (segunda opción)
```java
public class Logger {
    private static class LoggerHolder {
        private static final Logger INSTANCE = new Logger();
    }
    public static Logger getInstance() {
        return LoggerHolder.INSTANCE;
    }
}
```

---

#### **3. CONSTRUCTOR PRIVADO** siempre
```java
private DatabasePool() {
    // Previene instanciación externa
}
```

#### **4. CONSIDERAR SERIALIZATION**
```java
private Object readResolve() {
    return getInstance(); // Mantiene singleton tras deserialización
}
```

---

### ❌ Errores Comunes - QUÉ NO HACER

#### **1. NO usar Lazy simple** en multi-thread
```java
// ❌ INCORRECTO
public static Singleton getInstance() {
    if (instance == null) {        // Race condition
        instance = new Singleton();
    }
    return instance;
}
```

---

#### **2. NO olvidar protección contra reflexión**
```java
// ❌ VULNERABLE
Constructor<Singleton> constructor = 
    Singleton.class.getDeclaredConstructor();
constructor.setAccessible(true);
Singleton instance2 = constructor.newInstance(); // ❌ Segunda instancia
```

---

#### **3. NO usar herencia** con Singleton
```java
// ❌ PROBLEMÁTICO
public class ExtendedSingleton extends Singleton {
    // Rompe el patrón
}
```

#### **4. NO implementar Cloneable**
```java
// ❌ PELIGROSO
public class Singleton implements Cloneable {
    public Object clone() { ... } // Permite múltiples instancias
}
```

---

### 🔧 Técnicas Avanzadas

#### **🛡️ PROTECTION AGAINST REFLECTION**
```java
private Singleton() {
    if (instance != null) {
        throw new RuntimeException("Use getInstance() method");
    }
}
```

#### **🔄 GRACEFUL SHUTDOWN**
```java
public void shutdown() {
    // Limpieza de recursos
    if (connection != null) {
        connection.close();
    }
}
```

---

### 🧪 Testing Strategies

#### **🔌 DEPENDENCY INJECTION para testing**
```java
public class Service {
    private final Logger logger;
    
    public Service(Logger logger) { // ✅ Testeable
        this.logger = logger;
    }
}
```

#### **🔄 RESET method para tests** (solo en tests)
```java
@VisibleForTesting
static void resetInstance() {
    instance = null; // Solo para tests
}
```

---

### 🎯 Cuándo Elegir Cada Implementación

| **Implementación** | **Cuándo Usar** |
|:------------------:|:---------------:|
| 🏆 **ENUM** | Máxima robustez, protección total |
| 🏆 **BILL PUGH** | Alto rendimiento, thread-safe |
| 🏆 **EAGER** | Simple, siempre se usa la instancia |
| 🏆 **SYNCHRONIZED** | Multi-thread básico, bajo acceso |

---

### 🏗️ Arquitectura Moderna

```java
// ✅ RECOMENDADO: Combinar Singleton + DI
@Component
@Scope("singleton")
public class ModernService {
    // Spring gestiona el ciclo de vida
    // Testeable y mantenible
}
```

**Mejor de ambos mundos: Control de lifecycle + Testabilidad**

---

### 🎯 Reglas de Oro

1. **Enum first** para casos simples
2. **Bill Pugh** para performance crítico  
3. **DI frameworks** cuando sea posible
4. **Nunca lazy simple** en multi-thread
5. **Siempre considera testing**

---
