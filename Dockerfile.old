# Etapa 1: Construcción de la aplicación Angular
FROM node:22 AS build-angular
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run webapp:build:prod

# Etapa 2: Construcción de la aplicación Spring Boot
FROM maven:3.8.6-eclipse-temurin-17 AS build-spring
WORKDIR /app
COPY pom.xml mvnw *.properties *.json ./
COPY src ./src
COPY --from=build-angular /app/target/classes/static ./src/main/resources/static
RUN mvn -Pprod clean package -DskipTests -Dskip.npm -Dsonar.skip=true

# Etapa 3: Imagen final
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build-spring /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]