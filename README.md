# E-commerce Spring Boot Backend

Initial foundation for a production-ready e-commerce backend built with Java 21, Spring Boot 3, Maven, MySQL, Flyway, JWT-ready Spring Security, Lombok, MapStruct, OpenAPI, JUnit 5, and Mockito.

## Package

Base package: `com.smart.ecommerce`

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


### Gmail SMTP setup

To send the verification OTP from Gmail after registration, configure SMTP with a Gmail App Password. Do **not** commit the real App Password to git; keep it in your local `.env` or deployment secrets. The application imports an optional `.env` file from the working directory, so IntelliJ run configurations should use the project root as the working directory or define these variables directly in the run configuration.

```bash
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=naseralomosh1@gmail.com
SMTP_PASSWORD=your_16_character_gmail_app_password_without_spaces
SMTP_FROM=naseralomosh1@gmail.com
SMTP_TLS=true
SMTP_SSL=false
SMTP_CONNECTION_TIMEOUT=10000
SMTP_TIMEOUT=60000
SMTP_WRITE_TIMEOUT=60000
SMTP_STARTTLS_REQUIRED=true
SMTP_RETRY_MAX_ATTEMPTS=2
SMTP_RETRY_BACKOFF_MILLIS=1000
```

If these variables are not loaded, Spring Mail falls back to `localhost:25`, which causes `Connection refused` unless a local SMTP server is running. Gmail can also intermittently take longer than a few seconds to return an SMTP response after STARTTLS/authentication/message submission; keep the read/write timeouts above the old 5-second default and allow a small retry count for transient `SocketTimeoutException` failures. If sending still fails, registration fails and the server logs the SMTP error instead of silently ignoring it.

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


## Authentication & User Management

This module provides the e-commerce identity foundation for exactly three roles: `ADMIN`, `CUSTOMER`, and `DELIVERY`. Public registration is restricted to `CUSTOMER`; administrator endpoints create `ADMIN`, `DELIVERY`, or additional `CUSTOMER` accounts.

### Registration and email verification flow
1. `POST /api/v1/auth/register` creates a `CUSTOMER` with `PENDING` status, `emailVerified=false`, and `phoneVerified=false`. The request never accepts a role.
2. The backend generates a cryptographically secure six-digit OTP, stores only its BCrypt hash in `email_otps`, and sends it through the configured email service.
3. `POST /api/v1/auth/verify-email` consumes the OTP, marks the user `ACTIVE`, and sets `emailVerified=true`. Expired, consumed, or over-attempt OTPs are rejected.
4. `POST /api/v1/auth/resend-email-otp` invalidates prior email-verification OTPs before sending a replacement.

### Forgot password flow
`POST /api/v1/auth/forgot-password` never reveals whether an email exists. For existing users it sends a `PASSWORD_RESET` OTP. `POST /api/v1/auth/verify-password-reset-otp` consumes the OTP and returns a temporary reset token whose hash is stored in `password_reset_tokens`. `POST /api/v1/auth/reset-password` validates that token, changes the BCrypt password hash, consumes the token, and revokes all refresh tokens.

### JWT and refresh-token rotation
`POST /api/v1/auth/login` returns a JWT access token and a refresh token. Refresh tokens are stored only as SHA-256 hashes in `refresh_tokens`. `POST /api/v1/auth/refresh` revokes the presented active token and creates a replacement; expired, revoked, or reused refresh tokens are rejected and reuse revokes the user's outstanding refresh tokens. Logout revokes the supplied refresh token. Changing a password or blocking a user revokes all refresh tokens.

### Social login
`POST /api/v1/auth/social-login` supports `GOOGLE` and `APPLE` for customers only. Provider information is stored in `social_accounts`; `googleId` and `appleId` are intentionally not stored on `users`. Verified provider emails can create or link a `CUSTOMER`; unverified provider emails, existing non-customer accounts, and inactive accounts are rejected. Production deployments must verify issuer, audience, signature, expiration, and nonce server-side for the supplied identity token.

### Required environment variables
- `JWT_SECRET` - HMAC signing secret for access tokens.
- `JWT_EXPIRATION_MILLIS` - access-token lifetime, default `900000`.
- `REFRESH_TOKEN_EXPIRATION` - refresh-token lifetime, default `30d`.
- `OTP_EXPIRATION` - OTP lifetime, default `5m`.
- `OTP_MAX_ATTEMPTS` - maximum verification attempts, default `5`.
- `OTP_RESEND_COOLDOWN` - resend cooldown, default `1m`.
- `PASSWORD_RESET_TOKEN_EXPIRATION` - temporary reset-token lifetime, default `15m`.
- `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_FROM`, `SMTP_TLS` - SMTP settings for Spring Mail.
- `GOOGLE_AUDIENCE` - accepted Google OAuth client ID/audience.
- `APPLE_AUDIENCE` - accepted Apple Services ID/bundle audience.

### Implemented endpoints
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `POST /api/v1/auth/verify-email`
- `POST /api/v1/auth/resend-email-otp`
- `POST /api/v1/auth/forgot-password`
- `POST /api/v1/auth/verify-password-reset-otp`
- `POST /api/v1/auth/reset-password`
- `POST /api/v1/auth/social-login`
- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`
- `PUT /api/v1/users/me/password`
- `POST /api/v1/admin/users`
- `GET /api/v1/admin/users`
- `GET /api/v1/admin/users/{id}`
- `PATCH /api/v1/admin/users/{id}/status`

### MySQL/Flyway
Migration `V2__auth_user_management.sql` creates `users`, `refresh_tokens`, `email_otps`, `password_reset_tokens`, and `social_accounts` using snake_case names, unique constraints, indexes, and foreign keys.

### API localization headers
All API responses can be returned in English or Arabic by sending `Accept-Language`. Use `en` for English or `ar` for Arabic. Authenticated endpoints also require an `Authorization: Bearer <accessToken>` header.

Common headers:

```http
Content-Type: application/json
Accept: application/json
Accept-Language: en
```

Arabic example:

```http
Content-Type: application/json
Accept: application/json
Accept-Language: ar
```

### Authentication API examples

#### Register customer

```http
POST /api/v1/auth/register
Content-Type: application/json
Accept-Language: en
```

```json
{
  "firstName": "Sara",
  "lastName": "Ali",
  "email": "sara@example.com",
  "phoneNumber": "+962790000001",
  "password": "Str0ngPassword!"
}
```

#### Login

```http
POST /api/v1/auth/login
Content-Type: application/json
Accept-Language: ar
```

```json
{
  "email": "sara@example.com",
  "password": "Str0ngPassword!"
}
```

#### Refresh token

```http
POST /api/v1/auth/refresh
Content-Type: application/json
Accept-Language: en
```

```json
{
  "refreshToken": "refresh-token-from-login"
}
```

#### Logout

```http
POST /api/v1/auth/logout
Content-Type: application/json
Authorization: Bearer <accessToken>
Accept-Language: en
```

```json
{
  "refreshToken": "active-refresh-token"
}
```

#### Verify email OTP

```http
POST /api/v1/auth/verify-email
Content-Type: application/json
Accept-Language: ar
```

```json
{
  "email": "sara@example.com",
  "otp": "123456"
}
```

#### Resend email OTP

```http
POST /api/v1/auth/resend-email-otp
Content-Type: application/json
Accept-Language: en
```

```json
{
  "email": "sara@example.com"
}
```

#### Forgot password

```http
POST /api/v1/auth/forgot-password
Content-Type: application/json
Accept-Language: en
```

```json
{
  "email": "sara@example.com"
}
```

#### Verify password reset OTP

```http
POST /api/v1/auth/verify-password-reset-otp
Content-Type: application/json
Accept-Language: ar
```

```json
{
  "email": "sara@example.com",
  "otp": "123456"
}
```

#### Reset password

```http
POST /api/v1/auth/reset-password
Content-Type: application/json
Accept-Language: en
```

```json
{
  "resetToken": "temporary-reset-token",
  "newPassword": "N3wStrongPassword!"
}
```

#### Social login

```http
POST /api/v1/auth/social-login
Content-Type: application/json
Accept-Language: en
```

```json
{
  "provider": "GOOGLE",
  "identityToken": "provider-id-token",
  "nonce": "optional-client-nonce"
}
```

### Profile API examples

#### Get my profile

```http
GET /api/v1/users/me
Authorization: Bearer <accessToken>
Accept-Language: ar
```

#### Update my profile

```http
PUT /api/v1/users/me
Content-Type: application/json
Authorization: Bearer <accessToken>
Accept-Language: en
```

```json
{
  "firstName": "Sara",
  "lastName": "Hassan",
  "phoneNumber": "+962790000002",
  "profileImage": "https://cdn.example.com/profiles/sara.png"
}
```

#### Change my password

```http
PUT /api/v1/users/me/password
Content-Type: application/json
Authorization: Bearer <accessToken>
Accept-Language: en
```

```json
{
  "currentPassword": "Str0ngPassword!",
  "newPassword": "N3wStrongPassword!"
}
```

### Admin API examples

#### Create user

```http
POST /api/v1/admin/users
Content-Type: application/json
Authorization: Bearer <adminAccessToken>
Accept-Language: ar
```

```json
{
  "firstName": "Omar",
  "lastName": "Saleh",
  "email": "driver@example.com",
  "phoneNumber": "+962790000003",
  "password": "Str0ngPassword!",
  "role": "DELIVERY"
}
```

#### Search users

```http
GET /api/v1/admin/users?q=sara&role=CUSTOMER&status=ACTIVE&page=0&size=20&sort=createdAt,desc
Authorization: Bearer <adminAccessToken>
Accept-Language: en
```

#### Get user by ID

```http
GET /api/v1/admin/users/1
Authorization: Bearer <adminAccessToken>
Accept-Language: en
```

#### Update user status

```http
PATCH /api/v1/admin/users/1/status
Content-Type: application/json
Authorization: Bearer <adminAccessToken>
Accept-Language: ar
```

```json
{
  "status": "BLOCKED"
}
```
