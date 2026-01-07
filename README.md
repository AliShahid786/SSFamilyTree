# Family Tree - Person CRUD (Spring Boot)

A clean Spring Boot backend providing CRUD REST APIs for a Person entity in a family hierarchy. Focused only on basic persistence and retrieval—no authentication, authorization, or relationship logic.

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven
- Jakarta Bean Validation
- Lombok (optional, not used by default)

## Build
```powershell
mvn -f .\pom.xml clean package -DskipTests
```

## Run (with PostgreSQL)
Configure database via environment variables (or edit `src/main/resources/application.properties`). Defaults are shown below:
- `DB_URL` (default: `jdbc:postgresql://localhost:5432/familytree`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: `postgres`)

```powershell
# Example (PowerShell)
$env:DB_URL = "jdbc:postgresql://localhost:5432/familytree"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "postgres"

mvn -f .\pom.xml spring-boot:run
```

The app starts on `http://localhost:8080`.

## Entity Model
`Person` (table: `persons`)
- `id` (Long, PK, auto-generated)
- `firstName` (String, required)
- `lastName` (String)
- `gender` (Enum: `MALE`, `FEMALE`)
- `dateOfBirth` (LocalDate)
- `father` (self-referencing ManyToOne, LAZY, `father_id`, nullable)
- `mother` (self-referencing ManyToOne, LAZY, `mother_id`, nullable)

Notes:
- No `@OneToMany` mappings are defined.
- Parents are mapped with `@JoinColumn` and `FetchType.LAZY`.

## DTOs
Request: `PersonRequest`
- `firstName` (required)
- `lastName`
- `gender`
- `dateOfBirth`
- `fatherId` (Long)
- `motherId` (Long)

Response: `PersonResponse`
- `id`
- `firstName`
- `lastName`
- `gender`
- `dateOfBirth`
- `fatherId`
- `motherId`

## API Endpoints
Base path: `/api/persons`

- POST `/api/persons` — Create
  - Status: `201 Created`
  - Body (JSON):
    ```json
    {
      "firstName": "John",
      "lastName": "Doe",
      "gender": "MALE",
      "dateOfBirth": "1990-01-01",
      "fatherId": 1,
      "motherId": 2
    }
    ```

- PUT `/api/persons/{id}` — Update
  - Status: `200 OK`

- GET `/api/persons/{id}` — Get by ID
  - Status: `200 OK` (or `404 Not Found`)

- GET `/api/persons?name={query}` — Search by first name (contains, case-insensitive)
  - If `name` is omitted or blank, returns all persons
  - Status: `200 OK`

- DELETE `/api/persons/{id}` — Delete
  - Status: `204 No Content` (or `404 Not Found`)

## Implementation Notes
- Manual DTO ↔ Entity mapping (no mapping frameworks).
- `getReferenceById()` is used to associate `father`/`mother` lazily by ID.
- `EntityNotFoundException` (runtime) is thrown when a person is not found.
- `spring.jpa.open-in-view=false` and DTO responses prevent circular JSON.
- No authentication/authorization or relationship computation is included.

## Package Structure
- `controller` — REST controllers
- `service` — Service interfaces and implementations
- `repository` — Spring Data repositories
- `model` — JPA entities and enums
- `dto` — Request/Response DTOs

## Troubleshooting
- Ensure PostgreSQL is running and reachable with the configured credentials.
- The database and user must exist; schema tables are created by Hibernate (`ddl-auto=update`).
- For date formats, use ISO-8601 (e.g., `YYYY-MM-DD`).

