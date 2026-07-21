import 'package:flutter/material.dart';

import '../../../../../core/design_system/app_spacing.dart';
import '../../../../../core/responsive/content_containers.dart';

class WebCartView extends StatelessWidget {
  const WebCartView({super.key});

  @override
  Widget build(BuildContext context) {
    return WebContentContainer(
      child: ListView(
        children: [
          Text('Cart', style: Theme.of(context).textTheme.headlineLarge),
          const SizedBox(height: AppSpacing.md),
          const Text('Wide-screen placeholder content with desktop density, panels, and constrained width.'),
          const SizedBox(height: AppSpacing.lg),
          GridView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
              maxCrossAxisExtent: 280,
              mainAxisSpacing: AppSpacing.md,
              crossAxisSpacing: AppSpacing.md,
              childAspectRatio: 1.25,
            ),
            itemCount: 8,
            itemBuilder: (_, index) => Card(
              child: Padding(
                padding: const EdgeInsets.all(AppSpacing.md),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('Cart panel ${index + 1}', style: Theme.of(context).textTheme.titleMedium),
                    const Spacer(),
                    FilledButton(onPressed: () {}, child: const Text('Explore')),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
