# Etapa 1: Construcción del backend y frontend
FROM maven:3.8.6-eclipse-temurin-17 AS build

# Crear directorio de trabajo
WORKDIR /app

# Copiar los archivos del proyecto
COPY pom.xml mvnw *.properties *.json ./
COPY .mvn .mvn
COPY src src
COPY webpack webpack

# Instalar Node.js 22 y npm
RUN apt-get update && apt-get install -y curl gnupg && \
    curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs 

# Compilar la aplicación en modo producción
RUN ./mvnw -Pprod clean package -DskipTests -Dskip.npm

# Etapa 2: Construcción de la imagen de producción
FROM eclipse-temurin:17-jre-alpine

# Variables de entorno
ENV TZ=UTC \
    _JAVA_OPTIONS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=prod,api-docs


# Crear directorio de trabajo para la aplicación
WORKDIR /app

# Copiar el archivo JAR compilado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
