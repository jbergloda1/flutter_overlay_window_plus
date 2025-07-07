# Flutter Overlay Window Plus

A Flutter plugin for displaying a single, robust overlay window over other apps with enhanced features and better API.

## Features

- ✅ Display a **single instance** of an overlay window over other apps (prevents multiple overlays)
- ✅ Draggable overlays with position gravity
- ✅ Customizable size and position
- ✅ **Live data streaming** to the overlay from your main app
- ✅ **Minimize app to background** and **re-launch on overlay close**
- ✅ Permission management
- ✅ Event streaming for overlay state changes
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

// Share data with overlay (can be used for live streaming)
// In the overlay service, the text will be updated dynamically
await FlutterOverlayWindowPlus.shareData("Live update text: ${DateTime.now()}");

// Minimize the app to the background
await FlutterOverlayWindowPlus.minimizeApp();

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

### App Control
- `minimizeApp()` - Minimize the host application to the background.

## Example

See the `example/` directory for a complete working example demonstrating all features, including live text streaming.

## Limitations

- Only supports Android (iOS has strict limitations on overlay windows)
- Requires `SYSTEM_ALERT_WINDOW` permission
- Overlay content is currently rendered as a native Android view (TextView). Custom Flutter widgets in the overlay are not directly supported by this architecture, but the displayed text can be updated live from Flutter.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

This package is inspired by [flutter_overlay_window](https://github.com/jbergloda1/flutter_overlay_window) but with improved API design and additional features.

