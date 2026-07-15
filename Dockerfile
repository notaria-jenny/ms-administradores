# ─── Etapa 1: compilación ─────────────────────────────────
# Compila dentro del contenedor: no necesita Maven ni Java en el servidor
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app

# Primero solo el pom, para que Docker cachee la descarga de dependencias
# (si no cambias dependencias, los siguientes builds son mucho más rápidos)
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ─── Etapa 2: imagen final liviana (solo JRE) ─────────────
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

# Docker consulta el health check de Actuator (¡el paso 6 en acción!)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]