# Etapa 1: Compilación con Maven
FROM maven:3.8.4-openjdk-11-slim AS build

# Copiar el pom.xml primero para aprovechar la caché de dependencias
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Creación de la imagen de ejecución final
FROM openjdk:11-jre-slim

# Crear un usuario no-root para ejecutar la aplicación
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Configuración de la aplicación
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication"
ENV SPRING_PROFILES_ACTIVE="prod"

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"] 