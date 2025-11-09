package com.yours.live_updates

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import android.net.Uri
import android.app.Person as AndroidPerson
import android.graphics.drawable.Icon
import android.media.session.MediaSession
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import com.yours.live_updates.NotificationForegroundService

class LiveUpdateManager(private val context: Context) {
    private var plugin: LiveUpdatesPlugin? = null
    
    fun setPlugin(plugin: LiveUpdatesPlugin) {
        this.plugin = plugin
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "live_updates_channel"
        private const val CHANNEL_NAME = "Live Updates"
    }

    fun initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCustomNotification(
        notificationId: Int,
        title: String,
        text: String,
        ongoing: Boolean,
        autoCancel: Boolean,
        views: List<Map<String, Any>>,
        payload: String? = null
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.custom_notification)

        for (view in views) {
            when (view["type"]) {
                "textView" -> {
                    val textView = RemoteViews(context.packageName, R.layout.custom_notification_text)
                    textView.setTextViewText(R.id.custom_text, view["text"] as String)
                    remoteViews.addView(R.id.custom_notification_container, textView)
                }
                "scrollingTextView" -> {
                    val scrollingTextView = RemoteViews(context.packageName, R.layout.custom_notification_scrolling_text)
                    scrollingTextView.setTextViewText(R.id.custom_scrolling_text, view["text"] as String)
                    remoteViews.addView(R.id.custom_notification_container, scrollingTextView)
                }
                "imageView" -> {
                    val imageView = RemoteViews(context.packageName, R.layout.custom_notification_image)
                    val imageBytes = view["image"] as? ByteArray
                    if (imageBytes != null) {
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageView.setImageViewBitmap(R.id.custom_image, bitmap)
                        remoteViews.addView(R.id.custom_notification_container, imageView)
                    }
                }
            }
        }

        // Create a pending intent for notification click with payload
        val intent = Intent(context, context.javaClass).apply {
            action = "com.yours.live_updates.NOTIFICATION_CLICKED"
            putExtra("payload", payload)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(ongoing)
            .setAutoCancel(autoCancel)
            .setCustomContentView(remoteViews)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, builder.build())
        
        // Send payload to Flutter if notification is clicked
        if (payload != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(object : android.content.BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == "com.yours.live_updates.NOTIFICATION_CLICKED") {
                            val receivedPayload = intent.getStringExtra("payload")
                            plugin?.sendPayload(receivedPayload)
                        }
                    }
                }, android.content.IntentFilter("com.yours.live_updates.NOTIFICATION_CLICKED"), android.content.Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(object : android.content.BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == "com.yours.live_updates.NOTIFICATION_CLICKED") {
                            val receivedPayload = intent.getStringExtra("payload")
                            plugin?.sendPayload(receivedPayload)
                        }
                    }
                }, android.content.IntentFilter("com.yours.live_updates.NOTIFICATION_CLICKED"))
            }
        }
    }

    fun showNotification(
        title: String,
        text: String,
        notificationId: Int,
        ongoing: Boolean,
        autoCancel: Boolean,
        subText: String?,
        category: String?,
        fullScreen: Boolean,
        largeIcon: ByteArray?,
        progress: Int?,
        progressMax: Int?,
        progressIndeterminate: Boolean,
        payload: String? = null
    ) {
        // Create a pending intent for notification click with payload
        val intent = Intent(context, context.javaClass).apply {
            action = "com.yours.live_updates.NOTIFICATION_CLICKED"
            putExtra("payload", payload)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(ongoing)
            .setAutoCancel(autoCancel)
            .setContentIntent(pendingIntent)

        if (subText != null) {
            builder.setSubText(subText)
        }

        if (category != null) {
            builder.setCategory(category)
        }

        if (largeIcon != null) {
            val bitmap = BitmapFactory.decodeByteArray(largeIcon, 0, largeIcon.size)
            builder.setLargeIcon(bitmap)
        }

        // Apply different styles based on category
        when (category) {
            "call" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Use CallStyle for Android 12+ (API 31+)
                    val person = androidx.core.app.Person.Builder()
                        .setName(title)
                        .build()
                    
                    val callStyle = NotificationCompat.CallStyle.forIncomingCall(
                        person,
                        PendingIntent.getActivity(
                            context,
                            notificationId + 100,
                            Intent(context, context.javaClass).apply { action = "ANSWER_CALL" },
                            PendingIntent.FLAG_IMMUTABLE
                        ),
                        PendingIntent.getActivity(
                            context,
                            notificationId + 200,
                            Intent(context, context.javaClass).apply { action = "DECLINE_CALL" },
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    builder.setStyle(callStyle)
                    
                    // Always set fullScreenIntent for CallStyle notifications to meet Android requirements
                    val fullScreenIntent = Intent(context, context.javaClass)
                    val fullScreenPendingIntent = PendingIntent.getActivity(
                        context,
                        notificationId + 300,
                        fullScreenIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    builder.setFullScreenIntent(fullScreenPendingIntent, true)
                    
                    // Set high priority and category for call notifications
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                    builder.setCategory(NotificationCompat.CATEGORY_CALL)
                    
                    // Start foreground service for call notifications
                    val serviceIntent = Intent(context, NotificationForegroundService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
            }
            "progress" -> {
                if (progress != null && progressMax != null) {
                    builder.setProgress(progressMax, progress, progressIndeterminate)
                    // Use BigTextStyle for progress visualization
                     val progressStyle = NotificationCompat.BigTextStyle()
                         .bigText(text)
                         .setBigContentTitle(title)
                     builder.setStyle(progressStyle)
                }
            }
            else -> {
                // Use BigTextStyle as default
                val bigTextStyle = NotificationCompat.BigTextStyle()
                    .bigText(text)
                    .setBigContentTitle(title)
                builder.setStyle(bigTextStyle)
            }
        }

        if (fullScreen) {
            val fullScreenIntent = Intent(context, context.javaClass)
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }

        notificationManager.notify(notificationId, builder.build())
        
        // Send payload to Flutter if notification is clicked
        if (payload != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(object : android.content.BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == "com.yours.live_updates.NOTIFICATION_CLICKED") {
                            val receivedPayload = intent.getStringExtra("payload")
                            plugin?.sendPayload(receivedPayload)
                        }
                    }
                }, android.content.IntentFilter("com.yours.live_updates.NOTIFICATION_CLICKED"), android.content.Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(object : android.content.BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == "com.yours.live_updates.NOTIFICATION_CLICKED") {
                            val receivedPayload = intent.getStringExtra("payload")
                            plugin?.sendPayload(receivedPayload)
                        }
                    }
                }, android.content.IntentFilter("com.yours.live_updates.NOTIFICATION_CLICKED"))
            }
        }
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}