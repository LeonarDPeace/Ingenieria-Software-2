# Pipeline DevOps Listo para Producción

Aplicación CRUD para demostración de pipeline DevOps completo usando herramientas gratuitas.

## Estructura del Proyecto

```
.
├── backend/          # API backend (Node.js + Express)
├── frontend/         # Interfaz frontend (React)
├── infrastructure/   # Código de infraestructura (Terraform, Kubernetes)
└── .github/
    └── workflows/    # Configuraciones de CI/CD
```

## Tecnologías

- **Backend**: Node.js, Express, PostgreSQL
- **Frontend**: React
- **Contenedores**: Docker
- **Orquestación**: Kubernetes (K3d)
- **IaC**: Terraform
- **CI/CD**: GitHub Actions
- **Monitoreo**: Grafana, Prometheus
- **Seguridad**: Trivy, OWASP ZAP, CodeQL

## Requisitos Previos

- Git
- Docker
- Node.js & npm
- Terraform
- kubectl
- k3d
- Trivy
- OWASP ZAP

## Desarrollo Local

```bash
# Backend
cd backend
npm install
npm start

# Frontend
cd frontend
npm install
npm start
```

## Commits Semánticos

Este proyecto usa [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Nueva funcionalidad
- `fix:` Corrección de error
- `docs:` Cambios en documentación
- `style:` Formato de código
- `refactor:` Refactorización
- `test:` Pruebas
- `chore:` Mantenimiento
