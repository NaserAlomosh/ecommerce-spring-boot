# Flutter frontend prompt: simple guest order flow

Implement a simple guest checkout page at the frontend route **`/guest-order`**. Do not add generated links, tokens, slugs, expiry, guest-link CRUD, authentication, or a backend cart.

## Flow

1. The backend stores one URL, for example `https://domain.com/guest-order`, in `website_settings.guest_order_link`.
2. Admin reads that URL and copies/sends it to the customer.
3. Opening the URL routes the customer to `/guest-order`.
4. The page loads the existing public product list and keeps the cart only in Flutter state.
5. The customer enters name, mobile number, and a Google Maps location.
6. Flutter submits only customer/location values and `{productId, quantity}` entries. No login or token is required.

## APIs

All responses use the existing `BaseResponseModel<T>` envelope.

### Stored link

Public read (no token):

```http
GET /api/v1/guest-order-link
```

Admin read/update (admin or sub-admin token):

```http
GET /api/v1/admin/website/guest-order-link
PUT /api/v1/admin/website/guest-order-link
Content-Type: application/json

{ "url": "https://domain.com/guest-order" }
```

Response data:

```json
{ "url": "https://domain.com/guest-order" }
```

The admin UI only needs to display, copy, and optionally update this single stored URL. There is no list, create-link form, slug, enable/disable action, token, or expiry.

### Products

Reuse the existing product repository, models, entity, and use case:

```http
GET /api/v1/products?page=0&size=20
```

The existing endpoint returns active products. On the guest page show only products whose `inStock` value is true. Keep selected quantities in a local immutable Cubit list and never call customer cart APIs.

### Create guest order

```http
POST /api/v1/guest-order-link/orders
Content-Type: application/json
```

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

`address` is optional; all other values are required. Each quantity must be positive. The response data is the existing `OrderModel`, with `guestOrder: true`.

Never send product name, price, discount, currency, subtotal, total, or any calculated financial value. The displayed local subtotal is only an estimate. The backend reloads products, validates active status and stock, reads current prices, calculates totals, and decreases stock transactionally.

## UI

Build one guest-order screen with a non-scrollable two-page `PageView`:

1. Products and local cart: use `ListProductsUsecase`, add/increment/decrement/remove locally, show selected lines and estimated subtotal, and reject Next when empty.
2. Form: required name, required mobile, and required Google Maps coordinates; optional formatted address. Back preserves the cart. Submit is disabled while running. On success show the server order number/total and then clear local state.

The page is public and must never redirect to login. Do not attach an Authorization header for the stored-link read or order POST.

## Required architecture

Follow the existing Flutter layers exactly:

```dart
@freezed
abstract class GuestOrderState with _$GuestOrderState {
  const factory GuestOrderState({
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
    this._listProductsUsecase,
    this._createGuestOrderUsecase,
  ) : super(const GuestOrderState());

  final ListProductsUsecase _listProductsUsecase;
  final CreateGuestOrderUsecase _createGuestOrderUsecase;

  @override
  Future init() => loadProducts();

  Future<void> loadProducts() async {
    emit(state.copyWith(isLoading: true, errorMessage: null), closeLoading: false);
    final result = await _listProductsUsecase(const ListProductsParams());
    result.when(
      success: (page) => emit(state.copyWith(
        products: page.content.where((product) => product.inStock).toList(),
        isLoading: false,
      )),
      failure: (error) => emit(state.copyWith(
        isLoading: false,
        errorMessage: error.error?.toString() ?? 'Unable to load products',
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
      () => _apiService.createGuestOrder(params.toRequestModel()),
    );
    return result.map((model) => model.toEntity());
  }
}

@GET('/api/v1/guest-order-link')
Future<BaseResponseModel<GuestOrderLinkModel>> guestOrderLink();

@POST('/api/v1/guest-order-link/orders')
Future<BaseResponseModel<OrderModel>> createGuestOrder(
  @Body() GuestOrderRequestModel request,
);
```

Use the exact existing imports and conventions, including `ApiResult`, `SafeRequest.execute`, Freezed, injectable, `BaseCubit`, repository abstractions, and `result.when(success:, failure:)`. Do not call `ApiService` from widgets or Cubits.

Add tests for local quantity changes, empty-cart Next validation, page navigation, request JSON excluding financial fields, form validation, submission success/failure, and double-submit prevention.

## Important backend naming clarification

Do **not** implement or call `AdminGuestOrderLinkController`,
`GuestOrderLinkService`, `GuestLinkRequest`, `GuestLinkResponse`, or
`GuestLinkStatusRequest`. Those types belong to the rejected multi-link design
and intentionally do not exist.

The single-link admin contract is implemented by `AdminWebsiteController` and
uses only `GuestOrderLinkResponse` and `GuestOrderLinkUpdateRequest` from
`WebsiteDtos`. The guest checkout request is `GuestOrderRequest` from
`GuestOrderDtos`.
