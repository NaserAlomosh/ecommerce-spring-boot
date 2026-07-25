# Frontend implementation prompt: unrestricted admin order statuses

Implement the admin order-status UI against the backend contract below. The admin (including `SUB_ADMIN`) has full access to move an order from **any current status to any target status**, including reopening `CANCELLED`, `COMPLETED`, or `FAILED` orders. Do not enforce a frontend transition matrix.

## Endpoint

- Method: `PATCH`
- URL: `/api/v1/admin/orders/{orderNumber}/status`
- Authentication: `Authorization: Bearer <accessToken>` with role `ADMIN` or `SUB_ADMIN`
- Optional localization: `Accept-Language: en` or `ar`
- Content type: `application/json`

## Request

```json
{
  "status": "COMPLETED",
  "note": "Resolved manually by support",
  "failureReason": null,
  "failureNote": null
}
```

`status` is required and must be one of `PENDING`, `PROCESSING`, `OUT_FOR_DELIVERY`, `COMPLETED`, `FAILED`, or `CANCELLED`. `note`, `failureReason`, and `failureNote` are optional strings; notes have a maximum length of 500 characters. Valid `failureReason` values are `CUSTOMER_NOT_AVAILABLE`, `CUSTOMER_REFUSED_ORDER`, `WRONG_ADDRESS`, `DAMAGED_PACKAGE`, `DELIVERY_VEHICLE_ISSUE`, and `OTHER`. Send failure fields only when useful for `FAILED`; the backend clears them for every other target status.

The same endpoint is used for cancellation and rollback. For example, send `{"status":"CANCELLED","note":"Customer requested cancellation"}` to cancel, then `{"status":"PROCESSING","note":"Cancellation reversed"}` to reopen it. Inventory is restored when entering `CANCELLED` and reserved again when leaving it. If there is not enough stock to reopen, display the backend validation error and keep the existing status in the UI.

## Successful response

```json
{
  "success": true,
  "message": "Order status updated successfully",
  "timestamp": "2026-07-25T10:10:00Z",
  "data": {
    "orderNumber": "ORD-20260725-000001",
    "status": "COMPLETED",
    "statusDescriptionKey": "order.status.completed",
    "currency": "USD",
    "subtotal": 25.000,
    "totalAmount": 25.000,
    "totalItems": 1,
    "customerNote": null,
    "failureReason": null,
    "failureReasonDescriptionKey": null,
    "failureNote": null,
    "createdAt": "2026-07-25T10:00:00Z",
    "updatedAt": "2026-07-25T10:10:00Z",
    "cancelledAt": null,
    "completedAt": "2026-07-25T10:10:00Z",
    "customer": { "fullName": "Jane Doe", "email": "jane@example.com", "phoneNumber": "+1000000000" },
    "assignedDeliveryUser": null,
    "address": { "recipientName": "Jane Doe", "phoneNumber": "+1000000000", "city": "City", "latitude": 0.0, "longitude": 0.0, "area": null, "street": null, "additionalDirections": null },
    "items": []
  }
}
```

Validation failures return an appropriate HTTP 4xx status with this shape (the exact localized message and code depend on the failure):

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "errors": { "status": "must not be null" },
    "timestamp": "2026-07-25T10:10:00Z"
  },
  "timestamp": "2026-07-25T10:10:00Z"
}
```

Refresh the order from `data` after success rather than changing only the local status. Show a status selector containing all six values, an optional note textarea, and optional failure-reason/failure-note controls when `FAILED` is selected. Ask for confirmation when entering or leaving `CANCELLED`, because that operation changes inventory. Disable submission while the request is running and surface the API's localized `message` on success or error.

Display the audit trail from `GET /api/v1/admin/orders/{orderNumber}/history`. Each item contains `previousStatus`, `newStatus`, `changedByUserId`, `changedByRole`, optional `note`, optional `failureReason`, and `changedAt`. After a successful update, refetch this history so the admin immediately sees the new audit entry.
