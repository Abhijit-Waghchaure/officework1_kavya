#!/bin/sh
set -e
PORT=${PORT:-8080}

# Build frontend (Angular)
if [ -d salesfrontend ]; then
  cd salesfrontend

  # Install Node if npm not available (tries apt, yum, apk)
  if ! command -v npm >/dev/null 2>&1; then
    echo "npm not found — attempting to install Node.js"
    if command -v apt-get >/dev/null 2>&1; then
      apt-get update && apt-get install -y curl ca-certificates gnupg --no-install-recommends
      curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
      apt-get install -y nodejs --no-install-recommends
    elif command -v yum >/dev/null 2>&1; then
      curl -fsSL https://rpm.nodesource.com/setup_20.x | bash -
      yum install -y nodejs
    elif command -v apk >/dev/null 2>&1; then
      apk add --no-cache nodejs npm
    else
      echo "No supported package manager found to install Node.js; frontend build may fail"
    fi
  fi

  npm ci
  npm run build -- --output-path=dist
  cd ..
fi

# Build backend (Maven Spring Boot)
cd management
# Ensure the Maven wrapper is executable (fixes permission denied in containers)
if [ -f mvnw ]; then
  chmod +x mvnw || true
  ./mvnw -B -DskipTests || mvn -B -DskipTests
else
  mvn -B -DskipTests
fi

# If frontend built, copy into backend resources so Spring Boot serves it
if [ -d ../salesfrontend/dist ]; then
  mkdir -p src/main/resources/static
  cp -r ../salesfrontend/dist/* src/main/resources/static/
fi

JAR=$(ls target/*.jar | head -n1)
exec java -jar "$JAR"
