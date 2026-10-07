import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'core/design_system/app_theme.dart';
import 'core/routing/app_routes.dart';
import 'features/shell/presentation/cubit/customer_shell_cubit.dart';
import 'features/splash/presentation/pages/splash_page.dart';

void main() {
  runApp(const CustomerEcommerceApp());
}

class CustomerEcommerceApp extends StatelessWidget {
  const CustomerEcommerceApp({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => CustomerShellCubit(),
      child: MaterialApp(
        title: 'Smart E-Commerce',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.light(),
        initialRoute: AppRoutes.splash,
        routes: AppRoutes.routes,
        onUnknownRoute: (_) => MaterialPageRoute<void>(
          builder: (_) => const SplashPage(),
        ),
      ),
    );
  }
}
