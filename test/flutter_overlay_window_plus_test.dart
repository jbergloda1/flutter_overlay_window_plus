import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_overlay_window_plus/flutter_overlay_window_plus.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_overlay_window_plus');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        switch (methodCall.method) {
          case 'isPermissionGranted':
            return true;
          case 'requestPermission':
            return true;
          case 'showOverlay':
            return true;
          case 'closeOverlay':
            return true;
          case 'shareData':
            return true;
          case 'updateFlag':
            return true;
          case 'resizeOverlay':
            return true;
          case 'moveOverlay':
            return true;
          case 'getOverlayPosition':
            return {'x': 100, 'y': 200};
          default:
            return null;
        }
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  group('FlutterOverlayWindowPlus', () {
    test('isPermissionGranted returns true', () async {
      final result = await FlutterOverlayWindowPlus.isPermissionGranted();
      expect(result, true);
    });

    test('requestPermission returns true', () async {
      final result = await FlutterOverlayWindowPlus.requestPermission();
      expect(result, true);
    });

    test('showOverlay returns true', () async {
      final result = await FlutterOverlayWindowPlus.showOverlay();
      expect(result, true);
    });

    test('showOverlay with custom parameters returns true', () async {
      final result = await FlutterOverlayWindowPlus.showOverlay(
        height: 200,
        width: 300,
        alignment: OverlayAlignment.center,
        flag: OverlayFlag.defaultFlag,
        overlayTitle: "Test Overlay",
        overlayContent: "Test content",
        enableDrag: true,
        positionGravity: PositionGravity.auto,
      );
      expect(result, true);
    });

    test('closeOverlay returns true', () async {
      final result = await FlutterOverlayWindowPlus.closeOverlay();
      expect(result, true);
    });

    test('shareData returns true', () async {
      final result = await FlutterOverlayWindowPlus.shareData('test data');
      expect(result, true);
    });

    test('updateFlag returns true', () async {
      final result =
          await FlutterOverlayWindowPlus.updateFlag(OverlayFlag.defaultFlag);
      expect(result, true);
    });

    test('resizeOverlay returns true', () async {
      final result = await FlutterOverlayWindowPlus.resizeOverlay(200, 300);
      expect(result, true);
    });

    test('moveOverlay returns true', () async {
      final result = await FlutterOverlayWindowPlus.moveOverlay(
          const OverlayPosition(50, 75));
      expect(result, true);
    });

    test('getOverlayPosition returns position', () async {
      final result = await FlutterOverlayWindowPlus.getOverlayPosition();
      expect(result, isNotNull);
      expect(result!.x, 100);
      expect(result.y, 200);
    });

    test('overlayListener is not null', () {
      final listener = FlutterOverlayWindowPlus.overlayListener;
      expect(listener, isNotNull);
    });
  });

  group('OverlayPosition', () {
    test('fromMap creates correct position', () {
      final map = {'x': 50, 'y': 75};
      final position = OverlayPosition.fromMap(map);
      expect(position.x, 50);
      expect(position.y, 75);
    });

    test('toMap returns correct map', () {
      const position = OverlayPosition(100, 200);
      final map = position.toMap();
      expect(map['x'], 100);
      expect(map['y'], 200);
    });

    test('equality works correctly', () {
      const position1 = OverlayPosition(100, 200);
      const position2 = OverlayPosition(100, 200);
      const position3 = OverlayPosition(200, 100);

      expect(position1, equals(position2));
      expect(position1, isNot(equals(position3)));
    });

    test('toString returns correct format', () {
      const position = OverlayPosition(100, 200);
      expect(position.toString(), 'OverlayPosition(x: 100, y: 200)');
    });
  });

  group('WindowSize constants', () {
    test('constants have correct values', () {
      expect(WindowSize.matchParent, -1);
      expect(WindowSize.wrapContent, -2);
      expect(WindowSize.fullCover, -3);
    });
  });

  group('OverlayAlignment enum', () {
    test('has correct number of values', () {
      expect(OverlayAlignment.values.length, 9);
    });

    test('center is first value', () {
      expect(OverlayAlignment.center.index, 0);
    });

    test('all values are defined', () {
      expect(
          OverlayAlignment.values,
          containsAll([
            OverlayAlignment.center,
            OverlayAlignment.top,
            OverlayAlignment.bottom,
            OverlayAlignment.left,
            OverlayAlignment.right,
            OverlayAlignment.topLeft,
            OverlayAlignment.topRight,
            OverlayAlignment.bottomLeft,
            OverlayAlignment.bottomRight,
          ]));
    });
  });

  group('OverlayFlag enum', () {
    test('has correct number of values', () {
      expect(OverlayFlag.values.length, 3);
    });

    test('defaultFlag is first value', () {
      expect(OverlayFlag.defaultFlag.index, 0);
    });

    test('all values are defined', () {
      expect(
          OverlayFlag.values,
          containsAll([
            OverlayFlag.defaultFlag,
            OverlayFlag.focusPointer,
          ]));
    });
  });

  group('PositionGravity enum', () {
    test('has correct number of values', () {
      expect(PositionGravity.values.length, 4);
    });

    test('none is first value', () {
      expect(PositionGravity.none.index, 0);
    });

    test('all values are defined', () {
      expect(
          PositionGravity.values,
          containsAll([
            PositionGravity.none,
            PositionGravity.right,
            PositionGravity.left,
            PositionGravity.auto,
          ]));
    });
  });

  group('NotificationVisibility enum', () {
    test('has correct number of values', () {
      expect(NotificationVisibility.values.length, 3);
    });

    test('all values are defined', () {
      expect(
          NotificationVisibility.values,
          containsAll([
            NotificationVisibility.visibilityPublic,
            NotificationVisibility.visibilitySecret,
            NotificationVisibility.visibilityPrivate,
          ]));
    });
  });
}
