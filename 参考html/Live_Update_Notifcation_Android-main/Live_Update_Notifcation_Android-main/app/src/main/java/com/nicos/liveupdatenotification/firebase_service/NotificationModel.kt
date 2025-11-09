package com.nicos.liveupdatenotification.firebase_service

import org.json.JSONObject

data class NotificationModel(
    var title: String? = null,
    var body: String? = null,
    val currentProgress: Int? = null,
    val currentProgressSegmentOne: Int? = null,
    val currentProgressSegmentTwo: Int? = null,
    val currentProgressSegmentThree: Int? = null,
    val currentProgressSegmentFour: Int? = null,
    val currentProgressPointOne: Int? = null,
    val currentProgressPointTwo: Int? = null,
    val currentProgressPointThree: Int? = null
) {
    // Parse the data
    constructor(jsonObject: JSONObject) : this(
        jsonObject.optString("title"),
        jsonObject.optString("body"),
        jsonObject.optInt("currentProgress"),
        jsonObject.optInt("currentProgressSegmentOne"),
        jsonObject.optInt("currentProgressSegmentTwo"),
        jsonObject.optInt("currentProgressSegmentThree"),
        jsonObject.optInt("currentProgressSegmentFour"),
        jsonObject.optInt("currentProgressPointOne"),
        jsonObject.optInt("currentProgressPointTwo"),
        jsonObject.optInt("currentProgressPointThree")
    )

    // Empty Constructor
    constructor() : this(
        "",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )
}