import 'package:flutter/widgets.dart';
import '../../core/presentation/base_view.dart';

import 'app_breakpoints.dart';

typedef ResponsiveWidgetBuilder = Widget Function(BuildContext context, AppLayoutType layoutType, BoxConstraints constraints);

class ResponsiveBuilder extends BaseView {
  const ResponsiveBuilder({required this.builder, super.key});

  final ResponsiveWidgetBuilder builder;

  @override
  Widget buildView(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) => builder(
        context,
        AppBreakpoints.layoutForWidth(constraints.maxWidth),
        constraints,
      ),
    );
  }
}
