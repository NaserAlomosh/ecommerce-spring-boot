import 'package:flutter/material.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../widgets/mobile/mobile_navigation_shell.dart';
import '../widgets/web/web_navigation_shell.dart';

class CustomerShellPage extends StatelessWidget {
  const CustomerShellPage({super.key});

  @override
  Widget build(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileNavigationShell(),
      web: WebNavigationShell(),
    );
  }
}
