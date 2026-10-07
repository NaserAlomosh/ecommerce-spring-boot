import 'package:flutter/material.dart';
import '../../../../core/presentation/base_view.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../views/mobile/mobile_cart_view.dart';
import '../views/web/web_cart_view.dart';

class CartPage extends BaseView {
  const CartPage({super.key});

  @override
  Widget buildView(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileCartView(),
      web: WebCartView(),
    );
  }
}
