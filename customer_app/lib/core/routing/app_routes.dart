import 'package:flutter/material.dart';

import '../../features/shell/presentation/pages/customer_shell_page.dart';
import '../../features/splash/presentation/pages/splash_page.dart';

abstract final class AppRoutes {
  static const splash = '/';
  static const shell = '/shop';

  static Map<String, WidgetBuilder> get routes => {
        splash: (_) => const SplashPage(),
        shell: (_) => const CustomerShellPage(),
      };
}
