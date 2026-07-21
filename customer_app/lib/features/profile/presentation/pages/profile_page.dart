import 'package:flutter/material.dart';
import '../../../../core/presentation/base_view.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../views/mobile/mobile_profile_view.dart';
import '../views/web/web_profile_view.dart';

class ProfilePage extends BaseView {
  const ProfilePage({super.key});

  @override
  Widget buildView(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileProfileView(),
      web: WebProfileView(),
    );
  }
}
