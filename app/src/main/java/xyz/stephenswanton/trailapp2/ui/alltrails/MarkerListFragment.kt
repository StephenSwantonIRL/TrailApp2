package xyz.stephenswanton.trailapp2.ui.alltrails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.MainActivity
import xyz.stephenswanton.trailapp2.adapters.MarkerAdapter
import xyz.stephenswanton.trailapp2.adapters.NavigateAction
import xyz.stephenswanton.trailapp2.databinding.FragmentMarkerListBinding
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailMarker


class MarkerListFragment : Fragment(), NavigateAction {
    private var adapter: RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>? = null
    private lateinit var binding: FragmentMarkerListBinding

    private lateinit var rvView: View

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

    override fun onDeleteIconClick(marker: Long) {
        var app = activity?.application as MainApp?
        app!!.trails.deleteMarkerById(marker)

        var bundle = Bundle()
        var trailId = app!!.trails.idContainingMarker(marker)
        var trail = app!!.trails.findById(trailId!!)
        app!!.tempTrail.markers = trail!!.markers
        var markers = app!!.tempTrail.markers

        bundle.putParcelableArrayList("markers", markers as ArrayList<out Parcelable?>?)
        bundle.putParcelable("trail", trail)
        findNavController().navigate(R.id.viewTrailFragment, bundle)
    }

    override fun onEditIconClick(marker: TrailMarker) {
        var bundle = Bundle()
        bundle.putParcelable("marker", marker)
        findNavController().navigate(R.id.createMarkerFragment, bundle)
    }

    override fun onViewIconClick(marker: TrailMarker) {
        var bundle = Bundle()
        bundle.putParcelable("marker", marker)
        findNavController().navigate(R.id.viewMarkerFragment, bundle)
    }

}