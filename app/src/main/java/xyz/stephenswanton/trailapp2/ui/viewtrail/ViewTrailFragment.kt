package xyz.stephenswanton.trailapp2.ui.viewtrail


import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentViewTrailBinding
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.*
import xyz.stephenswanton.trailapp2.ui.alltrails.MarkerListFragment

class ViewTrailFragment : Fragment() {

    private var _fragBinding: FragmentViewTrailBinding? = null
    var edit: Boolean = false
    var trail = Trail()
    private var trailStore = TrailFirebaseStore()
    private var markerStore = MarkerFirebaseStore()
    private var markers: MutableList<TrailMarker> = mutableListOf()
    var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        if (arguments?.getParcelable<Trail>("trail") != null) {
            edit = true
            trail = arguments?.getParcelable<Trail>("trail")!!
            restOfFragmentOnViewCreated()
        } else if (arguments?.getString("trailId") != null) {
            findTrailById(arguments?.getString("trailId")!!)
        } else {
            trail = Trail()
            restOfFragmentOnViewCreated()
        }

    }

    override fun onResume() {
        super.onResume()
        //findMarkersByTrailId(trail.uid!!)


    }


    fun findMarkersByTrailId(trailId: String) {
        var snapshot = markerStore.dbReference.orderByChild("trailId").equalTo(trailId)
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
                Timber.i(markers.toString())
                markerStore.dbReference.removeEventListener(this)
                Timber.i("this was called")
                Timber.i(markers.toString())
                var markerListFragment = MarkerListFragment()
                var bundle = Bundle()
                bundle.putParcelableArrayList("markers", markers as ArrayList<out Parcelable?>?)
                markerListFragment.setArguments(bundle)

                childFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, markerListFragment)
                    commit()
                }
            }

        })

    }


    fun restOfFragmentOnViewCreated(){
        _fragBinding!!.tvTrailName.setText(trail.name)
        _fragBinding!!.tvTrailDescription.setText(trail.description)
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



    }

    private fun findTrailById(trailId: String) {
        trailStore.dbReference.child(trailId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localTrail = snapshot.getValue(Trail::class.java)
                    trail = localTrail ?: Trail()
                    Timber.i(trail.toString())
                    trailStore.dbReference.removeEventListener(this)

                    bundle.putParcelable("trail", trail as Trail?)
                    restOfFragmentOnViewCreated()
                }

            })

    }


}


