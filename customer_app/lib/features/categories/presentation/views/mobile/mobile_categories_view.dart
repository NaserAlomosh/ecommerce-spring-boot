import 'package:flutter/material.dart';

import '../../../../../core/design_system/app_spacing.dart';
import '../../../../../core/responsive/content_containers.dart';

class MobileCategoriesView extends StatelessWidget {
  const MobileCategoriesView({super.key});

  @override
  Widget build(BuildContext context) {
    return MobileContentContainer(
      child: ListView(
        children: [
          Text('Categories', style: Theme.of(context).textTheme.headlineMedium),
          const SizedBox(height: AppSpacing.md),
          const Text('Mobile-first placeholder content with touch-friendly full-width sections.'),
          const SizedBox(height: AppSpacing.md),
          for (var index = 1; index <= 4; index++)
            Card(
              child: ListTile(
                title: Text('Categories item $index'),
                subtitle: const Text('Compact card for narrow screens'),
                trailing: const Icon(Icons.chevron_right),
              ),
            ),
        ],
      ),
    );
  }
}
