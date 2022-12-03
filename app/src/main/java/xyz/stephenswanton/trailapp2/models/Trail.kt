package xyz.stephenswanton.trailapp2.models
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parceler
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
    var markers: MutableList<TrailMarker> = mutableListOf(),
    var uid: String? = null
) : Parcelable {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "description" to description,
            "distance" to distance,
            "trailType" to trailType,
            "markers" to markers,
        )
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    companion object : Parceler<Trail> {
        override fun Trail.write(dest: Parcel, flags: Int) {
            TODO("Not yet implemented")
        }

        override fun create(parcel: Parcel): Trail = TODO()
    }
}