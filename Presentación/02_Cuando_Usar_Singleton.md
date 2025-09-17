# Diapositiva 2: Cuándo Usar Singleton

##  Casos de Uso y Framework de Decisión

---

###  Casos de Uso Válidos

| **Configuración Global** | **Pool de Conexiones** |
|:------------------------:|:----------------------:|
| Properties del sistema | Base de datos |
| URLs de APIs | Conexiones caras |
| Credenciales | Reutilización |

| **Logging Centralizado** | **Cache Manager** |
|:------------------------:|:-----------------:|
| Un solo archivo log | Memoria compartida |
| Formato consistente | Evita duplicación |
| Thread-safe writing | Performance |

---

###  Framework de Decisión

```
 ¿Necesitas exactamente UNA instancia?
 ¿Es un recurso compartido costoso?
 ¿El acceso global está justificado?
 ¿No puedes usar Dependency Injection?

 4 SÍ = Considera Singleton
 Algún NO = Busca alternativas
```

---

###  Ejemplos Específicos

####  Configuración del Sistema
```java
// Una sola configuración para toda la app
String dbUrl = ConfigManager.getInstance()
    .getProperty("database.url");
int timeout = ConfigManager.getInstance()
    .getIntProperty("timeout", 30);
```

---

####  Pool de Conexiones
```java
// Reutilizar conexiones caras
Connection conn = ConnectionPool.getInstance()
    .getConnection();
// ... usar conexión ...
ConnectionPool.getInstance()
    .releaseConnection(conn);
```

---

####  Cache Centralizado
```java
// Cache compartido para performance
User user = CacheManager.getInstance()
    .get("user:" + userId);
if (user == null) {
    user = database.loadUser(userId);
    CacheManager.getInstance()
        .put("user:" + userId, user);
}
```

---

###  Cuándo NO Usar

- ** LÓGICA DE NEGOCIO**: Servicios de dominio
- ** OBJETOS CON ESTADO**: Datos de usuario específicos  
- ** TESTING CRÍTICO**: Cuando necesitas mocks frecuentes
- ** MICROSERVICIOS**: Estado debe ser distribuido
- ** FRAMEWORKS DI**: Spring, CDI disponibles

---

### 🎯 Regla de Oro

> **"Usa Singleton solo para RECURSOS, no para LÓGICA"**

---
