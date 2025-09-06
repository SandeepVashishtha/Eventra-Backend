# Azure Environment Variables Configuration
# Replace the placeholder values in your Azure App Service with actual values

## Critical Variables That Need Real Values:

### Database Configuration (REQUIRED)
AIVEN_DATABASE_URL=jdbc:mysql://YOUR_ACTUAL_AIVEN_HOST:PORT/YOUR_DATABASE_NAME?useSSL=true&requireSSL=true&verifyServerCertificate=false&useUnicode=true&characterEncoding=utf8
AIVEN_DATABASE_USERNAME=YOUR_ACTUAL_AIVEN_USERNAME  
AIVEN_DATABASE_PASSWORD=YOUR_ACTUAL_AIVEN_PASSWORD

### Security Configuration (REQUIRED)
JWT_SECRET=YOUR_STRONG_JWT_SECRET_AT_LEAST_32_CHARACTERS_LONG

### Other Required Variables
DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
DATABASE_DIALECT=org.hibernate.dialect.MySQL8Dialect
DDL_AUTO=update

### CORS Configuration (REQUIRED)
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,http://localhost:3000
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS,PATCH
CORS_ALLOWED_HEADERS=*
CORS_ALLOW_CREDENTIALS=true

## How to Update in Azure Portal:

1. Go to your Azure App Service
2. Navigate to Settings > Configuration
3. Update each application setting with the real values
4. Click "Save" to apply changes
5. The app will restart automatically

## Important Notes:
- Never commit real passwords to version control
- Generate a strong JWT secret (use a password generator for 32+ characters)
- Make sure your Aiven MySQL database is accessible
- Update CORS origins to include your actual frontend domain
