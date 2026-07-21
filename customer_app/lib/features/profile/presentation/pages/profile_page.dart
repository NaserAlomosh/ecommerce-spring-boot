import 'package:flutter/material.dart';

import '../../../../core/responsive/adaptive_page.dart';
import '../views/mobile/mobile_profile_view.dart';
import '../views/web/web_profile_view.dart';

class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context) {
    return const AdaptivePage(
      mobile: MobileProfileView(),
      web: WebProfileView(),
    );
  }
}
