package xyz.stephenswanton.trailapp2.ui.viewtrail


import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentViewTrailBinding
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailMarker
import xyz.stephenswanton.trailapp2.models.User
import xyz.stephenswanton.trailapp2.models.generateRandomId
import xyz.stephenswanton.trailapp2.ui.alltrails.MarkerListFragment

class ViewTrailFragment : Fragment() {

    private var _fragBinding: FragmentViewTrailBinding? = null
    var edit: Boolean = false
    var trail = Trail(generateRandomId(), "", "")


    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        app = activity?.application as MainApp
        app!!.markersArray = mutableListOf()
        edit = false




    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentViewTrailBinding.inflate(layoutInflater)
        return _fragBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var trail = Trail(generateRandomId(), "", "")
        if (arguments?.getParcelable<Trail>("trail") != null) {

            edit = true
            trail = arguments?.getParcelable<Trail>("trail")!!
            app!!.tempTrail = app!!.trails.findById(trail.id) ?: trail
            _fragBinding!!.tvTrailName.setText(trail.name)
            _fragBinding!!.tvTrailDescription.setText(trail.description)
        }

        var markers = trail.markers
        var markerListFragment = MarkerListFragment()
        var bundle = Bundle()
        bundle.putParcelableArrayList("markers", markers as ArrayList<out Parcelable?>?)
        markerListFragment.setArguments(bundle)

        var trailMapFragment = TrailMapFragment()
        trailMapFragment.setArguments(bundle)

        childFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, markerListFragment)
            commit()
        }

        _fragBinding!!.btnMarkers.setOnClickListener {
            childFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, markerListFragment)
                commit()
            }
        }

        _fragBinding!!.btnMapView
            .setOnClickListener {
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, trailMapFragment)
                    commit()
                }
            }
        fun onDeleteIconClick(marker: TrailMarker) {
            app!!.trails.deleteMarkerById(marker.id)
            childFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, markerListFragment)
                commit()
            }
        }
            }

        fun onSubmitForm(user: User, view: View) {

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


