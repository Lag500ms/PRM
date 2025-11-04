# Stage 1: Build project
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run project
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variables (có thể override khi chạy container)
ENV POSTGRES_URL=jdbc:postgresql://postgres:5432/BE
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=12345
ENV GEMINI_API_KEY=AIzaSyDFlWOFU0ax64CmChbdt9hrSbljN0fNy0U

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
