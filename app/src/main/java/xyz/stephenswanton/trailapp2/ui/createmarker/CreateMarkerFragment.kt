package xyz.stephenswanton.trailapp2.ui.createmarker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import timber.log.Timber
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentCreateMarkerBinding
import xyz.stephenswanton.trailapp2.helpers.showImagePicker
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailMarker
import xyz.stephenswanton.trailapp2.models.User
import xyz.stephenswanton.trailapp2.models.generateRandomId

class CreateMarkerFragment : Fragment() {

    private var _fragBinding: FragmentCreateMarkerBinding? = null
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var edit = false
    val IMAGE_REQUEST = 1

    var marker = TrailMarker(generateRandomId(), "0", "0", "")
    var latitudeRegex: Regex =
        """^(\+|-)?(?:90(?:(?:\.0{1,7})?)|(?:[0-9]|[1-8][0-9])(?:(?:\.[0-9]{1,7})?))$""".toRegex()
    var longitudeRegex: Regex =
        """^(\+|-)?(?:180(?:(?:\.0{1,7})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\.[0-9]{1,7})?))${'$'}""".toRegex()

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

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

        if (arguments?.getParcelable<TrailMarker>("marker") != null) {
            edit = true
            marker = arguments?.getParcelable<TrailMarker>("marker")!!
            _fragBinding!!.etLatitude.setText(marker.latitude)
            _fragBinding!!.etLongitude.setText(marker.longitude)
            _fragBinding!!.etNotes.setText(marker.notes)
            _fragBinding!!.btnSaveMarker.setText(R.string.save_marker)
            Picasso.get()
                .load(marker.image)
                .into(_fragBinding!!.ivMarkerImage)
        }



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
                        app!!.markersArray = app!!.markersArray.filter{item -> item.id != marker.id} as MutableList<TrailMarker>
                    }
                    app!!.markersArray.add(marker.copy())
                    app!!.tempTrail.markers = mutableListOf<TrailMarker>()
                    app!!.tempTrail.markers.addAll(app!!.markersArray)
                    app!!.tempTrailObject.update(app!!.tempTrail)
                    if(edit){
                    }
                    findNavController().navigateUp()
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
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data}")
                            marker.image = result.data!!.data!!
                            Picasso.get()
                                .load(marker.image)
                                .into(_fragBinding!!.ivMarkerImage)
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun onSubmitForm(user: User, view: View) {

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

}

