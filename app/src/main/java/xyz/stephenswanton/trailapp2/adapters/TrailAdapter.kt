package xyz.stephenswanton.trailapp2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.stephenswanton.trailapp2.databinding.ItemTrailBinding
import xyz.stephenswanton.trailapp2.models.Trail

interface TrailListener {
    fun onEditIconClick(trail: Trail)
    fun onDeleteTrailIconClick(trail: Trail)
    fun onViewIconClick(trail: Trail)
}


class TrailAdapter(
    var trails: List<Trail>,
    var listener: TrailListener
): RecyclerView.Adapter<TrailAdapter.TrailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailViewHolder {

        val binding = ItemTrailBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TrailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrailAdapter.TrailViewHolder, position: Int) {
        val trail = trails[position]
        holder.bind(trail, listener)
        }

    override fun getItemCount(): Int {
        return trails.size
    }

    class TrailViewHolder(private val binding : ItemTrailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trail: Trail, listener: TrailListener) {
            binding.tvTrailName.text = trail.name
            binding.ivViewTrail.setOnClickListener { listener.onViewIconClick(trail) }
            binding.ivEdit.setOnClickListener { listener.onEditIconClick(trail) }
            binding.ivDeleteTrail.setOnClickListener { listener.onDeleteTrailIconClick(trail) }
        }
    }
}