#!/bin/bash

echo "🚀 Iniciando Sistema Completo de Transporte con Observabilidad..."

# Detener servicios existentes
echo "⏹️  Deteniendo servicios existentes..."
docker-compose down

# Limpiar volúmenes si es necesario
read -p "¿Limpiar volúmenes? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker-compose down -v
    echo "🗑️  Volúmenes limpiados"
fi

# Construir y levantar servicios
echo "🔨 Construyendo y levantando servicios..."
docker-compose up -d --build

# Esperar a que MySQL esté listo
echo "⏳ Esperando a que MySQL esté listo..."
sleep 30

# Verificar estado de servicios
echo "📊 Verificando estado de servicios..."
docker-compose ps

# Esperar a que la aplicación esté lista
echo "⏳ Esperando a que la aplicación esté lista..."
sleep 60

# Verificar endpoints
echo "🔍 Verificando endpoints..."

# Health check
echo "✅ Health check:"
curl -f http://localhost:8080/actuator/health || echo "❌ Health check falló"

# Swagger UI
echo "📚 Swagger UI:"
curl -f http://localhost:8080/swagger-ui.html || echo "❌ Swagger UI no disponible"

# Prometheus metrics
echo "📈 Prometheus metrics:"
curl -f http://localhost:8080/actuator/prometheus || echo "❌ Prometheus metrics no disponible"

# Generar datos de prueba
echo "📊 Generando datos de prueba..."
chmod +x scripts/generate-test-data.sh
./scripts/generate-test-data.sh

echo ""
echo "🎉 ¡Sistema iniciado exitosamente!"
echo ""
echo "📋 URLs disponibles:"
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
echo "📝 Logs disponibles:"
echo "==================="
echo "📋 Ver logs de la aplicación: docker-compose logs -f transporte-app"
echo "📋 Ver logs de MySQL: docker-compose logs -f mysql"
echo "📋 Ver logs de Grafana: docker-compose logs -f grafana"
echo ""
echo "🔧 Comandos útiles:"
echo "==================="
echo "🔄 Reiniciar aplicación: docker-compose restart transporte-app"
echo "📊 Ver métricas en tiempo real: curl http://localhost:8080/actuator/metrics"
echo "🧪 Ejecutar pruebas JMeter: cd performance-tests && ./run-load-test.sh" 