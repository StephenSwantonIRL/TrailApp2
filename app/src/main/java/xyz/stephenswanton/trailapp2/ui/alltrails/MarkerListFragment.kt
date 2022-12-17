package xyz.stephenswanton.trailapp2.ui.alltrails

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.adapters.MarkerAdapter
import xyz.stephenswanton.trailapp2.adapters.NavigateAction
import xyz.stephenswanton.trailapp2.databinding.FragmentMarkerListBinding
import xyz.stephenswanton.trailapp2.models.MarkerFirebaseStore
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailFirebaseStore
import xyz.stephenswanton.trailapp2.models.TrailMarker


class MarkerListFragment : Fragment(), NavigateAction {
    private var adapter: RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>? = null
    private lateinit var binding: FragmentMarkerListBinding
    private lateinit var rvView: View
    private var store = TrailFirebaseStore()
    private var markerStore = MarkerFirebaseStore()
    private lateinit var markers: List<TrailMarker>
    var trail: Trail = Trail()
    var bundle: Bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        binding = FragmentMarkerListBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        rvView = itemView
        var markers =
            arguments?.getParcelableArrayList<TrailMarker>("markers") as? List<TrailMarker>
                ?: listOf<TrailMarker>()
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.rvMarkerList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        // set the custom adapter to the RecyclerView

        adapter = MarkerAdapter(markers, this)
        recyclerView.adapter = adapter

    }

    override fun onDeleteIconClick(marker: TrailMarker) {
        markerStore.deleteById(marker.uid!!)
        bundle = Bundle()
        bundle.putString("trail", marker.trailId!!)
        markerListener()

        findNavController().navigate(R.id.viewTrailFragment, bundle)
    }

    override fun onEditIconClick(marker: TrailMarker) {
        bundle = Bundle()
        bundle.putParcelable("marker", marker)
        bundle.putString("trailId", marker.trailId)
        findNavController().navigate(R.id.createMarkerFragment, bundle)
    }

    override fun onViewIconClick(marker: TrailMarker) {
        var bundle = Bundle()
        bundle.putParcelable("marker", marker)
        bundle.putString("trailId", marker.trailId)
        findNavController().navigate(R.id.viewMarkerFragment, bundle)
    }

    private fun markerListener() {
        markerStore.dbReference
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<TrailMarker>()
                    val children = snapshot.children

                    children.forEach {
                        val marker = it.getValue(TrailMarker::class.java)
                        localList.add(marker!!)

                    }
                    markers = localList as List<TrailMarker>
                    markerStore.dbReference.removeEventListener(this)

                    bundle.putParcelableArrayList("markers", markers as ArrayList<out Parcelable?>?)

                }

            })

    }


    fun bundle2string(bundle: Bundle?): String? {
        if (bundle == null) {
            return null
        }
        var string = "Bundle{"
        for (key in bundle.keySet()) {
            string += " " + key + " => " + bundle[key] + ";"
        }
        string += " }Bundle"
        return string
    }
    }

