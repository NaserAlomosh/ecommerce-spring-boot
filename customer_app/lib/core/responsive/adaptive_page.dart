import 'package:flutter/widgets.dart';
import '../../core/presentation/base_view.dart';

import 'adaptive_layout.dart';

class AdaptivePage extends BaseView {
  const AdaptivePage({required this.mobile, required this.web, super.key});

  final Widget mobile;
  final Widget web;

  @override
  Widget buildView(BuildContext context) {
    return AdaptiveLayout(compact: mobile, medium: web, expanded: web, large: web);
  }
}
