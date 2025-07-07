import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'src/overlay_enums.dart';
import 'src/overlay_position.dart';

// Export enums and classes for public use
export 'src/overlay_enums.dart';
export 'src/overlay_position.dart';

class FlutterOverlayWindowPlus {
  static const MethodChannel _channel = MethodChannel('flutter_overlay_window_plus');
  static const EventChannel _eventChannel = EventChannel('flutter_overlay_window_plus_events');

  static Stream<dynamic>? _overlayListener;

  /// Check if overlay permission is granted
  static Future<bool> isPermissionGranted() async {
    try {
      final bool result = await _channel.invokeMethod('isPermissionGranted');
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error checking permission: ${e.message}');
      return false;
    }
  }

  /// Request overlay permission
  /// Opens the overlay settings page and returns true once permission is granted
  static Future<bool> requestPermission() async {
    try {
      final bool result = await _channel.invokeMethod('requestPermission');
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error requesting permission: ${e.message}');
      return false;
    }
  }

  /// Show overlay window
  /// 
  /// [height] - overlay height (default: WindowSize.fullCover)
  /// [width] - overlay width (default: WindowSize.matchParent)
  /// [alignment] - overlay position on screen (default: OverlayAlignment.center)
  /// [visibility] - notification visibility (default: NotificationVisibility.visibilitySecret)
  /// [flag] - overlay flag (default: OverlayFlag.defaultFlag)
  /// [overlayTitle] - notification title (default: "Overlay Active")
  /// [overlayContent] - notification content
  /// [enableDrag] - enable/disable dragging (default: false)
  /// [positionGravity] - position gravity after drag (default: PositionGravity.none)
  /// [startPosition] - initial overlay position
  static Future<bool> showOverlay({
    int? height,
    int? width,
    OverlayAlignment alignment = OverlayAlignment.center,
    NotificationVisibility visibility = NotificationVisibility.visibilitySecret,
    OverlayFlag flag = OverlayFlag.defaultFlag,
    String overlayTitle = "Overlay Active",
    String? overlayContent,
    bool enableDrag = false,
    PositionGravity positionGravity = PositionGravity.none,
    OverlayPosition? startPosition,
  }) async {
    try {
      final Map<String, dynamic> arguments = {
        'height': height ?? WindowSize.fullCover,
        'width': width ?? WindowSize.matchParent,
        'alignment': alignment.index,
        'visibility': visibility.index,
        'flag': flag.index,
        'overlayTitle': overlayTitle,
        'overlayContent': overlayContent ?? overlayTitle,
        'enableDrag': enableDrag,
        'positionGravity': positionGravity.index,
        'startPosition': startPosition?.toMap(),
      };

      final bool result = await _channel.invokeMethod('showOverlay', arguments);
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error showing overlay: ${e.message}');
      return false;
    }
  }

  /// Close overlay if open
  static Future<bool> closeOverlay() async {
    try {
      final bool result = await _channel.invokeMethod('closeOverlay');
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error closing overlay: ${e.message}');
      return false;
    }
  }

  /// Share data between overlay and main app
  static Future<bool> shareData(String data) async {
    try {
      final bool result = await _channel.invokeMethod('shareData', {'data': data});
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error sharing data: ${e.message}');
      return false;
    }
  }

  /// Update overlay flag while overlay is active
  static Future<bool> updateFlag(OverlayFlag flag) async {
    try {
      final bool result = await _channel.invokeMethod('updateFlag', {'flag': flag.index});
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error updating flag: ${e.message}');
      return false;
    }
  }

  /// Resize overlay
  static Future<bool> resizeOverlay(int width, int height) async {
    try {
      final bool result = await _channel.invokeMethod('resizeOverlay', {
        'width': width,
        'height': height,
      });
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error resizing overlay: ${e.message}');
      return false;
    }
  }

  /// Move overlay to new position
  static Future<bool> moveOverlay(OverlayPosition position) async {
    try {
      final bool result = await _channel.invokeMethod('moveOverlay', {
        'position': position.toMap(),
      });
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error moving overlay: ${e.message}');
      return false;
    }
  }

  /// Get current overlay position
  static Future<OverlayPosition?> getOverlayPosition() async {
    try {
      final Map<dynamic, dynamic>? result = await _channel.invokeMethod('getOverlayPosition');
      if (result != null) {
        return OverlayPosition.fromMap(Map<String, dynamic>.from(result));
      }
      return null;
    } on PlatformException catch (e) {
      debugPrint('Error getting overlay position: ${e.message}');
      return null;
    }
  }

  /// Stream for listening to overlay events
  static Stream<dynamic> get overlayListener {
    _overlayListener ??= _eventChannel.receiveBroadcastStream();
    return _overlayListener!;
  }
}
