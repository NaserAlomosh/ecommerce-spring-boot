import 'package:flutter/material.dart';
import 'app_colors.dart';

abstract final class AppShadows {
  static const card = [
    BoxShadow(
      color: Color(0x1A172033),
      blurRadius: 18,
      offset: Offset(0, 8),
    ),
  ];
  static BoxBorder get border => Border.all(color: AppColors.border);
}
