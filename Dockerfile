#Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

#Testing
FROM maven:3.9.6-eclipse-temurin-21-alpine AS test
WORKDIR /app
COPY --from=build /app .
RUN mvn test

#Final image
FROM eclipse-temurin:21-jdk-alpine AS runtime
WORKDIR /app

COPY --from=build /app/target/SiiFundApi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]



