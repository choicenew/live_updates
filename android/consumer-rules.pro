# Keep the BroadcastReceiver that is triggered by the system when a notification is clicked.
-keep class com.yours.live_updates.NotificationClickReceiver { *; }

# Keep the ForegroundService that is required for certain notification styles.
-keep class com.yours.live_updates.NotificationForegroundService { *; }