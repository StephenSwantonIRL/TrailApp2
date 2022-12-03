package xyz.stephenswanton.trailapp2.models

import android.R.attr.subtitle
import android.R.id
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize


@Parcelize
data class TrailMarker (
    var id: Long,
    var latitude: String,
    var longitude: String,
    var notes: String = "",
    var image: Uri = Uri.EMPTY
    ) : Parcelable