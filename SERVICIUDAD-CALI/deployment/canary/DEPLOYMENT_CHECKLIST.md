# ✅ ServiCiudad Cali - Checklist de Despliegue Canario

> **Actualizado:** Noviembre 2025 | **Versión:** 2.0 | **Cobertura:** 87%

## 📋 Pre-Despliegue (1 hora antes)

### ✅ Verificaciones Técnicas
- [ ] Tests unitarios pasando (**87% coverage** ✓)
- [ ] Tests integración OK
- [ ] Build exitoso CI/CD (GitHub Actions)
- [ ] Docker: Imagen construida y taggeada
- [ ] Variables: `.env` configurado
- [ ] BD: Migración aplicada (si aplica)
- [ ] Rollback: Plan listo

### 📞 Comunicación
- [ ] Equipo desarrollo notificado
- [ ] Equipo soporte alertado
- [ ] Ventana mantenimiento comunicada
- [ ] Stakeholders informados

### 🖥️ Infraestructura
- [ ] Recursos suficientes (CPU, RAM, Disco)
- [ ] Prometheus/Grafana operativos
- [ ] Alertas configuradas y testeadas
- [ ] Logs centralizados accesibles
- [ ] Backup BD reciente (< 24h)

---

## 🚀 Durante Despliegue

### Fase 1: Canary 10% (1 hora)

#### Despliegue
- [ ] Ejecutar: `.\deploy-canary.ps1 -CanaryVersion X.X.X -CanaryPercent 10`
- [ ] Verificar health checks pasando
- [ ] Verificar smoke tests OK
- [ ] Confirmar distribución de tráfico en Nginx logs

#### Monitoreo (cada 15 minutos)
- [ ] **Tasa de errores:** Canary ≤ Stable + 10%
- [ ] **Latencia P95:** < 2 segundos
- [ ] **Uso de memoria:** < 85%
- [ ] **CPU:** < 80%
- [ ] **Requests/sec:** Canary recibe ~10% del tráfico

#### Validación Funcional
- [ ] Endpoint `/api/deuda/consultar` funciona
- [ ] Endpoint `/api/facturas/pagar` funciona
- [ ] Endpoint `/api/facturas/cliente/{id}` funciona
- [ ] Logs sin errores críticos
- [ ] No hay alertas activas

#### Decisión después de 1 hora
- [ ] **SI métricas OK:** Continuar a Fase 2
- [ ] **SI hay problemas:** Ejecutar rollback

---

### Fase 2: Canary 25% (2 horas)

#### Despliegue
- [ ] Ejecutar: `.\deploy-canary.ps1 -CanaryVersion X.X.X -CanaryPercent 25`
- [ ] Verificar escalado correcto (7 stable + 3 canary)
- [ ] Confirmar distribución de tráfico

#### Monitoreo (cada 20 minutos)
- [ ] Tasa de errores estable
- [ ] Latencia no incrementada
- [ ] Sin degradación de performance
- [ ] Logs limpios

#### Validación de Negocio
- [ ] Consultas de deuda procesadas correctamente
- [ ] Pagos registrados sin errores
- [ ] Facturas generadas correctamente
- [ ] Feedback de usuarios positivo

#### Decisión después de 2 horas
- [ ] **SI métricas OK:** Continuar a Fase 3
- [ ] **SI hay problemas:** Ejecutar rollback

---

### Fase 3: Canary 50% (4 horas)

#### Despliegue
- [ ] Ejecutar: `.\deploy-canary.ps1 -CanaryVersion X.X.X -CanaryPercent 50`
- [ ] Verificar escalado correcto (5 stable + 5 canary)
- [ ] Confirmar distribución de tráfico

#### Monitoreo (cada 30 minutos)
- [ ] Comparación detallada stable vs canary
- [ ] Análisis de tendencias en Grafana
- [ ] Revisión de logs agregados
- [ ] Verificar carga en base de datos

#### Validación Intensiva
- [ ] Ejecutar suite completa de tests de API
- [ ] Validar integraciones externas
- [ ] Revisar métricas de negocio
- [ ] Analizar feedback de usuarios

#### Decisión después de 4 horas
- [ ] **SI métricas OK:** Continuar a Fase 4
- [ ] **SI hay problemas:** Ejecutar rollback

---

### Fase 4: Full Rollout 100%

#### Despliegue
- [ ] Ejecutar: `.\deploy-canary.ps1 -CanaryVersion X.X.X -CanaryPercent 100`
- [ ] Verificar todas las réplicas en nueva versión
- [ ] Confirmar 100% tráfico en canary

#### Monitoreo Final (primeras 2 horas)
- [ ] Monitoreo intensivo cada 15 minutos
- [ ] Verificar todas las métricas
- [ ] Analizar logs en tiempo real
- [ ] Revisar alertas

#### Validación Post-Despliegue
- [ ] Todos los endpoints funcionando
- [ ] Métricas de negocio normales
- [ ] Sin alertas activas
- [ ] Feedback de usuarios positivo

---

## 🔄 Procedimiento de Rollback

### Cuándo hacer Rollback
- [ ] Tasa de errores > 150% de stable
- [ ] Latencia P95 > 3 segundos
- [ ] Alertas críticas activas
- [ ] Feedback negativo de usuarios
- [ ] Errores en logs críticos

### Pasos de Rollback
1. [ ] Ejecutar: `.\rollback-canary.ps1`
2. [ ] Verificar 100% tráfico en stable
3. [ ] Confirmar servicios saludables
4. [ ] Notificar al equipo
5. [ ] Documentar causa del rollback
6. [ ] Analizar logs y métricas
7. [ ] Planificar corrección

---

## ✅ Post-Despliegue (24 horas después)

### Validación Final
- [ ] Métricas estables durante 24 horas
- [ ] Sin alertas críticas
- [ ] Feedback de usuarios positivo
- [ ] Logs sin errores recurrentes
- [ ] Performance dentro de SLAs

### Limpieza
- [ ] Eliminar réplicas stable antiguas
- [ ] Remover imágenes Docker antiguas
- [ ] Actualizar documentación
- [ ] Archivar logs del despliegue

### Documentación
- [ ] Actualizar CHANGELOG
- [ ] Documentar issues encontrados
- [ ] Actualizar runbooks
- [ ] Compartir lecciones aprendidas

### Retrospectiva
- [ ] Reunión de equipo
- [ ] Discutir qué funcionó bien
- [ ] Identificar áreas de mejora
- [ ] Actualizar checklist

---

## 📊 Métricas Objetivo

| Métrica | Objetivo | Acción si supera |
|---------|----------|------------------|
| **Error Rate** | < 1% | Investigar si > 1.5%, Rollback si > 3% |
| **Latencia P95** | < 2s | Investigar si > 2.5s, Rollback si > 3s |
| **Latencia P99** | < 5s | Investigar si > 7s, Rollback si > 10s |
| **CPU** | < 70% | Escalar si > 80%, Alertar si > 90% |
| **Memoria** | < 80% | Escalar si > 85%, Alertar si > 90% |
| **Throughput** | ≥ Baseline | Investigar si < 90% baseline |
| **Availability** | > 99.9% | Rollback si < 99.5% |

---

## 🚨 Contactos de Emergencia

| Rol | Contacto | Disponibilidad |
|-----|----------|----------------|
| **Tech Lead** | [Nombre] - [Email] | 24/7 |
| **DevOps** | [Nombre] - [Email] | 24/7 |
| **Database Admin** | [Nombre] - [Email] | On-call |
| **Product Owner** | [Nombre] - [Email] | Business hours |

---

## 📝 Notas

```
Fecha de Despliegue: _______________
Versión Desplegada: _______________
Responsable: _______________
Hora de Inicio: _______________
Hora de Fin: _______________

Observaciones:
_________________________________________________
_________________________________________________
_________________________________________________

Incidencias:
_________________________________________________
_________________________________________________
_________________________________________________
```

---

**Firma del Responsable:** ________________  **Fecha:** ________________
