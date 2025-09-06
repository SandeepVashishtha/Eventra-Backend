# Eventra Backend

## Overview

The backend of Eventra is built using **Spring Boot** with **Java 17**. It provides RESTful APIs for managing events, users, authentication, and other core functionalities. The backend uses:

- Spring Data JPA for database interactions
- Spring Security for authentication and authorization
- MySQL as the primary database (with H2 for in-memory testing)
- OpenAPI (Swagger) for API documentation
- JWT for secure token-based authentication

## Setup and Running Instructions

### Prerequisites

- Java 17
- Maven
- MySQL database (or use H2 for in-memory testing)

### Local Development

1. Clone the repository
2. Navigate to backend directory:

```bash
cd backend
```

3. Install dependencies and run:

   - **Windows**:
     ```bash
     .\mvnw.cmd spring-boot:run
     ```

   - **Linux/Mac**:
     ```bash
     ./mvnw spring-boot:run
     ```

4. Or build and run the JAR:

   ```bash
   .\mvnw.cmd clean package
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

The backend will start on `http://localhost:8080` with:

- H2 in-memory database for development
- H2 Console available at `http://localhost:8080/h2-console`
- Swagger UI available at `http://localhost:8080/swagger-ui.html`
- API endpoints available at `http://localhost:8080/api`

### Production Setup

Configure the following environment variables for MySQL and JWT:

- AIVEN_DATABASE_URL=jdbc:mysql://your-host:port/database?useSSL=true
- AIVEN_DATABASE_USERNAME=your_username
- AIVEN_DATABASE_PASSWORD=your_password
- DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
- DATABASE_DIALECT=org.hibernate.dialect.MySQL8Dialect
- DDL_AUTO=update

Run with the production profile:

```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

### Testing

Run tests with:

```bash
mvn test
```

## API Documentation

The backend API is documented using OpenAPI (Swagger). After running the backend, access the API docs at:

```
http://localhost:8080/swagger-ui.html
```

The OpenAPI specification is also generated and can be found in the project root as `openapi.json` after running the build.

## Contribution

Contributions to the backend are welcome! Please follow these guidelines:

- Fork the repository and create a feature branch
- Write clear, maintainable code following existing conventions
- Write unit and integration tests for new features or bug fixes
- Ensure all tests pass before submitting a pull request
- Follow the root-level [CONTRIBUTING.md](../CONTRIBUTING.md) for general contribution guidelines

For any questions or help, please reach out to the maintainers.
