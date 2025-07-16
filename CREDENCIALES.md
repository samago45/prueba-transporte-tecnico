# 🔐 Credenciales del Sistema de Transporte

## 📊 **Servicios de Monitoreo y Calidad**

### **1. Grafana**
- **URL:** http://localhost:3000
- **Usuario:** `admin`
- **Contraseña:** `admin`
- **Descripción:** Dashboard de métricas y visualización

### **2. SonarQube**
- **URL:** http://localhost:9000
- **Usuario:** `admin`
- **Contraseña:** `admin`
- **Descripción:** Análisis de calidad de código

### **3. Prometheus**
- **URL:** http://localhost:9090
- **Usuario:** No requiere autenticación
- **Descripción:** Recolección de métricas

## 🗄️ **Base de Datos**

### **MySQL (Producción)**
- **Host:** localhost:3306
- **Base de datos:** `appdb`
- **Usuario:** `appuser`
- **Contraseña:** `secretAppPwd`
- **Root:** `rootpassword`

### **H2 (Pruebas)**
- **URL:** jdbc:h2:mem:testdb
- **Usuario:** `sa`
- **Contraseña:** (vacía)
- **Console:** http://localhost:8081/h2-console

## 🌐 **Aplicación Principal**

### **Spring Boot App**
- **URL:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs
- **Health Check:** http://localhost:8080/actuator/health
- **Metrics:** http://localhost:8080/actuator/metrics
- **Prometheus:** http://localhost:8080/actuator/prometheus

## 🔧 **Variables de Entorno**

### **Configuración por defecto:**
```bash
# Base de datos
DB_NAME=appdb
DB_USER=appuser
DB_PASSWORD=secretAppPwd
DB_ROOT_PASSWORD=rootpassword

# JWT
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=3600000

# Spring
SPRING_PROFILES_ACTIVE=prod
```

## 🚀 **Comandos Útiles**

### **Levantar servicios:**
```bash
# Todos los servicios
docker-compose up -d

# Solo aplicación y base de datos
docker-compose up -d mysql transporte-app

# Solo servicios de monitoreo
docker-compose up -d prometheus grafana sonarqube
```

### **Ver logs:**
```bash
# Logs de la aplicación
docker-compose logs -f transporte-app

# Logs de MySQL
docker-compose logs -f mysql

# Logs de Grafana
docker-compose logs -f grafana
```

### **Reiniciar servicios:**
```bash
# Reiniciar aplicación
docker-compose restart transporte-app

# Reconstruir y reiniciar
docker-compose up -d --build transporte-app
```

## 🔒 **Seguridad**

### **Cambiar contraseñas por defecto:**

#### **Grafana:**
1. Acceder a http://localhost:3000
2. Login con admin/admin
3. Ir a Configuration > Users
4. Cambiar contraseña del usuario admin

#### **SonarQube:**
1. Acceder a http://localhost:9000
2. Login con admin/admin
3. Ir a My Account > Security
4. Cambiar contraseña

#### **MySQL:**
```bash
# Conectar a MySQL
docker-compose exec mysql mysql -u root -p

# Cambiar contraseña
ALTER USER 'appuser'@'%' IDENTIFIED BY 'nueva_contraseña';
FLUSH PRIVILEGES;
```

## 📝 **Notas Importantes**

- **Grafana y SonarQube** requieren cambio de contraseña en el primer login
- **MySQL** usa contraseñas por defecto - cambiar en producción
- **JWT_SECRET** debe ser cambiado en producción
- Todos los servicios están configurados para desarrollo local

---

*Documento generado automáticamente - Sistema de Transporte* 