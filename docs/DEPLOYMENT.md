# Guía de Despliegue - ServiCiudad Conectada

## Introducción

Esta guía proporciona instrucciones detalladas para el despliegue de la plataforma ServiCiudad Conectada en diferentes entornos (desarrollo, testing, staging y producción).

## Prerequisitos

### Herramientas Requeridas
- **Docker**: 20.10+
- **Kubernetes**: 1.24+
- **Helm**: 3.8+
- **kubectl**: 1.24+
- **Java**: OpenJDK 17
- **Maven**: 3.8+
- **Git**: 2.30+

### Infraestructura Base
- **Cluster Kubernetes** con mínimo 3 nodos
- **PostgreSQL** 14+ (cluster con alta disponibilidad)
- **Redis** 6+ (cluster para caché)
- **Apache Kafka** 3.0+ (cluster para eventos)
- **Load Balancer** (NGINX o similar)
- **Storage Class** para volúmenes persistentes

## Configuración del Entorno

### 1. Configuración de Namespaces

```bash
# Crear namespaces para diferentes entornos
kubectl create namespace serviciudad-dev
kubectl create namespace serviciudad-staging
kubectl create namespace serviciudad-prod

# Aplicar labels para organización
kubectl label namespace serviciudad-prod environment=production
kubectl label namespace serviciudad-staging environment=staging
kubectl label namespace serviciudad-dev environment=development
```

### 2. Secrets y ConfigMaps

#### Database Secrets
```yaml
# database-secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: database-secrets
  namespace: serviciudad-prod
type: Opaque
data:
  postgres-user: c2VydmljaXVkYWQ=  # serviciudad
  postgres-password: cGFzc3dvcmQxMjM=  # password123
  redis-password: cmVkaXNfcGFzcw==  # redis_pass
```

```bash
kubectl apply -f database-secrets.yaml
```

#### Application ConfigMap
```yaml
# app-config.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: serviciudad-prod
data:
  application.yaml: |
    spring:
      profiles:
        active: production
      datasource:
        url: jdbc:postgresql://postgres-cluster:5432/serviciudad
        username: ${DB_USER}
        password: ${DB_PASSWORD}
      redis:
        host: redis-cluster
        port: 6379
        password: ${REDIS_PASSWORD}
      kafka:
        bootstrap-servers: kafka-cluster:9092
    
    resilience4j:
      circuitbreaker:
        instances:
          payment-service:
            failure-rate-threshold: 50
            wait-duration-in-open-state: 30s
          legacy-energy:
            failure-rate-threshold: 60
            wait-duration-in-open-state: 60s
    
    management:
      endpoints:
        web:
          exposure:
            include: health,info,metrics,prometheus
```

### 3. Almacenamiento Persistente

```yaml
# storage-class.yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: fast-ssd
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  fsType: ext4
  encrypted: "true"
allowVolumeExpansion: true
volumeBindingMode: WaitForFirstConsumer
```

## Despliegue de Infraestructura

### 1. PostgreSQL Cluster

```yaml
# postgres-cluster.yaml
apiVersion: postgresql.cnpg.io/v1
kind: Cluster
metadata:
  name: postgres-cluster
  namespace: serviciudad-prod
spec:
  instances: 3
  primaryUpdateStrategy: unsupervised
  
  postgresql:
    parameters:
      max_connections: "200"
      shared_buffers: "256MB"
      effective_cache_size: "1GB"
      
  bootstrap:
    initdb:
      database: serviciudad
      owner: serviciudad
      secret:
        name: database-secrets
        
  storage:
    size: 100Gi
    storageClass: fast-ssd
    
  monitoring:
    enabled: true
    
  backup:
    retentionPolicy: "30d"
    barmanObjectStore:
      destinationPath: "s3://serviciudad-backups/postgres"
```

### 2. Redis Cluster

```yaml
# redis-cluster.yaml
apiVersion: redis.redis.opstreelabs.in/v1beta1
kind: RedisCluster
metadata:
  name: redis-cluster
  namespace: serviciudad-prod
spec:
  clusterSize: 6
  
  redisExporter:
    enabled: true
    
  storage:
    volumeClaimTemplate:
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 20Gi
        storageClassName: fast-ssd
        
  securityContext:
    runAsUser: 1000
    fsGroup: 1000
```

### 3. Kafka Cluster

```yaml
# kafka-cluster.yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: kafka-cluster
  namespace: serviciudad-prod
spec:
  kafka:
    version: 3.0.0
    replicas: 3
    listeners:
      - name: plain
        port: 9092
        type: internal
        tls: false
      - name: tls
        port: 9093
        type: internal
        tls: true
        
    config:
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
      default.replication.factor: 3
      min.insync.replicas: 2
      
    storage:
      type: jbod
      volumes:
      - id: 0
        type: persistent-claim
        size: 50Gi
        class: fast-ssd
        
  zookeeper:
    replicas: 3
    storage:
      type: persistent-claim
      size: 10Gi
      class: fast-ssd
      
  entityOperator:
    topicOperator: {}
    userOperator: {}
```

## Despliegue de Microservicios

### 1. API Gateway

```yaml
# api-gateway-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
  namespace: serviciudad-prod
  labels:
    app: api-gateway
    version: v1
spec:
  replicas: 3
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
        version: v1
    spec:
      containers:
      - name: api-gateway
        image: serviciudad/api-gateway:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_USER
          valueFrom:
            secretKeyRef:
              name: database-secrets
              key: postgres-user
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: database-secrets
              key: postgres-password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
      volumes:
      - name: config-volume
        configMap:
          name: app-config
---
apiVersion: v1
kind: Service
metadata:
  name: api-gateway-service
  namespace: serviciudad-prod
spec:
  selector:
    app: api-gateway
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

### 2. Microservicio de Pagos

```yaml
# ms-pagos-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ms-pagos
  namespace: serviciudad-prod
  labels:
    app: ms-pagos
    version: v1
spec:
  replicas: 5
  selector:
    matchLabels:
      app: ms-pagos
  template:
    metadata:
      labels:
        app: ms-pagos
        version: v1
    spec:
      containers:
      - name: ms-pagos
        image: serviciudad/ms-pagos:1.0.0
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_USER
          valueFrom:
            secretKeyRef:
              name: database-secrets
              key: postgres-user
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: database-secrets
              key: postgres-password
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: database-secrets
              key: redis-password
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 15
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: ms-pagos-service
  namespace: serviciudad-prod
spec:
  selector:
    app: ms-pagos
  ports:
  - port: 8081
    targetPort: 8081
  type: ClusterIP
```

### 3. Horizontal Pod Autoscaler

```yaml
# hpa-ms-pagos.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: ms-pagos-hpa
  namespace: serviciudad-prod
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: ms-pagos
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
```

## Monitoreo y Observabilidad

### 1. Prometheus

```yaml
# prometheus-config.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: prometheus-config
  namespace: serviciudad-prod
data:
  prometheus.yml: |
    global:
      scrape_interval: 15s
    
    scrape_configs:
    - job_name: 'api-gateway'
      static_configs:
      - targets: ['api-gateway-service:80']
      metrics_path: /actuator/prometheus
      
    - job_name: 'ms-pagos'
      static_configs:
      - targets: ['ms-pagos-service:8081']
      metrics_path: /actuator/prometheus
      
    - job_name: 'postgres'
      static_configs:
      - targets: ['postgres-cluster-rw:5432']
      
    - job_name: 'redis'
      static_configs:
      - targets: ['redis-cluster:6379']
```

### 2. Grafana Dashboards

```yaml
# grafana-dashboard-configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: serviciudad-dashboard
  namespace: serviciudad-prod
data:
  dashboard.json: |
    {
      "dashboard": {
        "title": "ServiCiudad Conectada - Overview",
        "panels": [
          {
            "title": "Request Rate",
            "type": "graph",
            "targets": [
              {
                "expr": "rate(http_requests_total[5m])",
                "legendFormat": "{{service}}"
              }
            ]
          },
          {
            "title": "Payment Processing Time",
            "type": "graph", 
            "targets": [
              {
                "expr": "histogram_quantile(0.95, payment_processing_time_bucket)",
                "legendFormat": "95th percentile"
              }
            ]
          }
        ]
      }
    }
```

## Scripts de Despliegue

### 1. Script de Construcción

```bash
#!/bin/bash
# build.sh

set -e

echo "🏗️  Construyendo imágenes Docker..."

# Variables
REGISTRY="serviciudad"
VERSION="${1:-latest}"

# Microservicios a construir
services=("api-gateway" "ms-pagos" "ms-clientes" "ms-facturacion" "ms-notificaciones" "ms-incidencias" "ms-administracion")

for service in "${services[@]}"; do
    echo "Construyendo $service..."
    
    cd $service
    
    # Compilar con Maven
    ./mvnw clean package -DskipTests
    
    # Construir imagen Docker
    docker build -t $REGISTRY/$service:$VERSION .
    
    # Push al registry
    docker push $REGISTRY/$service:$VERSION
    
    cd ..
    
    echo "✅ $service construido exitosamente"
done

echo "🎉 Todas las imágenes han sido construidas y subidas"
```

### 2. Script de Despliegue

```bash
#!/bin/bash
# deploy.sh

set -e

ENVIRONMENT="${1:-production}"
NAMESPACE="serviciudad-${ENVIRONMENT}"

echo "🚀 Desplegando en entorno: $ENVIRONMENT"

# Verificar que el namespace existe
kubectl get namespace $NAMESPACE || {
    echo "❌ Namespace $NAMESPACE no existe"
    exit 1
}

# Aplicar configuraciones base
echo "📦 Aplicando configuraciones..."
kubectl apply -f infrastructure/ -n $NAMESPACE

# Esperar que la base de datos esté lista
echo "⏳ Esperando que PostgreSQL esté listo..."
kubectl wait --for=condition=ready pod -l app=postgres-cluster -n $NAMESPACE --timeout=300s

# Aplicar migraciones de base de datos
echo "🗃️  Ejecutando migraciones..."
kubectl run db-migration \
    --image=serviciudad/db-migration:latest \
    --env="DB_URL=jdbc:postgresql://postgres-cluster:5432/serviciudad" \
    --env="DB_USER=$(kubectl get secret database-secrets -o jsonpath='{.data.postgres-user}' | base64 -d)" \
    --env="DB_PASSWORD=$(kubectl get secret database-secrets -o jsonpath='{.data.postgres-password}' | base64 -d)" \
    --restart=Never \
    -n $NAMESPACE

kubectl wait --for=condition=complete job/db-migration -n $NAMESPACE --timeout=300s

# Desplegar microservicios
echo "🔧 Desplegando microservicios..."
kubectl apply -f deployments/ -n $NAMESPACE

# Esperar que todos los deployments estén listos
echo "⏳ Esperando que los servicios estén listos..."
kubectl rollout status deployment/api-gateway -n $NAMESPACE
kubectl rollout status deployment/ms-pagos -n $NAMESPACE
kubectl rollout status deployment/ms-clientes -n $NAMESPACE

# Verificar que todos los pods estén corriendo
echo "🔍 Verificando estado de los pods..."
kubectl get pods -n $NAMESPACE

# Aplicar configuración de ingress
echo "🌐 Configurando ingress..."
kubectl apply -f ingress/ -n $NAMESPACE

echo "✅ Despliegue completado exitosamente en $ENVIRONMENT"
echo "🌍 La aplicación estará disponible en: https://api.serviciudad.gov.co"
```

### 3. Script de Health Check

```bash
#!/bin/bash
# health-check.sh

NAMESPACE="${1:-serviciudad-prod}"
API_URL="${2:-https://api.serviciudad.gov.co}"

echo "🏥 Verificando salud del sistema..."

# Verificar pods
echo "Verificando pods..."
FAILED_PODS=$(kubectl get pods -n $NAMESPACE --field-selector=status.phase!=Running --no-headers | wc -l)
if [ $FAILED_PODS -gt 0 ]; then
    echo "❌ Hay $FAILED_PODS pods con problemas"
    kubectl get pods -n $NAMESPACE --field-selector=status.phase!=Running
    exit 1
fi

# Verificar endpoints de salud
services=("api-gateway" "ms-pagos" "ms-clientes")
for service in "${services[@]}"; do
    echo "Verificando $service..."
    response=$(curl -s -o /dev/null -w "%{http_code}" $API_URL/actuator/health)
    if [ $response -eq 200 ]; then
        echo "✅ $service está saludable"
    else
        echo "❌ $service no está saludable (HTTP $response)"
        exit 1
    fi
done

# Verificar métricas
echo "Verificando métricas..."
metrics_response=$(curl -s -o /dev/null -w "%{http_code}" $API_URL/actuator/prometheus)
if [ $metrics_response -eq 200 ]; then
    echo "✅ Métricas están disponibles"
else
    echo "❌ Métricas no están disponibles"
fi

echo "🎉 Todos los servicios están funcionando correctamente"
```

## Configuración de CI/CD

### 1. GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy ServiCiudad

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Run tests
      run: ./mvnw test
    
    - name: SonarCloud Scan
      uses: SonarSource/sonarcloud-github-action@master
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  build:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Build and push Docker images
      run: |
        echo "${{ secrets.DOCKER_PASSWORD }}" | docker login -u "${{ secrets.DOCKER_USERNAME }}" --password-stdin
        ./scripts/build.sh ${{ github.sha }}

  deploy-staging:
    needs: build
    runs-on: ubuntu-latest
    environment: staging
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Configure kubectl
      run: |
        echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > kubeconfig
        export KUBECONFIG=kubeconfig
    
    - name: Deploy to staging
      run: ./scripts/deploy.sh staging
    
    - name: Run health checks
      run: ./scripts/health-check.sh serviciudad-staging

  deploy-production:
    needs: deploy-staging
    runs-on: ubuntu-latest
    environment: production
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Configure kubectl
      run: |
        echo "${{ secrets.KUBE_CONFIG_PROD }}" | base64 -d > kubeconfig
        export KUBECONFIG=kubeconfig
    
    - name: Deploy to production
      run: ./scripts/deploy.sh production
    
    - name: Run health checks
      run: ./scripts/health-check.sh serviciudad-prod
```

## Rollback y Recuperación

### 1. Rollback de Deployment

```bash
# Rollback al deployment anterior
kubectl rollout undo deployment/ms-pagos -n serviciudad-prod

# Rollback a una revisión específica
kubectl rollout undo deployment/ms-pagos --to-revision=2 -n serviciudad-prod

# Verificar el estado del rollback
kubectl rollout status deployment/ms-pagos -n serviciudad-prod
```

### 2. Backup y Restore de Base de Datos

```bash
# Crear backup
kubectl exec -it postgres-cluster-1 -n serviciudad-prod -- \
    pg_dump -U serviciudad serviciudad > backup_$(date +%Y%m%d_%H%M%S).sql

# Restaurar backup
kubectl exec -i postgres-cluster-1 -n serviciudad-prod -- \
    psql -U serviciudad serviciudad < backup_20240115_143000.sql
```

## Troubleshooting

### Comandos Útiles de Debugging

```bash
# Ver logs de un pod
kubectl logs -f ms-pagos-deployment-abc123 -n serviciudad-prod

# Acceder a un pod para debugging
kubectl exec -it ms-pagos-deployment-abc123 -n serviciudad-prod -- /bin/bash

# Ver eventos del cluster
kubectl get events -n serviciudad-prod --sort-by='.lastTimestamp'

# Describir un recurso específico
kubectl describe pod ms-pagos-deployment-abc123 -n serviciudad-prod

# Ver recursos del cluster
kubectl top nodes
kubectl top pods -n serviciudad-prod
```

### Problemas Comunes

1. **Pod en estado CrashLoopBackOff**
   ```bash
   kubectl logs ms-pagos-deployment-abc123 -n serviciudad-prod --previous
   ```

2. **Servicios no accesibles**
   ```bash
   kubectl get svc -n serviciudad-prod
   kubectl describe svc ms-pagos-service -n serviciudad-prod
   ```

3. **Problemas de conectividad de base de datos**
   ```bash
   kubectl exec -it ms-pagos-deployment-abc123 -n serviciudad-prod -- \
     curl postgres-cluster:5432
   ```

---

Esta guía proporciona una base sólida para el despliegue y mantenimiento de ServiCiudad Conectada en entornos Kubernetes, garantizando alta disponibilidad, escalabilidad y facilidad de mantenimiento.
