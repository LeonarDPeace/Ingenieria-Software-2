# 🚀 GUÍA RÁPIDA DE ACTUALIZACIÓN - SERVICIUDAD-CALI

## ✅ YA COMPLETADO
- ✅ **CAMBIOS_ESTRUCTURA.md** creado con documentación completa de cambios
- ✅ **Fase 1** parcialmente actualizada con nueva estructura de carpetas

---

## 🔧 ACCIONES PENDIENTES USAR VS CODE

### **OPCIÓN 1: Búsqueda y Reemplazo Global (RECOMENDADO)**

**En VS Code:**
1. Presiona `Ctrl + Shift + H` (Buscar y Reemplazar en archivos)
2. En "Archivos para incluir": `**/PlanDesarrolloEntregable_Fase*.md`
3. Aplica los siguientes reemplazos **EN ORDEN**:

#### **Reemplazo 1: Package entity → domain**
```
Buscar:     com\.serviciudad\.entity
Reemplazar: com.serviciudad.domain
Usar regex: ✅ SÍ
```

#### **Reemplazo 2: Package declaration entity → domain**
```
Buscar:     package com\.serviciudad\.entity;
Reemplazar: package com.serviciudad.domain;
Usar regex: ✅ SÍ
```

#### **Reemplazo 3: Adapter class name**
```
Buscar:     ArchivoEnergiaAdapter
Reemplazar: AdaptadorArchivoEnergia
Usar regex: ❌ NO
```

#### **Reemplazo 4: Builder package**
```
Buscar:     com\.serviciudad\.builder\.DeudaConsolidadaBuilder
Reemplazar: com.serviciudad.service.DeudaConsolidadaBuilder
Usar regex: ✅ SÍ
```

#### **Reemplazo 5: Path del archivo de datos**
```
Buscar:     classpath:consumos_energia\.txt
Reemplazar: ./data/consumos_energia.txt
Usar regex: ✅ SÍ
```

#### **Reemplazo 6: Main Application class**
```
Buscar:     ServiCiudadApplication
Reemplazar: DeudaConsolidadaApplication
Usar regex: ❌ NO
```

---

### **OPCIÓN 2: Script PowerShell Automático**

Guarda este script como `actualizar-estructura.ps1` en la carpeta `PlanDeDesarrollo/`:

```powershell
# Script de actualización automática de nomenclatura
$archivos = Get-ChildItem -Path . -Filter "PlanDesarrolloEntregable_Fase*.md"

foreach ($archivo in $archivos) {
    Write-Host "Procesando: $($archivo.Name)" -ForegroundColor Cyan
    
    $contenido = Get-Content $archivo.FullName -Raw -Encoding UTF8
    
    # Reemplazo 1: entity -> domain
    $contenido = $contenido -replace 'com\.serviciudad\.entity', 'com.serviciudad.domain'
    $contenido = $contenido -replace 'package com\.serviciudad\.entity;', 'package com.serviciudad.domain;'
    
    # Reemplazo 2: Adapter class name
    $contenido = $contenido -replace 'ArchivoEnergiaAdapter', 'AdaptadorArchivoEnergia'
    
    # Reemplazo 3: Builder package
    $contenido = $contenido -replace 'com\.serviciudad\.builder\.DeudaConsolidadaBuilder', 'com.serviciudad.service.DeudaConsolidadaBuilder'
    
    # Reemplazo 4: Path archivo de datos
    $contenido = $contenido -replace 'classpath:consumos_energia\.txt', './data/consumos_energia.txt'
    $contenido = $contenido -replace 'src/main/resources/consumos_energia\.txt', './data/consumos_energia.txt'
    
    # Reemplazo 5: Main class
    $contenido = $contenido -replace 'ServiCiudadApplication', 'DeudaConsolidadaApplication'
    
    # Guardar cambios
    Set-Content -Path $archivo.FullName -Value $contenido -Encoding UTF8
    Write-Host "✅ Actualizado: $($archivo.Name)" -ForegroundColor Green
}

Write-Host "`n🎉 Actualización completada en todos los archivos!" -ForegroundColor Yellow
```

**Ejecutar:**
```powershell
cd "D:\OneDrive - Universidad Autonoma de Occidente\Universidad\7mo Semestre\Ing. de Software 2\ProyectoFinal\Ingenieria-Software-2\Entregable\PlanDeDesarrollo\"
.\actualizar-estructura.ps1
```

---

## 📋 CAMBIOS ADICIONALES MANUALES

Estos cambios requieren edición manual porque el contexto es más complejo:

### **Fase 1: Sección de Estructura**
- [ ] Verificar que el árbol de carpetas muestre `domain/` en lugar de `entity/`
- [ ] Verificar que `data/` esté en la raíz
- [ ] Verificar que `postman/` esté en la raíz
- [ ] Ya actualizado parcialmente ✅

### **Fase 2: application.yml**
```yaml
# Buscar manualmente y cambiar:
energia:
  archivo:
    path: ./data/consumos_energia.txt  # No classpath:
```

### **Fase 3: Sección Builder Pattern**
- [ ] Verificar que `DeudaConsolidadaBuilder` esté documentado como parte de `service/` package
- [ ] Actualizar ejemplo de código para mostrar ubicación correcta

### **Fase 4: Dockerfile**
```dockerfile
# Verificar que incluya:
COPY data/consumos_energia.txt /app/data/
```

### **Fase 4: docker-compose.yml**
```yaml
# Verificar volumen:
volumes:
  - ./data:/app/data
```

### **Fase 6: Nombres de Tests**
```java
// Cambiar manualmente:
ArchivoEnergiaAdapterTest → AdaptadorArchivoEnergiaTest
```

---

## ✅ CHECKLIST FINAL

Después de ejecutar el script o hacer reemplazos manuales:

- [ ] `com.serviciudad.entity` → `com.serviciudad.domain` en todos los archivos
- [ ] `ArchivoEnergiaAdapter` → `AdaptadorArchivoEnergia` en todos los archivos  
- [ ] `ServiCiudadApplication` → `DeudaConsolidadaApplication`
- [ ] Ruta archivo: `./data/consumos_energia.txt` (no en classpath)
- [ ] Builder en package `service/` (no `builder/`)
- [ ] Tests renombrados: `AdaptadorArchivoEnergiaTest`

---

## 🎯 RESULTADO ESPERADO

Después de aplicar todos los cambios, la estructura del proyecto en los documentos debe coincidir **exactamente** con:

```
SERVICIUDAD-CALI/
├── data/
│   └── consumos_energia.txt
├── postman/
│   └── ServiCiudad_API.postman_collection.json
├── src/
│   └── main/
│       ├── java/com/serviciudad/
│       │   ├── adapter/
│       │   │   └── AdaptadorArchivoEnergia.java
│       │   ├── controller/
│       │   │   └── DeudaConsolidadaController.java
│       │   ├── domain/
│       │   │   ├── FacturaAcueducto.java
│       │   │   └── FacturaEnergia.java
│       │   ├── dto/
│       │   │   ├── DetalleServicioDTO.java
│       │   │   └── DeudaConsolidadaDTO.java
│       │   ├── repository/
│       │   │   └── FacturaAcueductoRepository.java
│       │   ├── service/
│       │   │   ├── ClienteService.java
│       │   │   ├── DeudaConsolidadaBuilder.java
│       │   │   └── DeudaConsolidadaService.java
│       │   └── DeudaConsolidadaApplication.java
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

**🕒 Tiempo estimado:** 5-10 minutos usando el script PowerShell  
**🕒 Tiempo estimado:** 15-20 minutos usando búsqueda/reemplazo manual en VS Code

---

*Última actualización: Octubre 10, 2025*
