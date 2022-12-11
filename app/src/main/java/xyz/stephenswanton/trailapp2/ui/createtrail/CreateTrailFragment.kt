package xyz.stephenswanton.trailapp2.ui.createtrail

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentCreateMarkerBinding
import xyz.stephenswanton.trailapp2.databinding.FragmentCreateTrailBinding
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.*
import xyz.stephenswanton.trailapp2.ui.alltrails.MarkerListFragment
import xyz.stephenswanton.trailapp2.ui.createmarker.CreateMarkerFragment

class CreateTrailFragment : Fragment() {

    private var _fragBinding: FragmentCreateTrailBinding? = null
    var edit: Boolean = false
    var trail = Trail(generateRandomId(), "", "")
    private var store = TrailFirebaseStore()
    private var markerStore = MarkerFirebaseStore()
    private var markers: MutableList<TrailMarker> = mutableListOf()

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
        _fragBinding = FragmentCreateTrailBinding.inflate(layoutInflater)
        return _fragBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getParcelable<Trail>("trail") != null) {
            edit = true
            trail = arguments?.getParcelable<Trail>("trail")!!
            _fragBinding!!.etTrailName.setText(trail.name)
            _fragBinding!!.etTrailDescription.setText(trail.description)
            var trailTypes = resources.getStringArray(R.array.trail_type)
            var spinnerPosition = trailTypes.indexOf(trail.trailType) as Int
            _fragBinding!!.spTrailType.setSelection(spinnerPosition)
        }

        if (!edit) {

            trail.markers = mutableListOf()
            trail.uid = store.createKey()
            findMarkersByTrailId(trail.uid!!)
            i(markers.toString())
        } else {
            i(trail.uid!!.toString())
           findMarkersByTrailId(trail.uid!!)
            i(markers.toString())
        }

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
                    store.update(trail)
                    var trailWrapper = Bundle()
                    trailWrapper.putParcelable("trail", trail)
                    findNavController().navigate(R.id.createMarkerFragment, trailWrapper)
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

    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.create_trail_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miSave -> {
                if (trail.name.isEmpty()) {
                    Toast.makeText(activity,R.string.add_a_marker, Toast.LENGTH_LONG).show()
                } else {
                    trail.name = _fragBinding!!.etTrailName.text.toString()
                    trail.trailType = trail.trailType.toString()
                    trail.description = _fragBinding!!.etTrailDescription.text.toString()
                    if(edit){
                        store.update(trail.copy())
                    } else {
                        store.create(trail.copy())
                    }
                    findNavController().navigate(R.id.nav_my_trails)
                }

            };
            R.id.miCancel -> {
                findNavController().navigateUp()
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        findMarkersByTrailId(trail.uid!!)


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
                markerStore.dbReference.removeEventListener(this)
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



}


