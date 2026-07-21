import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../../core/presentation/base_view.dart';

import '../../../../../core/design_system/app_icons.dart';
import '../../cubit/customer_shell_cubit.dart';
import '../../../../../features/cart/presentation/pages/cart_page.dart';
import '../../../../../features/categories/presentation/pages/categories_page.dart';
import '../../../../../features/home/presentation/pages/home_page.dart';
import '../../../../../features/profile/presentation/pages/profile_page.dart';

class MobileNavigationShell extends BaseView {
  const MobileNavigationShell({super.key});

  @override
  Widget buildView(BuildContext context) {
    return BlocBuilder<CustomerShellCubit, CustomerShellTab>(
      builder: (context, tab) {
        return Scaffold(
          appBar: AppBar(title: const Text('Smart Shop')),
          body: IndexedStack(
            index: tab.index,
            children: const [HomePage(), CategoriesPage(), CartPage(), ProfilePage()],
          ),
          bottomNavigationBar: NavigationBar(
            selectedIndex: tab.index,
            onDestinationSelected: (index) => context.read<CustomerShellCubit>().select(CustomerShellTab.values[index]),
            destinations: const [
              NavigationDestination(icon: Icon(AppIcons.home), label: 'Home'),
              NavigationDestination(icon: Icon(AppIcons.categories), label: 'Categories'),
              NavigationDestination(icon: Icon(AppIcons.cart), label: 'Cart'),
              NavigationDestination(icon: Icon(AppIcons.profile), label: 'Profile'),
            ],
          ),
        );
      },
    );
  }
}
