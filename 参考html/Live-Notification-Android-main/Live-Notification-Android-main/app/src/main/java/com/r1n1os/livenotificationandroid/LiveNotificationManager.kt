package com.r1n1os.livenotificationandroid

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.toColorInt


object LiveNotificationManager {
    private lateinit var notificationManager: NotificationManager
    private lateinit var appContext: Context

    const val CHANNEL_ID = "live_updates_channel_id"
    private const val CHANNEL_NAME = "live_updates_channel_name"
    private const val NOTIFICATION_ID = 1234

    private const val FIRST_SEGMENT = 50
    private const val SECOND_SEGMENT = 50
    private const val THIRD_SEGMENT = 50
    private const val FIRST_POINT = 50
    private const val SECOND_POINT = 100

    private var trackingSteps = listOf<TrackingModel>()

    fun initialize(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        this.notificationManager = notificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT)
        appContext = context
        this.notificationManager.createNotificationChannel(channel)
    }

    fun showSegmentLiveNotification() {
        trackingSteps = buildTrackingModelList()
        showNotification()
    }

    private fun buildTrackingModelList(): List<TrackingModel> {
        return  listOf<TrackingModel>(
            TrackingModel(
                timeNeeded = 200,
                builder = Notification.Builder(appContext, CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setContentTitle("Route Accepted")
                    setContentText("Driver is on the way to you")
                    setAutoCancel(true)

                    // Building ProgressStyle for step one
                    var progressStyle: Notification.ProgressStyle? =
                        buildProgressStyle(
                            0,
                            "#28A745".toColorInt(),
                            "#8BC598".toColorInt(),
                            "#8BC598".toColorInt(),
                        )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA && progressStyle != null) {
                        style = progressStyle
                    }
                }
            ),
            TrackingModel(
                timeNeeded = 3000,
                builder = Notification.Builder(appContext, CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setContentTitle("Driver arrived")
                    setContentText("Driver is waiting for you")
                    setAutoCancel(true)

                    // Building ProgressStyle for step two
                    var progressStyle: Notification.ProgressStyle? =
                        buildProgressStyle(
                            55,
                            "#28A745".toColorInt(),
                            "#28A745".toColorInt(),
                            "#8BC598".toColorInt(),
                        )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA && progressStyle != null) {
                        style = progressStyle
                    }
                }
            ),
            TrackingModel(
                timeNeeded = 5000,
                builder = Notification.Builder(appContext, CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setContentTitle("Arrived")
                    setContentText("You have arrived at your destination")
                    setAutoCancel(true)

                    // Building ProgressStyle for step three
                    var progressStyle: Notification.ProgressStyle? =
                        buildProgressStyle(
                            150,
                            "#28A745".toColorInt(),
                            "#28A745".toColorInt(),
                            "#28A745".toColorInt(),
                        )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA && progressStyle != null) {
                        style = progressStyle
                    }
                }
            ),
        )
    }

    fun showPointsLiveNotification() {
        trackingSteps = listOf<TrackingModel>(
            TrackingModel(
                timeNeeded = 200,
                builder = Notification.Builder(appContext, CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setContentTitle("Route Accepted")
                    setContentText("Driver is on the way to you")

                    // Building ProgressStyle for step one
                    var progressStyle: Notification.ProgressStyle? =
                        buildProgressStyle(
                            0,
                            "#28A745".toColorInt(),
                            "#8BC598".toColorInt(),
                            "#8BC598".toColorInt(),
                            true,
                        )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA && progressStyle != null) {
                        style = progressStyle
                    }
                }
            ),
            TrackingModel(
                timeNeeded = 3000,
                builder = Notification.Builder(appContext, CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setContentTitle("Driver arrived")
                    setContentText("Driver is waiting for you")
                    setAutoCancel(true)

                    // Building ProgressStyle for step two
                    var progressStyle: Notification.ProgressStyle? =
                        buildProgressStyle(
                            75,
                            "#28A745".toColorInt(),
                            "#28A745".toColorInt(),
                            "#8BC598".toColorInt(),
                            true,
                        )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA && progressStyle != null) {
                        style = progressStyle
                    }
                }
            ),
            TrackingModel(
                timeNeeded = 5000,
                builder = Notification.Builder(appContext, CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setContentTitle("Arrived")
                    setContentText("You have arrived at your destination")
                    setAutoCancel(true)

                    // Building ProgressStyle for step three
                    var progressStyle: Notification.ProgressStyle? =
                        buildProgressStyle(
                            150,
                            "#28A745".toColorInt(),
                            "#28A745".toColorInt(),
                            "#28A745".toColorInt(),
                            true,
                        )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA && progressStyle != null) {
                        style = progressStyle
                    }
                }
            ),
        )
        showNotification()
    }

    private fun showNotification() {
        for (trackStep in trackingSteps) {
            Handler(Looper.getMainLooper()).postDelayed({
                with(NotificationManagerCompat.from(appContext)) {
                    if (ActivityCompat.checkSelfPermission(
                            appContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@with
                    }
                    notificationManager.notify(NOTIFICATION_ID, trackStep.builder.build())
                }
            }, trackStep.timeNeeded)
        }

    }

    private fun buildProgressStyle(
        currentProgress: Int,
        segmentOneColor: Int,
        segmentTwoColor: Int,
        segmentThreeColor: Int,
        isWithProgressPoints: Boolean = false
    ): Notification.ProgressStyle? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            // Creating the progress segments style
            val progressSegmentList: List<Notification.ProgressStyle.Segment> =
                handleProgressSegment(
                    segmentOneColor,
                    segmentTwoColor,
                    segmentThreeColor
                )

            Notification.ProgressStyle().apply {
                isStyledByProgress = false
                progress = currentProgress
                progressSegments = progressSegmentList
                progressStartIcon = Icon.createWithResource(
                    appContext,
                    R.drawable.driver
                )
                progressEndIcon = Icon.createWithResource(
                    appContext,
                    R.drawable.location_pin
                )
                // Creating the progress points style
                if (isWithProgressPoints) {
                    var progressPointList: List<Notification.ProgressStyle.Point>? = null
                    progressPointList = handleProgressPoint(
                        segmentTwoColor,
                        segmentThreeColor
                    )
                    progressPoints = progressPointList
                }
            }
        } else {
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    private fun handleProgressSegment(
        segmentOneColor: Int,
        segmentTwoColor: Int,
        segmentThreeColor: Int,
    ): List<Notification.ProgressStyle.Segment> {
        return listOf(
            Notification.ProgressStyle.Segment(FIRST_SEGMENT)
                .setColor(segmentOneColor),
            Notification.ProgressStyle.Segment(SECOND_SEGMENT)
                .setColor(segmentTwoColor),
            Notification.ProgressStyle.Segment(THIRD_SEGMENT)
                .setColor(segmentThreeColor),
            )
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    private fun handleProgressPoint(
        pointOneColor: Int,
        pointTwoColor: Int,
    ): List<Notification.ProgressStyle.Point> {
        return listOf(
            Notification.ProgressStyle.Point(FIRST_POINT)
                .setColor(pointOneColor),
            Notification.ProgressStyle.Point(SECOND_POINT)
                .setColor(pointTwoColor)
        )
    }
}