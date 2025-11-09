package com.nicos.liveupdatenotification.firebase_service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nicos.liveupdatenotification.BuildConfig
import com.nicos.liveupdatenotification.R
import org.json.JSONObject

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "Firebase Channel Id"
        private const val CHANNEL_NAME = "Firebase Channel Name"
        private const val CHANNEL_DESCRIPTION = "Firebase Channel Description"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (BuildConfig.DEBUG) {
            Log.d("payload", message.data.toString())
        }

        val notificationModel = NotificationModel(JSONObject(message.data as Map<*, *>))
        Notification.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_android_black_24dp)
            setContentTitle(notificationModel.title)
            setContentText(notificationModel.body)
            setAutoCancel(true)

            // Begin Live Update
            var progressStyle: Notification.ProgressStyle? =
                getAndCreateProgressStyle(notificationModel)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA && progressStyle != null) {
                setStyle(progressStyle)
            }
            // End of Live Update

            createNotificationChannel()

            with(NotificationManagerCompat.from(this@FirebaseMessagingService)) {
                if (ActivityCompat.checkSelfPermission(
                        this@FirebaseMessagingService,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notify(1, this@apply.build())
            }
        }
    }

    private fun getAndCreateProgressStyle(notificationModel: NotificationModel): Notification.ProgressStyle? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            // Handle the segments
            val progressSegmentList: List<Notification.ProgressStyle.Segment> =
                handleProgressSegment(notificationModel)
            // Handle the points
            val progressPointList: List<Notification.ProgressStyle.Point> =
                handleProgressPoint(notificationModel)
            Notification.ProgressStyle().apply {
                setStyledByProgress(false)
                // Set the current progress
                setProgress(notificationModel.currentProgress ?: 0)
                // Set the progress tracker icon
                setProgressTrackerIcon(
                    Icon.createWithResource(
                        this@FirebaseMessagingService,
                        R.drawable.ic_android_red_24dp
                    )
                )
                // Set the segments
                setProgressSegments(
                    progressSegmentList
                )
                // Set the points
                setProgressPoints(
                    progressPointList
                )
            }
        } else {
            // TODO("VERSION.SDK_INT < BAKLAVA")
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    private fun handleProgressSegment(notificationModel: NotificationModel): List<Notification.ProgressStyle.Segment> {
        if (notificationModel.currentProgressSegmentOne != null &&
            notificationModel.currentProgressSegmentTwo != null &&
            notificationModel.currentProgressSegmentThree != null &&
            notificationModel.currentProgressSegmentFour != null
        ) {
            return listOf(
                Notification.ProgressStyle.Segment(notificationModel.currentProgressSegmentOne)
                    .setColor(this@FirebaseMessagingService.getColor(R.color.teal_200)),
                Notification.ProgressStyle.Segment(notificationModel.currentProgressSegmentTwo)
                    .setColor(this@FirebaseMessagingService.getColor(R.color.purple_200)),
                Notification.ProgressStyle.Segment(notificationModel.currentProgressSegmentThree)
                    .setColor(this@FirebaseMessagingService.getColor(R.color.purple_700)),
                Notification.ProgressStyle.Segment(notificationModel.currentProgressSegmentFour)
                    .setColor(this@FirebaseMessagingService.getColor(R.color.teal_700))

            )
        }

        return emptyList()
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    private fun handleProgressPoint(notificationModel: NotificationModel): List<Notification.ProgressStyle.Point> {
        if (notificationModel.currentProgressPointOne != null &&
            notificationModel.currentProgressPointTwo != null &&
            notificationModel.currentProgressPointThree != null
        ) {
            return listOf(
                Notification.ProgressStyle.Point(notificationModel.currentProgressPointOne)
                    .setColor(this@FirebaseMessagingService.getColor(android.R.color.holo_green_light)),
                Notification.ProgressStyle.Point(notificationModel.currentProgressPointTwo)
                    .setColor(this@FirebaseMessagingService.getColor(android.R.color.holo_green_light)),
                Notification.ProgressStyle.Point(notificationModel.currentProgressPointThree)
                    .setColor(this@FirebaseMessagingService.getColor(android.R.color.holo_green_light))
            )
        }
        return emptyList()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESCRIPTION
            with((this@FirebaseMessagingService.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)) {
                createNotificationChannel(this@apply)
            }
        }
    }
}
