import 'package:flutter/widgets.dart';

import 'adaptive_layout.dart';

class AdaptivePage extends StatelessWidget {
  const AdaptivePage({required this.mobile, required this.web, super.key});

  final Widget mobile;
  final Widget web;

  @override
  Widget build(BuildContext context) {
    return AdaptiveLayout(compact: mobile, medium: web, expanded: web, large: web);
  }
}
