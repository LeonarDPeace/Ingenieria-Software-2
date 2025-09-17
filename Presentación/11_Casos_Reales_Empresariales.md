# Diapositiva 11: Casos Reales Empresariales

## 🏢 Implementaciones Singleton en Empresas Reales

---

### 🏢 Casos Reales Empresariales

| **Ejemplos de Empresas** |
|:------------------------:|
| 🏢 Ejemplos de empresas tecnológicas |
| 📱 Android, Java, Spring Framework |
| 🌐 Aplicaciones web y móviles |
| ⚡ Sistemas de alta disponibilidad |
| 💾 Gestión de recursos críticos |

---

### 📱 CASO 1: Android System

```java
// SystemService en Android
public class WindowManager {
    private static WindowManager sInstance;
    
    public static WindowManager getInstance() {
        if (sInstance == null) {
            sInstance = new WindowManager();
        }
        return sInstance;
    }
    
    // Gestiona todas las ventanas del sistema
}
```

**🎯 USO**: Control único del sistema de ventanas  
**✅ ÉXITO**: Garantiza consistencia en UI

---

### ☕ CASO 2: Java Runtime

```java
// Runtime class en Java
Runtime runtime = Runtime.getRuntime();
// Singleton built-in de Java
```

**🎯 USO**: Interacción con JVM y sistema operativo  
**✅ ÉXITO**: Una sola instancia para todo el runtime

---

### 🌱 CASO 3: Spring Framework

```java
@Component
@Scope("singleton")  // Patrón por defecto
public class DatabaseTransactionManager {
    // Spring garantiza una sola instancia por contexto
}
```

**🎯 USO**: Gestión centralizada de transacciones  
**✅ ÉXITO**: Coordinación global de base de datos

---

### 🎬 CASO 4: Netflix - Hystrix

```java
// Circuit Breaker pattern + Singleton
public class HystrixCircuitBreaker {
    private static final HystrixCircuitBreaker instance = 
        new HystrixCircuitBreaker();
    
    public static HystrixCircuitBreaker getInstance() {
        return instance;
    }
}
```

**🎯 USO**: Control de fallos en microservicios  
**✅ ÉXITO**: Protección global contra cascading failures

---

### 🌐 CASO 5: Google - Chrome Browser

```cpp
// Singleton en C++ para gestión de procesos
class ProcessSingleton {
private:
    static ProcessSingleton* instance_;
public:
    static ProcessSingleton* GetInstance();
    void NotifyOtherProcesses();
};
```

**🎯 USO**: Evitar múltiples instancias de Chrome  
**✅ ÉXITO**: Una sola instancia principal por usuario

---

### 💼 Casos de Uso Exitosos

#### **🔧 CONFIGURATION MANAGEMENT**
- AWS SDK Configuration
- Docker Runtime Settings  
- Kubernetes Cluster Config

#### **💾 CACHE SYSTEMS**
- Redis Connection Pool
- Memcached Client
- Application-level Cache

---

#### **📝 LOGGING SYSTEMS**
- Log4j Logger instances
- Enterprise logging frameworks
- Audit trail systems

#### **🖥️ HARDWARE CONTROL**
- Printer queue management
- Database connection pools
- File system access

---

### 📚 Lecciones Aprendidas

#### **✅ FUNCIONA BIEN** para:
- Recursos compartidos únicos
- Control de hardware/sistema  
- Configuración global inmutable

#### **❌ PROBLEMAS** en:
- Lógica de negocio compleja
- Estados mutables frecuentes
- Testing automatizado

---

### 📈 Evolución hacia DI

```
📈 2000s: Singleton muy popular
📈 2010s: Críticas y problemas identificados  
📈 2020s: DI frameworks dominantes
🔄 HOY: Singleton para casos específicos + DI para lógica
```

---

### 🎯 Takeaway Empresarial

> **Singleton sigue siendo válido para infraestructura,**  
> **pero DI domina para lógica de aplicación**

---
