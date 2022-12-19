package xyz.stephenswanton.trailapp2.ui.createmarker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import timber.log.Timber
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentCreateMarkerBinding
import xyz.stephenswanton.trailapp2.helpers.FirebaseImageManager
import xyz.stephenswanton.trailapp2.helpers.showImagePicker
import xyz.stephenswanton.trailapp2.models.MarkerFirebaseStore
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailFirebaseStore
import xyz.stephenswanton.trailapp2.models.TrailMarker
import java.io.ByteArrayOutputStream

import com.google.firebase.storage.FirebaseStorage


class CreateMarkerFragment : Fragment() {

    private var _fragBinding: FragmentCreateMarkerBinding? = null
    private val fragBinding get() = _fragBinding!!
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
        registerImagePickerCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentCreateMarkerBinding.inflate(layoutInflater)
        return fragBinding.root
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
            fragBinding.etLatitude.setText(marker.latitude)
            fragBinding.etLongitude.setText(marker.longitude)
            fragBinding.etNotes.setText(marker.notes)
            fragBinding.btnSaveMarker.setText(R.string.save_marker)
            if(marker.image !="") {
                FirebaseStorage.getInstance().reference.child("markers").child("${marker.uid}.jpg")
                .downloadUrl.addOnSuccessListener {
                    // Got the download URL for 'users/me/profile.png'

                        Picasso.get().load(it)
                            .into(fragBinding.ivMarkerImage)
                }.addOnFailureListener {
                    i("error loading cloud storage image")
                        i(it.message.toString())
                }




            }
        }
        i(trail.toString())
        marker.trailId = trail.uid!!


        fragBinding.btnSaveMarker
            .setOnClickListener{
                marker.latitude = fragBinding.etLatitude.text.toString()
                marker.longitude = fragBinding.etLongitude.text.toString()
                marker.notes = fragBinding.etNotes.text.toString()
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

        fragBinding.btnUseLocation
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
                            fragBinding.etLatitude.setText(location?.latitude.toString())
                            fragBinding.etLongitude.setText(location?.longitude.toString())

                        }
                }
            }


        fragBinding.btnAddImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

    }


    private fun registerImagePickerCallback() {

        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->

                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            marker.image = result.data!!.data!!.toString()
                            updateImage(marker.uid!!,result.data!!.data!!,fragBinding.ivMarkerImage, false  )

                        }
                    }

                }


    fun updateImage(markerId: String, imageUri : Uri?, imageView: ImageView, updating : Boolean) {
        Picasso.get().load(imageUri)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?,
                                            from: Picasso.LoadedFrom?
                ) {
                    Timber.i("DX onBitmapLoaded $bitmap")
                    uploadImageToFirebase(markerId, bitmap!!, updating)
                    imageView.setImageBitmap(bitmap)
                }

                override fun onBitmapFailed(e: java.lang.Exception?,
                                            errorDrawable: Drawable?) {
                    Timber.i("DX onBitmapFailed $e")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    Timber.i("DX onPrepareLoad $placeHolderDrawable")
                    //uploadImageToFirebase(userid, defaultImageUri.value,updating)
                }
            })
    }




    fun uploadImageToFirebase(markerId: String, bitmap: Bitmap, updating : Boolean) {
        // Get the data from an ImageView as bytes
        val imageRef = FirebaseStorage.getInstance().reference.child("markers").child("${markerId}.jpg")
        i(imageRef.toString())

        //val bitmap = (imageView as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        lateinit var uploadTask: UploadTask

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            i(it.message.toString())
        }.addOnSuccessListener {
            marker.image = imageRef.toString()

        }
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

