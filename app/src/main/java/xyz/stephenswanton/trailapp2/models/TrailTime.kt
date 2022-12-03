package xyz.stephenswanton.trailapp2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
data class TrailTime (
    var time: String?,
    var date: Date?
        ): Parcelable