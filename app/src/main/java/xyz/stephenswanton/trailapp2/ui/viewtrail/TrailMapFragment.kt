package xyz.stephenswanton.trailapp2.ui.viewtrail

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.models.TrailMarker

class TrailMapFragment : Fragment() {

    private lateinit var markers: List<TrailMarker>

    private val callback = OnMapReadyCallback { googleMap ->
        var locations: MutableList<LatLng> = mutableListOf()
        for (marker in markers){
            locations.add(LatLng(marker.latitude.toDouble(), marker.longitude.toDouble()))
        }
        for (location in locations) {
            googleMap.addMarker(MarkerOptions().position(location).title(""))
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 11f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markers =
            arguments?.getParcelableArrayList<TrailMarker>("markers") as? List<TrailMarker>
                ?: listOf<TrailMarker>()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}