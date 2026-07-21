import 'package:flutter/widgets.dart';

abstract class BaseView extends StatelessWidget {
  const BaseView({super.key});

  @override
  Widget build(BuildContext context) => buildView(context);

  Widget buildView(BuildContext context);
}
