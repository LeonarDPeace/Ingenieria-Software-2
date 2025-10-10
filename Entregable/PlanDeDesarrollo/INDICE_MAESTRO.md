# 📘 PLAN DE DESARROLLO ENTREGABLE CORTE 2
## SERVICIUDAD-CALI - ÍNDICE MAESTRO

---

**Proyecto:** Sistema de Consulta Unificada de Servicios Públicos  
**Universidad:** Universidad Autónoma de Occidente  
**Curso:** Ingeniería de Software 2 - 7mo Semestre  
**Equipo:** Eduard Criollo, Felipe Charria, Jhonathan Chicaiza, Emmanuel Mena, Juan Sebastian Castillo  
**Fecha:** Octubre 2025

---

## 🎯 DESCRIPCIÓN DEL PROYECTO

API RESTful monolítica desarrollada con **Spring Boot 3.x + Java 17** que consulta de forma unificada las deudas de servicios públicos (Energía + Acueducto) en la ciudad de Cali. 

El proyecto implementa **5 patrones de diseño obligatorios** y puede expandirse con patrones adicionales, Docker, CI/CD y documentación exhaustiva para sustentación técnica.

---

## 📂 ESTRUCTURA DE DOCUMENTOS

### 🚨 **DOCUMENTOS DE ACTUALIZACIÓN (LEER PRIMERO)**

| Archivo | Propósito | ¿Cuándo Usar? |
|---------|-----------|---------------|
| **[GUIA_ACTUALIZACION_RAPIDA.md](./GUIA_ACTUALIZACION_RAPIDA.md)** | Instrucciones para actualizar la nomenclatura en las 6 fases | **ANTES de comenzar implementación** |
| **[CAMBIOS_ESTRUCTURA.md](./CAMBIOS_ESTRUCTURA.md)** | Documentación detallada de todos los cambios de estructura | Referencia técnica completa |

⚠️ **IMPORTANTE:** La estructura del proyecto fue actualizada para seguir la convención:
- `domain/` en lugar de `entity/`
- `AdaptadorArchivoEnergia` en lugar de `ArchivoEnergiaAdapter`
- `DeudaConsolidadaBuilder` en `service/` en lugar de paquete `builder/` separado
- Archivo legacy en `data/` (raíz) en lugar de `resources/`

**Ejecuta los reemplazos indicados en `GUIA_ACTUALIZACION_RAPIDA.md` antes de seguir las fases.**

---

### 📋 **FASES DEL PLAN DE DESARROLLO**

#### **FASE 1: ESTRUCTURA DEL PROYECTO** 
📄 **[PlanDesarrolloEntregable_Fase1.md](./PlanDesarrolloEntregable_Fase1.md)**

**Contenido:**
- Estructura completa de carpetas y archivos
- Configuración de `.gitignore`
- Template inicial de `README.md`
- Creación del proyecto Maven base
- Explicación de diferencias entre proyecto principal y entregable

**Tiempo estimado:** 1-2 horas  
**Prerequisitos:** Git, Maven 3.9+, Java 17+

---

#### **FASE 2: CONFIGURACIÓN SPRING BOOT**
📄 **[PlanDesarrolloEntregable_Fase2.md](./PlanDesarrolloEntregable_Fase2.md)**

**Contenido:**
- `pom.xml` completo con dependencias
- `application.yml` con perfiles (dev, test, prod)
- Configuración de PostgreSQL
- `schema.sql` y `data.sql` con datos de ejemplo
- Configuración de OpenAPI, CORS, logs estructurados
- Archivo legacy `consumos_energia.txt` (formato COBOL)

**Tiempo estimado:** 2-3 horas  
**Prerequisitos:** PostgreSQL 15+ instalado

---

#### **FASE 3: PATRONES DE DISEÑO OBLIGATORIOS** ⭐
📄 **[PlanDesarrolloEntregable_Fase3.md](./PlanDesarrolloEntregable_Fase3.md)**

**Contenido:**

1. **Adapter Pattern** → Integración con archivo legacy (Mainframe simulado)
   - `AdaptadorArchivoEnergia.java`
   - `ServicioEnergiaPort.java`
   - Parseo de formato COBOL de ancho fijo

2. **Repository Pattern** → Acceso a datos con Spring Data JPA
   - `FacturaAcueductoRepository.java`
   - Entidades JPA: `FacturaAcueducto.java`, `FacturaEnergia.java`

3. **Builder Pattern** → Construcción de DTOs complejos
   - `DeudaConsolidadaBuilder.java` (en package `service/`)
   - Uso de Lombok `@Builder`

4. **DTO Pattern** → Separación de capas
   - `DeudaConsolidadaDTO.java`
   - `DetalleServicioDTO.java`
   - `ResumenDeudaDTO.java`
   - Mapper: `DeudaConsolidadaDTOMapper.java`

5. **IoC/DI Pattern** → Inversión de Control con Spring
   - `@Service`, `@Repository`, `@RestController`
   - Inyección por constructor

**Tiempo estimado:** 8-10 horas  
**Prerequisitos:** Fase 1 y 2 completadas

---

#### **FASE 4: DOCKER Y PATRONES ADICIONALES**
📄 **[PlanDesarrolloEntregable_Fase4.md](./PlanDesarrolloEntregable_Fase4.md)**

**Contenido:**

**Docker:**
- `Dockerfile` multi-stage (Maven build + JRE runtime)
- `docker-compose.yml` con 5 servicios:
  - `app` (Spring Boot)
  - `postgres` (Base de datos)
  - `redis` (Cache)
  - `pgadmin` (Admin DB)
  - `prometheus` (Métricas)

**Patrones Adicionales (Opcional):**
- **Circuit Breaker** con Resilience4j
- **Strategy Pattern** para múltiples fuentes de datos
- **Observer Pattern** para notificaciones
- **Command Pattern** para auditoría

**Tiempo estimado:** 4-6 horas  
**Prerequisitos:** Docker Desktop instalado

---

#### **FASE 5: DOCUMENTACIÓN TÉCNICA**
📄 **[PlanDesarrolloEntregable_Fase5.md](./PlanDesarrolloEntregable_Fase5.md)**

**Contenido:**

- **INFORME.md** completo:
  - Justificación técnica de cada patrón
  - Diagramas de arquitectura (ASCII)
  - Comparación monolito vs microservicios
  - Evaluación de alternativas
  - Aplicación de principios SOLID
  - Métricas de calidad

- **README.md** para usuarios:
  - Instrucciones de instalación
  - Configuración paso a paso
  - Ejemplos de uso
  - Troubleshooting

- **SUSTENTACION.md** para presentación oral:
  - Guión de sustentación
  - Respuestas a preguntas frecuentes
  - Demos en vivo

- **DEPLOYMENT.md** para producción:
  - Pasos de despliegue
  - Configuración de servidor
  - Monitoreo y logs

**Tiempo estimado:** 6-8 horas  
**Prerequisitos:** Fases 1-4 completadas

---

#### **FASE 6: TESTING Y CI/CD**
📄 **[PlanDesarrolloEntregable_Fase6.md](./PlanDesarrolloEntregable_Fase6.md)**

**Contenido:**

**Testing:**
- **Tests Unitarios** (JUnit 5 + Mockito)
  - `AdaptadorArchivoEnergiaTest.java`
  - `DeudaConsolidadaServiceTest.java`
  - `DeudaConsolidadaDTOTest.java`

- **Tests de Integración** (@SpringBootTest)
  - `DeudaConsolidadaIntegrationTest.java`
  - Testcontainers para PostgreSQL

- **Tests de Repository** (@DataJpaTest)
  - `FacturaAcueductoRepositoryTest.java`

**Colección Postman:**
- Requests de prueba con variables
- Tests automáticos con scripts PM
- Casos de éxito y error

**CI/CD Pipeline:**
- GitHub Actions workflow completo
- Build + Test + Coverage automático
- Docker image push
- Deploy a staging

**Checklist de Validación Final:**
- 30+ puntos de verificación
- Métricas esperadas (>80% cobertura)
- Comandos de entrega

**Tiempo estimado:** 6-8 horas  
**Prerequisitos:** Todas las fases anteriores completadas

---

## 🗺️ RUTA DE IMPLEMENTACIÓN RECOMENDADA

### **Semana 1: Setup y Configuración**
- [ ] Día 1-2: Fase 1 (Estructura) + Fase 2 (Configuración Spring Boot)
- [ ] Día 3-4: Inicio Fase 3 (Adapter + Repository Patterns)

### **Semana 2: Patrones Core**
- [ ] Día 1-2: Fase 3 continuación (Builder + DTO + IoC/DI)
- [ ] Día 3-4: Integración y pruebas manuales con Postman

### **Semana 3: Docker y Documentación**
- [ ] Día 1-2: Fase 4 (Docker + Patrones adicionales)
- [ ] Día 3-4: Fase 5 (Documentación técnica completa)

### **Semana 4: Testing y Entrega**
- [ ] Día 1-2: Fase 6 (Tests unitarios + integración)
- [ ] Día 3: CI/CD pipeline + validación final
- [ ] Día 4: Preparación de sustentación + entrega

---

## 📊 PROGRESO DEL PROYECTO

### **Checklist General**

#### **Configuración Inicial**
- [ ] Repositorio creado y clonado
- [ ] Estructura de carpetas creada (Fase 1)
- [ ] Maven configurado correctamente
- [ ] PostgreSQL instalado y configurado
- [ ] Archivo `consumos_energia.txt` creado en `data/`

#### **Implementación Core**
- [ ] Adapter Pattern implementado y funcional
- [ ] Repository Pattern con Spring Data JPA
- [ ] Builder Pattern en DTOs
- [ ] DTO Pattern en todas las capas
- [ ] IoC/DI aplicado en servicios y controladores
- [ ] Endpoint REST funcionando: `GET /api/v1/clientes/{id}/deuda-consolidada`

#### **Docker y Expansión**
- [ ] Dockerfile multi-stage funcional
- [ ] docker-compose.yml con todos los servicios
- [ ] Patrones adicionales implementados (opcional)

#### **Documentación**
- [ ] INFORME.md completo con justificaciones
- [ ] README.md con instrucciones claras
- [ ] SUSTENTACION.md preparada
- [ ] DEPLOYMENT.md con pasos de despliegue

#### **Testing y CI/CD**
- [ ] Tests unitarios (cobertura >80%)
- [ ] Tests de integración exitosos
- [ ] Colección Postman exportada y funcional
- [ ] GitHub Actions pipeline configurado

#### **Entrega Final**
- [ ] Código en GitHub con release tag v1.0.0
- [ ] Todos los entregables completos
- [ ] Presentación preparada
- [ ] Demo funcional lista

---

## 🎓 RECURSOS ADICIONALES

### **Tecnologías Utilizadas**
- **Backend:** Spring Boot 3.2.0, Java 17 LTS
- **Base de Datos:** PostgreSQL 15
- **Cache:** Redis 7
- **Build Tool:** Maven 3.9+
- **Testing:** JUnit 5, Mockito, AssertJ, Testcontainers
- **Containerización:** Docker, Docker Compose
- **CI/CD:** GitHub Actions
- **Documentación:** OpenAPI 3.0, Swagger UI

### **Patrones de Diseño**
- **Obligatorios (5):** Adapter, Repository, Builder, DTO, IoC/DI
- **Opcionales:** Circuit Breaker, Strategy, Observer, Command, Saga

### **Principios Aplicados**
- SOLID
- DRY (Don't Repeat Yourself)
- KISS (Keep It Simple, Stupid)
- Clean Code
- Domain-Driven Design (DDD)

---

## 📞 SOPORTE Y CONTACTO

**Preguntas Frecuentes:**
1. ¿Debo actualizar la nomenclatura antes de empezar? → **SÍ**, lee `GUIA_ACTUALIZACION_RAPIDA.md`
2. ¿Puedo omitir la Fase 4 (Docker)? → **NO recomendado**, pero posible
3. ¿Los patrones adicionales son obligatorios? → **NO**, pero suman puntos
4. ¿Necesito completar todos los tests? → **SÍ**, cobertura mínima 80%

**Repositorio del Proyecto:**
- GitHub: [LeonarDPeace/Ingenieria-Software-2](https://github.com/LeonarDPeace/Ingenieria-Software-2)

---

## 🏆 CRITERIOS DE ÉXITO

### **Funcional** ✅
- API responde en <2s
- Integración legacy + PostgreSQL exitosa
- 5 patrones implementados correctamente
- Respuesta JSON cumple especificación

### **Calidad** ✅
- Cobertura de tests >80%
- Sin vulnerabilidades críticas
- Código limpio y documentado
- Complejidad ciclomática <10

### **Documentación** ✅
- README permite instalación sin ayuda
- INFORME justifica técnicamente todos los patrones
- SUSTENTACION prepara para defensa oral
- Diagramas claros y precisos

### **Entrega** ✅
- Código en GitHub con tag de release
- Docker funcional con docker-compose
- Colección Postman completa
- Presentación preparada

---

**¡Éxito en el desarrollo del proyecto!** 🚀

---

*Índice generado: Octubre 10, 2025*  
*Versión del Plan: 1.0.0*
