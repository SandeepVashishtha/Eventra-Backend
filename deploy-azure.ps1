# Azure CLI Deployment Commands

# 1. Login to Azure
az login

# 2. Deploy the JAR file (replace with your actual app name)
az webapp deploy --resource-group YOUR_RESOURCE_GROUP --name eventra-backend-dgcae3etebbag8ft --src-path backend/target/backend-0.0.1-SNAPSHOT.jar --type jar

# 3. Restart the app service
az webapp restart --resource-group YOUR_RESOURCE_GROUP --name eventra-backend-dgcae3etebbag8ft

# 4. Check deployment status
az webapp log tail --resource-group YOUR_RESOURCE_GROUP --name eventra-backend-dgcae3etebbag8ft
