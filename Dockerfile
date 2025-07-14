# Etapa 1: Compilación con Maven
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

# Copiar el código fuente
WORKDIR /app
COPY . .

# Compilar el proyecto y generar el JAR
RUN mvn clean package -DskipTests

# Etapa 2: Creación de la imagen de ejecución final
FROM eclipse-temurin:17-jre-focal

# Crear un usuario no-root para ejecutar la aplicación por seguridad
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copiar el JAR desde la etapa de build
ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} app.jar

# Exponer el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app.jar"] 