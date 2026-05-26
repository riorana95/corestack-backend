# CoreStack Backend

Spring Boot API for CoreStack. This backend supports the Interview preparation product and the Splitwise-style expense sharing product.

## Backend Products

| Product | Main Package | Documentation |
| --- | --- | --- |
| Interview | `com.corestack.backend.interview` | [docs/INTERVIEW.md](docs/INTERVIEW.md) |
| Splitwise | `auth`, `group`, `expense`, `settlement` | [../SPLITWISE.md](../SPLITWISE.md) |

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web / Web MVC
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT using `jjwt`
- Maven wrapper
- JUnit, Spring Security Test, and H2 for tests

## Project Structure

```text
src/main/java/com/corestack/backend/
+-- auth/            # Register, login, refresh token, user profile
+-- common/          # Shared DTOs, exceptions, utilities, audit entity
+-- config/          # Security, CORS, Jackson, JPA auditing
+-- expense/         # Splitwise expenses, split logic, balances
+-- group/           # Splitwise groups and members
+-- interview/       # Interview companies and questions
+-- security/        # JWT filter, token provider, user principal
+-- settlement/      # Splitwise debts and settlements
```

## Local Setup

Copy the local properties example:

```powershell
copy src\main\resources\application-local.properties.example src\main\resources\application-local.properties
```

Set your local database and JWT values:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE

app.jwt.secret=local-dev-jwt-secret-minimum-32-characters-long
```

Do not commit `application-local.properties` or real secrets.

## Run Backend

```powershell
.\mvnw.cmd spring-boot:run
```

Default URL:

```text
http://localhost:8080
```

## Test

```powershell
.\mvnw.cmd test
```

## Build

```powershell
.\mvnw.cmd clean package
```

Build output:

```text
target/
```

## API Groups

| Area | Base Routes |
| --- | --- |
| Auth | `/api/v1/auth/**`, including local login/register and Google Sign-In |
| Splitwise Groups | `/api/v1/groups/**` |
| Splitwise Expenses | `/api/v1/groups/{groupId}/expenses/**` |
| Splitwise Settlements | `/api/v1/groups/{groupId}/settlements/**` |
| Interview Companies | `/company`, `/company/batch` |
| Interview Questions | `/questions`, `/questionBy`, `/question`, `/question/{id}`, `/question/batch` |

## Database

Flyway migrations are in:

```text
src/main/resources/db/migration/
```

Current migration:

```text
V1__init_splitwise_schema.sql
```

## Deployment Notes

- Configure production database credentials through platform secrets or environment variables.
- Configure `app.jwt.secret` with a strong secret.
- Configure `app.cors.allowed-origins` for the deployed frontend URL.
- Run tests before deployment.
- Never commit real DB passwords, AWS credentials, private keys, or JWT secrets.
