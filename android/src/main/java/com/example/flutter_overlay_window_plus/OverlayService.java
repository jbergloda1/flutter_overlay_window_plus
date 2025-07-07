package com.example.flutter_overlay_window_plus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import io.flutter.plugin.common.EventChannel;
import java.util.HashMap;
import java.util.Map;

public class OverlayService extends Service {
    private static final String TAG = "OverlayService";
    private static final String CHANNEL_ID = "overlay_service_channel";
    private static final int NOTIFICATION_ID = 1;
    
    private WindowManager windowManager;
    private View overlayView;
    private WindowManager.LayoutParams params;
    private static EventChannel.EventSink eventSink;
    
    // Overlay properties
    private int overlayWidth = WindowManager.LayoutParams.MATCH_PARENT;
    private int overlayHeight = WindowManager.LayoutParams.MATCH_PARENT;
    private int alignment = Gravity.CENTER;
    private int flag = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    private boolean enableDrag = false;
    private int positionGravity = 0; // 0: none, 1: right, 2: left, 3: auto
    
    // Drag variables
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    private boolean isDragging = false;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, String flags, int startId) {
        if (intent != null && "CLOSE_OVERLAY".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        // Get overlay properties from intent
        overlayWidth = intent.getIntExtra("width", WindowManager.LayoutParams.MATCH_PARENT);
        overlayHeight = intent.getIntExtra("height", WindowManager.LayoutParams.MATCH_PARENT);
        alignment = getAlignment(intent.getIntExtra("alignment", 0));
        flag = getFlag(intent.getIntExtra("flag", 1));
        enableDrag = intent.getBooleanExtra("enableDrag", false);
        positionGravity = intent.getIntExtra("positionGravity", 0);
        
        String overlayTitle = intent.getStringExtra("overlayTitle");
        String overlayContent = intent.getStringExtra("overlayContent");
        
        // Get start position if provided
        int startX = intent.getIntExtra("startX", -1);
        int startY = intent.getIntExtra("startY", -1);

        showOverlay(overlayTitle, overlayContent, startX, startY);
        startForeground(NOTIFICATION_ID, createNotification(overlayTitle, overlayContent));
        
        return START_STICKY;
    }

    private void showOverlay(String title, String content, int startX, int startY) {
        try {
            // Create overlay view
            overlayView = createOverlayView(title, content);
            
            // Create window parameters
            params = new WindowManager.LayoutParams(
                overlayWidth,
                overlayHeight,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                flag,
                PixelFormat.TRANSLUCENT
            );
            
            // Set initial position
            if (startX >= 0 && startY >= 0) {
                params.x = startX;
                params.y = startY;
            } else {
                params.gravity = alignment;
            }
            
            // Add overlay to window
            windowManager.addView(overlayView, params);
            
            // Send event to Flutter
            sendEvent("overlay_shown", null);
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing overlay: " + e.getMessage());
        }
    }

    private View createOverlayView(String title, String content) {
        FrameLayout container = new FrameLayout(this);
        container.setBackgroundColor(0x80000000); // Semi-transparent background
        
        TextView textView = new TextView(this);
        textView.setText(content != null ? content : title);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(16);
        textView.setPadding(20, 20, 20, 20);
        
        container.addView(textView);
        
        if (enableDrag) {
            container.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            isDragging = true;
                            return true;
                            
                        case MotionEvent.ACTION_MOVE:
                            if (isDragging) {
                                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                                windowManager.updateViewLayout(overlayView, params);
                            }
                            return true;
                            
                        case MotionEvent.ACTION_UP:
                            isDragging = false;
                            applyPositionGravity();
                            sendEvent("overlay_moved", createPositionMap());
                            return true;
                    }
                    return false;
                }
            });
        }
        
        return container;
    }

    private void applyPositionGravity() {
        if (positionGravity == 0) return; // none
        
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int overlayWidth = overlayView.getWidth();
        
        switch (positionGravity) {
            case 1: // right
                params.x = screenWidth - overlayWidth;
                break;
            case 2: // left
                params.x = 0;
                break;
            case 3: // auto
                if (params.x + overlayWidth / 2 > screenWidth / 2) {
                    params.x = screenWidth - overlayWidth;
                } else {
                    params.x = 0;
                }
                break;
        }
        
        windowManager.updateViewLayout(overlayView, params);
    }

    private int getAlignment(int alignmentIndex) {
        switch (alignmentIndex) {
            case 0: return Gravity.CENTER;
            case 1: return Gravity.TOP;
            case 2: return Gravity.BOTTOM;
            case 3: return Gravity.LEFT;
            case 4: return Gravity.RIGHT;
            case 5: return Gravity.TOP | Gravity.LEFT;
            case 6: return Gravity.TOP | Gravity.RIGHT;
            case 7: return Gravity.BOTTOM | Gravity.LEFT;
            case 8: return Gravity.BOTTOM | Gravity.RIGHT;
            default: return Gravity.CENTER;
        }
    }

    private int getFlag(int flagIndex) {
        switch (flagIndex) {
            case 0: return WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            case 1: return WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            case 2: return WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            default: return WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Overlay window service");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(String title, String content) {
        Intent notificationIntent = new Intent(this, getApplication().getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
        }
        sendEvent("overlay_closed", null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Static methods for communication with Flutter
    public static void setEventSink(EventChannel.EventSink sink) {
        eventSink = sink;
    }

    private void sendEvent(String event, Map<String, Object> data) {
        if (eventSink != null) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("event", event);
            if (data != null) {
                eventData.putAll(data);
            }
            eventSink.success(eventData);
        }
    }

    private Map<String, Object> createPositionMap() {
        Map<String, Object> position = new HashMap<>();
        position.put("x", params.x);
        position.put("y", params.y);
        return position;
    }

    // Public static methods for Flutter communication
    public static void shareData(String data) {
        // Implementation for sharing data
        Log.d(TAG, "Shared data: " + data);
    }

    public static void updateFlag(int flag) {
        // Implementation for updating flag
        Log.d(TAG, "Updated flag: " + flag);
    }

    public static void resizeOverlay(int width, int height) {
        // Implementation for resizing overlay
        Log.d(TAG, "Resize overlay: " + width + "x" + height);
    }

    public static void moveOverlay(int x, int y) {
        // Implementation for moving overlay
        Log.d(TAG, "Move overlay to: " + x + ", " + y);
    }

    public static int[] getOverlayPosition() {
        // Implementation for getting overlay position
        return new int[]{0, 0}; // Placeholder
    }
} 