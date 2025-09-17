# Cuándo Usar Singleton

## Casos de Uso y Framework de Decisión

---

### Casos de Uso Válidos

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

### Framework de Decisión

```
¿Necesitas exactamente UNA instancia?
¿Es un recurso compartido costoso?
¿El acceso global está justificado?
¿No puedes usar Dependency Injection?

 4 SÍ = Considera Singleton
 Algún NO = Busca alternativas
```

---

###  Cuándo NO Usar

- ** LÓGICA DE NEGOCIO**: Servicios de dominio
- ** OBJETOS CON ESTADO**: Datos de usuario específicos  
- ** TESTING CRÍTICO**: Cuando necesitas mocks frecuentes
- ** MICROSERVICIOS**: Estado debe ser distribuido
- ** FRAMEWORKS DI**: Spring, CDI disponibles

---

### Regla de Oro

> **"Usa Singleton solo para RECURSOS, no para LÓGICA"**

---
