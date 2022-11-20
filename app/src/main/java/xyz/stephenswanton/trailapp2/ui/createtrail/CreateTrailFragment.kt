package xyz.stephenswanton.trailapp2.ui.createtrail

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import xyz.stephenswanton.trailapp2.ui.alltrails.MarkerListFragment
import xyz.stephenswanton.trailapp2.ui.createmarker.CreateMarkerFragment

class CreateTrailFragment : Fragment() {

    private var _fragBinding: FragmentCreateTrailBinding? = null
    var edit: Boolean = false
    var trail = Trail(generateRandomId(), "", "")


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

        if (!edit) {
            app!!.markers = mutableListOf()
        } else {
            app!!.markers = app!!.tempTrail.markers
        }
        var markers = app!!.markers
        var markerListFragment = MarkerListFragment()
        var bundle = Bundle()
        bundle.putParcelableArrayList("markers", markers as ArrayList<out Parcelable?>?)
        markerListFragment.setArguments(bundle)

        childFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, markerListFragment)
            commit()
        }


        _fragBinding!!.btnNewMarker
            .setOnClickListener {
                trail.name = _fragBinding!!.etTrailName.text.toString()
                trail.description = _fragBinding!!.etTrailDescription.text.toString()
                if (trail.name.isEmpty()) {
                    Snackbar.make(it, R.string.enter_trail_name, Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    app!!.tempTrail = trail.copy()
                    app!!.tempTrailObject.update(app!!.tempTrail)
                    findNavController().navigate(R.id.createMarkerFragment)
                }
            }
        _fragBinding!!.spTrailType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    trail.trailType = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        fun onSubmitForm(user: User, view: View) {

        }




    }


    override fun onResume() {
        super.onResume()
        var markers = app!!.tempTrail.markers
        var markerListFragment = MarkerListFragment()
        var bundle = Bundle()
        bundle.putParcelableArrayList("markers", markers as ArrayList<out Parcelable?>?)
        markerListFragment.setArguments(bundle)

        childFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, markerListFragment)
            commit()
        }

    }
}


