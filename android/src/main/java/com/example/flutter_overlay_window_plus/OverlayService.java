package com.example.flutter_overlay_window_plus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
    private static OverlayService instance;
    private TextView textView;
    
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

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Immediately call startForeground to avoid ANR
        startForeground(NOTIFICATION_ID, createNotification("Overlay Service", "Initializing..."));

        if (intent == null) {
            return START_STICKY;
        }

        if ("CLOSE_OVERLAY".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        // Extract data from intent
        overlayWidth = intent.getIntExtra("width", WindowManager.LayoutParams.MATCH_PARENT);
        overlayHeight = intent.getIntExtra("height", WindowManager.LayoutParams.MATCH_PARENT);
        alignment = getAlignment(intent.getIntExtra("alignment", 0));
        flag = getFlag(intent.getIntExtra("flag", 1));
        enableDrag = intent.getBooleanExtra("enableDrag", false);
        positionGravity = intent.getIntExtra("positionGravity", 0);
        
        String overlayTitle = intent.getStringExtra("overlayTitle");
        String overlayContent = intent.getStringExtra("overlayContent");
        
        int startX = intent.getIntExtra("startX", -1);
        int startY = intent.getIntExtra("startY", -1);

        // Show the overlay view
        showOverlay(overlayTitle, overlayContent, startX, startY);

        // Update the notification with the correct content
        Notification updatedNotification = createNotification(overlayTitle, overlayContent);
        notificationManager.notify(NOTIFICATION_ID, updatedNotification);

        return START_STICKY;
    }

    private void showOverlay(String title, String content, int startX, int startY) {
        try {
            // If an overlay view already exists, remove it first to prevent duplicates
            if (overlayView != null && windowManager != null) {
                try {
                    windowManager.removeView(overlayView);
                } catch (IllegalArgumentException e) {
                    // This can happen if the view is already gone. Ignore.
                    Log.w(TAG, "Tried to remove a view that was not attached: " + e.getMessage());
                }
            }

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
        // Use a RelativeLayout to easily position the close button
        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(Color.parseColor("#80000000")); // Semi-transparent black

        // Content TextView
        textView = new TextView(this);
        textView.setText(content != null ? content : title);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setPadding(40, 40, 40, 40); // Increased padding
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textView.setLayoutParams(textParams);

        // Close Button
        Button closeButton = new Button(this);
        closeButton.setText("X");
        closeButton.setTextColor(Color.WHITE);
        closeButton.setBackgroundColor(Color.TRANSPARENT);
        closeButton.setOnClickListener(v -> {
            // Re-launch the app
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchIntent);
            }
            // Stop the service
            stopSelf();
        });

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeButton.setLayoutParams(buttonParams);

        layout.addView(textView);
        layout.addView(closeButton);
        
        if (enableDrag) {
            layout.setOnTouchListener(new View.OnTouchListener() {
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
        
        return layout;
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
            case 0: return WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // defaultFlag
            case 1: return WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; // focusPointer
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
        Intent notificationIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String notificationTitle = (title == null) ? "Overlay Active" : title;
        String notificationContent = (content == null) ? "Tap to open the app" : content;

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
        }
        sendEvent("overlay_closed", null);
    }

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

    // Public method to update overlay text
    public void updateOverlayText(final String text) {
        if (overlayView != null && textView != null) {
            overlayView.post(() -> textView.setText(text));
        }
    }

    // Public static methods for Flutter communication
    public static void shareData(String data) {
        if (instance != null) {
            instance.updateOverlayText(data);
        }
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