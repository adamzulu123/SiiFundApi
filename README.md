# SiiFundApi - Backend for Fundraising Events

A Spring Boot application containerized with Docker.
The application exposes a REST API to manage collection boxes and fundraising events.


## Project Overview

This application is built with:
- Java 21
- Spring Boot 3.4.5
- H2 in-memory database
- JPA for data access
- NBP API: api.nbp.pl + WebFlux
- JUnit 5, Mockito, Spring Test

### General project notes: 
I created an application as requested in task description. What is important: 
- All operations are validated and tested. Some manually with Postman and curl, 
but crucial parts of it with **JUnit 5, Mockito and Spring Test**. 
Of course there are test for both controller and service layer. 
- Currency conversion uses either fixed or dynamically fetched exchange rates (**NBP API**). 
Static rates are used only when API failed, because I don't want to fully rely on some web api, 
but in real application I would save rates in the database and periodically refresh them with external API. 
- Available currencies are defined in Currency enum: **PLN, EUR, USD, GBP**. 
- There is no authentication or authorization. 
- The tests are automatically run during the Docker image build process; however, they can also be executed manually if necessary.  

## Database Schema

### 1. **CollectionBox**
| Field               | Type          | Description                                                                     |
|---------------------|---------------|---------------------------------------------------------------------------------|
| `id`                | UUID          | Primary Key                                                                     |
| `uniqueIdentifier`  | String        | Unique identifier for the collection box (generated automatically when created) |
| `fundraisingEvent`  | FundraisingEvent | Many-to-One relationship with FundraisingEvent                                  |
| `moneyEntries`      | List<MoneyEntry> | One-to-Many relationship with MoneyEntry                                        |

### 2. **FundraisingEvent**
| Field              | Type         | Description                                      |
|--------------------|--------------|--------------------------------------------------|
| `id`               | Long         | Primary Key                                      |
| `eventName`        | String       | Name of the fundraising event (Unique)           |
| `accountCurrency`  | Currency     | Currency type for the event (EUR, USD, GBP, PLN) |
| `accountBalance`   | BigDecimal   | Balance of the fundraising event                 |
| `collectionBoxes`  | List<CollectionBox> | One-to-Many relationship with CollectionBox      |

### 3. **MoneyEntry**
| Field            | Type          | Description                                      |
|------------------|---------------|--------------------------------------------------|
| `id`             | Long          | Primary Key                                      |
| `amount`         | BigDecimal    | Amount of money entered into the collection box |
| `currency`       | Currency      | Currency type (EUR, USD, GBP, PLN)              |
| `collectionBox`  | CollectionBox | Many-to-One relationship with CollectionBox      |
| `createTime`     | LocalDateTime | Time when the transaction was created            |

### 4. **Currency (ENUM)**
- `EUR`
- `USD`
- `GBP`
- `PLN`


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

Build app, run tests and then start the application: 
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

### Advanced docker additional testing manually: 

This approach lets you view detailed test results directly in the terminal
```bash
docker build --target test -t siifundapi-test .
```
```bash
docker run -it --rm siifundapi-test sh
```
```bash
mvn run
```
```bash
exit
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

**Base url** : http://localhost:8080/sii/api/

### Fundraising Event:

#### 1. Create Fundraising Event: 
**Method:** `POST`  
**Endpoint:** `/events/create`  
**Description:** Creates a new fundraising event with a name and an account currency.

**Request Body**: `CreateFundraisingEventRequest`

**Example curl**:
```bash
curl -X POST http://localhost:8080/sii/api/events/create \
-H "Content-Type: application/json" \
-d '{"eventName": "Charity for Kids", "accountCurrency": "EUR"}'
```

#### 2. Generate Financial Report:
**Method:** `POST`  
**Endpoint:** `/events/create`  
**Description:** Returns a summary report of all fundraising events and their account balances.

**ResponseBody**: `List<FundraisingEventFinancialReportDto>`
```bash
curl -X POST http://localhost:8080/sii/api/events/financial-report
```
**Example response**:
```json
[
  {
    "fundraisingEventName": "Charity for Kids",
    "amount": 2048.00,
    "currency": "EUR"
  },
  {
    "fundraisingEventName": "Hope Foundation",
    "amount": 512.64,
    "currency": "GBP"
  }
]
```

### Collections Boxes 
#### 1. Create Collection Box:
**Method:** `POST`  
**Endpoint:** `/collection-boxes/create`  
**Description:** Creates a new collection box, which can be assigned immediately to a FundraisingEvent or not. \
What is important uniqueIdentifier is generated automatically!!!!

**Request Body**: `CreateCollectionBoxRequest`
```bash 
curl -X POST http://localhost:8080/sii/api/collection-boxes/create \
-H "Content-Type: application/json" \
-d '{"eventName": "Charity for Kids"}'
```

#### 2. List All Collection Boxes:
**Method:** `GET`  
**Endpoint:** `/collection-boxes`  
**Description:** Retrieves a list of all collection boxes. Shows whether they are assigned and whether they are empty. 
This endpoint don't expose box account amount or assigned FundraisingEvent name. 
```bash
curl http://localhost:8080/sii/api/collection-boxes
```
**Example output**:
```json
[
  {
    "uniqueIdentifier": "abc123",
    "assigned": true,
    "empty": false
  },
  {
    "uniqueIdentifier": "def456",
    "assigned": false,
    "empty": true
  }
]
```

#### 3. Remove Collection Box
**Remember to create collection box before it and use its uniqueIdentifier!!!!!**   
**Method:** `DELETE`  
**Endpoint:** `/collection-boxes`  
**Description:** Removes (unregisters) a collection box. It must be specified by its unique identifier.

**QueryParam**: uniqueIdentifier (String, required)
```bash
curl -X DELETE "http://localhost:8080/sii/api/collection-boxes?uniqueIdentifier=exampleIdentifier"
```

#### 4. Assign Collection Box to Fundraising Event
**Method:** `POST`  
**Endpoint:** `/collection-boxes/assign`  
**Description:** Assigns a collection box to a fundraising event. The box must be empty and not assigned.

**Request Body**: AssignBoxRequest
```bash
curl -X POST http://localhost:8080/sii/api/collection-boxes/assign \
-H "Content-Type: application/json" \
-d '{"uniqueIdentifier": "abc123", "eventName": "Charity for Kids"}'
```

#### 5. Add Money to Collection Box
**Method:** `POST`  
**Endpoint:** `/collection-boxes/fund`  
**Description:** Adds money in a specific currency to a collection box

**Request Body**: AddMoneyRequest
```bash
curl -X POST http://localhost:8080/sii/api/collection-boxes/fund \
-H "Content-Type: application/json" \
-d '{"uniqueIdentifier": "abc123", "amount": 50.00, "currency": "USD"}'
```

#### 6. Transfer Money from Box to Event Account
**Remember to create collection box before it and use its uniqueIdentifier!!!!!!**   
**Method**: `POST`   
**Endpoint**: `/collection-boxes/transfer`   
**Description**: Transfers all money from a collection box to the fundraising event’s account. Currency is converted automatically.   

**QueryParam**: uniqueIdentifier (String) --> because we can only transfer money from assigned box to its event. 
```bash
curl -X POST "http://localhost:8080/sii/api/collection-boxes/transfer?uniqueIdentifier=exampleIdentifier"
```









