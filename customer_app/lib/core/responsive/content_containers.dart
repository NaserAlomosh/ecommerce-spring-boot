import 'package:flutter/material.dart';
import '../../core/presentation/base_view.dart';

import '../design_system/app_spacing.dart';
import 'app_constraints.dart';

class WebContentContainer extends BaseView {
  const WebContentContainer({required this.child, this.maxWidth = AppConstraints.webMaxWidth, super.key});

  final Widget child;
  final double maxWidth;

  @override
  Widget buildView(BuildContext context) {
    return Center(
      child: ConstrainedBox(
        constraints: BoxConstraints(maxWidth: maxWidth),
        child: Padding(padding: const EdgeInsets.all(AppSpacing.lg), child: child),
      ),
    );
  }
}

class MobileContentContainer extends BaseView {
  const MobileContentContainer({required this.child, super.key});

  final Widget child;

  @override
  Widget buildView(BuildContext context) {
    return SafeArea(
      child: Padding(padding: const EdgeInsets.all(AppSpacing.md), child: child),
    );
  }
}
