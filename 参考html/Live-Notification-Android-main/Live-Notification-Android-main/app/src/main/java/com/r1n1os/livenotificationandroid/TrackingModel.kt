package com.r1n1os.livenotificationandroid

import android.app.Notification

data class TrackingModel(
    val timeNeeded: Long,
    val builder:  Notification.Builder
)
