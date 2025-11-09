import 'dart:ui';

/// A data model for defining the style of a custom notification.
///
/// This class is intended to be used as a "style contract" between the app
/// and the plugin. The app can create a UI for users to customize these
/// properties, persist them, and then use them to dynamically construct
/// the list of `LiveUpdateView`s for `showCustomNotification`.
class LiveUpdateStyleProvider {
  /// The background color of the notification.
  final Color? backgroundColor;

  /// The color of the title text.
  final Color? titleColor;

  /// The font size of the title text.
  final double? titleSize;

  /// The color of the main content text.
  final Color? textColor;

  /// The font size of the main content text.
  final double? textSize;

  /// Creates a new instance of [LiveUpdateStyleProvider].
  LiveUpdateStyleProvider({
    this.backgroundColor,
    this.titleColor,
    this.titleSize,
    this.textColor,
    this.textSize,
  });

  /// Creates a [LiveUpdateStyleProvider] from a map.
  factory LiveUpdateStyleProvider.fromMap(Map<String, dynamic> map) {
    return LiveUpdateStyleProvider(
      backgroundColor: map['backgroundColor'] != null ? Color(map['backgroundColor']) : null,
      titleColor: map['titleColor'] != null ? Color(map['titleColor']) : null,
      titleSize: map['titleSize']?.toDouble(),
      textColor: map['textColor'] != null ? Color(map['textColor']) : null,
      textSize: map['textSize']?.toDouble(),
    );
  }

  /// Converts the [LiveUpdateStyleProvider] to a map.
  Map<String, dynamic> toMap() {
    return {
      'backgroundColor': backgroundColor?.value,
      'titleColor': titleColor?.value,
      'titleSize': titleSize,
      'textColor': textColor?.value,
      'textSize': textSize,
    };
  }
}