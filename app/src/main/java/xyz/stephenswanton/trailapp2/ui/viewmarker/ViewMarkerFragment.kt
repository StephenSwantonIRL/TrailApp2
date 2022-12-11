package xyz.stephenswanton.trailapp2.ui.viewmarker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.squareup.picasso.Picasso

import xyz.stephenswanton.trailapp2.databinding.FragmentViewMarkerBinding
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.MarkerFirebaseStore
import xyz.stephenswanton.trailapp2.models.TrailMarker
import xyz.stephenswanton.trailapp2.models.User
import xyz.stephenswanton.trailapp2.models.generateRandomId

class ViewMarkerFragment : Fragment() {

    private var _fragBinding: FragmentViewMarkerBinding? = null
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var edit = false
    val IMAGE_REQUEST = 1
    var markerStore = MarkerFirebaseStore()

    var marker = TrailMarker(generateRandomId(), "0", "0", "", "", markerStore.createKey(), "" )
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
       var edit = false

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentViewMarkerBinding.inflate(layoutInflater)
        return _fragBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getParcelable<TrailMarker>("marker") != null) {
            edit = true
            marker = arguments?.getParcelable<TrailMarker>("marker")!!
            _fragBinding!!.tvLatitudeView.setText(marker.latitude)
            _fragBinding!!.tvLongitudeView.setText(marker.longitude)
            if(marker.notes !="") {
                _fragBinding!!.tvNotesView.setText(marker.notes)
            } else {
                _fragBinding!!.tvNotesView.setText("No notes provided")
            }
            if(marker.image !=""){
                Picasso.get()
                    .load(marker.image)
                    .into(_fragBinding!!.ivMarkerImage)
            }

        }
    }


    private fun onSubmitForm(user: User, view: View) {

    }


}


