import 'package:flutter/material.dart';

import '../design_system/app_spacing.dart';
import 'app_constraints.dart';

class WebContentContainer extends StatelessWidget {
  const WebContentContainer({required this.child, this.maxWidth = AppConstraints.webMaxWidth, super.key});

  final Widget child;
  final double maxWidth;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: ConstrainedBox(
        constraints: BoxConstraints(maxWidth: maxWidth),
        child: Padding(padding: const EdgeInsets.all(AppSpacing.lg), child: child),
      ),
    );
  }
}

class MobileContentContainer extends StatelessWidget {
  const MobileContentContainer({required this.child, super.key});

  final Widget child;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Padding(padding: const EdgeInsets.all(AppSpacing.md), child: child),
    );
  }
}
