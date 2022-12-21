package xyz.stephenswanton.trailapp2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.stephenswanton.trailapp2.databinding.ItemTrailBinding
import xyz.stephenswanton.trailapp2.databinding.ItemTrailViewonlyBinding
import xyz.stephenswanton.trailapp2.models.Trail


class TrailViewOnlyAdapter(
    var trails: List<Trail>,
    var listener: TrailListener
): RecyclerView.Adapter<TrailViewOnlyAdapter.ViewOnlyTrailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewOnlyTrailViewHolder {

        val binding = ItemTrailViewonlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewOnlyTrailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrailViewOnlyAdapter.ViewOnlyTrailViewHolder, position: Int) {
        val trail = trails[position]
        holder.bind(trail, listener)
        }

    override fun getItemCount(): Int {
        return trails.size
    }

    class ViewOnlyTrailViewHolder(private val binding : ItemTrailViewonlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trail: Trail, listener: TrailListener) {
            binding.tvTrailName.text = trail.name
            binding.ivViewTrail.setOnClickListener { listener.onViewIconClick(trail) }
        }
    }
}