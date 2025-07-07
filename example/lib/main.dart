import 'package:flutter/material.dart';
import 'package:flutter_overlay_window_plus/flutter_overlay_window_plus.dart';
import 'dart:developer' as developer;

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Overlay Window Plus Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Overlay Window Plus Demo'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  bool _isOverlayActive = false;
  String _lastEvent = 'No events yet';

  @override
  void initState() {
    super.initState();
    _listenToOverlayEvents();
  }

  void _listenToOverlayEvents() {
    FlutterOverlayWindowPlus.overlayListener.listen((event) {
      setState(() {
        _lastEvent = 'Event: ${event['event']}';
        if (event['event'] == 'overlay_shown') {
          _isOverlayActive = true;
        } else if (event['event'] == 'overlay_closed') {
          _isOverlayActive = false;
        }
      });
      developer.log('Overlay event: $event');
    });
  }

  Future<void> _checkPermission() async {
    final hasPermission = await FlutterOverlayWindowPlus.isPermissionGranted();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(hasPermission ? 'Permission granted' : 'Permission denied'),
          backgroundColor: hasPermission ? Colors.green : Colors.red,
        ),
      );
    }
  }

  Future<void> _requestPermission() async {
    final granted = await FlutterOverlayWindowPlus.requestPermission();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(granted ? 'Permission granted' : 'Please grant permission manually'),
          backgroundColor: granted ? Colors.green : Colors.orange,
        ),
      );
    }
  }

  Future<void> _showOverlay() async {
    final success = await FlutterOverlayWindowPlus.showOverlay(
      height: 200,
      width: 300,
      alignment: OverlayAlignment.center,
      flag: OverlayFlag.defaultFlag,
      overlayTitle: "Demo Overlay",
      overlayContent: "This is a demo overlay window",
      enableDrag: true,
      positionGravity: PositionGravity.auto,
    );

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(success ? 'Overlay shown' : 'Failed to show overlay'),
          backgroundColor: success ? Colors.green : Colors.red,
        ),
      );
    }
  }

  Future<void> _showClickThroughOverlay() async {
    final success = await FlutterOverlayWindowPlus.showOverlay(
      height: WindowSize.fullCover,
      width: WindowSize.matchParent,
      alignment: OverlayAlignment.center,
      flag: OverlayFlag.clickThrough,
      overlayTitle: "Click-Through Overlay",
      overlayContent: "This overlay allows clicks to pass through",
      enableDrag: false,
    );

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(success ? 'Click-through overlay shown' : 'Failed to show overlay'),
          backgroundColor: success ? Colors.green : Colors.red,
        ),
      );
    }
  }

  Future<void> _showSmallOverlay() async {
    final success = await FlutterOverlayWindowPlus.showOverlay(
      height: 100,
      width: 150,
      alignment: OverlayAlignment.topRight,
      flag: OverlayFlag.defaultFlag,
      overlayTitle: "Small Overlay",
      overlayContent: "Small overlay in top-right",
      enableDrag: true,
      positionGravity: PositionGravity.right,
    );

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(success ? 'Small overlay shown' : 'Failed to show overlay'),
          backgroundColor: success ? Colors.green : Colors.red,
        ),
      );
    }
  }

  Future<void> _closeOverlay() async {
    final success = await FlutterOverlayWindowPlus.closeOverlay();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(success ? 'Overlay closed' : 'Failed to close overlay'),
          backgroundColor: success ? Colors.green : Colors.red,
        ),
      );
    }
  }

  Future<void> _shareData() async {
    final success = await FlutterOverlayWindowPlus.shareData('Hello from main app!');
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(success ? 'Data shared' : 'Failed to share data'),
          backgroundColor: success ? Colors.green : Colors.red,
        ),
      );
    }
  }

  Future<void> _getPosition() async {
    final position = await FlutterOverlayWindowPlus.getOverlayPosition();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(position != null 
            ? 'Position: (${position.x}, ${position.y})' 
            : 'No overlay active'),
          backgroundColor: Colors.blue,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Status card
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Status',
                      style: Theme.of(context).textTheme.headlineSmall,
                    ),
                    const SizedBox(height: 8),
                    Text('Overlay Active: $_isOverlayActive'),
                    Text('Last Event: $_lastEvent'),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Permission section
            Text(
              'Permissions',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: _checkPermission,
                    child: const Text('Check Permission'),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: ElevatedButton(
                    onPressed: _requestPermission,
                    child: const Text('Request Permission'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // Overlay controls
            Text(
              'Overlay Controls',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: _isOverlayActive ? null : _showOverlay,
              child: const Text('Show Default Overlay'),
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: _isOverlayActive ? null : _showClickThroughOverlay,
              child: const Text('Show Click-Through Overlay'),
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: _isOverlayActive ? null : _showSmallOverlay,
              child: const Text('Show Small Overlay'),
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: _isOverlayActive ? _closeOverlay : null,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                foregroundColor: Colors.white,
              ),
              child: const Text('Close Overlay'),
            ),
            const SizedBox(height: 16),

            // Communication
            Text(
              'Communication',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: _isOverlayActive ? _shareData : null,
                    child: const Text('Share Data'),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: ElevatedButton(
                    onPressed: _isOverlayActive ? _getPosition : null,
                    child: const Text('Get Position'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
