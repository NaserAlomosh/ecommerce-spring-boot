import 'package:flutter/material.dart';
import '../../../../../core/presentation/base_view.dart';

import '../../../../../core/design_system/app_spacing.dart';
import '../../../../../core/responsive/content_containers.dart';

class MobileHomeView extends BaseView {
  const MobileHomeView({super.key});

  @override
  Widget buildView(BuildContext context) {
    return MobileContentContainer(
      child: ListView(
        children: [
          Text('Home', style: Theme.of(context).textTheme.headlineMedium),
          const SizedBox(height: AppSpacing.md),
          const Text('Mobile-first placeholder content with touch-friendly full-width sections.'),
          const SizedBox(height: AppSpacing.md),
          for (var index = 1; index <= 4; index++)
            Card(
              child: ListTile(
                title: Text('Home item $index'),
                subtitle: const Text('Compact card for narrow screens'),
                trailing: const Icon(Icons.chevron_right),
              ),
            ),
        ],
      ),
    );
  }
}
