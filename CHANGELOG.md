## 1.0.1

*   **Breaking Change**: Complete rewrite of overlay system to support custom Flutter widgets instead of simple text content.
*   **Breaking Change**: Removed `overlayTitle`, `overlayContent`, and `overlayTextColor` parameters from `showOverlay()` method.
*   **Feature**: Overlay now uses a dedicated FlutterEngine to render custom widgets defined in `overlayMain()` function.
*   **Feature**: Added support for `@pragma("vm:entry-point")` annotation for overlay entry point.
*   **Feature**: Implemented BasicMessageChannel for data communication between main app and overlay widget.
*   **Improvement**: Overlay widget can now be fully customized with any Flutter widgets and supports state management.
*   **Improvement**: Better separation between overlay engine and main app engine for improved performance.
*   **Example**: Updated example app to demonstrate custom overlay widget with live data streaming.
*   **Test**: Updated test files to match new API without deprecated parameters.

## 1.0.0

*   **Breaking Change**: The plugin now ensures only a single overlay can be active at one time. Calling `showOverlay` while an overlay is already visible will replace the existing one.
*   **Feature**: Added `minimizeApp()` to programmatically send the application to the background.
*   **Feature**: The overlay can now be updated with live data from the main app using `shareData()`.
*   **Feature**: Closing the overlay from the 'X' button will now bring the minimized application back to the foreground.
*   **Docs**: Updated `README.md` with all new features and a clearer API reference.
*   **Example**: The example app now demonstrates live data streaming to the overlay.
*   Initial release.
