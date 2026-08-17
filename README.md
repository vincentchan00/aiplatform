# AI Platform Backend

Spring Boot multi-tenant backend. Stage 2 adds tenant-aware JWT authentication,
validation, BCrypt password storage, stateless Spring Security, and unit tests.

## Run

Requirements: Java 21 and PostgreSQL with an `ai_platform` database.

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/ai_platform"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "root"
$env:JWT_SECRET = "replace-with-a-random-secret-of-at-least-32-bytes"
.\mvnw.cmd spring-boot:run
```

## Authentication flow

Create a tenant (this bootstrap endpoint is public):

```http
POST /api/tenants
Content-Type: application/json

{"name":"demo"}
```

Register a user using the returned tenant ID:

```http
POST /api/auth/register
Content-Type: application/json

{"email":"user@example.com","password":"password123","tenantId":"<tenant-uuid>"}
```

Log in with the same email, password, and tenant ID:

```http
POST /api/auth/login
Content-Type: application/json

{"email":"user@example.com","password":"password123","tenantId":"<tenant-uuid>"}
```

Send the returned token to protected endpoints:

```http
Authorization: Bearer <token>
```

Run tests with `.\mvnw.cmd test`.
