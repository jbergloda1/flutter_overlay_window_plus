/// Represents the position of an overlay window
class OverlayPosition {
  /// X coordinate
  final int x;

  /// Y coordinate
  final int y;

  const OverlayPosition(this.x, this.y);

  /// Create OverlayPosition from map
  factory OverlayPosition.fromMap(Map<String, dynamic> map) {
    return OverlayPosition(
      map['x'] as int? ?? 0,
      map['y'] as int? ?? 0,
    );
  }

  /// Convert to map
  Map<String, dynamic> toMap() {
    return {
      'x': x,
      'y': y,
    };
  }

  @override
  String toString() {
    return 'OverlayPosition(x: $x, y: $y)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is OverlayPosition && other.x == x && other.y == y;
  }

  @override
  int get hashCode => x.hashCode ^ y.hashCode;
}
