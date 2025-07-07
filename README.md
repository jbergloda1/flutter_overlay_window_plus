# Flutter Overlay Window Plus

A Flutter plugin for displaying overlay windows over other apps with enhanced features and better API.

## Features

- ✅ Display overlay windows over other apps
- ✅ Multiple overlay types (default, click-through, focus pointer)
- ✅ Draggable overlays with position gravity
- ✅ Customizable size and position
- ✅ Real-time communication between overlay and main app
- ✅ Permission management
- ✅ Event streaming
- ✅ Better error handling and API design

## Installation

Add the package to your `pubspec.yaml`:

```yaml
dependencies:
  flutter_overlay_window_plus: ^1.0.0
```

## Android Setup

### 1. Add Permissions

Add the following permissions to your `android/app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
```

### 2. Add Service

Add the overlay service to your `android/app/src/main/AndroidManifest.xml` inside the `<application>` tag:

```xml
<service 
  android:name="com.example.flutter_overlay_window_plus.OverlayService" 
  android:exported="false"
  android:foregroundServiceType="specialUse">
  <property 
    android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
    android:value="overlay_window_service" />
</service>
```

### 3. Entry Point

Create an entry point for your overlay widget in your `main.dart`:

```dart
// overlay entry point
@pragma("vm:entry-point")
void overlayMain() {
  runApp(const MaterialApp(
    debugShowCheckedModeBanner: false,
    home: Material(child: Text("My overlay"))
  ));
}
```

## Usage

### Basic Usage

```dart
import 'package:flutter_overlay_window_plus/flutter_overlay_window_plus.dart';

// Check permission
final bool hasPermission = await FlutterOverlayWindowPlus.isPermissionGranted();

// Request permission
final bool granted = await FlutterOverlayWindowPlus.requestPermission();

// Show overlay
final bool success = await FlutterOverlayWindowPlus.showOverlay(
  height: 200,
  width: 300,
  alignment: OverlayAlignment.center,
  overlayTitle: "My Overlay",
  overlayContent: "This is my overlay content",
  enableDrag: true,
);
```

### Advanced Usage

```dart
// Show click-through overlay
await FlutterOverlayWindowPlus.showOverlay(
  height: WindowSize.fullCover,
  width: WindowSize.matchParent,
  flag: OverlayFlag.clickThrough,
  overlayTitle: "Click-Through Overlay",
  enableDrag: false,
);

// Show small draggable overlay
await FlutterOverlayWindowPlus.showOverlay(
  height: 100,
  width: 150,
  alignment: OverlayAlignment.topRight,
  flag: OverlayFlag.defaultFlag,
  overlayTitle: "Small Overlay",
  enableDrag: true,
  positionGravity: PositionGravity.right,
);

// Close overlay
await FlutterOverlayWindowPlus.closeOverlay();

// Share data with overlay
await FlutterOverlayWindowPlus.shareData("Hello from main app!");

// Get overlay position
final position = await FlutterOverlayWindowPlus.getOverlayPosition();
```

### Listening to Events

```dart
// Listen to overlay events
FlutterOverlayWindowPlus.overlayListener.listen((event) {
  print('Event: ${event['event']}');
  
  switch (event['event']) {
    case 'overlay_shown':
      print('Overlay is now visible');
      break;
    case 'overlay_moved':
      print('Overlay moved to: ${event['x']}, ${event['y']}');
      break;
    case 'overlay_closed':
      print('Overlay was closed');
      break;
  }
});
```

## API Reference

### Window Size Constants

```dart
class WindowSize {
  static const int matchParent = -1;    // Match parent width/height
  static const int wrapContent = -2;    // Wrap content
  static const int fullCover = -3;      // Full screen cover
}
```

### Overlay Alignment

```dart
enum OverlayAlignment {
  center,      // Center of screen
  top,         // Top of screen
  bottom,      // Bottom of screen
  left,        // Left of screen
  right,       // Right of screen
  topLeft,     // Top left corner
  topRight,    // Top right corner
  bottomLeft,  // Bottom left corner
  bottomRight, // Bottom right corner
}
```

### Overlay Flags

```dart
enum OverlayFlag {
  clickThrough,  // Window can never receive touch events
  defaultFlag,   // Default window flag (won't get key input focus)
  focusPointer,  // Allow pointer events outside window
}
```

### Position Gravity

```dart
enum PositionGravity {
  none,  // Allow overlay to be positioned anywhere
  right, // Stick to right side of screen
  left,  // Stick to left side of screen
  auto,  // Auto-stick to left or right side
}
```

### Notification Visibility

```dart
enum NotificationVisibility {
  visibilityPublic,   // Show notification on lock screen
  visibilitySecret,   // Hide notification on lock screen
  visibilityPrivate,  // Show notification but hide sensitive content
}
```

## Methods

### Permission Management

- `isPermissionGranted()` - Check if overlay permission is granted
- `requestPermission()` - Request overlay permission

### Overlay Control

- `showOverlay()` - Show overlay window with various options
- `closeOverlay()` - Close overlay if open
- `resizeOverlay(width, height)` - Resize overlay
- `moveOverlay(position)` - Move overlay to new position
- `getOverlayPosition()` - Get current overlay position

### Communication

- `shareData(data)` - Share data between overlay and main app
- `updateFlag(flag)` - Update overlay flag while active
- `overlayListener` - Stream for listening to overlay events

## Example

See the `example/` directory for a complete working example.

## Limitations

- Only supports Android (iOS has strict limitations on overlay windows)
- Requires `SYSTEM_ALERT_WINDOW` permission
- Overlay content is currently limited to basic text (custom widgets require additional implementation)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

This package is inspired by [flutter_overlay_window](https://github.com/jbergloda1/flutter_overlay_window) but with improved API design and additional features.

