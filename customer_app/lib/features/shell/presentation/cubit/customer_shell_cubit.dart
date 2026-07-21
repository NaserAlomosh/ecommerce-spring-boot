import 'package:flutter_bloc/flutter_bloc.dart';

enum CustomerShellTab { home, categories, cart, profile }

class CustomerShellCubit extends Cubit<CustomerShellTab> {
  CustomerShellCubit() : super(CustomerShellTab.home);

  void select(CustomerShellTab tab) => emit(tab);
}
