enum AppLayoutType { compact, medium, expanded, large }

abstract final class AppBreakpoints {
  static const double compactMax = 599;
  static const double mediumMax = 1023;
  static const double expandedMax = 1439;

  static AppLayoutType layoutForWidth(double width) {
    if (width <= compactMax) return AppLayoutType.compact;
    if (width <= mediumMax) return AppLayoutType.medium;
    if (width <= expandedMax) return AppLayoutType.expanded;
    return AppLayoutType.large;
  }
}
