# Sistema de Gestión de Transporte Urbano

## Descripción del Proyecto
Sistema backend para la gestión de transporte urbano que permite administrar vehículos, conductores y pedidos. Implementado con Java 11+ y Spring Boot, siguiendo principios de Clean Architecture y Domain-Driven Design.

## Flujo de Trabajo y Ramas

### Ramas Principales
- `main`: Rama de producción, contiene código estable y probado
- `dev`: Rama de desarrollo, integración de nuevas características
- `test`: Rama para pruebas y validación de funcionalidades



## CI/CD Pipeline

### Proceso Automatizado
El pipeline se ejecuta automáticamente en:
- Push a ramas: main, dev, test
- Pull Requests hacia estas ramas

### Etapas del Pipeline
1. **Build**
   ```bash
   mvn -B clean compile
   ```

2. **Pruebas Unitarias**
   ```bash
   mvn -B test
   ```

3. **Pruebas de Integración**
   ```bash
   mvn -B verify -P integration-test
   ```

4. **Análisis de Cobertura**
   - Mínimo requerido: 80%
   - Reporte generado con JaCoCo
   ```bash
   mvn jacoco:report
   mvn jacoco:check -Djacoco.minimum.coverage=0.80
   ```

5. **Seguridad**
   - Escaneo de vulnerabilidades con Trivy
   - Análisis de dependencias

### Métricas y Reportes
- Cobertura de código en Codecov
- Reportes de pruebas en target/surefire-reports
- Análisis de seguridad en cada build

## Guía de Instalación y Ejecución

### Prerrequisitos
- Java 11+
- Maven 3.6+
- Docker y Docker Compose
- Git

### Configuración del Entorno Local

1. **Clonar el repositorio**
   ```bash
   git clone <url-repositorio>
   cd transporte
   ```

2. **Configurar variables de entorno**
   Crear archivo `.env` en la raíz:
   ```env
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   JWT_SECRET=your_jwt_secret
   ```

3. **Compilar el proyecto**
   ```bash
   mvn clean package
   ```

### Configuración de Bases de Datos

#### **Desarrollo (MySQL)**
- **Base de datos:** MySQL 8.0
- **Host:** localhost:3306
- **Database:** appdb
- **Usuario:** appuser
- **Contraseña:** secretAppPwd
- **Configuración:** `application.properties`

#### **Pruebas (H2)**
- **Base de datos:** H2 en memoria
- **URL:** jdbc:h2:mem:testdb
- **Usuario:** sa
- **Contraseña:** (vacía)
- **Configuración:** `application-test.properties`

### Ejecución Local

1. **Modo Desarrollo (MySQL)**
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=dev
   # O simplemente:
   mvn spring-boot:run
   ```

2. **Modo Test (H2 Database)**
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=test
   ```

3. **Docker Compose**
   ```bash
   # Construir y ejecutar contenedores
   docker-compose up -d

   # Ver logs
   docker-compose logs -f

   # Detener servicios
   docker-compose down
   ```

### Verificación de la Instalación

1. **Healthcheck**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **Swagger UI**
   - Acceder a: http://localhost:8080/swagger-ui.html
   - Documentación completa de endpoints disponible

3. **Métricas**
   ```bash
   curl http://localhost:8080/actuator/metrics
   ```

## Desarrollo y Pruebas

### Ejecutar Pruebas
```bash
# Todas las pruebas
mvn verify

# Solo pruebas unitarias
mvn test

# Pruebas de integración
mvn verify -P integration-test

# Pruebas de rendimiento
cd performance-tests
./run-load-test.sh
```

### Análisis de Código
```bash
# Análisis completo
mvn verify sonar:sonar

# Solo cobertura
mvn jacoco:report

# Análisis de calidad
mvn spotbugs:check
```

### Generación de Documentación
```bash
# Generar JavaDoc
mvn javadoc:javadoc

# Actualizar Swagger
mvn springdoc:generate
```

## Monitoreo y Mantenimiento

### Logs
- Ubicación: `/var/log/transporte/`
- Rotación: Diaria con 7 días de retención
- Nivel: INFO en producción, DEBUG en desarrollo

### Métricas de Aplicación
- **Spring Boot Actuator** con endpoints de salud y métricas
- **Prometheus** para recolección de métricas (http://localhost:9090)
- **Grafana** para visualización de dashboards (http://localhost:3000)
- **Métricas de negocio** personalizadas:
  - Pedidos por hora
  - Vehículos disponibles
  - Tiempo de respuesta de APIs
  - Tasa de éxito de asignaciones
- **Alertas configuradas** para:
  - Uso de CPU > 80%
  - Memoria > 85%
  - Latencia API > 2 segundos
  - Error rate > 5%

### Backup y Recuperación
```bash
# Backup de base de datos
./scripts/backup.sh

# Restaurar backup
./scripts/restore.sh <backup-file>
```

## Soporte y Contacto

### Reportar Problemas
- Crear issue en el repositorio
- Incluir:
  - Descripción detallada
  - Pasos para reproducir
  - Logs relevantes
  - Ambiente (dev/test/prod)



## Características Principales

### Seguridad
- Autenticación JWT
- Roles: ADMIN, CONDUCTOR, CLIENTE
- Protección de endpoints por rol
- Manejo global de excepciones de seguridad

### Gestión de Datos
- Auditoría automática de entidades
- Soft Delete implementado
- Validaciones de negocio robustas
- Manejo transaccional

### API REST
- API versionada (/api/v1)
- Documentación OpenAPI/Swagger
- Paginación y filtrado
- Manejo global de errores

### Caché y Rendimiento
- Caché implementado para consultas frecuentes
- Consultas optimizadas
- Métricas de negocio

## Documentación API

### Swagger UI
- Disponible en: `http://localhost:8080/swagger-ui.html`
- Documentación completa de endpoints
- Ejemplos de requests y responses

### Endpoints Principales

#### Vehículos
- GET /api/v1/vehiculos
- POST /api/v1/vehiculos
- PUT /api/v1/vehiculos/{id}
- DELETE /api/v1/vehiculos/{id}

#### Conductores
- GET /api/v1/conductores
- POST /api/v1/conductores
- PUT /api/v1/conductores/{id}
- DELETE /api/v1/conductores/{id}

#### Pedidos
- GET /api/v1/pedidos
- POST /api/v1/pedidos
- PUT /api/v1/pedidos/{id}/estado

#### Estadísticas
- GET /api/v1/estadisticas/general
- GET /api/v1/estadisticas/conductor/{id}

## Pruebas

### Ejecutar Pruebas
```bash
# Pruebas unitarias
mvn test

# Pruebas de integración
mvn verify
```

### Cobertura de Código
- Generada con JaCoCo
- Reporte en: `target/site/jacoco/index.html`

## Validaciones de Negocio

### Vehículos
- Formato de placa: AAA999
- Capacidad: Entre 100kg y 5000kg
- Estado activo/inactivo

### Conductores
- Máximo 3 vehículos por conductor
- Licencia formato: A12345
- Validación de horario de servicio

### Pedidos
- Validación de capacidad disponible
- Estados: PENDIENTE, EN_PROCESO, ENTREGADO, CANCELADO
- Validaciones de conductor y vehículo activos



 