import 'package:flutter/material.dart';
import '../../../../core/presentation/base_view.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../views/mobile/mobile_categories_view.dart';
import '../views/web/web_categories_view.dart';

class CategoriesPage extends BaseView {
  const CategoriesPage({super.key});

  @override
  Widget buildView(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileCategoriesView(),
      web: WebCategoriesView(),
    );
  }
}
