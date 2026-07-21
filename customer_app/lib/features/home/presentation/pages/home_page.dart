import 'package:flutter/material.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../views/mobile/mobile_home_view.dart';
import '../views/web/web_home_view.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileHomeView(),
      web: WebHomeView(),
    );
  }
}
