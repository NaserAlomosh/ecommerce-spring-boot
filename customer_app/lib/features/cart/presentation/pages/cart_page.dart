import 'package:flutter/material.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../views/mobile/mobile_cart_view.dart';
import '../views/web/web_cart_view.dart';

class CartPage extends StatelessWidget {
  const CartPage({super.key});

  @override
  Widget build(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileCartView(),
      web: WebCartView(),
    );
  }
}
