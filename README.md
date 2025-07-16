# Sistema de Gestión de Transporte Urbano

## Descripción del Proyecto
Sistema backend para la gestión de transporte urbano que permite administrar vehículos, conductores y pedidos. Implementado con Java 17+ y Spring Boot, siguiendo principios de Clean Architecture y Domain-Driven Design.

## Arquitectura del Proyecto

### Arquitectura General
El proyecto sigue los principios de **Clean Architecture** y **Domain-Driven Design (DDD)**, organizado en capas bien definidas:

┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Controllers   │  │   DTOs/Mappers  │  │   Security   │ │
│  │   (REST API)    │  │   (MapStruct)   │  │   (JWT)      │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │ Application     │  │   Exception     │  │   Metrics    │ │
│  │   Services      │  │   Handling      │  │   Service    │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Entities      │  │   Domain        │  │ Repository   │ │
│  │   (Models)      │  │   Services      │  │  Interfaces  │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                  INFRASTRUCTURE LAYER                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Repository    │  │   Security      │  │   Monitoring │ │
│  │  Implementations│  │   Config        │  │   (Prometheus)│ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘

### Estructura de Paquetes

## Características Principales

### Seguridad
- Autenticación JWT con tokens de acceso y renovación
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

### Monitoreo y Observabilidad
- Spring Boot Actuator con endpoints de salud
- Prometheus para recolección de métricas
- Grafana para visualización de dashboards
- Métricas de negocio personalizadas
- Logging estructurado

### Análisis de Código
- SonarQube para análisis de calidad de código
- Cobertura de pruebas automática
- Detección de vulnerabilidades
- Métricas de mantenibilidad

## Flujo de Trabajo y Ramas

### Ramas Principales
- `main`: Rama de producción, contiene código estable y probado
- `dev`: Rama de desarrollo, integración de nuevas características
- `test`: Rama para pruebas y validación de funcionalidades

## Guía de Instalación y Ejecución

### Prerrequisitos
- Java 17+
- Maven 3.8+
- Docker y Docker Compose
- Git

### Configuración del Entorno Local

1. **Clonar el repositorio**
   ```bash
   git clone <url-repositorio>
   cd transporte
   ```

2. **Configurar variables de entorno (opcional)**
   Crear archivo `.env` en la raíz:
   ```env
   DB_USERNAME=appuser
   DB_PASSWORD=secretAppPwd
   JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
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

### Ejecución con Docker Compose

#### **Ejecución Completa (Recomendado)**
```bash
# Detener servicios existentes y limpiar
docker-compose down -v

# Reconstruir y ejecutar todo
docker-compose up --build -d

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs específicos
docker-compose logs -f transporte-app
docker-compose logs -f mysql
```

#### **Servicios Disponibles**
- **Aplicación Principal:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3000
- **SonarQube:** http://localhost:9000

### Ejecución Local (sin Docker)

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

## Usuarios y Credenciales

### Usuarios por Defecto

#### **Usuario Administrador Principal**
- **Username:** `admin`
- **Email:** `admin@transporte.com`
- **Password:** `admin123`
- **Rol:** ADMIN

#### **Usuario de Prueba**
- **Username:** `sncastro20`
- **Email:** `sncastro20@gmail.com`
- **Password:** `14307?Castro`
- **Rol:** ADMIN

### Grafana
- **URL:** http://localhost:3000
- **Usuario:** `admin`
- **Password:** `admin`

### Prometheus
- **URL:** http://localhost:9090
- **No requiere autenticación** (solo lectura)

### SonarQube
- **URL:** http://localhost:9000
- **Usuario:** `admin`
- **Password:** `admin`

## Análisis de Código con SonarQube

### Configuración Automática
El sistema incluye SonarQube configurado automáticamente para análisis de calidad de código.

#### **Características de SonarQube**
- **Análisis automático** de código Java
- **Detección de vulnerabilidades** de seguridad
- **Métricas de calidad** (mantenibilidad, confiabilidad, seguridad)
- **Cobertura de código** con JaCoCo
- **Duplicaciones** de código
- **Code smells** y bugs

#### **Métricas Analizadas**
- **Reliability:** Bugs y problemas de confiabilidad
- **Security:** Vulnerabilidades de seguridad
- **Maintainability:** Code smells y deuda técnica
- **Coverage:** Cobertura de pruebas unitarias
- **Duplications:** Código duplicado
- **Size:** Líneas de código, funciones, clases

### Uso de SonarQube

#### **Acceso y Configuración**
1. **Abrir SonarQube:** http://localhost:9000
2. **Login:** admin/admin
3. **Crear proyecto:** El proyecto se crea automáticamente al ejecutar análisis

#### **Análisis Automático**
```bash
# Ejecutar análisis completo
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=admin \
  -Dsonar.projectKey=transporte \
  -Dsonar.projectName="Sistema de Transporte"
```

#### **Configuración en pom.xml**
```xml
<properties>
    <!-- Configuración de SonarQube -->
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.login>admin</sonar.login>
    <sonar.password>admin</sonar.password>
    <sonar.projectKey>transporte</sonar.projectKey>
    <sonar.projectName>Sistema de Transporte</sonar.projectName>
    <sonar.projectVersion>1.0.0</sonar.projectVersion>
    <sonar.sources>src/main/java</sonar.sources>
    <sonar.tests>src/test/java</sonar.tests>
    <sonar.java.binaries>target/classes</sonar.java.binaries>
    <sonar.java.test.binaries>target/test-classes</sonar.java.test.binaries>
    <sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
</properties>
```

#### **Dashboard de SonarQube**
Una vez ejecutado el análisis, podrás ver:

**Métricas Principales:**
- **Reliability Rating:** A (Excelente) a E (Muy pobre)
- **Security Rating:** A (Excelente) a E (Muy pobre)
- **Maintainability Rating:** A (Excelente) a E (Muy pobre)
- **Coverage:** Porcentaje de cobertura de pruebas
- **Duplications:** Porcentaje de código duplicado

**Detalles del Análisis:**
- **Issues:** Problemas encontrados por categoría
- **Hotspots:** Puntos críticos de seguridad
- **Measures:** Métricas detalladas por archivo
- **Code:** Navegación del código con highlights

#### **Quality Gates**
SonarQube incluye Quality Gates predefinidos:
- **Coverage:** Mínimo 80% de cobertura
- **Duplications:** Máximo 3% de código duplicado
- **Reliability:** Máximo 0 bugs
- **Security:** Máximo 0 vulnerabilidades críticas
- **Maintainability:** Máximo 5% de deuda técnica

### Troubleshooting de SonarQube

#### **SonarQube no inicia**
```bash
# Verificar que esté corriendo
docker-compose ps sonarqube

# Ver logs
docker-compose logs -f sonarqube

# Reiniciar SonarQube
docker-compose restart sonarqube
```

#### **Análisis falla**
```bash
# Verificar conexión
curl http://localhost:9000/api/system/status

# Verificar configuración
mvn sonar:help

# Ejecutar con debug
mvn clean verify sonar:sonar -X
```

#### **Proyecto no aparece**
1. Verificar que el análisis se ejecutó correctamente
2. Verificar configuración de projectKey
3. Revisar logs de SonarQube
4. Ejecutar análisis nuevamente

## Monitoreo y Observabilidad

### Configuración Automática
El sistema incluye configuración automática de monitoreo:

#### **Prometheus**
- **Configuración:** `monitoring/prometheus.yml`
- **Métricas recolectadas:**
  - Métricas de aplicación Spring Boot
  - Métricas de negocio personalizadas
  - Métricas de JVM y sistema
- **Intervalo de scraping:** 15 segundos

#### **Grafana**
- **Configuración automática:** `monitoring/grafana/provisioning/`
- **Dashboards incluidos:**
  - Dashboard general de transporte
  - Métricas de rendimiento
  - Métricas de negocio
- **Datasource:** Prometheus configurado automáticamente

### Uso de Prometheus

#### **Acceso y Navegación**
1. **Abrir Prometheus:** http://localhost:9090
2. **Verificar Targets:** Ir a "Status" → "Targets"
   - `transporte-app:8080` debe estar en estado "UP"
   - `mysql:3306` puede estar en "DOWN" (es normal)

#### **Consultas Básicas**
Una vez en la pestaña "Query", prueba estas consultas:

```promql
# Verificar que la aplicación esté funcionando
up

# Métricas de JVM
jvm_memory_used_bytes

# Métricas de HTTP
http_server_requests_seconds_count

# Métricas de sistema
process_cpu_usage

# Métricas de negocio (si están configuradas)
transporte_pedidos_total
transporte_vehiculos_disponibles
```

#### **Visualización**
- **Tabla:** Ver datos en formato tabla
- **Graph:** Ver datos en gráfico temporal
- **Explain:** Explicación de la consulta

### Uso de Grafana

#### **Acceso y Configuración**
1. **Abrir Grafana:** http://localhost:3000
2. **Login:** admin/admin
3. **Datasource:** Prometheus configurado automáticamente
4. **Dashboards:** Cargados automáticamente

#### **Dashboards Disponibles**
- **Sistema de Transporte - Dashboard:** Métricas generales del sistema
- **Métricas de Rendimiento:** Performance de la aplicación
- **Métricas de Negocio:** Pedidos, vehículos, conductores

#### **Crear Consultas Personalizadas**
1. Ir a "Create" → "Dashboard"
2. Agregar panel
3. Seleccionar datasource "Prometheus"
4. Escribir consulta PromQL
5. Configurar visualización

### Métricas de Negocio Disponibles
- `transporte.pedidos.total` - Total de pedidos creados
- `transporte.pedidos.por_hora` - Pedidos por hora
- `transporte.vehiculos.disponibles` - Vehículos disponibles
- `transporte.vehiculos.en_mantenimiento` - Vehículos en mantenimiento
- `transporte.asignacion.tiempo` - Tiempo de respuesta para asignaciones
- `transporte.asignaciones.exitosas` - Asignaciones exitosas
- `transporte.asignaciones.fallidas` - Asignaciones fallidas
- `transporte.conductores.activos` - Conductores activos

### Métricas del Sistema
- `jvm_memory_used_bytes` - Memoria JVM utilizada
- `jvm_threads_live_threads` - Hilos activos
- `http_server_requests_seconds_count` - Contador de requests HTTP
- `http_server_requests_seconds_sum` - Tiempo total de requests
- `process_cpu_usage` - Uso de CPU del proceso
- `process_uptime_seconds` - Tiempo de actividad

### Alertas Configuradas
- **Uso de CPU > 80%**
- **Memoria > 85%**
- **Latencia API > 2 segundos**
- **Error rate > 5%**

### Troubleshooting del Monitoreo

#### **Prometheus sin datos**
```bash
# Verificar que la aplicación esté corriendo
curl http://localhost:8080/actuator/health

# Verificar que las métricas estén disponibles
curl http://localhost:8080/actuator/prometheus

# Reiniciar Prometheus
docker-compose restart prometheus
```

#### **Grafana sin conexión a Prometheus**
1. Verificar que Prometheus esté corriendo
2. Verificar configuración de datasource
3. Reiniciar Grafana: `docker-compose restart grafana`

#### **Targets en estado DOWN**
- Verificar que los servicios estén corriendo
- Verificar configuración de red en Docker
- Revisar logs: `docker-compose logs prometheus`

## API REST

### Documentación
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

### Autenticación
```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'

# Usar token en requests
curl -X GET http://localhost:8080/api/v1/vehiculos \
  -H "Authorization: Bearer {access_token}"
```

### Endpoints Principales

#### **Autenticación**
- `POST /api/v1/auth/login` - Iniciar sesión
- `POST /api/v1/auth/register` - Registrar usuario
- `POST /api/v1/auth/refresh-token` - Renovar token
- `POST /api/v1/auth/logout` - Cerrar sesión

#### **Vehículos**
- `GET /api/v1/vehiculos` - Listar vehículos
- `POST /api/v1/vehiculos` - Crear vehículo
- `PUT /api/v1/vehiculos/{id}` - Actualizar vehículo
- `DELETE /api/v1/vehiculos/{id}` - Eliminar vehículo

#### **Conductores**
- `GET /api/v1/conductores` - Listar conductores
- `POST /api/v1/conductores` - Crear conductor
- `PUT /api/v1/conductores/{id}` - Actualizar conductor
- `DELETE /api/v1/conductores/{id}` - Eliminar conductor

#### **Pedidos**
- `GET /api/v1/pedidos` - Listar pedidos
- `POST /api/v1/pedidos` - Crear pedido
- `PUT /api/v1/pedidos/{id}/estado` - Cambiar estado

#### **Estadísticas**
- `GET /api/v1/estadisticas/general` - Estadísticas generales
- `GET /api/v1/estadisticas/conductor/{id}` - Estadísticas por conductor

## Desarrollo y Pruebas

### Ejecutar Pruebas
```bash
# Todas las pruebas
mvn verify

# Solo pruebas unitarias
mvn test

# Pruebas de integración
mvn verify -P integration-test


### Análisis de Código
```bash
# Análisis completo con SonarQube
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=admin

# Solo cobertura
mvn jacoco:report

# Análisis de calidad
mvn spotbugs:check
```

### Cobertura de Código
- **Mínimo requerido:** 80%
- **Generado con:** JaCoCo
- **Reporte:** `target/site/jacoco/index.html`

## Validaciones de Negocio

### Vehículos
- **Formato de placa:** AAA999
- **Capacidad:** Entre 100kg y 5000kg
- **Estado:** activo/inactivo
- **Conductor:** Opcional (puede estar sin asignar)

### Conductores
- **Máximo vehículos:** 3 por conductor
- **Licencia formato:** A12345
- **Estado:** activo/inactivo

### Pedidos
- **Estados:** PENDIENTE, EN_PROCESO, ENTREGADO, CANCELADO
- **Validaciones:** Conductor y vehículo deben estar activos
- **Capacidad:** No puede exceder la capacidad del vehículo

## Logs y Debugging

### Configuración de Logs
- **Ubicación:** `logs/transporte.log`
- **Rotación:** Diaria con 7 días de retención
- **Nivel:** INFO en producción, DEBUG en desarrollo

### Verificar Estado de la Aplicación
```bash
# Health check
curl http://localhost:8080/actuator/health

# Métricas
curl http://localhost:8080/actuator/metrics

# Info de la aplicación
curl http://localhost:8080/actuator/info
```

## Troubleshooting

### Problemas Comunes

#### **Error de conexión a base de datos**
```bash
# Verificar que MySQL esté corriendo
docker-compose ps mysql

# Ver logs de MySQL
docker-compose logs mysql
```

#### **Error de autenticación**
- Verificar que las credenciales sean correctas
- Usar usuarios por defecto: `admin/admin123` o `sncastro20@gmail.com/14307?Castro`

#### **Error de compilación**
```bash
# Limpiar y recompilar
mvn clean package

# Verificar Java version
java -version
```

#### **Problemas con Docker**
```bash
# Limpiar todo y reconstruir
docker-compose down -v
docker system prune -a
docker-compose up --build
```

## CI/CD Pipeline

### Proceso Automatizado
El pipeline se ejecuta automáticamente en:
- Push a ramas: main, dev, test
- Pull Requests hacia estas ramas

### Etapas del Pipeline
1. **Build:** `mvn -B clean compile`
2. **Pruebas Unitarias:** `mvn -B test`
3. **Pruebas de Integración:** `mvn -B verify -P integration-test`
4. **Análisis de Cobertura:** Mínimo 80% con JaCoCo
5. **Análisis de Calidad:** SonarQube
6. **Seguridad:** Escaneo con Trivy





 