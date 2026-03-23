# Workout Service

A REST API service for managing exercises and workouts, built with Java 25 and Spring Boot 4.

## Tech Stack

- **Java 25**, **Spring Boot 4**
- **PostgreSQL** — persistence, schema managed by **Flyway**
- **JPA/Hibernate**, **MapStruct**, **Lombok**
- **OpenAPI Generator** — spec-first API contract
- **Testcontainers**, **ArchUnit** — integration and architecture testing

## Architecture

The service follows the Clean Architecture principles.

### Layers & Boundaries

```
api → application, domain
infrastructure → application, domain
application → domain
domain → (nothing)
```

Layer boundaries are enforced at build time by **ArchUnit**.

### Project structure

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

## API

The API base path is: `/api`

See [`openapi.yaml`](src/main/resources/static/openapi.yaml) for the full API specification.

> Swagger UI (`/api/swagger-ui.html`) and verbose SQL/actuator logging are only available when the `dev` profile is
> active.

## Build & Run

### Commands

```bash
# Compile (required after editing openapi.yaml)
mvn clean compile

# Unit tests
mvn test

# Unit + integration tests
mvn verify
```

### Running locally

#### Dev mode — Testcontainer (recommended for development)

Starts a PostgreSQL Testcontainer automatically and activates the `dev` profile (Swagger UI, SQL logging, all actuator
endpoints):

```bash
mvn spring-boot:test-run
```

#### Normal mode — external PostgreSQL

Requires a PostgreSQL instance. The following environment variables configure the connection (shown with their
defaults):

| Variable      | Default                                    |
|---------------|--------------------------------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:5432/workout` |
| `DB_USER`     | `postgres`                                 |
| `DB_PASSWORD` | `postgres`                                 |

```bash
mvn spring-boot:run
```

In this mode, Swagger UI is disabled and only the `/api/actuator/health` endpoint is exposed.
To enable dev features, add `-Dspring-boot.run.profiles=dev` or add the env variables `SPRING_PROFILES_ACTIVE=dev`.

### Docker

#### Prerequisites

- Docker 23.0+ (BuildKit)
- Docker Compose v2 (`docker compose`, not `docker-compose`)

#### Build the image

Uses Cloud Native Buildpacks (no Dockerfile required). Produces `workie/workout-service:latest` and
`workie/workout-service:${project.version}`:

```bash
mvn spring-boot:build-image
```

> The first run downloads the Paketo builder (can be slow).

#### Configure credentials

Database credentials are read from `.env` at the project root. Defaults work out of the box:

```
DB_NAME=workout
DB_USER=postgres
DB_PASSWORD=postgres
```

Edit `.env` to override before starting.

#### Start

```bash
docker compose up -d
```

The app waits for PostgreSQL to pass its health check before starting.

#### Useful commands

```bash
docker compose logs -f workout-service   # follow app logs
docker compose up -d                     # start the stack in detached mode
docker compose stop                      # stop 
docker compose down                      # delete, keep DB volume
docker compose down -v                   # delete and wipe DB volume
```

## OpenAPI Code Generation

The API contract is defined in `src/main/resources/static/openapi.yaml`. Controller interfaces and DTOs are generated
from it at compile time into `target/generated-sources/` — never edit generated files directly.

After any change to the spec, run `mvn clean compile` before editing source files that depend on generated types.

## Database

Schema is managed exclusively by Flyway. The JPA `ddl-auto` is set to `validate` — never `update` or `create`.
Migrations live in `src/main/resources/db/migration/`.

## Testing

- **Unit tests** (`{ClassName}Test`) — JUnit 5 + Mockito, no Spring context. Run with `mvn test`.
- **Integration tests** (`{ClassName}IT`) — Testcontainers + Spring Boot slices. Run with `mvn verify`.
- **Architecture tests** — ArchUnit validates layer boundaries on every build.

### Coverage

Coverage reports are generated automatically during `mvn verify`. Three reports are produced:

| Report                      | Location                               |
|-----------------------------|----------------------------------------|
| Unit tests                  | `target/site/jacoco/index.html`        |
| Integration tests           | `target/site/jacoco-it/index.html`     |
| Merged (unit + integration) | `target/site/jacoco-merged/index.html` |

The following are excluded from all reports: generated API contract classes
(`org/openapitools/**`, `org/workie/workout/api/contract/**`) and `WorkoutServiceApplication`.
