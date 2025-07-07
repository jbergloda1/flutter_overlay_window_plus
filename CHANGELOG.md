## 1.0.0

*   **Breaking Change**: The plugin now ensures only a single overlay can be active at one time. Calling `showOverlay` while an overlay is already visible will replace the existing one.
*   **Feature**: Added `minimizeApp()` to programmatically send the application to the background.
*   **Feature**: The overlay can now be updated with live data from the main app using `shareData()`.
*   **Feature**: Closing the overlay from the 'X' button will now bring the minimized application back to the foreground.
*   **Docs**: Updated `README.md` with all new features and a clearer API reference.
*   **Example**: The example app now demonstrates live data streaming to the overlay.
*   Initial release.
