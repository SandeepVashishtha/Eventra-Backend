# Eventra Backend

A comprehensive event management system backend built with Spring Boot, providing secure RESTful APIs for event creation, user management, and administrative operations.

## 🚀 Features

- **Authentication & Authorization**: JWT-based security with role-based access control
- **Event Management**: Create, update, delete, and manage events
- **User Management**: User registration, profile management, and admin operations
- **Project Management**: Handle event-related projects and collaborations
- **Health Monitoring**: Built-in health checks and monitoring endpoints
- **API Documentation**: Interactive Swagger UI with OpenAPI 3.0 specification
- **Database Flexibility**: Support for both MySQL (production) and H2 (development)
- **Azure Cloud Ready**: Complete deployment configuration for Azure App Service

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.3.1
- **Language**: Java 17
- **Build Tool**: Maven
- **Security**: Spring Security with JWT
- **Database**: MySQL (production) / H2 (development)
- **ORM**: Spring Data JPA with Hibernate
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Validation**: Spring Boot Validation
- **Testing**: JUnit 5 with Spring Boot Test
- **Cloud**: Azure App Service deployment ready

## 📋 Prerequisites

Before running the application, ensure you have:

- **Java 17** or higher
- **Maven 3.6+** (or use included Maven wrapper)
- **MySQL 8.0+** (for production environment)
- **Git** for version control

## 🏃‍♂️ Quick Start

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/SandeepVashishtha/Eventra-Backend
   cd Eventra-Backend
   ```

2. **Run with Maven Wrapper (Recommended)**
   
   **Windows:**
   ```cmd
   .\mvnw.cmd spring-boot:run
   ```
   
   **Linux/Mac:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Alternative: Build and run JAR**
   ```bash
   .\mvnw.cmd clean package
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

The application will start on `http://localhost:8080` using H2 in-memory database for development.

### 🌐 Access Points

Once running, you can access:

- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console` (dev mode only)
- **Health Check**: `http://localhost:8080/health`

## 🗄 Database Configuration

### Development (H2 - Default)
The application uses H2 in-memory database by default for development with these credentials:
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: *(empty)*

### Production (MySQL)
Configure the following environment variables for MySQL:

```bash
AIVEN_DATABASE_URL=jdbc:mysql://your-mysql-host:3306/eventra_db?useSSL=true
AIVEN_DATABASE_USERNAME=your_username
AIVEN_DATABASE_PASSWORD=your_password
DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
DATABASE_DIALECT=org.hibernate.dialect.MySQL8Dialect
DDL_AUTO=update
```

### Using Different Profiles

**Development Profile:**
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

**Production Profile:**
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

**Azure Profile:**
```bash
mvn spring-boot:run -Dspring.profiles.active=azure
```

## 🔐 Security Configuration

The application uses JWT-based authentication. Configure these environment variables:

```bash
JWT_SECRET=your-256-bit-secret-key
JWT_EXPIRATION=86400000  # 24 hours in milliseconds
```

## 📚 API Documentation

### Interactive Documentation
Access the Swagger UI at: `http://localhost:8080/swagger-ui.html`

### Main API Endpoints

| Endpoint Category | Base Path | Description |
|------------------|-----------|-------------|
| Authentication | `/api/auth` | Login, register, token management |
| Users | `/api/users` | User profile and management |
| Events | `/api/events` | Event CRUD operations |
| Admin | `/api/admin` | Administrative operations |
| Projects | `/api/projects` | Project management |
| Health | `/health` | Application health checks |

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token

### Event Management
- `GET /api/events` - List all events
- `POST /api/events` - Create new event
- `GET /api/events/{id}` - Get event by ID
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Classes
```bash
./mvnw test -Dtest=BackendApplicationTests
./mvnw test -Dtest=ErrorHandlingTest
```

### Test Coverage
The project includes:
- **Unit Tests**: Individual component testing
- **Integration Tests**: End-to-end API testing
- **Security Tests**: Authentication and authorization testing

## 🏗 Project Structure

```
Eventra-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/eventra/
│   │   │   ├── BackendApplication.java          # Main application class
│   │   │   ├── SecurityConfig.java              # Security configuration
│   │   │   ├── controller/                      # REST Controllers
│   │   │   │   ├── AuthController.java          # Authentication endpoints
│   │   │   │   ├── UserController.java          # User management
│   │   │   │   ├── EventController.java         # Event operations
│   │   │   │   ├── AdminController.java         # Admin operations
│   │   │   │   └── ...
│   │   │   ├── service/                         # Business logic
│   │   │   ├── repository/                      # Data access layer
│   │   │   ├── entity/                          # JPA entities
│   │   │   ├── dto/                             # Data transfer objects
│   │   │   ├── config/                          # Configuration classes
│   │   │   ├── exception/                       # Custom exceptions
│   │   │   ├── filter/                          # Security filters
│   │   │   └── util/                            # Utility classes
│   │   └── resources/
│   │       ├── application.properties           # Main configuration
│   │       ├── application-dev.properties       # Development config
│   │       ├── application-prod.properties      # Production config
│   │       └── application-azure.properties     # Azure config
│   └── test/                                    # Test classes
├── target/                                      # Compiled output
├── bin/                                         # Build scripts
├── pom.xml                                      # Maven configuration
├── mvnw, mvnw.cmd                              # Maven wrapper
├── deploy-azure.ps1                            # Azure deployment script
├── test-azure-backend.ps1                     # Azure testing script
├── AZURE_DEPLOYMENT_GUIDE.md                  # Azure deployment guide
└── README.md                                   # This file
```

## ☁️ Azure Deployment

This project is configured for Azure App Service deployment. Follow these guides:

### Quick Azure Deployment
1. **Configure Azure CLI** and login to your account
2. **Run the deployment script**:
   ```powershell
   .\deploy-azure.ps1
   ```

### Detailed Guides
- 📖 **[Azure Deployment Guide](AZURE_DEPLOYMENT_GUIDE.md)** - Complete Azure setup instructions
- 📖 **[Azure Config Template](AZURE_CONFIG_TEMPLATE.md)** - Environment configuration template
- 🛠 **[Database Migration Guide](DATABASE_MIGRATION.md)** - Database setup and migration

### Test Azure Deployment
```powershell
.\test-azure-backend.ps1
```

## 🔧 Environment Variables

### Required for Production

| Variable | Description | Example |
|----------|-------------|---------|
| `AIVEN_DATABASE_URL` | MySQL database URL | `jdbc:mysql://host:3306/db` |
| `AIVEN_DATABASE_USERNAME` | Database username | `your_username` |
| `AIVEN_DATABASE_PASSWORD` | Database password | `your_password` |
| `JWT_SECRET` | JWT signing secret | `your-256-bit-secret` |

### Optional Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | `8080` |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` |
| `DB_MAX_POOL_SIZE` | Max connection pool size | `10` |
| `SHOW_SQL` | Show SQL queries | `false` |

## 🐛 Troubleshooting

### Common Issues

**Port already in use:**
```bash
# Change port in application.properties or set environment variable
export PORT=8081
```

**Database connection failed:**
- Verify MySQL is running and accessible
- Check connection string and credentials
- Ensure database exists

**JWT token issues:**
- Verify JWT_SECRET is properly set
- Check token expiration time
- Ensure proper Authorization header format: `Bearer <token>`

### Debug Mode
Enable debug logging:
```bash
mvn spring-boot:run -Dspring.profiles.active=dev -Dlogging.level.com.eventra=DEBUG
```

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes** following the existing code style
4. **Add tests** for new functionality
5. **Run all tests**: `./mvnw test`
6. **Commit your changes**: `git commit -m 'Add amazing feature'`
7. **Push to the branch**: `git push origin feature/amazing-feature`
8. **Open a Pull Request**

### Code Style Guidelines
- Follow Java naming conventions
- Use Lombok annotations to reduce boilerplate
- Add proper JavaDoc for public methods
- Write comprehensive tests for new features
- Follow RESTful API design principles

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For questions and support:
- 📧 Create an issue in this repository
- 📚 Check the [documentation](docs/)
- 💬 Join our community discussions

## 🚀 What's Next?

Upcoming features and improvements:
- [ ] Real-time notifications
- [ ] Event analytics dashboard
- [ ] Mobile API optimization
- [ ] Advanced search capabilities
- [ ] Integration with external calendar systems

---

Built with ❤️ using Spring Boot and Java
