import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../../core/presentation/base_view.dart';

import '../../../../../core/design_system/app_colors.dart';
import '../../../../../core/design_system/app_icons.dart';
import '../../../../../core/design_system/app_spacing.dart';
import '../../../../../core/responsive/content_containers.dart';
import '../../cubit/customer_shell_cubit.dart';
import '../../../../../features/cart/presentation/pages/cart_page.dart';
import '../../../../../features/categories/presentation/pages/categories_page.dart';
import '../../../../../features/home/presentation/pages/home_page.dart';
import '../../../../../features/profile/presentation/pages/profile_page.dart';

class WebNavigationShell extends BaseView {
  const WebNavigationShell({super.key});

  @override
  Widget buildView(BuildContext context) {
    return BlocBuilder<CustomerShellCubit, CustomerShellTab>(
      builder: (context, tab) {
        return Scaffold(
          body: Column(
            children: [
              Material(
                color: AppColors.surface,
                child: WebContentContainer(
                  child: Row(
                    children: [
                      const Icon(Icons.shopping_bag, color: AppColors.primary),
                      const SizedBox(width: AppSpacing.sm),
                      Text('Smart Shop', style: Theme.of(context).textTheme.titleLarge),
                      const SizedBox(width: AppSpacing.xl),
                      for (final item in CustomerShellTab.values)
                        TextButton.icon(
                          onPressed: () => context.read<CustomerShellCubit>().select(item),
                          icon: Icon(_iconFor(item)),
                          label: Text(_labelFor(item)),
                        ),
                      const Spacer(),
                      const SizedBox(width: 360, child: SearchBar(hintText: 'Search products, brands, and categories')),
                      IconButton(tooltip: 'Wishlist', onPressed: () {}, icon: const Icon(AppIcons.wishlist)),
                      IconButton(tooltip: 'Cart', onPressed: () => context.read<CustomerShellCubit>().select(CustomerShellTab.cart), icon: const Icon(AppIcons.cart)),
                    ],
                  ),
                ),
              ),
              Expanded(child: IndexedStack(index: tab.index, children: const [HomePage(), CategoriesPage(), CartPage(), ProfilePage()])),
            ],
          ),
        );
      },
    );
  }

  IconData _iconFor(CustomerShellTab tab) => switch (tab) {
        CustomerShellTab.home => AppIcons.home,
        CustomerShellTab.categories => AppIcons.categories,
        CustomerShellTab.cart => AppIcons.cart,
        CustomerShellTab.profile => AppIcons.profile,
      };

  String _labelFor(CustomerShellTab tab) => switch (tab) {
        CustomerShellTab.home => 'Home',
        CustomerShellTab.categories => 'Categories',
        CustomerShellTab.cart => 'Cart',
        CustomerShellTab.profile => 'Profile',
      };
}
