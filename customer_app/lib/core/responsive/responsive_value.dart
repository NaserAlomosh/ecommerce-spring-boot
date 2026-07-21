import 'app_breakpoints.dart';

class ResponsiveValue<T> {
  const ResponsiveValue({required this.compact, this.medium, this.expanded, this.large});

  final T compact;
  final T? medium;
  final T? expanded;
  final T? large;

  T resolve(AppLayoutType type) => switch (type) {
        AppLayoutType.compact => compact,
        AppLayoutType.medium => medium ?? compact,
        AppLayoutType.expanded => expanded ?? medium ?? compact,
        AppLayoutType.large => large ?? expanded ?? medium ?? compact,
      };
}
