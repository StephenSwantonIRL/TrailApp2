package xyz.stephenswanton.trailapp2.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Trail(
    var id: Long = 0L,
    var name: String,
    var description: String? = null,
    var distance: Double? = null,
    var trailType: String = "",
    var times: MutableList<TrailTime> = mutableListOf(),
    var markers: MutableList<TrailMarker> = mutableListOf()
) : Parcelable