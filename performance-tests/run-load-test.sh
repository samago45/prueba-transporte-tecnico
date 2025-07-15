#!/bin/bash

# Script para ejecutar pruebas de carga con JMeter
# Autor: Sistema de Transporte
# Fecha: $(date)

echo "🚀 Iniciando pruebas de carga para la aplicación de transporte..."

# Variables de configuración
JMETER_HOME=${JMETER_HOME:-"/opt/apache-jmeter"}
TEST_PLAN="transporte-load-test.jmx"
RESULTS_DIR="results"
REPORT_DIR="reports"

# Crear directorios si no existen
mkdir -p $RESULTS_DIR $REPORT_DIR

# Verificar que JMeter esté disponible
if ! command -v jmeter &> /dev/null; then
    echo "❌ JMeter no está instalado. Instalando..."
    
    # Descargar JMeter si no está disponible
    if [ ! -d "$JMETER_HOME" ]; then
        echo "📥 Descargando Apache JMeter..."
        wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.4.1.tgz
        tar -xzf apache-jmeter-5.4.1.tgz
        mv apache-jmeter-5.4.1 $JMETER_HOME
        rm apache-jmeter-5.4.1.tgz
    fi
    
    export PATH=$PATH:$JMETER_HOME/bin
fi

# Verificar que la aplicación esté ejecutándose
echo "🔍 Verificando que la aplicación esté ejecutándose..."
if ! curl -f http://localhost:8080/actuator/health &> /dev/null; then
    echo "❌ La aplicación no está ejecutándose en http://localhost:8080"
    echo "💡 Ejecuta: docker-compose up -d"
    exit 1
fi

echo "✅ Aplicación detectada en http://localhost:8080"

# Ejecutar pruebas de carga
echo "⚡ Ejecutando pruebas de carga..."
jmeter -n \
    -t $TEST_PLAN \
    -l $RESULTS_DIR/load-test-results.jtl \
    -e \
    -o $REPORT_DIR/html-report \
    -Jhost=localhost \
    -Jport=8080 \
    -Jprotocol=http

# Verificar resultados
if [ $? -eq 0 ]; then
    echo "✅ Pruebas de carga completadas exitosamente"
    echo "📊 Reporte HTML generado en: $REPORT_DIR/html-report/index.html"
    echo "📈 Resultados detallados en: $RESULTS_DIR/load-test-results.jtl"
    
    # Mostrar resumen de métricas
    echo ""
    echo "📋 RESUMEN DE PRUEBAS DE CARGA:"
    echo "================================="
    
    # Extraer métricas básicas del archivo de resultados
    if [ -f "$RESULTS_DIR/load-test-results.jtl" ]; then
        TOTAL_REQUESTS=$(grep -c "200\|201\|400\|500" $RESULTS_DIR/load-test-results.jtl 2>/dev/null || echo "0")
        SUCCESS_REQUESTS=$(grep -c "200\|201" $RESULTS_DIR/load-test-results.jtl 2>/dev/null || echo "0")
        ERROR_REQUESTS=$(grep -c "400\|500" $RESULTS_DIR/load-test-results.jtl 2>/dev/null || echo "0")
        
        echo "📊 Total de requests: $TOTAL_REQUESTS"
        echo "✅ Requests exitosos: $SUCCESS_REQUESTS"
        echo "❌ Requests con error: $ERROR_REQUESTS"
        
        if [ "$TOTAL_REQUESTS" -gt 0 ]; then
            SUCCESS_RATE=$((SUCCESS_REQUESTS * 100 / TOTAL_REQUESTS))
            echo "📈 Tasa de éxito: $SUCCESS_RATE%"
        fi
    fi
    
    echo ""
    echo "🎯 RECOMENDACIONES:"
    echo "==================="
    echo "• Revisa el reporte HTML para análisis detallado"
    echo "• Verifica métricas en Prometheus: http://localhost:9090"
    echo "• Visualiza dashboards en Grafana: http://localhost:3000"
    echo "• Considera optimizaciones si la tasa de éxito es < 95%"
    
else
    echo "❌ Error al ejecutar las pruebas de carga"
    exit 1
fi 