# Flutter frontend implementation prompt: social sales ordering

Implement the new **social sales order** flow in the Flutter application. The old employee-specific/generated-link flow has been removed conceptually: do not generate, mutate, or request a new link per sales employee. Read the single static URL stored by the backend instead.

## Backend API changes

### New public APIs (no access token and no backend cart)

- `GET /api/v1/social-sales-link` returns the database-backed static link:

```json
{
  "success": true,
  "message": "Social sales link loaded successfully",
  "data": { "url": "https://example.com/socialSealsLinkOrder" },
  "timestamp": "2026-07-28T00:00:00Z"
}
```

- `POST /api/v1/social-sales-link/orders` creates an order directly. It does **not** create, read, update, or clear a server cart and does not require login.

```json
{
  "name": "Customer Name",
  "phoneNumber": "+962790000000",
  "location": {
    "city": "Amman",
    "latitude": 31.9539,
    "longitude": 35.9106,
    "area": "Abdoun",
    "street": "Main Street",
    "additionalDirections": "Building 10"
  },
  "products": [
    { "productId": 12, "quantity": 2 },
    { "productId": 20, "quantity": 1 }
  ],
  "customerNote": "Optional note"
}
```

The response `data` is the existing full `OrderResponse`. Expect validation errors for an empty product list, non-positive IDs/quantities, invalid phone number, missing name/city/coordinates, or out-of-range coordinates. The backend merges duplicate product IDs, revalidates product status/stock/prices, calculates totals on the server, snapshots product and location data, reduces inventory, and creates a `PENDING` order atomically.

### New admin API

- `PUT /api/v1/admin/website/social-sales-link`, authenticated as `ADMIN` or `SUB_ADMIN`:

```json
{ "url": "https://example.com/socialSealsLinkOrder" }
```

### Removed frontend behavior

Delete UI, repository calls, use cases, state, and models that generate a unique social-media link for each sales employee. Never append an employee token or assume a backend cart/customer address exists in this public flow. Product listing continues to use `GET /api/v1/products`.

## Required navigation and screens

Register the route named exactly `socialSealsLinkOrder`. Build the flow as one screen containing a non-user-scrollable two-page `PageView`, preserving page state:

1. **Product selection page**
   - Load active products with the existing product-list endpoint and present product image, localized name, effective price, currency, and available stock.
   - Maintain a local-only cart in the cubit: product plus quantity. Provide increment, decrement, and remove actions and never call the backend cart APIs.
   - Under the products list, show a persistent selected-items summary with product details, quantities, line totals, and estimated subtotal.
   - Show a Checkout/Next button. Validate that the local cart is not empty before changing pages; remain on page one and show a localized error/snackbar when empty.

2. **Order form page**
   - Fields: required customer name, required mobile number, and required location selected from Google Maps.
   - The map selection must supply city, latitude, and longitude; area, street, and additional directions may be optional.
   - Include Back and Submit Order actions. Disable duplicate submission and show loading/error state.
   - Convert the local cart to `products: [{productId, quantity}]`, submit the public order API, then show an order-success state with the order number and clear local state only after success.

The app bar/back action on page two should return to page one without losing selections. On page one it should close the route.

## Architecture and coding structure

Follow the project layers and the exact state-management style below. Use Freezed immutable state, injectable dependency injection, `BaseCubit`, use cases, repository abstractions/implementations, Retrofit `ApiService`, `SafeRequest.execute`, `ApiResult`, entities, and models. Do not make direct API calls from widgets or the cubit.

```dart
import 'package:ecommerce_flutter/data/core/networking/api_result/api_result.dart';
import 'package:ecommerce_flutter/domain/entity/product/product_entity.dart';
import 'package:ecommerce_flutter/domain/usecase/product/list_products_usecase.dart';
import 'package:ecommerce_flutter/presentation/core/base/cubit/base_cubit.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:injectable/injectable.dart';

part 'social_sales_order_cubit.freezed.dart';

@freezed
abstract class SocialSalesOrderState with _$SocialSalesOrderState {
  const factory SocialSalesOrderState({
    @Default(<ProductEntity>[]) List<ProductEntity> products,
    @Default(<LocalOrderItem>[]) List<LocalOrderItem> selectedItems,
    @Default(0) int pageIndex,
    @Default(false) bool isLoadingProducts,
    @Default(false) bool isSubmitting,
    String? errorMessage,
    OrderEntity? createdOrder,
  }) = _SocialSalesOrderState;
}

@injectable
class SocialSalesOrderCubit extends BaseCubit<SocialSalesOrderState> {
  SocialSalesOrderCubit(
    this._listProductsUsecase,
    this._createPublicOrderUsecase,
  ) : super(const SocialSalesOrderState());

  final ListProductsUsecase _listProductsUsecase;
  final CreatePublicOrderUsecase _createPublicOrderUsecase;

  @override
  Future init() => loadProducts();

  Future<void> loadProducts() async {
    emit(
      state.copyWith(isLoadingProducts: true, errorMessage: null),
      closeLoading: false,
    );
    final result = await _listProductsUsecase(const ListProductsParams());
    result.when(
      success: (page) => emit(
        state.copyWith(products: page.content, isLoadingProducts: false),
      ),
      failure: (error) => emit(
        state.copyWith(
          isLoadingProducts: false,
          errorMessage: error.error?.toString() ?? 'Unable to load products',
        ),
      ),
    );
  }

  bool goToCheckout() {
    if (state.selectedItems.isEmpty) {
      emit(state.copyWith(errorMessage: 'Select at least one product'));
      return false;
    }
    emit(state.copyWith(pageIndex: 1, errorMessage: null));
    return true;
  }
}

@injectable
class CreatePublicOrderUsecase {
  CreatePublicOrderUsecase(this._repository);

  final SocialSalesOrderRepository _repository;

  Future<ApiResult<OrderEntity>> call(CreatePublicOrderParams params) =>
      _repository.createOrder(params);
}

@LazySingleton(as: SocialSalesOrderRepository)
class SocialSalesOrderRepositoryImpl implements SocialSalesOrderRepository {
  SocialSalesOrderRepositoryImpl(this._apiService);

  final ApiService _apiService;

  @override
  Future<ApiResult<OrderEntity>> createOrder(
    CreatePublicOrderParams params,
  ) async {
    final result = await SafeRequest.execute<OrderModel>(
      () => _apiService.createSocialSalesOrder(params.toRequestModel()),
    );
    return result.map((model) => model.toEntity());
  }
}

@POST('/api/v1/social-sales-link/orders')
Future<BaseResponseModel<OrderModel>> createSocialSalesOrder(
  @Body() PublicOrderRequestModel request,
);

@GET('/api/v1/social-sales-link')
Future<BaseResponseModel<SocialSalesLinkModel>> socialSalesLink();
```

Adapt generic `ApiResult.map` syntax only if the existing project exposes a different mapping helper, but preserve this dependency direction and `result.when(success:, failure:)` cubit pattern. Add unit tests for local quantity changes, empty-cart first-page validation, successful page navigation, request mapping, submission success/failure, and double-submit prevention; add widget tests for both `PageView` pages and form validation.
