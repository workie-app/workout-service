# Workout Service

A REST API service for managing exercises and workouts, built with Java 25 and Spring Boot 4.

## Tech Stack

- **Java 25**, **Spring Boot 4**
- **PostgreSQL** — persistence, schema managed by **Flyway**
- **JPA/Hibernate**, **MapStruct**, **Lombok**
- **OpenAPI Generator** — spec-first API contract
- **Testcontainers**, **ArchUnit** — integration and architecture testing

## Architecture

Clean Architecture with strict inward dependency rules:

```
api → application, domain
infrastructure → application, domain
application → domain
domain → (nothing)
```

```
src/main/java/org/workie/workout/
├── domain/{aggregate}/         # Entities, value objects, repository interfaces
├── application/{aggregate}/    # Use cases, command objects
├── infrastructure/
│   ├── {aggregate}/            # JPA entities, Spring Data repos, repository adapters
│   ├── config/                 # @Configuration — use cases registered as @Bean here
│   └── shared/                 # Cross-aggregate infrastructure utilities
├── api/
│   ├── {aggregate}/            # REST controllers, MapStruct API mappers
│   ├── config/                 # @RestControllerAdvice and API-layer config
│   └── shared/                 # Cross-aggregate API utilities
└── WorkoutServiceApplication.java
```

Layer boundaries are enforced at build time by **ArchUnit**.

## API

Base path: `/api`
Swagger UI: `/api/swagger-ui.html`

See [`openapi.yaml`](src/main/resources/static/openapi.yaml) for the full API specification.

## Build & Run

```bash
# Compile (required after editing openapi.yaml)
mvn clean compile

# Unit tests
mvn test

# Unit + integration tests
mvn verify

# Run locally (starts a PostgreSQL Testcontainer automatically)
mvn spring-boot:test-run
```

A running PostgreSQL instance is required for normal startup. Use `mvn spring-boot:test-run` for local development — it spins up a container automatically via `WorkoutServiceTestApplication`.

## OpenAPI Code Generation

The API contract is defined in `src/main/resources/static/openapi.yaml`. Controller interfaces and DTOs are generated from it at compile time into `target/generated-sources/` — never edit generated files directly.

After any change to the spec, run `mvn clean compile` before editing source files that depend on generated types.

## Database

Schema is managed exclusively by Flyway. The JPA `ddl-auto` is set to `validate` — never `update` or `create`. Migrations live in `src/main/resources/db/migration/`.

## Testing

- **Unit tests** (`{ClassName}Test`) — JUnit 5 + Mockito, no Spring context. Run with `mvn test`.
- **Integration tests** (`{ClassName}IT`) — Testcontainers + Spring Boot slices. Run with `mvn verify`.
- **Architecture tests** — ArchUnit validates layer boundaries on every build.
