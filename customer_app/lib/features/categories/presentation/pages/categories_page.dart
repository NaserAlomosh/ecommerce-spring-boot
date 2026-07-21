import 'package:flutter/material.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../views/mobile/mobile_categories_view.dart';
import '../views/web/web_categories_view.dart';

class CategoriesPage extends StatelessWidget {
  const CategoriesPage({super.key});

  @override
  Widget build(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileCategoriesView(),
      web: WebCategoriesView(),
    );
  }
}
