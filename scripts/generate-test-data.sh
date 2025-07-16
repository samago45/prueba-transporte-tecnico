#!/bin/bash

echo "🚀 Generando datos de prueba para el sistema de transporte..."

# Esperar a que la aplicación esté lista
echo "⏳ Esperando a que la aplicación esté lista..."
sleep 30

# URL base de la aplicación
BASE_URL="http://localhost:8080/api/v1"

# Función para hacer requests y generar métricas
make_request() {
    local endpoint=$1
    local method=$2
    local data=$3
    
    if [ "$method" = "POST" ]; then
        curl -X POST "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data" \
            -s -o /dev/null -w "%{http_code}"
    else
        curl -X GET "$BASE_URL$endpoint" \
            -s -o /dev/null -w "%{http_code}"
    fi
}

echo "📊 Generando métricas de pedidos..."

# Crear pedidos para generar métricas
for i in {1..10}; do
    echo "Creando pedido $i..."
    make_request "/pedidos" "POST" "{
        \"origen\": \"Origen Test $i\",
        \"destino\": \"Destino Test $i\",
        \"peso\": $((RANDOM % 1000 + 100)),
        \"volumen\": $((RANDOM % 500 + 50)),
        \"prioridad\": \"NORMAL\",
        \"fechaEntrega\": \"2024-12-31\"
    }"
    sleep 1
done

echo "🚗 Generando métricas de vehículos..."

# Consultar vehículos
for i in {1..5}; do
    echo "Consultando vehículos $i..."
    make_request "/vehiculos" "GET"
    sleep 2
done

echo "👨‍💼 Generando métricas de conductores..."

# Consultar conductores
for i in {1..5}; do
    echo "Consultando conductores $i..."
    make_request "/conductores" "GET"
    sleep 2
done

echo "📈 Generando métricas de asignaciones..."

# Consultar asignaciones
for i in {1..3}; do
    echo "Consultando asignaciones $i..."
    make_request "/asignaciones" "GET"
    sleep 3
done

echo "✅ Datos de prueba generados exitosamente!"
echo "📊 Métricas disponibles en:"
echo "   - Prometheus: http://localhost:9090"
echo "   - Grafana: http://localhost:3000"
echo "   - Actuator: http://localhost:8080/actuator/metrics" 