package xyz.stephenswanton.trailapp2.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserInfo(
    var name: String? = "",
    var mobile: String? = "",
    var uid: String? = ""
)