# Eventra Backend - MySQL Deployment Guide

## 🗄️ Database Migration: PostgreSQL → MySQL

This project has been migrated from PostgreSQL (Neon) to MySQL (Aiven) for better performance and reliability.

## 📁 Project Structure

### ✅ Files Safe for GitHub
- `azure-environment-variables.json` - Template with placeholders for Azure
- `DATABASE_MIGRATION.md` - Technical migration details
- `DEPLOYMENT_GUIDE.md` - This deployment guide
- `.env.example` - Environment template for local development
- All application properties files (using environment variables)

### 🔐 Security & Secrets
All sensitive data is managed through environment variables. No secrets are committed to the repository.

## 🚀 Deployment Instructions

### Local Development Setup
1. **Clone the repository**
2. **Run with H2 (in-memory database)**:
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=dev
   ```
3. **Access application**:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

### Azure App Service Deployment

#### Step 1: Environment Variables Configuration
Set these environment variables in Azure App Service:

```json
[
  {
    "name": "DATABASE_DIALECT",
    "value": "org.hibernate.dialect.MySQL8Dialect",
    "slotSetting": false
  },
  {
    "name": "DATABASE_DRIVER", 
    "value": "com.mysql.cj.jdbc.Driver",
    "slotSetting": false
  },
  {
    "name": "DDL_AUTO",
    "value": "update",
    "slotSetting": false
  },
  {
    "name": "JWT_SECRET",
    "value": "YOUR_JWT_SECRET_HERE",
    "slotSetting": false
  },
  {
    "name": "AIVEN_DATABASE_PASSWORD",
    "value": "YOUR_AIVEN_PASSWORD",
    "slotSetting": false
  },
  {
    "name": "AIVEN_DATABASE_URL",
    "value": "jdbc:mysql://YOUR_AIVEN_HOST:PORT/defaultdb?useSSL=true&requireSSL=true&verifyServerCertificate=false&useUnicode=true&characterEncoding=utf8",
    "slotSetting": false
  },
  {
    "name": "AIVEN_DATABASE_USERNAME",
    "value": "YOUR_AIVEN_USERNAME",
    "slotSetting": false
  },
  {
    "name": "SHOW_SQL",
    "value": "false",
    "slotSetting": false
  }
]
```

#### Step 2: Azure Portal Configuration
1. Go to **Azure Portal** → **App Service** → **Environment variables**
2. Import the JSON above or add variables manually
3. Replace placeholder values with your actual Aiven MySQL credentials
4. **Save** and **restart** the App Service

#### Step 3: Verification
Test these endpoints after deployment:
- **Health**: `https://your-app.azurewebsites.net/health`
- **API Docs**: `https://your-app.azurewebsites.net/swagger-ui.html`
- **Status**: `https://your-app.azurewebsites.net/api/status`

## 🔧 Aiven MySQL Setup

### Getting Connection Details
1. Log in to [Aiven Console](https://console.aiven.io/)
2. Navigate to your MySQL service
3. Copy connection details:
   - **Host** and **Port**
   - **Database name** (usually `defaultdb`)
   - **Username** (usually `avnadmin`)
   - **Password**

### Connection URL Format
```
jdbc:mysql://HOST:PORT/DATABASE?useSSL=true&requireSSL=true&verifyServerCertificate=false&useUnicode=true&characterEncoding=utf8
```

## 🧪 Testing & Validation

### Build Verification
```bash
# Clean build
mvn clean compile

# Package application
mvn clean package -DskipTests

# Run tests
mvn test
```

### Local Testing with Profiles
```bash
# Development (H2)
mvn spring-boot:run -Dspring.profiles.active=dev

# Production simulation (requires MySQL env vars)
mvn spring-boot:run -Dspring.profiles.active=prod

# Azure simulation
mvn spring-boot:run -Dspring.profiles.active=azure
```

## 📋 Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `AIVEN_DATABASE_URL` | MySQL connection URL | `jdbc:mysql://host:port/db?params` |
| `AIVEN_DATABASE_USERNAME` | MySQL username | `avnadmin` |
| `AIVEN_DATABASE_PASSWORD` | MySQL password | `your_password` |
| `DATABASE_DRIVER` | JDBC driver class | `com.mysql.cj.jdbc.Driver` |
| `DATABASE_DIALECT` | Hibernate dialect | `org.hibernate.dialect.MySQL8Dialect` |
| `JWT_SECRET` | JWT signing secret | `your_jwt_secret` |
| `DDL_AUTO` | Hibernate DDL mode | `update` |
| `SHOW_SQL` | Show SQL in logs | `false` |

## 🛡️ Security Best Practices

1. **Never commit secrets** to version control
2. **Use environment variables** for all sensitive configuration
3. **Rotate secrets regularly** (JWT secret, database passwords)
4. **Monitor application logs** for security issues
5. **Use HTTPS** in production (enforced in Azure)

## 🔄 Migration Summary

**Previous**: PostgreSQL (Neon)
**Current**: MySQL (Aiven)

### Changes Made:
- ✅ Updated Maven dependencies (removed PostgreSQL, kept MySQL)
- ✅ Modified all application property files for MySQL
- ✅ Updated environment variable names (`NEON_*` → `AIVEN_*`)
- ✅ Configured SSL and charset settings for Aiven
- ✅ Maintained H2 fallback for development

### Benefits:
- 🚀 **Better Performance**: MySQL optimized for web applications
- 🔒 **Enhanced Security**: Aiven provides managed SSL and security
- 🌐 **Global Availability**: Aiven's distributed infrastructure
- 💾 **Reliable Backups**: Automated backup and recovery
- 📊 **Better Monitoring**: Aiven's comprehensive dashboard

## 🆘 Troubleshooting

### Common Issues:

**Connection Timeout**
- Verify Aiven service is running
- Check firewall/network connectivity
- Validate connection URL format

**Authentication Failed**
- Verify username and password in Azure environment variables
- Check if Aiven user has proper permissions

**SSL Errors**
- Ensure connection URL includes SSL parameters
- Verify Aiven SSL certificates are valid

**Application Fails to Start**
- Check Azure App Service logs
- Verify all environment variables are set
- Ensure correct driver and dialect configuration

### Logs & Monitoring:
- **Azure**: App Service → Logs → Log stream
- **Aiven**: Console → Service → Logs tab
- **Application**: Check `/health` endpoint for status

For technical details about the migration process, see `DATABASE_MIGRATION.md`.
