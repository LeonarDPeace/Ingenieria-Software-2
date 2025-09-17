# Diapositiva 10: Problemas y Alternativas

## ⚠️ Críticas al Singleton y Soluciones Modernas

---

### ⚠️ Problemas y Alternativas

| **Problemas Identificados** |
|:---------------------------:|
| ⚠️ **ANTIPATTERN** en algunos contextos |
| ❌ Problemas de testing y acoplamiento |
| ❌ Violación de principios SOLID |
| ✅ Alternativas modernas disponibles |
| ✅ Dependency Injection como solución |

---

### ❌ Principales Problemas

#### **1. TESTABILIDAD**
```java
// Difícil de testear
public class OrderService {
    public void processOrder() {
        // ❌ Hard-coded dependency
        DatabaseConnection db = DatabaseConnection.getInstance(); 
        // Imposible usar mock en tests
    }
}
```

---

#### **2. ACOPLAMIENTO FUERTE**
```java
// Clases fuertemente acopladas al Singleton
public class UserService {
    public void saveUser() {
        // ❌ Dependencia oculta
        Logger.getInstance().log("Saving user"); 
    }
}
```

---

#### **3. ESTADO GLOBAL**
```java
// Estado global compartido = problemas de concurrencia
ConfigManager.getInstance().setValue("timeout", 30);
// Cambios afectan toda la aplicación
```

---

### 🚫 Violación Principios SOLID

- **Single Responsibility**: Maneja creación + lógica de negocio
- **Open/Closed**: Difícil extender sin modificar
- **Dependency Inversion**: Clases dependen de implementación concreta

---

### ✅ Alternativas Modernas

#### **1. DEPENDENCY INJECTION**
```java
@Service
public class OrderService {
    private final DatabaseConnection dbConnection;
    
    // ✅ Inyección en constructor
    public OrderService(DatabaseConnection dbConnection) { 
        this.dbConnection = dbConnection;
    }
}
```

---

#### **2. SPRING FRAMEWORK**
```java
@Component
@Scope("singleton")  // ✅ Spring maneja el ciclo de vida
public class ConfigurationService {
    // Spring garantiza una sola instancia
}
```

---

#### **3. FACTORY PATTERN + DI**
```java
@Configuration
public class AppConfig {
    @Bean
    @Singleton
    public DatabaseConnection databaseConnection() {
        return new DatabaseConnection();
    }
}
```

---

### ✅ Cuándo SÍ Usar Singleton

- ✅ Control de **hardware** (impresoras, dispositivos)
- ✅ **Cache global** de aplicación  
- ✅ **Configuración inmutable**
- ✅ **Logging** simple

### ❌ Cuándo NO Usar Singleton

- ❌ **Lógica de negocio** compleja
- ❌ Necesitas **múltiples configuraciones**
- ❌ Aplicación usa **DI container**
- ❌ **Testing** es prioritario

---

### 🔄 Migración a DI

```java
// Antes (Singleton)
public class EmailService {
    private static EmailService instance;
    public static EmailService getInstance() { ... }
}

// Después (DI)
@Component
public class EmailService {
    // Spring maneja la instancia única
}
```

---

### 🎯 Estrategia Moderna

```
Legacy Systems → Singleton para recursos específicos
Modern Apps → Dependency Injection por defecto
Hybrid → DI para lógica + Singleton para infraestructura
```

---
