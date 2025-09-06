# Azure Backend Testing Script

Write-Host "Testing Azure Backend Deployment..." -ForegroundColor Green

$baseUrl = "https://eventra-backend-dgcae3etebbag8ft.centralindia-01.azurewebsites.net"

# Test 1: Health Check
Write-Host "1. Testing Health Endpoint..." -ForegroundColor Yellow
try {
    $health = Invoke-WebRequest -Uri "$baseUrl/health" -Method GET
    Write-Host "✅ Health Check: $($health.StatusCode)" -ForegroundColor Green
    Write-Host $health.Content
} catch {
    Write-Host "❌ Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Projects Endpoint
Write-Host "`n2. Testing Projects Endpoint..." -ForegroundColor Yellow
try {
    $projects = Invoke-WebRequest -Uri "$baseUrl/api/projects" -Method GET
    Write-Host "✅ Projects: $($projects.StatusCode)" -ForegroundColor Green
    $projectsData = $projects.Content | ConvertFrom-Json
    Write-Host "Projects Count: $($projectsData.Count)"
} catch {
    Write-Host "❌ Projects Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Authentication Endpoint
Write-Host "`n3. Testing Authentication Endpoint..." -ForegroundColor Yellow
try {
    $loginData = @{
        email = "admin@eventra.com"
        password = "admin123"
    } | ConvertTo-Json

    $auth = Invoke-WebRequest -Uri "$baseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body $loginData
    Write-Host "✅ Authentication: $($auth.StatusCode)" -ForegroundColor Green
    Write-Host "Login successful!"
} catch {
    Write-Host "❌ Authentication Failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
}

Write-Host "`nTesting Complete!" -ForegroundColor Green
