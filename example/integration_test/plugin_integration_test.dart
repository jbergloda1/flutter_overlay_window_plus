// This is a basic Flutter integration test.
//
// Since integration tests run in a full Flutter application, they can interact
// with the host side of a plugin implementation, unlike Dart unit tests.
//
// For more information about Flutter integration tests, please see
// https://flutter.dev/to/integration-testing

import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

import 'package:flutter_overlay_window_plus/flutter_overlay_window_plus.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('permission test', (WidgetTester tester) async {
    // Test permission checking
    final bool hasPermission = await FlutterOverlayWindowPlus.isPermissionGranted();
    expect(hasPermission, isA<bool>());
  });

  testWidgets('overlay listener test', (WidgetTester tester) async {
    // Test that overlay listener is available
    final listener = FlutterOverlayWindowPlus.overlayListener;
    expect(listener, isNotNull);
  });
}
