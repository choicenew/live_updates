package com.yours.live_updates

import com.yours.live_updates.LiveUpdateManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.EventChannel.StreamHandler

/** LiveUpdatesPlugin */
class LiveUpdatesPlugin : FlutterPlugin, MethodCallHandler, StreamHandler {
    private lateinit var channel: MethodChannel
    private lateinit var payloadChannel: EventChannel
    private lateinit var liveUpdateManager: LiveUpdateManager
    private var eventSink: EventSink? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "live_updates")
        channel.setMethodCallHandler(this)
        
        payloadChannel = EventChannel(flutterPluginBinding.binaryMessenger, "live_updates/payload")
        payloadChannel.setStreamHandler(this)
        
        liveUpdateManager = LiveUpdateManager(flutterPluginBinding.applicationContext)
        liveUpdateManager.setPlugin(this)
        liveUpdateManager.initialize()
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "showCustomNotification" -> {
                try {
                    val notificationId = call.argument<Int>("notificationId") ?: 0
                    val title = call.argument<String>("title") ?: ""
                    val text = call.argument<String>("text") ?: ""
                    val ongoing = call.argument<Boolean>("ongoing") ?: false
                    val autoCancel = call.argument<Boolean>("autoCancel") ?: true
                    val views = call.argument<List<Map<String, Any>>>("views") ?: emptyList()
                    val payload = call.argument<String>("payload")

                    liveUpdateManager.showCustomNotification(
                        notificationId,
                        title,
                        text,
                        ongoing,
                        autoCancel,
                        views,
                        payload
                    )
                    result.success(null)
                } catch (e: Exception) {
                    result.error("showCustomNotification_error", e.message, null)
                }
            }
            "showNotification" -> {
                try {
                    val title = call.argument<String>("title") ?: ""
                    val text = call.argument<String>("text") ?: ""
                    val notificationId = call.argument<Int>("notificationId") ?: 0
                    val ongoing = call.argument<Boolean>("ongoing") ?: false
                    val autoCancel = call.argument<Boolean>("autoCancel") ?: true
                    val subText = call.argument<String>("subText")
                    val category = call.argument<String>("category")
                    val fullScreen = call.argument<Boolean>("fullScreen") ?: false
                    val largeIcon = call.argument<ByteArray>("largeIcon")
                    val progress = call.argument<Int>("progress")
                    val progressMax = call.argument<Int>("progressMax")
                    val progressIndeterminate = call.argument<Boolean>("progressIndeterminate") ?: false
                    val payload = call.argument<String>("payload")

                    liveUpdateManager.showNotification(
                        title,
                        text,
                        notificationId,
                        ongoing,
                        autoCancel,
                        subText,
                        category,
                        fullScreen,
                        largeIcon,
                        progress,
                        progressMax,
                        progressIndeterminate,
                        payload
                    )
                    result.success(null)
                } catch (e: Exception) {
                    result.error("showNotification_error", e.message, null)
                }
            }
            "cancelNotification" -> {
                try {
                    val notificationId = call.argument<Int>("notificationId") ?: 0
                    liveUpdateManager.cancelNotification(notificationId)
                    result.success(null)
                } catch (e: Exception) {
                    result.error("cancelNotification_error", e.message, null)
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        payloadChannel.setStreamHandler(null)
    }
    
    override fun onListen(arguments: Any?, events: EventSink?) {
        eventSink = events
    }
    
    override fun onCancel(arguments: Any?) {
        eventSink = null
    }
    
    fun sendPayload(payload: String?) {
        eventSink?.success(payload)
    }
}
