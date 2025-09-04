# Guía de Contribución - ServiCiudad Conectada

## Introducción

Bienvenido al proyecto ServiCiudad Conectada. Esta guía describe cómo contribuir efectivamente al desarrollo de la plataforma de transformación digital para los servicios públicos de Santiago de Cali.

## Código de Conducta

### Principios Fundamentales
- **Respeto**: Tratamos a todos los colaboradores con respeto y profesionalismo
- **Inclusión**: Valoramos la diversidad y promovemos un ambiente inclusivo
- **Colaboración**: Trabajamos juntos hacia objetivos comunes
- **Calidad**: Nos comprometemos con la excelencia técnica
- **Transparencia**: Comunicamos abierta y honestamente

### Comportamiento Esperado
- Usar lenguaje profesional y constructivo
- Ser respetuoso con diferentes puntos de vista
- Aceptar críticas constructivas
- Enfocarse en el beneficio del proyecto y la comunidad
- Mostrar empatía hacia otros miembros del equipo

## Estructura del Proyecto

```
Ingenieria-Software-2/
├── docs/                   # Documentación técnica
├── Documentos/            # Documentos de análisis y requerimientos
├── Graficos/              # Diagramas y recursos visuales
│   ├── Drawio/           # Archivos fuente de diagramas
│   ├── Pdf/              # Versiones PDF de diagramas
│   └── Xml/              # Diagramas en formato XML
├── src/                   # Código fuente (cuando aplique)
├── scripts/               # Scripts de automatización
├── README.md             # Información principal del proyecto
└── CONTRIBUTING.md       # Esta guía
```

## Proceso de Contribución

### 1. Fork y Clone

```bash
# Fork el repositorio en GitHub, luego clone tu fork
git clone https://github.com/tu-usuario/Ingenieria-Software-2.git
cd Ingenieria-Software-2

# Agregar el repositorio original como upstream
git remote add upstream https://github.com/LeonarDPeace/Ingenieria-Software-2.git
```

### 2. Crear Branch de Trabajo

```bash
# Sincronizar con el repositorio principal
git fetch upstream
git checkout main
git merge upstream/main

# Crear nuevo branch para tu contribución
git checkout -b feature/nueva-funcionalidad
# o
git checkout -b fix/correccion-bug
# o
git checkout -b docs/actualizacion-documentacion
```

### 3. Realizar Cambios

#### Convenciones de Naming
- **Feature branches**: `feature/descripcion-breve`
- **Bug fixes**: `fix/descripcion-del-problema`
- **Documentation**: `docs/area-actualizada`
- **Architecture**: `arch/cambio-arquitectonico`
- **Security**: `security/mejora-seguridad`

#### Guidelines para Commits
```bash
# Formato de commit message
tipo: Descripción breve (máximo 50 caracteres)

Descripción detallada del cambio si es necesario.
Explicar el QUÉ y el POR QUÉ, no el CÓMO.

- Punto específico 1
- Punto específico 2

Fixes #123
```

**Tipos de commit válidos**:
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Cambios en documentación
- `style`: Cambios de formato (espacios, formato, etc.)
- `refactor`: Refactoring de código
- `test`: Agregar o modificar tests
- `chore`: Mantenimiento (build, dependencias, etc.)
- `arch`: Cambios arquitectónicos
- `security`: Mejoras de seguridad

### 4. Testing y Validación

#### Para Documentación
```bash
# Validar markdown
markdownlint docs/

# Verificar enlaces
markdown-link-check docs/**/*.md

# Verificar ortografía (español)
aspell check -l es docs/NUEVO_DOCUMENTO.md
```

#### Para Diagramas
- Verificar que los diagramas sean consistentes con la arquitectura
- Validar que los formatos XML, PDF y PNG estén sincronizados
- Confirmar que los diagramas sigan las convenciones establecidas

#### Para Código (cuando aplique)
```bash
# Compilar y verificar
mvn clean compile

# Ejecutar tests
mvn test

# Análisis de calidad
mvn sonar:sonar

# Verificar dependencias
mvn dependency:analyze
```

### 5. Pull Request

#### Preparar el PR
```bash
# Asegurar que el branch esté actualizado
git fetch upstream
git rebase upstream/main

# Push del branch
git push origin feature/nueva-funcionalidad
```

#### Template de Pull Request
```markdown
## Descripción
Breve descripción de los cambios realizados.

## Tipo de Cambio
- [ ] Bug fix (corrección que no rompe funcionalidad existente)
- [ ] Nueva funcionalidad (cambio que agrega funcionalidad)
- [ ] Breaking change (cambio que podría romper funcionalidad existente)
- [ ] Documentación (cambios solo en documentación)
- [ ] Refactoring (cambios que no agregan funcionalidad ni corrigen bugs)

## Testing
Describe las pruebas realizadas para validar los cambios:
- [ ] Tests unitarios pasan
- [ ] Tests de integración pasan
- [ ] Documentación actualizada
- [ ] Diagramas actualizados (si aplica)

## Checklist
- [ ] Mi código sigue las convenciones del proyecto
- [ ] He realizado una auto-revisión de mi código
- [ ] He comentado áreas complejas de mi código
- [ ] He actualizado la documentación correspondiente
- [ ] Mis cambios no generan nuevos warnings
- [ ] He agregado tests que prueban mis cambios
- [ ] Tests nuevos y existentes pasan localmente

## Screenshots (si aplica)
Agregar capturas de pantalla para cambios visuales.

## Información Adicional
Cualquier información adicional relevante para los revisores.
```

## Guidelines Específicos

### Documentación

#### Estilo de Escritura
- **Claridad**: Usar lenguaje claro y directo
- **Consistencia**: Mantener estilo consistente en todo el documento
- **Completitud**: Incluir toda la información necesaria
- **Ejemplos**: Proporcionar ejemplos prácticos cuando sea útil

#### Formato Markdown
```markdown
# Título Principal (H1) - Solo uno por documento
## Sección Mayor (H2)
### Subsección (H3)
#### Detalle (H4)

**Texto importante** o __texto importante__
*Texto enfatizado* o _texto enfatizado_

`código inline` o comandos

```bash
# Bloque de código con sintaxis highlighting
comando --opcion valor
```

- Lista no ordenada
  - Sub-item
1. Lista ordenada
2. Segundo item

| Columna 1 | Columna 2 |
|-----------|-----------|
| Dato 1    | Dato 2    |

[Enlace](https://ejemplo.com)
![Imagen](ruta/imagen.png "Descripción alternativa")
```

#### Documentación de APIs
```markdown
### POST /api/v1/endpoint

**Descripción**: Breve descripción del endpoint

**Autenticación**: Bearer token requerido

**Parámetros**:
- `param1` (string, required): Descripción del parámetro
- `param2` (integer, optional): Descripción del parámetro

**Request Example**:
```json
{
  "param1": "valor",
  "param2": 123
}
```

**Response Example** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "abc123",
    "status": "completed"
  }
}
```

**Error Responses**:
- `400 Bad Request`: Invalid parameters
- `401 Unauthorized`: Authentication required
- `500 Internal Server Error`: Server error
```

### Diagramas

#### Convenciones de Draw.io
- **Colores estándar**:
  - Azul (#4472C4): Sistemas externos
  - Verde (#70AD47): Usuarios/Actores
  - Amarillo (#FFC000): Procesos/Servicios
  - Rojo (#C5504B): Sistemas legacy
  - Violeta (#7030A0): APIs/Interfaces

- **Formas**:
  - Rectángulos: Componentes de sistema
  - Cilindros: Bases de datos
  - Nubes: Servicios externos
  - Actores: Usuarios del sistema

#### Naming de Archivos
```
# Formato general
[Número]. [TipoDigrama][_Alcance][_ComponenteEspecífico].drawio

# Ejemplos
1. DiagramaArquitectura_ServiCiudadConectada.drawio
2. DiagramaC4_Contexto.drawio
3. DiagramaClases_MicroservicioPagos.drawio
4. DiagramaSecuencia_ProcesamientoPago.drawio
```

### Código (cuando aplique)

#### Java/Spring Boot
```java
// Convenciones de naming
public class PaymentService {           // PascalCase para clases
    private final PaymentRepository repository;  // camelCase para variables
    
    public static final String CONSTANT_VALUE = "VALUE";  // UPPER_SNAKE_CASE para constantes
    
    public PaymentResult processPayment(PaymentRequest request) {  // camelCase para métodos
        // Implementación
    }
}
```

#### Comentarios
```java
/**
 * Procesa un pago unificado para múltiples servicios.
 * 
 * @param request Datos del pago a procesar
 * @return Resultado del procesamiento con estado y detalles
 * @throws PaymentValidationException Si los datos son inválidos
 * @throws PaymentProcessingException Si ocurre error en el procesamiento
 */
public PaymentResult processUnifiedPayment(PaymentRequest request) {
    // Validar entrada
    validatePaymentRequest(request);
    
    // Procesar pago - lógica compleja que requiere explicación
    return executePaymentFlow(request);
}
```

## Revisión de Código

### Proceso de Review

#### Como Autor
1. **Auto-revisión**: Revisar tu propio código antes de crear el PR
2. **Descripción clara**: Explicar qué cambia y por qué
3. **Tests**: Asegurar que hay tests adecuados
4. **Documentación**: Actualizar documentación relacionada

#### Como Reviewer
1. **Comprender el contexto**: Leer la descripción y objetivos
2. **Revisar lógica**: Verificar que la implementación es correcta
3. **Verificar estilo**: Confirmar adherencia a convenciones
4. **Sugerir mejoras**: Proponer optimizaciones cuando sea apropiado
5. **Ser constructivo**: Dar feedback útil y específico

### Criterios de Aceptación
- [ ] El código compila sin errores ni warnings
- [ ] Los tests pasan completamente
- [ ] La documentación está actualizada
- [ ] El código sigue las convenciones del proyecto
- [ ] No introduce vulnerabilidades de seguridad
- [ ] El performance no se degrada significativamente

## Releases y Versionado

### Versionado Semántico
Seguimos [Semantic Versioning](https://semver.org/):
- **MAJOR**: Cambios incompatibles en la API
- **MINOR**: Nueva funcionalidad compatible hacia atrás
- **PATCH**: Correcciones de bugs compatibles hacia atrás

### Proceso de Release
1. **Preparación**: Actualizar CHANGELOG.md
2. **Testing**: Ejecutar suite completa de tests
3. **Tagging**: Crear tag con formato `v1.2.3`
4. **Documentation**: Actualizar documentación de versión
5. **Announcement**: Comunicar cambios al equipo

## Comunicación

### Canales
- **GitHub Issues**: Para bugs, features y discusiones técnicas
- **GitHub Discussions**: Para preguntas generales y brainstorming
- **Slack #serviciudad-dev**: Comunicación diaria del equipo
- **Email**: Para comunicaciones formales

### Reporting de Bugs
```markdown
**Título**: Descripción concisa del problema

**Descripción**:
Descripción detallada del comportamiento observado vs esperado.

**Pasos para Reproducir**:
1. Ir a...
2. Hacer click en...
3. Observar que...

**Comportamiento Esperado**:
Lo que debería ocurrir.

**Comportamiento Actual**:
Lo que actualmente ocurre.

**Información Adicional**:
- OS: [ej. Windows 10]
- Browser: [ej. Chrome 95]
- Version: [ej. 1.2.3]

**Screenshots**:
Si aplica, agregar screenshots del problema.
```

### Solicitud de Features
```markdown
**Título**: Nueva funcionalidad solicitada

**Descripción**:
Descripción clara de la funcionalidad deseada.

**Motivación**:
¿Por qué es necesaria esta funcionalidad?

**Solución Propuesta**:
Descripción de cómo debería funcionar.

**Alternativas Consideradas**:
Otras formas de resolver el problema.

**Información Adicional**:
Cualquier contexto adicional relevante.
```

## Recursos y Herramientas

### Herramientas Recomendadas
- **IDE**: IntelliJ IDEA, Visual Studio Code
- **Diagramas**: Draw.io, Lucidchart
- **Markdown**: Typora, Mark Text
- **Git GUI**: GitKraken, SourceTree (opcional)

### Referencias Útiles
- [Convenciones de Git](https://www.conventionalcommits.org/)
- [Guía de Markdown](https://www.markdownguide.org/)
- [Spring Boot Best Practices](https://springframework.guru/spring-boot-best-practices/)
- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)

## Contacto

### Maintainers
- **Eduard Criollo Yule** - Project Manager (ID: 2220335)
- **Arquitecto de Software** - Revisión técnica y arquitectónica
- **Tech Lead** - Revisión de código y mejores prácticas

### Soporte
- **GitHub Issues**: Para problemas técnicos
- **Email**: serviciudad-dev@universidad.edu.co
- **Oficina**: Universidad Autónoma de Occidente

---

Gracias por contribuir a ServiCiudad Conectada. Tu participación ayuda a mejorar los servicios públicos digitales para los ciudadanos de Santiago de Cali.
