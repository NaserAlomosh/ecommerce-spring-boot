import 'package:flutter/material.dart';
import '../../../../core/presentation/base_view.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../widgets/mobile/mobile_navigation_shell.dart';
import '../widgets/web/web_navigation_shell.dart';

class CustomerShellPage extends BaseView {
  const CustomerShellPage({super.key});

  @override
  Widget buildView(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileNavigationShell(),
      web: WebNavigationShell(),
    );
  }
}
