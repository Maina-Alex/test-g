# Digital Health Backend API

A Spring Boot-based REST API for managing patient healthcare records in a county hospital outpatient department. This production-ready service enables clinicians to efficiently record and retrieve core patient information, encounters, and observations.

## Overview

This service addresses critical operational challenges faced by clinicians:

- **Quick patient retrieval**: Search by family name, given name, identifier, or birth date
- **Complete visit history**: Track and retrieve patient encounters and observations
- **Duplicate prevention**: Unique patient identifiers prevent duplicate records
- **Scalable architecture**: Ready for extension with additional healthcare data models

## Quick Start

### Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.6+**
- **Docker** (optional, for containerized deployment)

### Running the Application

#### Option 1: Using Maven

```bash
# Clone the repository
git clone <repository-url>
cd digital-health-backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

#### Option 2: Using Docker

```bash
# Build and run with Docker Compose
docker-compose up --build
```

#### Option 3: Using JAR file

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/Digital-health-backend-1.0.0.jar
```

The application will start on **http://localhost:8080**

### Access Points

- **API Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/testdatabase`
  - Username: `sa`
  - Password: `password`

## 🔐 Authentication

This API uses **API Key authentication**. All requests must include the following header:

```
X-API-KEY: [On CONFIG]
```

**Configuration**: Update the API key in `src/main/resources/application.yml`:

```yaml
api:
  key:
    header: X-API-KEY
    secret: XXXXX
```

##  Testing

### Run All Tests

```bash
mvn clean test
```

### Run with Coverage

```bash
mvn clean test jacoco:report
```

### Test Coverage

- Unit tests for service layer logic
- Controller integration tests
- Repository tests
- Test data isolation using H2 in-memory database



## Code Quality with Spotless

This project uses [Spotless](https://github.com/diffplug/spotless) to maintain consistent code formatting.

### Check Code Formatting

```bash
mvn spotless:check
```

### Auto-format Code

```bash
mvn spotless:apply
```

### Spotless Configuration

The project uses **Google Java Format** with **AOSP style**:

- Formatting is applied automatically before commits
- Configured in `pom.xml` under the `spotless-maven-plugin`
- Enforces consistent indentation, spacing, and imports
- Refactors long strings where appropriate

**Formatting Rules**:

- Java files follow Google Java Format (AOSP style)
- Only changed files are checked (ratchet from origin/main)
- Trailing whitespace removed
- Files end with newline

## 🗄️ Database Management

### Liquibase Migrations

Database schema is managed via Liquibase changelogs:

**Location**: `src/main/resources/db/changelog/`


### H2 Console Access

Access the H2 console at http://localhost:8080/h2-console

##  Deployment

### Docker Deployment

A `Dockerfile` and `docker-compose.yml` are included for containerized deployment:

```bash
docker-compose up --build
```

### Production Considerations

For production deployment:

1. Replace H2 with PostgreSQL or MySQL
2. Update `application.yml` with production database credentials
3. Use environment variables for sensitive configuration
4. Enable HTTPS/SSL
5. Configure proper logging and monitoring
6. Set up health checks and metrics
7. Use a secrets manager for API keys

## 🔍 Validation & Error Handling

### Validation Rules

- **Patient Identifier**: Must be 7-8 digits (validated by check constraint)
- **Gender**: Must be MALE or FEMALE
- **Required Fields**: Enforced at database and application layers
- **Date Formats**: ISO 8601 format (YYYY-MM-DD)


