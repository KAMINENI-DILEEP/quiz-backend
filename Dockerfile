# Stage 1: Build the application using official Maven image
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy source and pom file
COPY pom.xml .
COPY src ./src

# Build package using standard mvn command (no wrapper needed)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render assigns a dynamic port, but Spring Boot defaults or picks up $PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
