# Xora Backend

Spring Boot 4 / Java 21 backend for the Xora platform. Currently hosts two
products in a single deployable — Interview Prep and Split Vise. The
package structure is already organized so each product can be lifted into
its own microservice with minimal rewiring (see `../docs/ARCHITECTURE.md`).

## Tech Stack

| Concern              | Choice                                  |
| -------------------- | --------------------------------------- |
| Language             | Java 21                                 |
| Framework            | Spring Boot 4.0.5                       |
| Web                  | Spring Web (MVC)                        |
| Persistence          | Spring Data JPA + Hibernate             |
| Database             | PostgreSQL 14+                          |
| Migrations           | Flyway                                  |
| Security             | Spring Security + JWT (jjwt 0.12.6)     |
| Validation           | Spring Boot Starter Validation (Jakarta)|
| OAuth                | Google Sign-In (token-info verification)|
| Build                | Maven 3.9+ (wrapper included)           |
| Test                 | JUnit 5, Spring Security Test, H2       |
| Lombok               | Yes (annotation processor wired in)     |

## Project Structure

```
src/main/java/com/xora/backend/
+-- XoraBackendApplication.java     # Entry point
+-- common/                         # SHARED — exception handler, base entity, utils, request-id filter
|   +-- dto/                        # ApiErrorResponse envelope
|   +-- entity/                     # BaseAuditEntity (createdAt/updatedAt)
|   +-- exception/                  # GlobalExceptionHandler + BusinessException
|   +-- filter/                     # RequestIdFilter (correlation id)
|   +-- util/                       # SecurityUtils, MoneyUtil
+-- config/                         # SecurityConfig, CORS, Jackson, JPA auditing
+-- security/                       # JWT filter, token provider, entry point
+-- auth/                           # Register / login / refresh / Google OAuth
+-- interview/                      # Product 1: Interview Prep
|   +-- controller/                 # /api/v1/interview/**
|   +-- service/                    # QuestionService, CompanyService
|   +-- repository/                 # Spring Data JPA repos
|   +-- entity/                     # QuestionEntity, CompanyEntity
|   +-- dto/                        # Validated request/response DTOs
|   +-- mapper/                     # Hand-written entity <-> DTO mappers
+-- group/                          # Product 2: Split Vise — groups & members
+-- expense/                        # Product 2: Split Vise — expenses & splits
+-- settlement/                     # Product 2: Split Vise — settlements & debts
```

## Products

| Product       | Base Route                       | Package(s)                                   | Status |
| ------------- | -------------------------------- | -------------------------------------------- | ------ |
| Interview Prep| `/api/v1/interview/**`           | `interview`                                  | Live   |
| Split Vise    | `/api/v1/groups/**`              | `group`, `expense`, `settlement`             | Live   |
| Ecommerce     | (none — Phase 2)                 | —                                            | Hidden |

## Local Development Setup

### Prerequisites
- Java 21 JDK
- Maven 3.9+ (or use the bundled `./mvnw`)
- PostgreSQL 14+ running locally
- (Optional) A Google OAuth Client ID if you want to test Google login

### 1. Create the database

```bash
psql -U postgres -c "CREATE DATABASE xora;"
```

### 2. Copy the local properties template

```bash
cp src/main/resources/application-local.properties.example \
   src/main/resources/application-local.properties
```

Then edit `application-local.properties` with your local DB credentials
and a generated JWT secret:

```bash
# Generate a strong JWT secret
openssl rand -base64 48
```

### 3. Run the backend

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The API will be available at `http://localhost:8080`.

### 4. Run the frontend

See `../xora-frontend/README.md` for Angular dev server setup.

### 5. (Optional) Run the AI proxy

See `../xora-ai-proxy/README.md` for the Interview Prep AI proxy.

## Environment Variables

The application reads these env vars (all required in prod, none have
insecure hardcoded fallbacks anymore):

| Variable                | Purpose                                          | Required? |
| ----------------------- | ------------------------------------------------ | --------- |
| `XORA_DB_URL`           | JDBC URL (`jdbc:postgresql://host:port/db`)      | Yes       |
| `XORA_DB_USERNAME`      | DB username                                      | Yes       |
| `XORA_DB_PASSWORD`      | DB password                                      | Yes       |
| `XORA_JWT_SECRET`       | JWT signing secret (>= 32 chars). No fallback.   | Yes       |
| `XORA_JWT_ACCESS_MS`    | Access token TTL (ms). Default: 900000 (15 min)  | No        |
| `XORA_JWT_REFRESH_MS`   | Refresh token TTL (ms). Default: 604800000 (7d)  | No        |
| `XORA_CORS_ORIGINS`     | Comma-separated allowed origins                  | No        |
| `GOOGLE_CLIENT_ID`      | Google OAuth client ID (for Google Sign-In)      | Optional  |
| `DDL_AUTO`              | Hibernate ddl-auto. Default: `validate`          | No        |
| `SHOW_SQL`              | Log SQL. Default: `false`                        | No        |
| `PORT`                  | HTTP port. Default: 8080                         | No        |

For local dev, copy `application-local.properties.example` to
`application-local.properties` and override there.

## API Endpoints Overview

### Auth (`/api/v1/auth/**` — public)

| Method | Path             | Description                         |
| ------ | ---------------- | ----------------------------------- |
| POST   | `/register`      | Email + password registration       |
| POST   | `/login`         | Email + password login              |
| POST   | `/google`        | Google Sign-In (id_token)           |
| POST   | `/refresh`       | Exchange refresh token for new pair |
| GET    | `/me`            | Current user profile (auth required)|

### Interview Prep (`/api/v1/interview/**` — auth required)

| Method | Path                          | Description                          |
| ------ | ----------------------------- | ------------------------------------ |
| GET    | `/questions`                  | Paginated + filtered question list   |
| GET    | `/questions/by-company`       | Questions by company id              |
| POST   | `/questions`                  | Create a question                    |
| PUT    | `/questions/{id}`             | Update a question                    |
| POST   | `/questions/batch`            | Batch create questions               |
| GET    | `/companies`                  | All companies                        |
| POST   | `/companies`                  | Create a company                     |
| POST   | `/companies/batch`            | Batch create companies               |

### Split Vise (`/api/v1/groups/**` — auth required)

| Method | Path                                                  | Description                       |
| ------ | ----------------------------------------------------- | --------------------------------- |
| GET    | `/groups`                                             | List my groups                    |
| POST   | `/groups`                                             | Create group                      |
| GET    | `/groups/{groupId}`                                   | Group detail                      |
| PUT    | `/groups/{groupId}`                                   | Update group (admin)              |
| POST   | `/groups/{groupId}/members`                           | Add member (admin)                |
| DELETE | `/groups/{groupId}/members/{userId}`                  | Remove member                     |
| GET    | `/groups/{groupId}/users/search?q=`                   | Search users (admin)              |
| POST   | `/groups/{groupId}/expenses`                          | Create expense                    |
| GET    | `/groups/{groupId}/expenses`                          | Paginated expense list            |
| GET    | `/groups/{groupId}/expenses/{expenseId}`              | Single expense                    |
| GET    | `/groups/{groupId}/balances`                          | Net balance per member            |
| GET    | `/groups/{groupId}/debts`                             | Simplified min-cashflow debts     |
| GET    | `/groups/{groupId}/settlements`                       | List settlements                  |
| POST   | `/groups/{groupId}/settlements`                       | Create settlement                 |
| PATCH  | `/groups/{groupId}/settlements/{id}/complete`         | Mark settled                      |
| PATCH  | `/groups/{groupId}/settlements/{id}/cancel`           | Cancel settlement                 |

All errors return a consistent `ApiErrorResponse` envelope:

```json
{
  "timestamp": "2026-07-08T12:34:56.789Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "question: Question text is required",
  "path": "/api/v1/interview/questions",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

## Database Migrations

Flyway migrations live in `src/main/resources/db/migration/`:

```
V1__init_splitwise_schema.sql   # Split Vise tables (sw_ prefix, UUIDs)
V2__init_interview_schema.sql   # Interview tables (questions, companies, ...)
```

## Build & Test

```bash
# Compile
./mvnw clean compile

# Run tests
./mvnw test

# Build production JAR
./mvnw clean package -DskipTests

# Run the JAR
java -jar target/xora-backend-0.0.1-SNAPSHOT.jar
```

## Deployment Notes

- **Never commit** `application-local.properties`, real DB passwords, or JWT
  secrets. The `.gitignore` excludes the first; the rest should live in your
  deployment platform's secret manager (Heroku config vars, AWS Secrets
  Manager, Vercel env vars, etc.).
- Set `DDL_AUTO=validate` (the default) in prod so Hibernate never mutates
  the schema — Flyway owns migrations.
- Set `SHOW_SQL=false` (the default) in prod to avoid log noise.
- The JWT secret must be >= 32 characters; the app will refuse to start
  otherwise (no silent fallback).
- Configure `XORA_CORS_ORIGINS` to include your deployed frontend URL(s).

## Phase 2 Roadmap (informational)

See `../docs/ARCHITECTURE.md` for the plan to split this backend into two
separate services (`xora-interview-service` and `xora-split-service`)
with a shared `xora-common` library.
