/// Window size constants
class WindowSize {
  static const int matchParent = -1;
  static const int wrapContent = -2;
  static const int fullCover = -3;
}

/// Overlay alignment on screen
enum OverlayAlignment {
  /// Center of screen
  center,
  /// Top of screen
  top,
  /// Bottom of screen
  bottom,
  /// Left of screen
  left,
  /// Right of screen
  right,
  /// Top left corner
  topLeft,
  /// Top right corner
  topRight,
  /// Bottom left corner
  bottomLeft,
  /// Bottom right corner
  bottomRight,
}

/// Notification visibility for overlay service
enum NotificationVisibility {
  /// Show notification on lock screen
  visibilityPublic,
  /// Hide notification on lock screen
  visibilitySecret,
  /// Show notification on lock screen but hide sensitive content
  visibilityPrivate,
}

/// Overlay window flags
enum OverlayFlag {
  /// Default window flag (won't get key input focus)
  defaultFlag,

  /// Allow pointer events outside window to be sent to windows behind it
  focusPointer,
}

/// Position gravity for overlay after drag
enum PositionGravity {
  /// Allow overlay to be positioned anywhere on screen
  none,
  /// Stick overlay to right side of screen
  right,
  /// Stick overlay to left side of screen
  left,
  /// Auto-stick to left or right side depending on position
  auto,
} 