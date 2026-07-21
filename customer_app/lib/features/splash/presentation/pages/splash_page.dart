import 'package:flutter/material.dart';

import '../../../../core/design_system/app_colors.dart';
import '../../../../core/design_system/app_spacing.dart';
import '../../../../core/presentation/base_view.dart';
import '../../../../core/routing/app_routes.dart';

class SplashPage extends BaseView {
  const SplashPage({super.key});

  @override
  Widget buildView(BuildContext context) {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Future<void>.delayed(const Duration(milliseconds: 700), () {
        if (context.mounted) Navigator.of(context).pushReplacementNamed(AppRoutes.shell);
      });
    });

    return const Scaffold(
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.shopping_bag, size: 72, color: AppColors.primary),
            SizedBox(height: AppSpacing.md),
            Text('Smart E-Commerce'),
            SizedBox(height: AppSpacing.lg),
            CircularProgressIndicator(),
          ],
        ),
      ),
    );
  }
}
