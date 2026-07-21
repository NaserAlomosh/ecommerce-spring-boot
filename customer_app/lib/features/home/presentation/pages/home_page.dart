import 'package:flutter/material.dart';
import '../../../../core/presentation/base_view.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../views/mobile/mobile_home_view.dart';
import '../views/web/web_home_view.dart';

class HomePage extends BaseView {
  const HomePage({super.key});

  @override
  Widget buildView(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileHomeView(),
      web: WebHomeView(),
    );
  }
}
