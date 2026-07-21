import 'package:flutter/widgets.dart';

import 'app_breakpoints.dart';

typedef ResponsiveWidgetBuilder = Widget Function(BuildContext context, AppLayoutType layoutType, BoxConstraints constraints);

class ResponsiveBuilder extends StatelessWidget {
  const ResponsiveBuilder({required this.builder, super.key});

  final ResponsiveWidgetBuilder builder;

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) => builder(
        context,
        AppBreakpoints.layoutForWidth(constraints.maxWidth),
        constraints,
      ),
    );
  }
}
