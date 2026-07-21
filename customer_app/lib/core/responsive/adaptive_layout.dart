import 'package:flutter/widgets.dart';
import '../../core/presentation/base_view.dart';

import 'app_breakpoints.dart';
import 'responsive_builder.dart';

class AdaptiveLayout extends BaseView {
  const AdaptiveLayout({required this.compact, this.medium, this.expanded, this.large, super.key});

  final Widget compact;
  final Widget? medium;
  final Widget? expanded;
  final Widget? large;

  @override
  Widget buildView(BuildContext context) {
    return ResponsiveBuilder(
      builder: (_, layoutType, __) => switch (layoutType) {
        AppLayoutType.compact => compact,
        AppLayoutType.medium => medium ?? compact,
        AppLayoutType.expanded => expanded ?? medium ?? compact,
        AppLayoutType.large => large ?? expanded ?? medium ?? compact,
      },
    );
  }
}
