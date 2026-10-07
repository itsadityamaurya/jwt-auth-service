# JWT Auth Service

Production-style **authentication and authorization service** built with Spring Boot 3, Spring Security 6 and JWT.
Stateless, role-based (USER / ADMIN), documented with Swagger, covered by unit and integration tests.

## Features
- Register / login with BCrypt-hashed passwords
- JWT access tokens (HS256, configurable expiry) via a custom `OncePerRequestFilter`
- Role-based access control (`/api/admin/**` is ADMIN only)
- Clean JSON error responses: 400 validation, 401, 403, 409
- Swagger UI with Bearer-token support
- H2 in-memory by default, MySQL via `mysql` profile
- Dockerfile included
- Tests: JWT unit tests + full MockMvc integration flow

## Tech
Java 17, Spring Boot 3.3, Spring Security, Spring Data JPA, jjwt 0.12, MySQL/H2, springdoc-openapi, JUnit 5

## Run
```bash
mvn spring-boot:run                      # H2, zero setup
mvn spring-boot:run -Dspring-boot.run.profiles=mysql   # MySQL (set DB_URL, DB_USER, DB_PASSWORD)
mvn test
```
Swagger UI: http://localhost:8080/swagger-ui.html

A seeded admin is created on start: `admin@example.com` / `Admin@12345` (change in `application.yml`; set `JWT_SECRET` env var in production).

## API
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/users/me` | Any logged-in user |
| GET | `/api/admin/users` | ADMIN |

```bash
curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"name":"Aditya","email":"aditya@example.com","password":"Password@123"}'

TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"aditya@example.com","password":"Password@123"}' | jq -r .token)

curl localhost:8080/api/users/me -H "Authorization: Bearer $TOKEN"
```

## Design notes
- Request flow: `JwtAuthFilter` validates the token, loads the user, sets the `SecurityContext`.
- `AppConfig` is separate from `SecurityConfig` to avoid circular bean dependencies.
- Emails are normalized to lowercase; passwords never leave the service.
- Possible next steps: refresh tokens, token blacklist (Redis), rate limiting on login.
