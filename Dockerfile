# Multi-stage Dockerfile: build Angular frontend, build Spring Boot backend, produce runtime image

# 1) Build frontend
FROM node:20 AS ui-build
WORKDIR /app/salesfrontend
COPY salesfrontend/package*.json ./
COPY salesfrontend/ ./
RUN npm ci --silent
RUN npm run build -- --output-path=dist || npm run build -- --outputPath=dist

# 2) Build backend (Maven wrapper)
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app/management
COPY management/ ./
# Ensure mvnw is executable if present
RUN if [ -f mvnw ]; then chmod +x mvnw; fi
COPY --from=ui-build /app/salesfrontend/dist /app/management/src/main/resources/static
RUN ./mvnw -B -DskipTests package || mvn -B -DskipTests package

# 3) Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=backend-build /app/management/target/*.jar /app/app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]
