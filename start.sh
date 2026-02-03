#!/bin/sh
set -e
PORT=${PORT:-8080}

# Build frontend (Angular)
if [ -d salesmanagement ]; then
  cd salesmanagement
  npm ci
  npm run build -- --output-path=dist
  cd ..
fi

# Build backend (Maven Spring Boot)
cd management
./mvnw -B package -DskipTests

# If frontend built, copy into backend resources so Spring Boot serves it
if [ -d ../salesmanagement/dist ]; then
  mkdir -p src/main/resources/static
  cp -r ../salesmanagement/dist/* src/main/resources/static/
fi

JAR=$(ls target/*.jar | head -n1)
exec java -jar "$JAR"
