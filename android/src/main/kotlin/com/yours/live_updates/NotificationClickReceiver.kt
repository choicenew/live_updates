package com.yours.live_updates

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val payload = intent.getStringExtra("payload")
        
        // 通过静态方法将 payload 发送回 Flutter
        LiveUpdatesPlugin.sendPayload(payload)

        // 照常启动 App
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            launchIntent.putExtra("payload", payload)
            context.startActivity(launchIntent)
        }
    }
}
