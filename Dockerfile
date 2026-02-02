# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY pricing-domain/pom.xml ./pricing-domain/
COPY pricing-application/pom.xml ./pricing-application/
COPY pricing-infrastructure/pom.xml ./pricing-infrastructure/

# Copy source code
COPY docs ./docs
COPY pricing-domain/src ./pricing-domain/src
COPY pricing-application/src ./pricing-application/src
COPY pricing-infrastructure/src ./pricing-infrastructure/src

# Build all modules
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/pricing-infrastructure/target/pricing-infrastructure-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
