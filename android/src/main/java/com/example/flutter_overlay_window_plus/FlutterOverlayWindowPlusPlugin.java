package com.example.flutter_overlay_window_plus;

import android.app.Activity;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.EventChannel;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/** FlutterOverlayWindowPlusPlugin */
public class FlutterOverlayWindowPlusPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private EventChannel eventChannel;
  private Context context;
  private Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_overlay_window_plus");
    channel.setMethodCallHandler(this);
    
    eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_overlay_window_plus_events");
    eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object arguments, EventChannel.EventSink events) {
        OverlayService.setEventSink(events);
      }

      @Override
      public void onCancel(Object arguments) {
        OverlayService.setEventSink(null);
      }
    });
    
    context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "isPermissionGranted":
        result.success(isPermissionGranted());
        break;
      case "requestPermission":
        requestPermission(result);
        break;
      case "showOverlay":
        showOverlay(call, result);
        break;
      case "closeOverlay":
        closeOverlay(result);
        break;
      case "shareData":
        shareData(call, result);
        break;
      case "updateFlag":
        updateFlag(call, result);
        break;
      case "resizeOverlay":
        resizeOverlay(call, result);
        break;
      case "moveOverlay":
        moveOverlay(call, result);
        break;
      case "getOverlayPosition":
        getOverlayPosition(result);
        break;
      case "minimizeApp":
        minimizeApp(result);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private boolean isPermissionGranted() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return Settings.canDrawOverlays(context);
    }
    return true;
  }

  private void requestPermission(Result result) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!Settings.canDrawOverlays(context)) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        result.success(false);
      } else {
        result.success(true);
      }
    } else {
      result.success(true);
    }
  }

  private void showOverlay(MethodCall call, Result result) {
    if (!isPermissionGranted()) {
      result.success(false);
      return;
    }

    try {
      Intent intent = new Intent(context, OverlayService.class);
      intent.putExtra("height", call.argument("height") != null ? ((Number) call.argument("height")).intValue() : 0);
      intent.putExtra("width", call.argument("width") != null ? ((Number) call.argument("width")).intValue() : 0);
      intent.putExtra("alignment", call.argument("alignment") != null ? ((Number) call.argument("alignment")).intValue() : 0);
      intent.putExtra("visibility", call.argument("visibility") != null ? ((Number) call.argument("visibility")).intValue() : 0);
      intent.putExtra("flag", call.argument("flag") != null ? ((Number) call.argument("flag")).intValue() : 0);
      intent.putExtra("overlayTitle", (String) call.argument("overlayTitle"));
      intent.putExtra("overlayContent", (String) call.argument("overlayContent"));
      intent.putExtra("enableDrag", call.argument("enableDrag") != null ? (Boolean) call.argument("enableDrag") : false);
      intent.putExtra("positionGravity", call.argument("positionGravity") != null ? ((Number) call.argument("positionGravity")).intValue() : 0);
      
      if (call.argument("startPosition") != null) {
        java.util.Map startPosition = (java.util.Map) call.argument("startPosition");
        intent.putExtra("startX", startPosition.get("x") != null ? ((Number) startPosition.get("x")).intValue() : 0);
        intent.putExtra("startY", startPosition.get("y") != null ? ((Number) startPosition.get("y")).intValue() : 0);
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent);
      } else {
        context.startService(intent);
      }
      
      result.success(true);
    } catch (Exception e) {
      Log.e("FlutterOverlayWindowPlus", "Error showing overlay: " + e.getMessage());
      result.success(false);
    }
  }

  private void closeOverlay(Result result) {
    try {
      Intent intent = new Intent(context, OverlayService.class);
      intent.setAction("CLOSE_OVERLAY");
      context.stopService(intent);
      result.success(true);
    } catch (Exception e) {
      Log.e("FlutterOverlayWindowPlus", "Error closing overlay: " + e.getMessage());
      result.success(false);
    }
  }

  private void shareData(MethodCall call, Result result) {
    try {
      String data = call.argument("data");
      OverlayService.shareData(data);
      result.success(true);
    } catch (Exception e) {
      Log.e("FlutterOverlayWindowPlus", "Error sharing data: " + e.getMessage());
      result.success(false);
    }
  }

  private void updateFlag(MethodCall call, Result result) {
    try {
      int flag = call.argument("flag");
      OverlayService.updateFlag(flag);
      result.success(true);
    } catch (Exception e) {
      Log.e("FlutterOverlayWindowPlus", "Error updating flag: " + e.getMessage());
      result.success(false);
    }
  }

  private void resizeOverlay(MethodCall call, Result result) {
    try {
      int width = call.argument("width");
      int height = call.argument("height");
      OverlayService.resizeOverlay(width, height);
      result.success(true);
    } catch (Exception e) {
      Log.e("FlutterOverlayWindowPlus", "Error resizing overlay: " + e.getMessage());
      result.success(false);
    }
  }

  private void moveOverlay(MethodCall call, Result result) {
    try {
      java.util.Map position = call.argument("position");
      int x = (int) position.get("x");
      int y = (int) position.get("y");
      OverlayService.moveOverlay(x, y);
      result.success(true);
    } catch (Exception e) {
      Log.e("FlutterOverlayWindowPlus", "Error moving overlay: " + e.getMessage());
      result.success(false);
    }
  }

  private void getOverlayPosition(Result result) {
    try {
      int[] position = OverlayService.getOverlayPosition();
      if (position != null) {
        java.util.Map<String, Integer> positionMap = new java.util.HashMap<>();
        positionMap.put("x", position[0]);
        positionMap.put("y", position[1]);
        result.success(positionMap);
      } else {
        result.success(null);
      }
    } catch (Exception e) {
      Log.e("FlutterOverlayWindowPlus", "Error getting overlay position: " + e.getMessage());
      result.success(null);
    }
  }

  private void minimizeApp(Result result) {
    if (activity != null) {
      activity.moveTaskToBack(true);
      result.success(true);
    } else {
      result.error("NO_ACTIVITY", "The plugin is not attached to an activity.", null);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    eventChannel.setStreamHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    this.activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    this.activity = null;
  }
} 