#!/bin/bash

echo "🔄 Reiniciando servicios del sistema de transporte..."

# Detener todos los servicios
echo "⏹️  Deteniendo servicios..."
docker-compose down

# Limpiar volúmenes si es necesario (opcional)
# docker-compose down -v

# Reconstruir la imagen de la aplicación
echo "🔨 Reconstruyendo imagen de la aplicación..."
docker-compose build --no-cache transporte-app

# Levantar todos los servicios
echo "🚀 Levantando servicios..."
docker-compose up -d

# Esperar a que MySQL esté listo
echo "⏳ Esperando a que MySQL esté listo..."
sleep 30

# Verificar estado de los servicios
echo "📊 Verificando estado de los servicios..."
docker-compose ps

# Verificar logs de la aplicación
echo "📋 Logs de la aplicación:"
docker-compose logs --tail=20 transporte-app

# Verificar endpoints
echo "🔍 Verificando endpoints..."

# Health check
echo "✅ Health check:"
curl -f http://localhost:8080/actuator/health || echo "❌ Health check falló"

# Swagger UI
echo "📚 Swagger UI:"
curl -f http://localhost:8080/swagger-ui.html || echo "❌ Swagger UI no disponible"

# API Docs
echo "📖 API Docs:"
curl -f http://localhost:8080/api-docs || echo "❌ API Docs no disponible"

# Prometheus metrics
echo "📈 Prometheus metrics:"
curl -f http://localhost:8080/actuator/prometheus || echo "❌ Prometheus metrics no disponible"

echo ""
echo "🎯 URLs disponibles:"
echo "==================="
echo "🌐 Aplicación: http://localhost:8080"
echo "📚 Swagger UI: http://localhost:8080/swagger-ui.html"
echo "📖 API Docs: http://localhost:8080/api-docs"
echo "💚 Health: http://localhost:8080/actuator/health"
echo "📊 Metrics: http://localhost:8080/actuator/metrics"
echo "📈 Prometheus: http://localhost:9090"
echo "📊 Grafana: http://localhost:3000 (admin/admin)"
echo "🔍 SonarQube: http://localhost:9000 (admin/admin)"
echo ""
echo "✅ Servicios reiniciados exitosamente!" 