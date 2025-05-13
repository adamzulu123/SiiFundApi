# SiiFundApi - Backend for Fundraising Events

A Spring Boot application containerized with Docker for easy deployment and testing.

## Project Overview

This application is built with:
- Java 21
- Spring Boot 3.4.5
- H2 in-memory database
- JPA for data access

## Docker Setup

The project uses a multi-stage Docker build process that:
1. Builds the application
2. Runs tests
3. Creates a lightweight runtime image

## Getting Started

### Prerequisites

- Docker and Docker Compose installed on your machine (By Installing Docker Desktop is the easiest and most useful way)

### Running the Application

1. Clone the repository:
```bash
git clone https://github.com/adamzulu123/SiiFundApi.git
cd SiiFundApi
```

2. Start the application with Docker Compose:
```bash
docker-compose up -d
```

3. Check if the container is running:
```bash
docker ps
```

4. Access the application:
    - Application: http://localhost:8080
    - H2 Database Console: http://localhost:8080/h2-console 
        - JDBC URL: `jdbc:h2:mem:siidb`
        - Username: `sa`
        - Password: ` ` (empty)

### Stopping the Application

```bash
docker-compose down
```

### Running Locally without Docker

You can also run the application directly on your local machine:

1. Make sure you have JDK 21 installed:
```bash
java -version
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

4. Alternatively, run the JAR file directly after building:
```bash
java -jar target/SiiFundApi-0.0.1-SNAPSHOT.jar
```

### Testing Locally without Docker 
1. Run all tests:
```bash
./mvnw test
```

## API Overview: 
