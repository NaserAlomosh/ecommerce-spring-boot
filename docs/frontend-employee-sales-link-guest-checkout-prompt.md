# Frontend implementation prompt: employee social sales link and guest checkout

Use this document as a complete implementation prompt for the frontend team or a frontend coding agent.

## Feature objective

Build two connected experiences:

1. **Employee sales-link screen:** an authenticated `ADMIN` or `SUB_ADMIN` creates a secure, optionally expiring sales link and shares a frontend URL through WhatsApp, Facebook, Instagram, SMS, or another social channel.
2. **Guest storefront and checkout:** a customer opens that shared URL, browses active products, selects products and quantities, enters their name, phone number, and delivery location, and creates an order **without login or signup**.

The backend public URL is an API URL, not the final browser page. The frontend should expose a route such as:

```text
https://shop.example.com/shared-sales/{token}
```

Keep the raw token in the route and call the link-scoped APIs described below. Never place the employee access token in the shared URL.

---

## User stories

### Story 1: employee creates and shares a link

**As an** admin or sub-admin employee,  
**I want** to create a secure sales link and share it on social media,  
**so that** a customer can order products without creating an account.

#### Acceptance criteria

- Only signed-in users with `ADMIN` or `SUB_ADMIN` role can open the employee screen or call the create-link endpoint.
- The employee can choose an optional future expiration date/time.
- Creating a link displays a shareable **frontend** URL and copy/share actions.
- The raw token is shown only from the successful create response. Store it only as needed to present/share the link; do not log it or send it to analytics.
- The screen provides at least: **Copy link**, **WhatsApp**, and native **Share** (when `navigator.share` is available).
- Creating another link generates a different token.
- A backend validation or authorization error is shown without losing the employee's entered expiration value.

### Story 2: guest browses products from a shared link

**As a** customer receiving a social link,  
**I want** to see the available products and select quantities,  
**so that** I can prepare an order without logging in.

#### Acceptance criteria

- No login redirect, signup modal, access token, cart API, or customer account is required.
- The frontend extracts `{token}` from its shared-sales route and uses it on every public API call.
- Products are paginated and may be filtered by category or featured status.
- Inactive/deleted products are not offered by the API.
- Out-of-stock products are visible if returned but cannot be added; use `inStock` and `stockQuantity`.
- Quantity must be between `1` and `999` and must not exceed the currently displayed stock.
- If the link is invalid, inactive, or expired, display a dedicated “This sales link is unavailable or expired” state rather than a login prompt.

### Story 3: guest submits an order

**As a** guest customer,  
**I want** to enter my contact and delivery details and submit my selected products,  
**so that** the store can fulfill my order without requiring an account.

#### Acceptance criteria

- Require name, phone number, city, latitude, longitude, and at least one selected item.
- Allow optional area, street, additional directions, and customer note.
- Provide a map picker or “Use my location” control, but also allow coordinates to be selected manually on a map.
- Show an order review before submission, including products, quantities, effective prices, and currency.
- Disable repeated submissions while the request is running.
- After success, clear the guest's local selection and show the backend `orderNumber`, total, delivery address, and a confirmation message.
- Do not redirect the guest to authenticated customer order history because guest orders have no customer account.
- If stock or price changed, display the API error, refetch products, and allow the customer to correct the selection.

---

## Screen and route requirements

### Employee screen: “Social sales links”

Suggested route: `/admin/social-sales-links`.

Include:

- Page title and short explanation.
- Optional expiration date/time picker. It must be in the future and sent as an ISO-8601 UTC instant.
- **Create link** button.
- Read-only generated frontend URL.
- Copy confirmation feedback.
- WhatsApp share link, for example:

```text
https://wa.me/?text={URL_ENCODED_MESSAGE_AND_FRONTEND_URL}
```

- Native Web Share API support with clipboard fallback.
- A warning that anyone holding the link can use it until it expires or is deactivated.

Do not build link listing, editing, revocation, or usage-statistics controls in this version: the current backend contract exposes only link creation.

### Guest product screen

Suggested route: `/shared-sales/:token`.

Include:

- Responsive product grid.
- Product image, localized product name, effective price, old price/discount when present, stock state, and quantity selector.
- Category and featured filters when required by the existing storefront design.
- Persistent cart summary containing selected count and subtotal estimate.
- **Continue to delivery details** action.
- Empty, loading, pagination, expired-link, and general-error states.

The product price displayed for ordering should be `effectivePrice`. The backend recalculates and snapshots all prices during submission; never submit a client-calculated price.

### Guest checkout screen or step

Include:

- Full name.
- Phone number with country code guidance.
- City.
- Location picker producing latitude and longitude.
- Optional area, street, and additional directions.
- Optional customer note.
- Selected-item review.
- Consent/confirmation action and **Place order** button.

Keep the product selection in frontend state or local/session storage scoped by a hash of the sales token. Do **not** use `/api/v1/customer/cart/**`, because that API requires customer authentication. Avoid storing the raw token as a general analytics/user property.

---

## API contract

All responses use this envelope:

```json
{
  "success": true,
  "message": "Localized message",
  "data": {},
  "timestamp": "2026-07-28T12:00:00Z"
}
```

Use `Accept-Language: en` or `Accept-Language: ar` when localization is needed.

### 1. Create an employee sales link

- **Method:** `POST`
- **URL:** `/api/v1/employee/sales-links`
- **Authentication:** `Authorization: Bearer <employeeAccessToken>`
- **Allowed roles:** `ADMIN`, `SUB_ADMIN`
- **Content-Type:** `application/json`

Request without expiration:

```json
{}
```

Request with expiration:

```json
{
  "expiresAt": "2026-08-31T23:59:59Z"
}
```

`expiresAt` is optional, but when supplied it must be a future ISO-8601 instant.

Successful response example:

```json
{
  "success": true,
  "message": "Sales link created successfully",
  "data": {
    "id": 42,
    "token": "QhXa1Bf8kpcwCzVwUuNzX7JxPZQhW8pc6wG9Jr6Yf9A",
    "publicPath": "/api/v1/public/sales-links/QhXa1Bf8kpcwCzVwUuNzX7JxPZQhW8pc6wG9Jr6Yf9A/products",
    "expiresAt": "2026-08-31T23:59:59Z",
    "active": true
  },
  "timestamp": "2026-07-28T12:00:00Z"
}
```

`publicPath` points to the backend product API. For social sharing, construct the configured frontend route instead:

```ts
const frontendUrl = `${PUBLIC_WEB_ORIGIN}/shared-sales/${encodeURIComponent(data.token)}`;
```

Never assume the API host and public web host are the same.

### 2. List products through the shared link

- **Method:** `GET`
- **URL:** `/api/v1/public/sales-links/{token}/products`
- **Authentication:** none
- **Optional query parameters:** `categoryId`, `featured`, `page`, `size`, and standard Spring `sort`

Request example:

```http
GET /api/v1/public/sales-links/QhXa1Bf8kpcwCzVwUuNzX7JxPZQhW8pc6wG9Jr6Yf9A/products?page=0&size=12&featured=true
Accept-Language: en
```

Successful response example:

```json
{
  "success": true,
  "message": "Products",
  "data": {
    "content": [
      {
        "id": 101,
        "category": {
          "id": 7,
          "nameEn": "Electronics",
          "nameAr": "إلكترونيات"
        },
        "nameEn": "Wireless Headphones",
        "nameAr": "سماعات لاسلكية",
        "sku": "WH-101",
        "price": 30.000,
        "currency": "USD",
        "discountPrice": 25.000,
        "effectivePrice": 25.000,
        "discountPercentage": 16.67,
        "stockQuantity": 8,
        "lowStockThreshold": 3,
        "inStock": true,
        "lowStock": false,
        "active": true,
        "featured": true,
        "averageRating": 4.5,
        "reviewsCount": 12,
        "images": [
          {
            "id": 501,
            "imageUrl": "https://cdn.example.com/products/wh-101.jpg",
            "primaryImage": true,
            "sortOrder": 0
          }
        ]
      }
    ],
    "page": 0,
    "size": 12,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2026-07-28T12:01:00Z"
}
```

### 3. Get one product through the shared link

- **Method:** `GET`
- **URL:** `/api/v1/public/sales-links/{token}/products/{productId}`
- **Authentication:** none

Request example:

```http
GET /api/v1/public/sales-links/QhXa1Bf8kpcwCzVwUuNzX7JxPZQhW8pc6wG9Jr6Yf9A/products/101
Accept-Language: en
```

The `data` object has the same product shape shown in the list response.

### 4. Create a guest order

- **Method:** `POST`
- **URL:** `/api/v1/public/sales-links/{token}/orders`
- **Authentication:** none; do not send a customer bearer token
- **Content-Type:** `application/json`

Request rules:

| Field | Required | Validation |
|---|---:|---|
| `name` | Yes | Non-blank, maximum 150 characters |
| `phoneNumber` | Yes | Optional leading `+`, followed by 8–15 digits |
| `city` | Yes | Non-blank, maximum 100 characters |
| `latitude` | Yes | Decimal from `-90` through `90` |
| `longitude` | Yes | Decimal from `-180` through `180` |
| `area` | No | Maximum 100 characters |
| `street` | No | Maximum 255 characters |
| `additionalDirections` | No | Maximum 500 characters |
| `customerNote` | No | Maximum 1,000 characters |
| `items` | Yes | 1–100 lines |
| `items[].productId` | Yes | Positive integer |
| `items[].quantity` | Yes | Integer from 1 through 999 |

Request example:

```json
{
  "name": "Jane Doe",
  "phoneNumber": "+15551234567",
  "city": "Amman",
  "latitude": 31.9539494,
  "longitude": 35.9106350,
  "area": "Abdoun",
  "street": "Example Street, Building 12",
  "additionalDirections": "Second floor, call on arrival",
  "customerNote": "Please deliver after 5 PM",
  "items": [
    {
      "productId": 101,
      "quantity": 2
    },
    {
      "productId": 205,
      "quantity": 1
    }
  ]
}
```

If the same `productId` is accidentally included more than once, the backend combines the quantities. The frontend should still normalize the selection to one row per product.

Successful response example:

```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderNumber": "ORD-20260728-000042",
    "status": "PENDING",
    "statusDescriptionKey": "order.status.pending",
    "currency": "USD",
    "subtotal": 65.000,
    "totalAmount": 65.000,
    "totalItems": 3,
    "customerNote": "Please deliver after 5 PM",
    "failureReason": null,
    "failureReasonDescriptionKey": null,
    "failureNote": null,
    "createdAt": "2026-07-28T12:05:00Z",
    "updatedAt": "2026-07-28T12:05:00Z",
    "cancelledAt": null,
    "completedAt": null,
    "customer": null,
    "assignedDeliveryUser": null,
    "address": {
      "recipientName": "Jane Doe",
      "phoneNumber": "+15551234567",
      "city": "Amman",
      "latitude": 31.9539494,
      "longitude": 35.9106350,
      "area": "Abdoun",
      "street": "Example Street, Building 12",
      "additionalDirections": "Second floor, call on arrival"
    },
    "items": [
      {
        "productId": 101,
        "productName": "Wireless Headphones",
        "productImageUrl": "https://cdn.example.com/products/wh-101.jpg",
        "quantity": 2,
        "unitPrice": 25.000,
        "currency": "USD",
        "totalPrice": 50.000
      },
      {
        "productId": 205,
        "productName": "Phone Case",
        "productImageUrl": "https://cdn.example.com/products/case-205.jpg",
        "quantity": 1,
        "unitPrice": 15.000,
        "currency": "USD",
        "totalPrice": 15.000
      }
    ]
  },
  "timestamp": "2026-07-28T12:05:00Z"
}
```

A guest order intentionally returns `customer: null`. Use `address.recipientName` and `address.phoneNumber` on the confirmation screen.

---

## Error and edge-case behavior

Handle HTTP status and the response `message`; do not depend only on English message text.

### Invalid or expired link

The API returns a not-found response for an unknown, inactive, or expired token. Show one safe message for all cases so token validity details are not exposed:

```text
This sales link is unavailable or has expired. Ask the employee for a new link.
```

### Validation failure example

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "errors": {
      "phoneNumber": "must match the required format",
      "items": "must not be empty"
    },
    "timestamp": "2026-07-28T12:06:00Z"
  },
  "timestamp": "2026-07-28T12:06:00Z"
}
```

Map field errors to the relevant form controls and provide an accessible summary.

### Business errors to support

- Product no longer exists.
- Product became inactive.
- Requested quantity exceeds current stock.
- Product price is invalid.
- Selected products have mixed currencies.
- Link expired between product browsing and order submission.
- Network timeout or unknown server failure.

Keep the entered contact/address details after recoverable errors. For product/stock errors, refresh the catalog before allowing resubmission. For an expired link, disable checkout permanently for that token.

---

## Frontend state and API behavior

- Use one API client method for each endpoint and type the envelope and DTOs explicitly.
- Encode path parameters with `encodeURIComponent`.
- Do not attach authentication headers to public calls unless the shared HTTP client does so harmlessly; preferably use a public API client to avoid leaking credentials.
- Cancel stale product-list requests when filters or pages change.
- Prevent double clicks with an in-flight submission state and, on uncertain network failure, warn the user before retrying because the current API does not expose guest order lookup or an idempotency key.
- Treat backend totals and item snapshots as authoritative after creation.
- Support both LTR and RTL layouts and use `nameEn`/`nameAr` based on the selected locale.
- Add accessible labels, keyboard navigation, focus management for errors, sufficient contrast, and loading announcements.
- On mobile, keep the selection summary and checkout action reachable without covering form fields.

## Suggested frontend types

```ts
type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};

type CreateSalesLinkRequest = {
  expiresAt?: string;
};

type SalesLinkResponse = {
  id: number;
  token: string;
  publicPath: string;
  expiresAt: string | null;
  active: boolean;
};

type GuestOrderItemRequest = {
  productId: number;
  quantity: number;
};

type GuestOrderRequest = {
  name: string;
  phoneNumber: string;
  city: string;
  latitude: number;
  longitude: number;
  area?: string;
  street?: string;
  additionalDirections?: string;
  customerNote?: string;
  items: GuestOrderItemRequest[];
};
```

## Definition of done

- Employee role guards and create/share flow are implemented.
- Shared frontend URL opens the guest storefront and retains the token across product and checkout navigation.
- Catalog pagination, filtering, selection, stock handling, and product details work through the token-scoped endpoints.
- Guest checkout validates all required fields and supports a location picker.
- Successful orders show a confirmation with the backend order number and totals.
- No authentication or signup is required anywhere in the guest flow.
- Expired links, validation errors, stock changes, empty states, loading states, offline/network errors, LTR/RTL layouts, mobile layouts, and accessibility are covered.
- Unit/component tests cover form validation, link construction, quantity selection, and error states.
- Integration/E2E tests cover employee link creation, opening the frontend URL as a logged-out user, browsing products, and completing a guest order.
