# 🔄 CAMBIOS DE ESTRUCTURA DEL PROYECTO

## 📋 RESUMEN EJECUTIVO

Este documento describe los cambios en la estructura del proyecto **SERVICIUDAD-CALI** para alinearlo con la nomenclatura correcta proporcionada por el usuario.

---

## 🏗️ ESTRUCTURA CORRECTA DEL PROYECTO

```
SERVICIUDAD-CALI/
├── data/
│   └── consumos_energia.txt
├── postman/
│   └── ServiCiudad_API.postman_collection.json
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── serviciudad/
│       │           ├── adapter/
│       │           │   └── AdaptadorArchivoEnergia.java
│       │           ├── controller/
│       │           │   └── DeudaConsolidadaController.java
│       │           ├── domain/
│       │           │   ├── FacturaAcueducto.java
│       │           │   └── FacturaEnergia.java
│       │           ├── dto/
│       │           │   ├── DetalleServicioDTO.java
│       │           │   └── DeudaConsolidadaDTO.java
│       │           ├── repository/
│       │           │   └── FacturaAcueductoRepository.java
│       │           ├── service/
│       │           │   ├── ClienteService.java
│       │           │   ├── DeudaConsolidadaBuilder.java
│       │           │   └── DeudaConsolidadaService.java
│       │           └── DeudaConsolidadaApplication.java
│       └── resources/
│           ├── application.yml
│           ├── data.sql
│           └── schema.sql
├── target/
├── .gitignore
├── INFORME.md
├── pom.xml
└── README.md
```

---

## 🔄 TABLA DE CAMBIOS CRÍTICOS

### 1. **Cambios de Nomenclatura**

| **Antes** | **Ahora** | **Razón** |
|-----------|-----------|-----------|
| `ArchivoEnergiaAdapter.java` | `AdaptadorArchivoEnergia.java` | Consistencia en español |
| `ServiCiudadApplication.java` | `DeudaConsolidadaApplication.java` | Nombre específico del módulo |
| Carpeta `entity/` | Carpeta `domain/` | Alineación con DDD |
| Carpeta `builder/` separada | `DeudaConsolidadaBuilder.java` en `service/` | Builder es lógica de negocio |

### 2. **Cambios de Ubicación**

| **Archivo/Carpeta** | **Ubicación Anterior** | **Ubicación Nueva** | **Impacto** |
|---------------------|------------------------|---------------------|-------------|
| `consumos_energia.txt` | `src/main/resources/` | `data/` (raíz) | Separar data externa del classpath |
| `INFORME.md` | `docs/` | Raíz del proyecto | Mayor visibilidad |
| `postman/` | Dentro de `docs/` | Raíz del proyecto | Acceso directo para testing |
| `DeudaConsolidadaBuilder.java` | `builder/` | `service/` | Es lógica de construcción de objetos de negocio |

### 3. **Cambios de Imports en Código Java**

#### **Todos los archivos deben cambiar:**

```java
// ❌ ANTES (INCORRECTO)
import com.serviciudad.entity.FacturaAcueducto;
import com.serviciudad.entity.FacturaEnergia;
import com.serviciudad.adapter.ArchivoEnergiaAdapter;
import com.serviciudad.builder.DeudaConsolidadaBuilder;

// ✅ AHORA (CORRECTO)
import com.serviciudad.domain.FacturaAcueducto;
import com.serviciudad.domain.FacturaEnergia;
import com.serviciudad.adapter.AdaptadorArchivoEnergia;
import com.serviciudad.service.DeudaConsolidadaBuilder;
```

---

## 📦 ACTUALIZACIÓN POR PAQUETE

### **Paquete `adapter/`**
- **Clase:** `ArchivoEnergiaAdapter.java` → **`AdaptadorArchivoEnergia.java`**
- **Port Interface:** `ServicioEnergiaPort.java` (sin cambios)
- **Excepciones:** Sin cambios

**Actualizar en:**
- Fase 3: Implementación del Adapter Pattern
- Fase 6: Tests unitarios (`AdaptadorArchivoEnergiaTest.java`)

---

### **Paquete `domain/`** (antes `entity/`)
- **Clase:** `FacturaAcueducto.java` (sin cambios de nombre)
- **Clase:** `FacturaEnergia.java` (sin cambios de nombre)
- **Cambio:** Package declaration debe ser `package com.serviciudad.domain;`

**Actualizar imports en:**
- `FacturaAcueductoRepository.java`
- `DeudaConsolidadaService.java`
- `DeudaConsolidadaDTOMapper.java`
- Todos los tests que usen estas entidades

---

### **Paquete `service/`**
- **Nueva clase:** `DeudaConsolidadaBuilder.java` (movida desde `builder/`)
- **Clase:** `ClienteService.java` (nueva)
- **Clase:** `DeudaConsolidadaService.java` (sin cambios)

**Actualizar imports en:**
- `DeudaConsolidadaService.java` debe importar `DeudaConsolidadaBuilder` desde `service`
- Fase 3: Sección del Builder Pattern

---

### **Archivo `application.yml`**
Debe referenciar la ruta correcta del archivo de datos:

```yaml
# ❌ ANTES
energia:
  archivo:
    path: classpath:consumos_energia.txt

# ✅ AHORA
energia:
  archivo:
    path: ./data/consumos_energia.txt
```

---

### **Clase Main: `DeudaConsolidadaApplication.java`**
```java
package com.serviciudad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeudaConsolidadaApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeudaConsolidadaApplication.java, args);
    }
}
```

---

## 🛠️ CHECKLIST DE ACTUALIZACIÓN DE FASES

### **Fase 1: Estructura del Proyecto** ✅
- [x] Actualizar árbol de carpetas con `domain/` en lugar de `entity/`
- [x] Mover `data/` a raíz
- [x] Mover `postman/` a raíz
- [x] Eliminar referencias a `docker/` y `scripts/` (se ven en fases posteriores)
- [x] Actualizar `DeudaConsolidadaApplication` como clase main

### **Fase 2: Configuración Spring Boot**
- [ ] Actualizar `application.yml` con path correcto: `./data/consumos_energia.txt`
- [ ] Cambiar `@EntityScan` para usar `com.serviciudad.domain` en lugar de `.entity`
- [ ] Actualizar nombres en `schema.sql` y `data.sql` (sin cambios, solo verificar)

### **Fase 3: Implementación Patrones**
- [ ] **Adapter Pattern:**
  - Renombrar `ArchivoEnergiaAdapter` → `AdaptadorArchivoEnergia`
  - Actualizar imports en `DeudaConsolidadaService`
  
- [ ] **Repository Pattern:**
  - Cambiar import `entity` → `domain` en `FacturaAcueductoRepository`
  
- [ ] **Builder Pattern:**
  - Mover `DeudaConsolidadaBuilder` de paquete `builder/` a `service/`
  - Actualizar ejemplo de código para mostrar `service.DeudaConsolidadaBuilder`
  
- [ ] **DTO Pattern:**
  - Sin cambios (DTOs permanecen en `dto/`)
  
- [ ] **IoC/DI Pattern:**
  - Actualizar ejemplos con imports correctos

### **Fase 4: Docker**
- [ ] En `Dockerfile`, usar path correcto: `COPY data/consumos_energia.txt /app/data/`
- [ ] En `docker-compose.yml`, mapear volumen: `./data:/app/data`

### **Fase 5: Documentación**
- [ ] Actualizar diagramas de arquitectura con nombres correctos de paquetes
- [ ] Cambiar referencias de clase en ejemplos de código
- [ ] INFORME.md debe estar en raíz, no en `docs/`

### **Fase 6: Testing**
- [ ] Renombrar test: `ArchivoEnergiaAdapterTest` → `AdaptadorArchivoEnergiaTest`
- [ ] Actualizar imports en todos los tests: `entity` → `domain`
- [ ] Tests de integración deben usar path: `data/consumos_energia_test.txt`

---

## 🔍 COMANDO DE BÚSQUEDA Y REEMPLAZO

### **Para actualizar todos los archivos de las fases (Usar búsqueda en VS Code):**

```regex
# Buscar referencias a paquete entity
Buscar: com\.serviciudad\.entity
Reemplazar: com.serviciudad.domain

# Buscar clase del adapter
Buscar: ArchivoEnergiaAdapter
Reemplazar: AdaptadorArchivoEnergia

# Buscar builder en paquete incorrecto
Buscar: com\.serviciudad\.builder\.DeudaConsolidadaBuilder
Reemplazar: com.serviciudad.service.DeudaConsolidadaBuilder

# Buscar referencias al archivo de datos
Buscar: classpath:consumos_energia\.txt
Reemplazar: ./data/consumos_energia.txt

# Buscar Main class incorrecta
Buscar: ServiCiudadApplication
Reemplazar: DeudaConsolidadaApplication
```

---

## 🎯 RESUMEN DE CAMBIOS POR FASE

| Fase | Archivos a Actualizar | Prioridad |
|------|----------------------|-----------|
| **Fase 1** | Estructura de carpetas, árbol del proyecto | 🔴 ALTA |
| **Fase 2** | `application.yml`, configuraciones Spring | 🔴 ALTA |
| **Fase 3** | Código Java de patrones (5 clases principales) | 🔴 ALTA |
| **Fase 4** | Dockerfile, docker-compose.yml | 🟡 MEDIA |
| **Fase 5** | INFORME.md, diagramas, ejemplos | 🟡 MEDIA |
| **Fase 6** | Tests unitarios e integración | 🟢 BAJA |

---

## ✅ VALIDACIÓN FINAL

Después de aplicar todos los cambios, verificar:

1. ✅ **Estructura de carpetas** coincide con la proporcionada
2. ✅ **Nombres de clases** en español consistente
3. ✅ **Package declarations** correctos en todos los archivos `.java`
4. ✅ **Imports** actualizados en todos los archivos
5. ✅ **Paths de archivos** apuntando a `./data/` en configuraciones
6. ✅ **Tests** con nombres actualizados y paths correctos
7. ✅ **Documentación** en ubicaciones correctas (raíz vs. subcarpetas)

---

## 📞 SIGUIENTE PASO

**Aplicar estos cambios manualmente a las 6 fases** o **ejecutar script de búsqueda/reemplazo** en VS Code sobre los archivos:

```
PlanDesarrolloEntregable_Fase1.md
PlanDesarrolloEntregable_Fase2.md
PlanDesarrolloEntregable_Fase3.md
PlanDesarrolloEntregable_Fase4.md
PlanDesarrolloEntregable_Fase5.md
PlanDesarrolloEntregable_Fase6.md
```

---

*Documento generado: Octubre 10, 2025*  
*Autor: GitHub Copilot*  
*Propósito: Guía de actualización de nomenclatura y estructura del proyecto SERVICIUDAD-CALI*
