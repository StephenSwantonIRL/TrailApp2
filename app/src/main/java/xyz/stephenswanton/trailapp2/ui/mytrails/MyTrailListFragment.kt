package xyz.stephenswanton.trailapp2.ui.mytrails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.TrailAdapter
import xyz.stephenswanton.trailapp2.TrailListener
import xyz.stephenswanton.trailapp2.adapters.MarkerAdapter
import xyz.stephenswanton.trailapp2.adapters.NavigateAction
import xyz.stephenswanton.trailapp2.databinding.FragmentListTrailsBinding
import xyz.stephenswanton.trailapp2.databinding.FragmentMarkerListBinding
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailMarker


class MyTrailListFragment : Fragment(), NavigateAction, TrailListener {
    private var adapter: RecyclerView.Adapter<TrailAdapter.TrailViewHolder>? = null
    private lateinit var binding: FragmentListTrailsBinding

    private lateinit var rvView: View
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        binding = FragmentListTrailsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        rvView = itemView
        //to do replace with users own trails
        var trails = app!!.trails.findAll()
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.rvTrails)
        recyclerView.layoutManager = LinearLayoutManager(context)
        // set the custom adapter to the RecyclerView

        adapter = TrailAdapter(trails, this)
        recyclerView.adapter = adapter



        binding!!.rvTrails.setLayoutManager(LinearLayoutManager(activity))
        binding.rvTrails.adapter = TrailAdapter(app.trails.findAll(),this)
        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.createTrailFragment)
        }


    }

    override fun onDeleteIconClick(marker: Long) {
        var app = activity?.application as MainApp?
        app!!.trails.deleteMarkerById(marker)
        var trailId = app!!.trails.idContainingMarker(marker)

        activity?.let {
        }
    }

    override fun onEditIconClick(marker: TrailMarker) {
        var app = activity?.application as MainApp?
        activity?.let {

        }
    }

    override fun onViewIconClick(marker: TrailMarker) {
        var app = activity?.application as MainApp?
        activity?.let {

        }
    }

    override fun onEditIconClick(trail: Trail) {
        var bundle = Bundle()
        bundle.putParcelable("trail", trail)
        findNavController().navigate(R.id.createTrailFragment, bundle)
    }

    override fun onDeleteTrailIconClick(trail: Trail) {
        app!!.trails.deleteById(trail.id)
        findNavController().navigate(R.id.nav_my_trails)
    }

    override fun onViewIconClick(trail: Trail) {
        TODO("Not yet implemented")
    }

}