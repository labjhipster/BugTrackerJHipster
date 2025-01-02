# Etapa 1: Construcción del backend y frontend
FROM maven:3.8.6-eclipse-temurin-17 AS build

# Crear directorio de trabajo
WORKDIR /app

# Copiar los archivos del proyecto
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src

# Compilar la aplicación en modo producción
RUN ./mvnw -Pprod clean package -DskipTests

# Etapa 2: Construcción de la imagen de producción
FROM eclipse-temurin:17-jre-alpine

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod

# Crear directorio de trabajo para la aplicación
WORKDIR /app

# Copiar el archivo JAR compilado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
