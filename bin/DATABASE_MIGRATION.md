# Database Migration: PostgreSQL (Neon) to MySQL (Aiven)

This document outlines the migration from PostgreSQL (Neon) to MySQL (Aiven) for the Eventra backend.

## Changes Made

### 1. Dependencies (pom.xml)
- ✅ Removed PostgreSQL driver dependency
- ✅ Kept MySQL connector (already present)
- ✅ Kept H2 for development fallback

### 2. Application Properties Files Updated

#### Main Configuration (`application.properties`)
- Changed environment variable names from `NEON_*` to `AIVEN_*`
- Updated MySQL-specific settings
- Replaced PostgreSQL-specific configurations

#### Production Configuration (`application-prod.properties`)
- Updated for Aiven MySQL connection
- Changed driver to `com.mysql.cj.jdbc.Driver`
- Set dialect to `org.hibernate.dialect.MySQL8Dialect`
- Added MySQL-specific timezone and connection settings

#### Azure Configuration (`application-azure.properties`)
- Updated database configuration for MySQL
- Maintained Azure-specific settings
- Updated environment variable references

#### New MySQL Profile (`application-mysql.properties`)
- Created dedicated MySQL configuration
- Optimized connection pool settings for MySQL
- Added SSL and charset configurations for Aiven

## Environment Variables Required

When deploying to production, set these environment variables:

```bash
# Aiven MySQL Database
AIVEN_DATABASE_URL=jdbc:mysql://your-aiven-host:port/database_name?useSSL=true&requireSSL=true&verifyServerCertificate=false
AIVEN_DATABASE_USERNAME=your_username
AIVEN_DATABASE_PASSWORD=your_password

# For production deployment, also set:
DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
DATABASE_DIALECT=org.hibernate.dialect.MySQL8Dialect
```

## Aiven MySQL Connection Setup

### 1. Get Connection Details from Aiven Console
1. Log in to [Aiven Console](https://console.aiven.io/)
2. Navigate to your MySQL service
3. Get the connection details:
   - Host
   - Port
   - Database name
   - Username
   - Password

### 2. Construct Connection URL
```
jdbc:mysql://HOST:PORT/DATABASE_NAME?useSSL=true&requireSSL=true&verifyServerCertificate=false&useUnicode=true&characterEncoding=utf8
```

### 3. Example Environment Variables
```bash
AIVEN_DATABASE_URL=jdbc:mysql://mysql-abc123-def456.a.aivencloud.com:12345/defaultdb?useSSL=true&requireSSL=true&verifyServerCertificate=false&useUnicode=true&characterEncoding=utf8
AIVEN_DATABASE_USERNAME=avnadmin
AIVEN_DATABASE_PASSWORD=your_secure_password
```

## Running with Different Profiles

### Development (H2 in-memory)
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### MySQL (Aiven)
```bash
mvn spring-boot:run -Dspring.profiles.active=mysql
```

### Production
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

### Azure Deployment
```bash
mvn spring-boot:run -Dspring.profiles.active=azure
```

## Important Notes

1. **SSL Required**: Aiven MySQL requires SSL connections
2. **Timezone**: Configured to use UTC timezone
3. **Character Encoding**: Set to UTF-8 for proper character support
4. **Connection Pooling**: Optimized for MySQL performance
5. **Entity Compatibility**: All existing JPA entities are compatible with MySQL

## Testing the Migration

1. Set up your Aiven MySQL instance
2. Configure environment variables
3. Run the application with MySQL profile
4. Verify database connection and table creation
5. Test API endpoints to ensure data persistence works correctly

## Rollback Plan

If needed to rollback to PostgreSQL:
1. Restore PostgreSQL dependency in `pom.xml`
2. Revert environment variables to `NEON_*` format
3. Update properties files to use PostgreSQL driver and dialect
4. The entities will work with both databases

## Data Migration (if needed)

If you have existing data in PostgreSQL that needs to be migrated:
1. Export data from PostgreSQL using `pg_dump`
2. Transform the SQL for MySQL compatibility
3. Import data into Aiven MySQL instance
4. Verify data integrity after migration
