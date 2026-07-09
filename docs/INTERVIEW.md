# Interview Backend

Backend module for storing and serving interview preparation content. It manages companies, roles, questions, tags, descriptions, difficulty, and optional rich content.

## Location

```text
xora-backend/src/main/java/com/xora/backend/interview/
```

## Package Structure

```text
interview/
+-- controller/     # REST controllers for companies and questions
+-- dto/            # Request and response DTOs
+-- entity/         # CompanyEntity and QuestionEntity
+-- repository/     # Spring Data JPA repositories
+-- service/        # Service contracts
+-- service/impl/   # Service implementations
```

## Main Responsibilities

- Create single or batch company records.
- Create single or batch interview questions.
- Link questions to companies.
- Filter questions by company name and tag.
- Return paginated question results.
- Update existing questions.

## API Reference

The Interview routes currently do not use the `/api/v1` prefix.

### Companies

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/company` | List all companies |
| `POST` | `/company` | Create one company |
| `POST` | `/company/batch` | Create multiple companies |

Example company request:

```json
{
  "name": "Google",
  "role": "Frontend Developer"
}
```

### Questions

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/questions` | Get paginated and filtered questions |
| `GET` | `/questionBy?companyId={id}` | Get questions for one company |
| `POST` | `/question` | Create one question |
| `PUT` | `/question/{id}` | Update one question |
| `POST` | `/question/batch` | Create multiple questions |

Query parameters for `/questions`:

| Parameter | Required | Default | Description |
| --- | --- | --- | --- |
| `companyName` | No | none | Filter by company |
| `tag` | No | none | Filter by tag |
| `page` | No | `0` | Page number |
| `size` | No | `5` | Page size |

Example question request:

```json
{
  "question": "Explain Angular change detection.",
  "description": "Describe how Angular detects and applies view updates.",
  "difficulty": "Intermediate",
  "contentType": "text",
  "content": "Include examples for signals and components.",
  "tags": ["Angular", "Frontend"],
  "companyId": 1
}
```

## Important Classes

| Class | Purpose |
| --- | --- |
| `CompanyController` | Company API endpoints |
| `QuestionController` | Question API endpoints |
| `CompanyServiceImpl` | Company persistence logic |
| `QuestionServiceImpl` | Question query, filter, create, and update logic |
| `CompanyEntity` | Company database entity |
| `QuestionEntity` | Question database entity |
| `PageResponseDTO` | Paginated API response wrapper |

## Frontend Integration

Frontend services call these endpoints from:

```text
app1/src/app/home/interview/
```

Related frontend documentation:

```text
../../app1/docs/INTERVIEW.md
```

## Notes

- The Interview module is separate from the Splitwise `/api/v1` APIs.
- Question creation requires a valid `companyId`.
- If a question update request includes an `id`, it must match the path id.
- Consider adding a versioned `/api/v1/interview/**` route prefix in the future for consistency.
