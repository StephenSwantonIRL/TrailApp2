package xyz.stephenswanton.trailapp2.ui.createmarker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import timber.log.Timber
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentCreateMarkerBinding
import xyz.stephenswanton.trailapp2.helpers.showImagePicker
import xyz.stephenswanton.trailapp2.models.MarkerFirebaseStore
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailFirebaseStore
import xyz.stephenswanton.trailapp2.models.TrailMarker

class CreateMarkerFragment : Fragment() {

    private var _fragBinding: FragmentCreateMarkerBinding? = null
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var bundle: Bundle = Bundle()

    var edit = false
    private var trailStore = TrailFirebaseStore()
    private var markerStore = MarkerFirebaseStore()
    val IMAGE_REQUEST = 1
    var marker: TrailMarker = TrailMarker(0L, "0", "0", "", "",markerStore.createKey(),"")
    private var trail: Trail = Trail()
    var latitudeRegex: Regex =
        """^(\+|-)?(?:90(?:(?:\.0{1,7})?)|(?:[0-9]|[1-8][0-9])(?:(?:\.[0-9]{1,7})?))$""".toRegex()
    var longitudeRegex: Regex =
        """^(\+|-)?(?:180(?:(?:\.0{1,7})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\.[0-9]{1,7})?))${'$'}""".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        var edit = false

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentCreateMarkerBinding.inflate(layoutInflater)
        return _fragBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments?.getParcelable<Trail>("trail") != null ) {
            trail = arguments?.getParcelable<Trail>("trail")!!
            restOfFragmentOnViewCreated()
        } else if (arguments?.getString("trailId") != null) {
            findTrailById(arguments?.getString("trailId")!!)

        } else {
            trail = Trail()
            restOfFragmentOnViewCreated()
        }

    }

    private fun restOfFragmentOnViewCreated(){

        if (arguments?.getParcelable<TrailMarker>("marker") != null) {
            edit = true
            marker = arguments?.getParcelable<TrailMarker>("marker")!!
            _fragBinding!!.etLatitude.setText(marker.latitude)
            _fragBinding!!.etLongitude.setText(marker.longitude)
            _fragBinding!!.etNotes.setText(marker.notes)
            _fragBinding!!.btnSaveMarker.setText(R.string.save_marker)
            if(marker.image !="") {
                Picasso.get()
                    .load(marker.image)
                    .into(_fragBinding!!.ivMarkerImage)
            }
        }
        i(trail.toString())
        marker.trailId = trail.uid!!


        _fragBinding!!.btnSaveMarker
            .setOnClickListener{
                marker.latitude = _fragBinding!!.etLatitude.text.toString()
                marker.longitude = _fragBinding!!.etLongitude.text.toString()
                marker.notes = _fragBinding!!.etNotes.text.toString()
                if (marker.latitude.isEmpty() || marker.longitude.isEmpty() ) {
                    if (marker.latitude.isEmpty()) {
                        Snackbar.make(it, R.string.enter_latitude, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    if (marker.longitude.isEmpty()) {
                        Snackbar.make(it, R.string.enter_longitude, Snackbar.LENGTH_LONG)
                            .show()
                    }
                } else if (!latitudeRegex.matches(marker.latitude) || !longitudeRegex.matches(marker.longitude) ) {
                    if (!latitudeRegex.matches(marker.latitude)) {
                        Snackbar.make(it, R.string.enter_valid_latitude, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    if (!longitudeRegex.matches(marker.longitude)) {
                        Snackbar.make(it, R.string.enter_valid_longitude, Snackbar.LENGTH_LONG)
                            .show()
                    }

                } else {
                    if(edit){
                        trail.markers = trail.markers.filter{item -> item != marker.uid} as MutableList<String>
                    }
                    trail.markers.add(marker.uid!!)
                    markerStore.update(marker)
                    trailStore.update(trail)
                    var trailWrapper = Bundle()
                    trailWrapper.putParcelable("trail", trail)
                    findNavController().navigate(R.id.createTrailFragment, trailWrapper)
                }
            }

        _fragBinding!!.btnUseLocation
            .setOnClickListener{

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
                            _fragBinding!!.etLatitude.setText(location?.latitude.toString())
                            _fragBinding!!.etLongitude.setText(location?.longitude.toString())

                        }
                }
            }

        _fragBinding!!.btnAddImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }
        registerImagePickerCallback()

    }


    private fun registerImagePickerCallback() {

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

    private fun findTrailById(trailId:String) {
        trailStore.dbReference.child(trailId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localTrail = snapshot.getValue(Trail::class.java)
                    trail = localTrail ?: Trail()
                    i(trail.toString())
                    trailStore.dbReference.removeEventListener(this)

                    bundle.putParcelable("trail", trail as Trail?)
                    restOfFragmentOnViewCreated()
                }

            })

    }



}

