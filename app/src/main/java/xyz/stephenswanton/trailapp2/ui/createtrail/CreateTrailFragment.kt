package xyz.stephenswanton.trailapp2.ui.createtrail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentCreateMarkerBinding
import xyz.stephenswanton.trailapp2.databinding.FragmentCreateTrailBinding
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailMarker
import xyz.stephenswanton.trailapp2.models.User
import xyz.stephenswanton.trailapp2.models.generateRandomId

class CreateTrailFragment : Fragment() {

    private var _fragBinding: FragmentCreateTrailBinding? = null
    var edit: Boolean = false
    var trail = Trail( generateRandomId(), "","" )



    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        app!!.markersArray = mutableListOf()
        edit = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentCreateTrailBinding.inflate(layoutInflater)
        return _fragBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

                }





    private fun onSubmitForm(user: User, view: View) {

    }


}


