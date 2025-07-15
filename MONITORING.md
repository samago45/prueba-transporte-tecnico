# 📊 Sistema de Monitoreo y Pruebas de Rendimiento

## 🎯 **Descripción General**

Este documento describe el sistema completo de monitoreo y pruebas de rendimiento implementado para la aplicación de transporte, incluyendo **Spring Boot Actuator**, **Prometheus**, **Grafana** y **pruebas de estrés con JMeter**.

---

## 🚀 **Componentes del Sistema**

### 1. **Spring Boot Actuator + Métricas Personalizadas**

#### **Endpoints disponibles:**
- `http://localhost:8080/actuator/health` - Estado de salud de la aplicación
- `http://localhost:8080/actuator/metrics` - Métricas del sistema
- `http://localhost:8080/actuator/prometheus` - Métricas en formato Prometheus

#### **Métricas de negocio implementadas:**
- `transporte.pedidos.total` - Número total de pedidos creados
- `transporte.pedidos.por_hora` - Pedidos por hora
- `transporte.vehiculos.disponibles` - Vehículos disponibles
- `transporte.vehiculos.en_mantenimiento` - Vehículos en mantenimiento
- `transporte.asignacion.tiempo` - Tiempo de respuesta para asignaciones
- `transporte.asignaciones.exitosas` - Asignaciones exitosas
- `transporte.asignaciones.fallidas` - Asignaciones fallidas
- `transporte.pedidos.creacion.tiempo` - Tiempo de creación de pedidos
- `transporte.conductores.activos` - Conductores activos

### 2. **Prometheus**

#### **Configuración:**
- **URL:** http://localhost:9090
- **Scrape interval:** 15 segundos
- **Targets:** Aplicación Spring Boot en puerto 8080

#### **Métricas recolectadas:**
- Métricas del sistema (CPU, memoria, JVM)
- Métricas HTTP (requests, response time, status codes)
- Métricas de negocio personalizadas
- Métricas de base de datos

### 3. **Grafana**

#### **Configuración:**
- **URL:** http://localhost:3000
- **Usuario:** admin
- **Contraseña:** admin
- **Datasource:** Prometheus

#### **Dashboards disponibles:**
- **Sistema:** CPU, memoria, JVM metrics
- **Aplicación:** HTTP requests, response times, error rates
- **Negocio:** Pedidos, vehículos, asignaciones, conductores

### 4. **Pruebas de Estrés con JMeter**

#### **Configuración de pruebas:**
- **Usuarios concurrentes:** 50 para pedidos, 20 para asignaciones
- **Ramp-up time:** 30 segundos para pedidos, 15 para asignaciones
- **Duración:** 10 iteraciones para pedidos, 5 para asignaciones
- **Assertions:** Response time < 2 segundos, Status codes 200/201

---

## 🛠️ **Instalación y Configuración**

### **1. Levantar el entorno completo:**

```bash
# Levantar todos los servicios
docker-compose up -d

# Verificar que todos los servicios estén ejecutándose
docker-compose ps
```

### **2. Verificar endpoints:**

```bash
# Health check de la aplicación
curl http://localhost:8080/actuator/health

# Métricas de Prometheus
curl http://localhost:8080/actuator/prometheus

# Estadísticas del sistema
curl http://localhost:8080/actuator/metrics
```

### **3. Acceder a las interfaces:**

- **Aplicación:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3000 (admin/admin)
- **SonarQube:** http://localhost:9000 (admin/admin)

---

## 📈 **Pruebas de Rendimiento**

### **Ejecutar pruebas de carga:**

```bash
# Navegar al directorio de pruebas
cd performance-tests

# Ejecutar script de pruebas
./run-load-test.sh
```

### **Configuración de JMeter:**

El plan de pruebas incluye:

1. **Pruebas de Pedidos API:**
   - 50 usuarios concurrentes
   - 10 iteraciones por usuario
   - Ramp-up de 30 segundos
   - GET /api/v1/pedidos
   - POST /api/v1/pedidos

2. **Pruebas de Asignaciones API:**
   - 20 usuarios concurrentes
   - 5 iteraciones por usuario
   - Ramp-up de 15 segundos
   - GET /api/v1/asignaciones

### **Métricas de rendimiento esperadas:**

- **Response Time:** < 2 segundos (95th percentile)
- **Throughput:** > 100 requests/segundo
- **Error Rate:** < 5%
- **Success Rate:** > 95%

---

## 📊 **Análisis de Resultados**

### **1. Métricas de Prometheus:**

```promql
# Requests por segundo
rate(http_server_requests_seconds_count[5m])

# Response time promedio
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])

# Error rate
rate(http_server_requests_seconds_count{status=~"4..|5.."}[5m]) / rate(http_server_requests_seconds_count[5m])

# Métricas de negocio
transporte_pedidos_total
transporte_asignaciones_exitosas_total
transporte_vehiculos_disponibles
```

### **2. Alertas recomendadas:**

```yaml
# Response time > 2 segundos
- alert: HighResponseTime
  expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 2
  for: 1m

# Error rate > 5%
- alert: HighErrorRate
  expr: rate(http_server_requests_seconds_count{status=~"4..|5.."}[5m]) / rate(http_server_requests_seconds_count[5m]) > 0.05
  for: 1m

# Sin vehículos disponibles
- alert: NoVehiclesAvailable
  expr: transporte_vehiculos_disponibles == 0
  for: 5m
```

---

## 🔧 **Configuración Avanzada**

### **Personalizar métricas de negocio:**

```java
// En MetricsService.java
public void actualizarMetricaPersonalizada(String nombre, double valor) {
    meterRegistry.gauge("transporte." + nombre, valor);
}
```

### **Agregar nuevos endpoints de monitoreo:**

```java
@RestController
@RequestMapping("/api/v1/monitoring")
public class MonitoringController {
    
    @GetMapping("/stats")
    public String getStats() {
        return metricsService.obtenerEstadisticas();
    }
}
```

### **Configurar alertas en Grafana:**

1. Ir a Alerting > Notification channels
2. Crear canal de notificación (email, Slack, etc.)
3. Configurar alertas en los dashboards
4. Definir thresholds y condiciones

---

## 🚨 **Solución de Problemas**

### **Problemas comunes:**

1. **Prometheus no puede scrape la aplicación:**
   - Verificar que el endpoint `/actuator/prometheus` esté habilitado
   - Comprobar conectividad de red entre contenedores

2. **Grafana no puede conectar con Prometheus:**
   - Verificar URL del datasource: `http://prometheus:9090`
   - Comprobar que Prometheus esté ejecutándose

3. **JMeter no puede conectar con la aplicación:**
   - Verificar que la aplicación esté ejecutándose en puerto 8080
   - Comprobar firewall y configuración de red

### **Logs útiles:**

```bash
# Logs de la aplicación
docker-compose logs transporte-app

# Logs de Prometheus
docker-compose logs prometheus

# Logs de Grafana
docker-compose logs grafana
```

---

## 📚 **Recursos Adicionales**

- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [JMeter User Manual](https://jmeter.apache.org/usermanual/)

---

## 🎯 **Próximos Pasos**

1. **Implementar dashboards específicos de negocio**
2. **Configurar alertas automáticas**
3. **Integrar con sistemas de notificación**
4. **Implementar métricas de SLA**
5. **Agregar pruebas de estrés automatizadas al CI/CD**

---

*Documento generado automáticamente - Sistema de Transporte* 