import '../../../../core/presentation/base_cubit.dart';

enum CustomerShellTab { home, categories, cart, profile }

class CustomerShellCubit extends BaseCubit<CustomerShellTab> {
  CustomerShellCubit() : super(CustomerShellTab.home);

  void select(CustomerShellTab tab) => emit(tab);
}
