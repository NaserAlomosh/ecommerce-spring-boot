# Flutter frontend implementation prompt: guest order flow

Implement the complete **Guest Order Links** admin feature and public guest checkout in the Flutter application. Follow the application's existing clean architecture and use the exact dependency direction and state style shown below: widget → Freezed `BaseCubit` → use case → repository abstraction → repository implementation → Retrofit `ApiService`. Use `SafeRequest.execute`, `ApiResult`, injectable, entities, request/response models, and generated JSON/Retrofit/Freezed code. Never call Retrofit directly from a widget or cubit.

## Backend contract and response envelope

Every successful endpoint returns:

```json
{ "success": true, "message": "...", "data": {}, "timestamp": "2026-07-29T12:00:00Z" }
```

Failures use the same envelope with `success: false`; `data` contains the existing `ApiError`, including field validation errors. These public APIs require **no Authorization header**, JWT, account, or server-side cart.

### Guest link models

```json
{
  "id": 4,
  "title": "Facebook summer offer",
  "slug": "facebook-summer",
  "active": true,
  "createdAt": "2026-07-29T12:00:00Z",
  "updatedAt": "2026-07-29T12:00:00Z"
}
```

The shareable frontend route should be `/guest-order/{slug}`. Store/send only the slug to the APIs; the link never expires and can be reused until disabled.

## Authenticated admin APIs

All require an admin/sub-admin access token.

- `GET /api/v1/admin/guest-links` — `data` is a JSON array of guest links.
- `POST /api/v1/admin/guest-links` — creates a link (HTTP 201).
- `PUT /api/v1/admin/guest-links/{id}` — updates title, slug, and active state.
- `PATCH /api/v1/admin/guest-links/{id}/status` — enables/disables it.

Create/update body:

```json
{ "title": "Facebook summer offer", "slug": "facebook-summer", "active": true }
```

Status body:

```json
{ "active": false }
```

Slug validation is lowercase letters/numbers separated by single hyphens, e.g. `facebook`, `summer-offer`. Build an admin **Guest Order Links** screen with loading, empty, error, create/edit dialogs, enable/disable confirmation, and Copy/Share actions that compose `{publicWebBaseUrl}/guest-order/{slug}`. Refresh or update immutable cubit state after mutations.

## Public guest APIs and flow

### 1. Resolve link

`GET /api/v1/public/guest-links/{slug}`

Call this first when `/guest-order/{slug}` opens. A missing link returns 404. A disabled link returns 400. Show a dedicated unavailable-link state and do not load products or permit checkout after either failure.

### 2. Load only orderable products

`GET /api/v1/public/guest-links/{slug}/products?page=0&size=20`

The `data` is the existing `PaginationModel<ProductModel>`. The backend returns only active products in active categories with stock greater than zero. Support pagination using the current product model/entity; do not make a duplicate product representation. Do not call backend cart endpoints.

### 3. Submit the order

`POST /api/v1/public/guest-links/{slug}/orders` (HTTP 201)

```json
{
  "customerName": "John Doe",
  "phoneNumber": "+962799999999",
  "latitude": 31.95,
  "longitude": 35.91,
  "address": "Amman",
  "items": [
    { "productId": 5, "quantity": 2 },
    { "productId": 8, "quantity": 1 }
  ]
}
```

`customerName`, `phoneNumber`, coordinates, and at least one item are required. `address` is optional. Phone must contain 8–15 digits with an optional leading `+`; latitude is -90..90 and longitude -180..180; each quantity is 1..999. Send each product once.

**Never send product names, images, prices, discounts, currency, subtotals, totals, or any other financial value.** Displayed cart totals are estimates only. The backend reloads and locks products, checks active/category/stock state, reads current prices, calculates totals, snapshots data, and decrements inventory atomically.

The response `data` is the existing `OrderModel`, extended with:

```json
{ "guestOrder": true, "guestLinkSlug": "facebook-summer" }
```

Preserve all existing order fields. On success show the server order number and server-calculated total, then clear local state. On stock/product validation failure keep the form/cart, show the backend error, and allow products to refresh. Disable Submit while a request is running and prevent double submission.

## Required public screen behavior

Use one screen with a non-user-scrollable two-page `PageView` that preserves state:

1. **Products/local cart:** resolve link, page through products, display image/localized name/effective price/currency/current stock, and maintain local quantities with add/increment/decrement/remove. Show selected lines and estimated subtotal. Next must reject an empty cart.
2. **Customer/location form:** required name and phone; choose a Google Maps point returning latitude/longitude and optional formatted address. Back returns to products without losing state. Submit maps local items strictly to `{productId, quantity}`.

The system/app-bar back action returns from page two to page one; from page one it closes the guest route. Handle loading, retry, disabled/not-found link, empty products, validation, submitting, API error, and success states. Do not require or redirect to login.

## Required project structure and style

Use the project's equivalents of these layers and preserve this pattern:

```dart
@freezed
abstract class GuestOrderState with _$GuestOrderState {
  const factory GuestOrderState({
    GuestLinkEntity? link,
    @Default(<ProductEntity>[]) List<ProductEntity> products,
    @Default(<LocalGuestOrderItem>[]) List<LocalGuestOrderItem> selectedItems,
    @Default(0) int pageIndex,
    @Default(false) bool isLoading,
    @Default(false) bool isSubmitting,
    String? errorMessage,
    OrderEntity? createdOrder,
  }) = _GuestOrderState;
}

@injectable
class GuestOrderCubit extends BaseCubit<GuestOrderState> {
  GuestOrderCubit(
    this._getGuestLinkUsecase,
    this._listGuestProductsUsecase,
    this._createGuestOrderUsecase,
  ) : super(const GuestOrderState());

  final GetGuestLinkUsecase _getGuestLinkUsecase;
  final ListGuestProductsUsecase _listGuestProductsUsecase;
  final CreateGuestOrderUsecase _createGuestOrderUsecase;

  Future<void> load(String slug) async {
    emit(state.copyWith(isLoading: true, errorMessage: null), closeLoading: false);
    final result = await _getGuestLinkUsecase(slug);
    result.when(
      success: (link) => _loadProductsAfterLink(link),
      failure: (error) => emit(state.copyWith(
        isLoading: false,
        errorMessage: error.error?.toString() ?? 'Guest link is unavailable',
      )),
    );
  }
}

@injectable
class CreateGuestOrderUsecase {
  CreateGuestOrderUsecase(this._repository);
  final GuestOrderRepository _repository;

  Future<ApiResult<OrderEntity>> call(CreateGuestOrderParams params) =>
      _repository.createOrder(params);
}

@LazySingleton(as: GuestOrderRepository)
class GuestOrderRepositoryImpl implements GuestOrderRepository {
  GuestOrderRepositoryImpl(this._apiService);
  final ApiService _apiService;

  @override
  Future<ApiResult<OrderEntity>> createOrder(CreateGuestOrderParams params) async {
    final result = await SafeRequest.execute<OrderModel>(
      () => _apiService.createGuestOrder(params.slug, params.toRequestModel()),
    );
    return result.map((model) => model.toEntity());
  }
}

@GET('/api/v1/public/guest-links/{slug}')
Future<BaseResponseModel<GuestLinkModel>> guestLink(@Path('slug') String slug);

@GET('/api/v1/public/guest-links/{slug}/products')
Future<BaseResponseModel<PaginationModel<ProductModel>>> guestProducts(
  @Path('slug') String slug,
  @Query('page') int page,
  @Query('size') int size,
);

@POST('/api/v1/public/guest-links/{slug}/orders')
Future<BaseResponseModel<OrderModel>> createGuestOrder(
  @Path('slug') String slug,
  @Body() GuestOrderRequestModel request,
);
```

Use the exact existing imports in this codebase, including `api_result.dart`, `BaseCubit`, existing product/order entities, Freezed annotations, and injectable. Adapt only generic mapping syntax to the current helpers.

## Tests

Add cubit/use-case/repository/model tests for link resolution, unavailable/disabled link, pagination, local quantity bounds, empty-cart prevention, navigation/state preservation, request JSON excluding all financial fields, success/failure, and double-submit prevention. Add widget tests for admin CRUD states, both guest pages, Google Maps selection handoff, form validation, back behavior, and success UI.
