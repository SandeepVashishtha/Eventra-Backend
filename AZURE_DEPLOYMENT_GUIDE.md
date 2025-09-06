# Azure Deployment Guide for Eventra Backend

## Issues Identified:
1. **Database Configuration**: Placeholder values in environment variables
2. **Authentication Endpoints**: CGI application errors
3. **Empty Projects**: Database connection problems

## Steps to Fix Azure Backend:

### 1. Update Environment Variables in Azure
You need to replace the placeholder values in your Azure App Service configuration:

```json
{
  "name": "AIVEN_DATABASE_URL",
  "value": "jdbc:mysql://YOUR_ACTUAL_AIVEN_HOST:PORT/YOUR_DATABASE_NAME?useSSL=true&requireSSL=true&verifyServerCertificate=false&useUnicode=true&characterEncoding=utf8"
}
```

**Replace with your actual Aiven MySQL connection details.**

### 2. Set Required Environment Variables
Ensure these are configured in Azure App Service:

1. **Database Configuration:**
   - `AIVEN_DATABASE_URL`: Your actual Aiven MySQL connection string
   - `AIVEN_DATABASE_USERNAME`: Your actual Aiven username
   - `AIVEN_DATABASE_PASSWORD`: Your actual Aiven password
   - `DATABASE_DRIVER`: `com.mysql.cj.jdbc.Driver`
   - `DATABASE_DIALECT`: `org.hibernate.dialect.MySQL8Dialect`

2. **Security Configuration:**
   - `JWT_SECRET`: A strong secret key (at least 32 characters)

3. **CORS Configuration:**
   - `CORS_ALLOWED_ORIGINS`: Your frontend URL(s)

### 3. Build and Deploy Updated Backend

Run these commands to build and deploy:

```powershell
# Build the updated backend
cd backend
.\mvnw.cmd clean package -DskipTests

# The JAR file will be created at: target/backend-0.0.1-SNAPSHOT.jar
```

### 4. Deploy to Azure
You can deploy using:
- Azure CLI
- GitHub Actions
- Manual upload via Azure Portal

### 5. Test the Deployment
After deployment, test these endpoints:
- Health: `https://your-app.azurewebsites.net/health`
- Projects: `https://your-app.azurewebsites.net/api/projects`
- Login: `https://your-app.azurewebsites.net/api/auth/login`

## Frontend Configuration for Azure
Update your `.env` file to point to the Azure backend:

```properties
REACT_APP_API_URL=https://eventra-backend-dgcae3etebbag8ft.centralindia-01.azurewebsites.net/api
```
