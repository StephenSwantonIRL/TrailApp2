package xyz.stephenswanton.trailapp2.ui.nearme

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.models.MarkerFirebaseStore
import xyz.stephenswanton.trailapp2.models.TrailMarker


class NearMeMapFragment : Fragment() {

    var markers = mutableListOf<TrailMarker>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var mapLocation = LatLng(0.0,0.0)
    val markerStore = MarkerFirebaseStore()

    private val callback = OnMapReadyCallback { googleMap ->
        i(markers.toString())
        var locations: MutableList<LatLng> = mutableListOf()
        for (marker in markers){
            locations.add(LatLng(marker.latitude.toDouble(), marker.longitude.toDouble()))
        }
        for (location in locations) {
            googleMap.addMarker(MarkerOptions().position(location).title(""))
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 11f))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentLocation()



    }


    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
        }
        }
    }

    fun getCurrentLocation(){
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))

        if (ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mapLocation = LatLng(location!!.latitude, location!!.longitude )
                    findAllMarkersAndApplyToMap()



                }
        }

    }

    fun findAllMarkersAndApplyToMap() {
        var snapshot = markerStore.dbReference
        snapshot.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.i("Firebase error : ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot){
                val localList = ArrayList<TrailMarker>()
                val children = snapshot.children

                children.forEach {
                    val marker = it.getValue(TrailMarker::class.java)
                    localList.add(marker!!)

                }
                markers = localList
                markerStore.dbReference.removeEventListener(this)
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync(callback)
            }

        })

    }


}