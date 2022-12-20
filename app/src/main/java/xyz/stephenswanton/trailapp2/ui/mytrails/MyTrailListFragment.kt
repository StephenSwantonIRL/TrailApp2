package xyz.stephenswanton.trailapp2.ui.mytrails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.TrailAdapter
import xyz.stephenswanton.trailapp2.TrailListener
import xyz.stephenswanton.trailapp2.adapters.MarkerAdapter
import xyz.stephenswanton.trailapp2.adapters.NavigateAction
import xyz.stephenswanton.trailapp2.databinding.FragmentListTrailsBinding
import xyz.stephenswanton.trailapp2.databinding.FragmentMarkerListBinding
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.MarkerFirebaseStore
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailFirebaseStore
import xyz.stephenswanton.trailapp2.models.TrailMarker


class MyTrailListFragment : Fragment(), TrailListener {
    private var adapter: RecyclerView.Adapter<TrailAdapter.TrailViewHolder>? = null
    private lateinit var binding: FragmentListTrailsBinding

    private lateinit var rvView: View
    private var store = TrailFirebaseStore()
    private var markerStore = MarkerFirebaseStore()
    lateinit var listTrails: List<Trail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listTrails = store.findAll()
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
        trailListener()

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.createTrailFragment)
        }

    }


    override fun onEditIconClick(trail: Trail) {
        var bundle = Bundle()
        bundle.putParcelable("trail", trail)
        findNavController().navigate(R.id.createTrailFragment, bundle)
    }

    override fun onDeleteTrailIconClick(trail: Trail) {
        i(trail.uid.toString())
        store.deleteById(trail.uid!!)
        deleteMarkersByTrail(trail.uid!!)
        trailListener()
    }

    override fun onViewIconClick(trail: Trail) {
        var bundle = Bundle()
        bundle.putParcelable("trail", trail)
        findNavController().navigate(R.id.createTrailFragment, bundle)
    }

    private fun trailListener() {
        store.dbReference.orderByChild("createdBy").equalTo(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<Trail>()
                    val children = snapshot.children

                    children.forEach {
                        val trail = it.getValue(Trail::class.java)
                        localList.add(trail!!)

                    }
                    listTrails = localList
                    store.dbReference.removeEventListener(this)
                    binding!!.rvTrails.layoutManager = LinearLayoutManager(activity)
                    binding.rvTrails.adapter = TrailAdapter(listTrails, this@MyTrailListFragment)
                }

            })

    }


    private fun deleteMarkersByTrail(trailId: String){
        var snapshot = markerStore.dbReference.orderByChild("trailId").equalTo(trailId)
        snapshot.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.i("Firebase error : ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot){
                val children = snapshot.children

                children.forEach {
                    val marker = it.getValue(TrailMarker::class.java)
                    i(marker.toString())
                    markerStore.dbReference.child(marker!!.uid!!).removeValue()

                }
                markerStore.dbReference.removeEventListener(this)
            }

        })
    }


}