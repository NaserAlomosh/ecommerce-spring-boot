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

This module provides the e-commerce identity foundation for four roles: `ADMIN`, `SUB_ADMIN`, `CUSTOMER`, and `DELIVERY`. Public registration is restricted to `CUSTOMER`; administrators create `ADMIN`, `SUB_ADMIN`, `DELIVERY`, or additional `CUSTOMER` accounts. `ADMIN` users can create other admins and sub-admins, reset passwords for sub-admins and drivers, and delete sub-admin, driver, and customer accounts. `SUB_ADMIN` users can create, block, and delete driver and customer accounts only.

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

### Creating the first admin account
Set the bootstrap variables before the first startup. If `BOOTSTRAP_ADMIN_ENABLED=true` and there is no active admin, the application creates one active, email-verified `ADMIN` account automatically. Disable the flag after the first successful startup.

```bash
BOOTSTRAP_ADMIN_ENABLED=true
BOOTSTRAP_ADMIN_EMAIL=admin@example.com
BOOTSTRAP_ADMIN_PASSWORD=Str0ngPassword!
BOOTSTRAP_ADMIN_FIRST_NAME=System
BOOTSTRAP_ADMIN_LAST_NAME=Admin
BOOTSTRAP_ADMIN_PHONE=+962790000000
```

Then log in through `POST /api/v1/auth/login` and use the returned access token with `/api/v1/admin/users`.

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
- `PATCH /api/v1/admin/users/{id}/password`
- `DELETE /api/v1/admin/users/{id}`

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

#### Change a managed user password

```http
PATCH /api/v1/admin/users/2/password
Content-Type: application/json
Authorization: Bearer <adminAccessToken>
Accept-Language: en
```

```json
{
  "newPassword": "N3wStrongPassword!"
}
```

#### Delete a managed user

```http
DELETE /api/v1/admin/users/2
Authorization: Bearer <adminOrSubAdminAccessToken>
Accept-Language: ar
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

## Product image storage

Admin product creation accepts `multipart/form-data` at `POST /api/v1/admin/products` with a JSON `product` part and zero or more `images` parts. Uploaded images are stored on the local filesystem under `${FILE_UPLOAD_DIR:uploads}/products` and exposed publicly at `/uploads/products/{fileName}`. Only JPEG, PNG, and WebP uploads are accepted; files are size-limited by `MAX_IMAGE_SIZE` (default `5MB`) and each product is limited by `MAX_IMAGES_PER_PRODUCT` (default `10`).

Image binary data is never stored in MySQL. The `product_images` table stores only the generated public URL and storage path. File names are generated with UUID values and safe extensions derived from validated content types. Product soft deletion retains image files for audit/restoration; physical files are deleted when an image record is explicitly deleted or replaced.

## Customer Cart and Wishlist API

All customer cart and wishlist endpoints require an authenticated JWT with role `CUSTOMER` (`ROLE_CUSTOMER` authority). Requests never accept `customerId`; the customer is derived from the bearer token. Product names and responses include English and Arabic fields (`nameEn`, `nameAr`).

### Cart endpoints

| Method | Endpoint | Role | Description |
| --- | --- | --- | --- |
| `GET` | `/api/v1/customer/cart` | `CUSTOMER` | Return the active cart, recalculating prices and availability. |
| `POST` | `/api/v1/customer/cart/items` | `CUSTOMER` | Add a product or increase its quantity. |
| `PATCH` | `/api/v1/customer/cart/items/{cartItemId}` | `CUSTOMER` | Replace a cart item quantity. |
| `DELETE` | `/api/v1/customer/cart/items/{cartItemId}` | `CUSTOMER` | Remove one item from the current customer's cart. |
| `DELETE` | `/api/v1/customer/cart/items` | `CUSTOMER` | Clear all active cart items. |

Add item request:

```json
{ "productId": 1, "quantity": 2 }
```

Update quantity request:

```json
{ "quantity": 3 }
```

Cart response example:

```json
{
  "success": true,
  "message": "Cart",
  "data": {
    "id": 10,
    "status": "ACTIVE",
    "items": [
      {
        "id": 20,
        "product": {
          "id": 1,
          "nameEn": "Coffee",
          "nameAr": "قهوة",
          "sku": "COF-1",
          "primaryImageUrl": "/uploads/products/coffee.png",
          "active": true,
          "inStock": true,
          "availableStock": 8
        },
        "quantity": 2,
        "unitPrice": 10.00,
        "discountPrice": 8.00,
        "effectivePrice": 8.00,
        "lineTotal": 16.00,
        "available": true,
        "quantityExceedsStock": false
      }
    ],
    "totalItems": 2,
    "distinctItems": 1,
    "subtotal": 16.00
  }
}
```

Validation and business rules: `productId` is required, `quantity` is required and must be at least `1`, prices are always loaded from the database, inactive/deleted/out-of-stock products cannot be added or updated, and requested quantity cannot exceed current stock. Common errors include `Product not found`, `Product is inactive`, `Product is unavailable`, `Insufficient stock`, and `Cart item not found`.

### Wishlist endpoints

| Method | Endpoint | Role | Description |
| --- | --- | --- | --- |
| `GET` | `/api/v1/customer/wishlist` | `CUSTOMER` | List wishlist items with current product availability. |
| `POST` | `/api/v1/customer/wishlist` | `CUSTOMER` | Add a product idempotently. |
| `DELETE` | `/api/v1/customer/wishlist/{productId}` | `CUSTOMER` | Remove a product from the wishlist. |
| `GET` | `/api/v1/customer/wishlist/check/{productId}` | `CUSTOMER` | Return whether the product is wishlisted. |

Add wishlist item request:

```json
{ "productId": 1 }
```

Wishlist response example:

```json
{
  "success": true,
  "message": "Wishlist",
  "data": {
    "items": [
      {
        "id": 5,
        "product": {
          "id": 1,
          "categoryId": 2,
          "nameEn": "Coffee",
          "nameAr": "قهوة",
          "sku": "COF-1",
          "price": 10.00,
          "discountPrice": 8.00,
          "effectivePrice": 8.00,
          "discountPercentage": 20.00,
          "primaryImageUrl": "/uploads/products/coffee.png",
          "active": true,
          "inStock": true,
          "availableStock": 8
        },
        "createdAt": "2026-07-20T00:00:00Z"
      }
    ],
    "totalItems": 1
  }
}
```

Check response example:

```json
{ "wishlisted": true }
```

Validation and business rules: `productId` is required, deleted products are treated as not found, adding the same product twice is idempotent, and inactive or out-of-stock products may remain in the wishlist while exposing current `active`, `inStock`, and `availableStock` fields. Common errors include `Product not found` and `Wishlist item not found`; non-`CUSTOMER` roles receive `403 Forbidden`.
