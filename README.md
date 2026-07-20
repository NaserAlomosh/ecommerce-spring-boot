# E-commerce Spring Boot Backend

Initial foundation for a production-ready e-commerce backend built with Java 21, Spring Boot 3, Maven, MySQL, Flyway, JWT-ready Spring Security, Lombok, MapStruct, OpenAPI, JUnit 5, and Mockito.

## Package

Base package: `com.naser.ecommerce`

Created package structure:

- `config`
- `controller`
- `dto`
- `entity`
- `enums`
- `exception`
- `mapper`
- `repository`
- `security`
- `service`
- `specification`
- `util`

## Configuration

The application uses YAML configuration with `dev` and `prod` profiles. Database credentials and JWT settings are read from environment variables. Hibernate DDL auto-generation is set to `validate`; Flyway owns schema management.

Copy `.env.example` to `.env` and update secrets before running locally.

## Local development

```bash
docker compose up -d
export $(cat .env | xargs)
mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/api/v1/health
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Verification

```bash
mvn test
mvn compile
```

## Localization

API messages support English and Arabic. Send a locale in the standard `Accept-Language` header:

```bash
curl -H "Accept-Language: ar" http://localhost:8080/api/v1/health
curl -H "Accept-Language: en" http://localhost:8080/api/v1/health
```

For compatibility with clients that send the custom `accept/language` header, the backend also checks that header when `Accept-Language` is missing. Unsupported or invalid locales fall back to English.
